/*
 * Copyright (c) 2002-2026 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wetator.test.jetty;

import java.io.IOException;
import java.net.BindException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.http.CookieCompliance;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.UriCompliance;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.annotation.MultipartConfig;

/**
 * Helpers to centralize Jetty access.
 *
 * @author Ronald Brill
 */
public final class JettyServerUtils {

  /**
   * Starts a web server with the given configuration.
   *
   * @param port the port to bind to
   * @param resourceBase the resource base directory
   * @param servlets map of servlet path specs to servlet classes
   * @param socketListeners map of WebSocket path specs to listener classes
   * @param serverCharset the charset for the server (can be null)
   * @param isBasicAuthentication whether to enable basic authentication
   * @return the started server
   * @throws Exception if server startup fails
   */
  public static Server startWebServer(final int port, final String resourceBase,
      final Map<String, Class<? extends Servlet>> servlets, final Charset serverCharset,
      final Map<String, String> extraMimeTypes, final boolean isBasicAuthentication) throws Exception {
    final Server server = buildServer(port, serverCharset);

    final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");

    if (extraMimeTypes != null) {
      final MimeTypes.Mutable mimes = context.getMimeTypes();
      extraMimeTypes.forEach(mimes::addMimeMapping);
    }

    final Resource baseResource = ResourceFactory.of(context).newResource(getResourceBasePath(resourceBase));
    context.setBaseResource(baseResource);

    context.setErrorHandler(new ConsoleErrorHandler());

    if (isBasicAuthentication) {
      final Path realmPath = Paths.get("./src/test/java/org/wetator/test/jetty/realm.properties").toAbsolutePath();
      if (!Files.exists(realmPath)) {
        throw new IOException("Realm file not found: '" + realmPath + "'");
      }

      final Resource realmResource = ResourceFactory.of(context).newResource(realmPath);
      final HashLoginService loginService = new HashLoginService("wetator", realmResource);
      server.addBean(loginService);

      final ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
      securityHandler.setLoginService(loginService);
      securityHandler.setAuthenticator(new BasicAuthenticator());

      final Constraint constraint = new Constraint.Builder().authorization(Constraint.Authorization.SPECIFIC_ROLE)
          .roles("user").build();

      final ConstraintMapping mapping = new ConstraintMapping();
      mapping.setConstraint(constraint);
      mapping.setPathSpec("/snoopyAuth.php");
      securityHandler.setConstraintMappings(List.of(mapping));

      context.setSecurityHandler(securityHandler);
    }

    boolean overwritesDefaultPath = false;
    // in startWebServer, where servlets are registered:
    for (final Map.Entry<String, Class<? extends Servlet>> entry : servlets.entrySet()) {
      final String pathSpec = entry.getKey();
      final Class<? extends Servlet> servlet = entry.getValue();

      final ServletHolder holder = new ServletHolder(servlet);

      // enable multipart if the servlet declares @MultipartConfig
      if (servlet.isAnnotationPresent(MultipartConfig.class)) {
        final MultipartConfig config = servlet.getAnnotation(jakarta.servlet.annotation.MultipartConfig.class);
        final MultipartConfigElement multipartConfig = new MultipartConfigElement(
            config.location().isEmpty() ? System.getProperty("java.io.tmpdir") : config.location(),
            config.maxFileSize() == -1L ? 10 * 1024 * 1024 : config.maxFileSize(),
            config.maxRequestSize() == -1L ? 20 * 1024 * 1024 : config.maxRequestSize(), config.fileSizeThreshold());
        holder.getRegistration().setMultipartConfig(multipartConfig);
      }

      context.addServlet(holder, pathSpec);
      overwritesDefaultPath |= "/".equals(pathSpec);
    }
    if (overwritesDefaultPath) {
      context.addServlet(DefaultServlet.class, "/favicon.ico");
    } else {
      // For static resources - use DefaultServlet
      final ServletHolder defaultHolder = new ServletHolder("default", DefaultServlet.class);
      // Don't set resourceBase here - it will use the context's base resource
      // defaultHolder.setInitParameter("resourceBase", resourceBase);
      defaultHolder.setInitParameter("dirAllowed", "true");
      defaultHolder.setInitParameter("pathInfoOnly", "true");
      context.addServlet(defaultHolder, "/");
    }

    server.setHandler(context);

    tryStart(port, server);

    return server;
  }

  private static Server buildServer(final int port, final Charset queryEncoding) {
    final QueuedThreadPool threadPool = new QueuedThreadPool(10, 2);

    final Server server = new Server(threadPool);

    final HttpConfiguration httpConfig = new HttpConfiguration();

    if (queryEncoding != null) {
      httpConfig.setRequestCookieCompliance(CookieCompliance.RFC6265);
      httpConfig.setResponseCookieCompliance(CookieCompliance.RFC6265);
      // Note: In Jetty 12, query encoding is handled via UriCompliance
      httpConfig.setUriCompliance(UriCompliance.DEFAULT);
    }

    final HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfig);

    final ServerConnector connector = new ServerConnector(server, 1, -1, httpConnectionFactory);
    connector.setPort(port);
    server.addConnector(connector);

    return server;
  }

  /**
   * Starts the server; handles BindExceptions and retries.
   *
   * @param port the port only used for the error message
   * @param server the server to start
   * @throws Exception in case of error
   */
  private static void tryStart(final int port, final Server server) throws Exception {
    final long maxWait = System.currentTimeMillis() + 1000;

    while (true) {
      try {
        server.start();
        return;
      } catch (final BindException e) {
        if (System.currentTimeMillis() > maxWait) {
          // destroy the server to free all associated resources
          stopServer(server);

          throw (BindException) new BindException("Port " + port + " is already in use").initCause(e);
        }
        Thread.sleep(200);
      } catch (final IOException e) {
        // looks like newer jetty already catches the bind exception
        if (e.getCause() instanceof BindException) {
          if (System.currentTimeMillis() > maxWait) {
            // destroy the server to free all associated resources
            stopServer(server);

            throw (BindException) new BindException("Port " + port + " is already in use").initCause(e);
          }
          Thread.sleep(200);
        } else {
          // destroy the server to free all associated resources
          stopServer(server);

          throw e;
        }
      }
    }
  }

  public static void stopServer(Server server) throws Exception {
    if (server != null) {
      server.stop();
      server.destroy();
    }
  }

  private static Path getResourceBasePath(final String resourceBase) throws IOException {
    if (StringUtils.isEmpty(resourceBase)) {
      throw new IllegalArgumentException("Resource base cannot be null or empty");
    }

    final Path resourceBasePath = Paths.get(resourceBase).toAbsolutePath();
    if (!Files.isDirectory(resourceBasePath)) {
      throw new IOException("Resource path is not a directory: '" + resourceBasePath + "'");
    }
    if (!Files.isReadable(resourceBasePath)) {
      throw new IOException("Resource path is not readable: '" + resourceBasePath + "'");
    }

    return resourceBasePath;
  }

  private static final class ConsoleErrorHandler extends ErrorHandler {
    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Throwable errorException = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
      if (errorException != null) {
        System.err.println("\n==== Jetty Servlet Error ====");
        System.err.println("URI:       " + request.getHttpURI());
        System.err.println("Method:    " + request.getMethod());
        System.err.println("Exception: " + errorException.getClass().getName());
        System.err.println("Message:   " + errorException.getMessage());
        errorException.printStackTrace(System.err);
        System.err.println("=============================\n");
      }

      return super.handle(request, response, callback);
    }
  }
}