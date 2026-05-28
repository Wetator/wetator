/*
 * Copyright (c) 2008-2026 wetator.org
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


package org.wetator.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
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
import org.wetator.test.jetty.JettyServerUtils;
import org.wetator.test.jetty.RedirectServlet;
import org.wetator.test.jetty.SnoopyServlet;

import jakarta.servlet.Servlet;

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

    final Map<String, Class<? extends Servlet>> servlets = new HashMap<String, Class<? extends Servlet>>();
    servlets.put("/http_header.php", HttpHeaderServlet.class);
    servlets.put("/redirect_header.php", RedirectServlet.class);
    servlets.put("/redirect_js.php", RedirectServlet.class);
    servlets.put("/redirect_meta.php", RedirectServlet.class);
    servlets.put("/create_excel", ContentServlet.class);
    servlets.put("/snoopy.php", SnoopyServlet.class);
    servlets.put("/snoopyAuth.php", SnoopyServlet.class);

    Map<String, String> mimeTypes = Map.of("json", "application/json", "xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    server = JettyServerUtils.startWebServer(DEFAULT_PORT, DEFAULT_DOCUMENT_ROOT, servlets, null, mimeTypes, true);
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
