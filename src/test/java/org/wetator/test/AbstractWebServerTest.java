/*
 * Copyright (c) 2008-2025 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wetator.test;

import java.io.File;
import java.util.Properties;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.wetator.commandset.IncubatorCommandSet;
import org.wetator.commandset.SqlCommandSet;
import org.wetator.commandset.TestCommandSet;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.InvalidInputException;
import org.wetator.progresslistener.StdOutProgressListener;
import org.wetator.test.jetty.ContentServlet;
import org.wetator.test.jetty.HttpHeaderServlet;
import org.wetator.test.jetty.RedirectServlet;
import org.wetator.test.jetty.SnoopyServlet;

/**
 * Base test class for all WetatorEngine tests that need a web server.
 *
 * @author frank.danek
 */
public abstract class AbstractWebServerTest extends AbstractBrowserTest {

  /** The listener port for the web server. */
  public static final int DEFAULT_PORT = Integer.valueOf(System.getProperty("wetator.test.port", "4711"));
  protected static final String DEFAULT_DOCUMENT_ROOT = "test/webpage";

  private static Server server;

  private WetatorEngine wetatorEngine;
  private JUnitProgressListener listener;

  /**
   * Starts the web server.<br>
   * The default port is {@link #DEFAULT_PORT}.
   * The default document root is {@link #DEFAULT_DOCUMENT_ROOT}.<br>
   *
   * @throws Exception if an error occurs starting the web server
   */
  @BeforeClass
  public static void startWebServer() throws Exception {
    if (server != null) {
      throw new IllegalStateException("startWebServer() can not be called twice");
    }
    server = new Server(DEFAULT_PORT);

    // resources
    final ResourceHandler tmpResourceHandler = new ResourceHandler();
    tmpResourceHandler.setDirectoriesListed(true);
    tmpResourceHandler.setWelcomeFiles(new String[] { "index.html" });
    tmpResourceHandler.setResourceBase(DEFAULT_DOCUMENT_ROOT);
    tmpResourceHandler.setRedirectWelcome(true); // see https://github.com/eclipse/jetty.project/issues/1856

    // servlets
    final ServletContextHandler tmpContextHandler = new ServletContextHandler();
    tmpContextHandler.setContextPath("/");
    tmpContextHandler.setAttribute("javax.servlet.context.tempdir", new File("./tmp"));
    tmpContextHandler.addServlet(new ServletHolder(new HttpHeaderServlet()), "/http_header.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_header.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_js.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_meta.php");
    tmpContextHandler.addServlet(new ServletHolder(new ContentServlet()), "/create_excel");
    tmpContextHandler.addServlet(addMultipartConfig(new ServletHolder(new SnoopyServlet())), "/snoopy.php");
    tmpContextHandler.addServlet(addMultipartConfig(new ServletHolder(new SnoopyServlet())), "/snoopyAuth.php");

    final HandlerList tmpHandlers = new HandlerList();
    tmpHandlers.setHandlers(new Handler[] { tmpResourceHandler, tmpContextHandler, new DefaultHandler() });
    server.setHandler(tmpHandlers);

    // security
    final Constraint tmpConstraint = new Constraint();
    tmpConstraint.setName(Constraint.__BASIC_AUTH);
    tmpConstraint.setRoles(new String[] { "user" });
    tmpConstraint.setAuthenticate(true);

    final ConstraintMapping tmpConstraintMapping = new ConstraintMapping();
    tmpConstraintMapping.setConstraint(tmpConstraint);
    tmpConstraintMapping.setPathSpec("/snoopyAuth.php");

    final LoginService tmpLoginService = new HashLoginService("wetator",
        "src/test/java/org/wetator/test/jetty/realm.properties");

    final ConstraintSecurityHandler tmpSecurityHandler = new ConstraintSecurityHandler();
    tmpSecurityHandler.setLoginService(tmpLoginService);
    tmpSecurityHandler.setAuthMethod(Constraint.__BASIC_AUTH);
    tmpSecurityHandler.addConstraintMapping(tmpConstraintMapping);

    tmpContextHandler.setSecurityHandler(tmpSecurityHandler);

    // time to start
    server.start();

    // since jetty 9.3 we have to do this after the start of the server;
    // seems like the server start overwrites the mime types
    tmpResourceHandler.getMimeTypes().addMimeMapping("json", "application/json");
    tmpResourceHandler.getMimeTypes().addMimeMapping("xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    tmpResourceHandler.getMimeTypes().addMimeMapping("docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
  }

  private static ServletHolder addMultipartConfig(final ServletHolder aServletHolder) {
    final File tmpTempDir = new File(System.getProperty("java.io.tmpdir"));
    final MultipartConfigElement tmpMultipartConfig = new MultipartConfigElement(tmpTempDir.getAbsolutePath(),
        1024 * 1024 * 50, -1L, 1024 * 1024);

    aServletHolder.getRegistration().setMultipartConfig(tmpMultipartConfig);
    return aServletHolder;
  }

  /**
   * Creates a Wetator engine and configures it.
   */
  @Before
  public void createWetatorEngine() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost:" + DEFAULT_PORT + "/");
    if (getBrowser() != null) {
      tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BROWSER_TYPE, getBrowser().getSymbol());
    }
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_ACCEPT_LANGUAGE, "de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");

    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_XSL_TEMPLATES, "./xsl/run_report.xsl");
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_COMMAND_SETS, IncubatorCommandSet.class.getName() + ", "
        + SqlCommandSet.class.getName() + ", " + TestCommandSet.class.getName());
    tmpProperties.setProperty("wetator.db.connections", "wetdb, secondDb");

    setIfNotNull(tmpProperties, "wetator.proxyHost", System.getProperty("http.proxyHost"));
    setIfNotNull(tmpProperties, "wetator.proxyPort", System.getProperty("http.proxyPort"));
    setIfNotNull(tmpProperties, "wetator.proxyUser", System.getProperty("http.proxyUser"));
    setIfNotNull(tmpProperties, "wetator.proxyPassword", System.getProperty("http.proxyPassword"));
    setIfNotNull(tmpProperties, "wetator.proxyHostsToBypass", System.getProperty("http.nonProxyHosts"));

    tmpProperties.setProperty("wetator.basicAuthUser", "wetator");
    tmpProperties.setProperty("wetator.basicAuthPassword", "secret");

    tmpProperties.setProperty("wetator.db.wetdb.driver", "org.hsqldb.jdbcDriver");
    tmpProperties.setProperty("wetator.db.wetdb.url", "jdbc:hsqldb:mem:wetdb");
    tmpProperties.setProperty("wetator.db.wetdb.user", "sa");
    tmpProperties.setProperty("wetator.db.wetdb.password", "");

    tmpProperties.setProperty("wetator.db.secondDb.driver", "org.hsqldb.jdbcDriver");
    tmpProperties.setProperty("wetator.db.secondDb.url", "jdbc:hsqldb:mem:second_db");
    tmpProperties.setProperty("wetator.db.secondDb.user", "sa");
    tmpProperties.setProperty("wetator.db.secondDb.password", "");

    tmpProperties.setProperty("$app_user", "dobby");
    tmpProperties.setProperty("$$app_password", "secret");

    tmpProperties.setProperty("$wet", "Wetator");
    tmpProperties.setProperty("$$wet-secret", "Wetator");

    final WetatorConfiguration tmpConfiguration = new WetatorConfiguration(new File("."), tmpProperties,
        new Properties(), null);

    listener = new JUnitProgressListener();

    wetatorEngine = new WetatorEngine();
    wetatorEngine.addProgressListener(listener);
    wetatorEngine.addProgressListener(new StdOutProgressListener());
    wetatorEngine.init(tmpConfiguration);
  }

  private static void setIfNotNull(final Properties aProperties, final String aKey, final String aValue) {
    if (aValue != null) {
      aProperties.setProperty(aKey, aValue);
    }
  }

  /**
   * Stops the web server.
   *
   * @throws Exception if an error occurs stopping the web server
   */
  @AfterClass
  public static void stopWebServer() throws Exception {
    if (server != null) {
      server.stop();
    }
    server = null;
  }

  protected void executeTestFile(final File aTestFile) throws InvalidInputException {
    executeTestFile(aTestFile.getName(), aTestFile);
  }

  protected void executeTestFile(final String aTestName, final File aTestFile) throws InvalidInputException {
    wetatorEngine.addTestCase(aTestName, aTestFile);
    wetatorEngine.executeTests();
  }

  /**
   * @return the number of errors
   * @see org.wetator.test.JUnitProgressListener#getErrors()
   */
  public int getErrors() {
    return listener.getErrors();
  }

  /**
   * @return the number of failures
   * @see org.wetator.test.JUnitProgressListener#getFailures()
   */
  public int getFailures() {
    return listener.getFailures();
  }

  /**
   * @return the number of steps
   * @see org.wetator.test.JUnitProgressListener#getSteps()
   */
  public int getSteps() {
    return listener.getSteps();
  }
}
