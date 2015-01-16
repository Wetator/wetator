/*
 * Copyright (c) 2008-2014 wetator.org
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


package org.wetator.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.backend.control.IControl;
import org.wetator.commandset.DefaultCommandSet;
import org.wetator.exception.ConfigurationException;
import org.wetator.scripter.ExcelScripter;
import org.wetator.scripter.LegacyXMLScripter;
import org.wetator.scripter.WikiTextScripter;
import org.wetator.scripter.XMLScripter;
import org.wetator.util.FileUtil;
import org.wetator.util.SecretString;
import org.wetator.util.StringUtil;

/**
 * The configuration for the Wetator and it's components.
 * 
 * @author rbri
 * @author frank.danek
 */
// TODO we have to split this into one common part and one part for configuring the backend
public class WetatorConfiguration {

  private static final Log LOG = LogFactory.getLog(WetatorConfiguration.class);

  /**
   * The prefix for all wetator properties.
   */
  public static final String PROPERTY_PREFIX = "wetator.";

  // wetator
  /**
   * The property name to set the supported {@link ICommandSet}s.
   */
  public static final String PROPERTY_COMMAND_SETS = PROPERTY_PREFIX + "commandSets";
  /**
   * The property name to set the WPath separator.
   */
  public static final String PROPERTY_WPATH_SEPARATOR = PROPERTY_PREFIX + "wpath.separator";
  private static final String DEFAULT_WPATH_SEPARATOR = ">";
  /**
   * The property name to set the supported {@link IControl}s.
   */
  public static final String PROPERTY_CONTROLS = PROPERTY_PREFIX + "controls";
  /**
   * The property name to set the supported {@link IScripter}s.
   */
  public static final String PROPERTY_SCRIPTERS = PROPERTY_PREFIX + "scripters";
  /**
   * The property name to set the base URL.
   */
  public static final String PROPERTY_BASE_URL = PROPERTY_PREFIX + "baseUrl";

  /**
   * The property name to set the javascript timeout.
   */
  public static final String PROPERTY_JAVASCRIPT_TIMEOUT = PROPERTY_PREFIX + "jsTimeout";
  /**
   * The property name to set the http timeout.
   */
  public static final String PROPERTY_HTTP_TIMEOUT = PROPERTY_PREFIX + "httpTimeout";

  // output
  /**
   * The property name to set the output directory.
   */
  public static final String PROPERTY_OUTPUT_DIR = PROPERTY_PREFIX + "outputDir";
  private static final String DEFAULT_OUTPUT_DIR = "./logs";
  /**
   * The property name to set whether a distinct output directory should be used.
   */
  public static final String PROPERTY_DISTINCT_OUTPUT = PROPERTY_PREFIX + "distinctOutput";
  private static final String DEFAULT_DISTINCT_OUTPUT = "false";
  /**
   * The property name to set the XSL templates used to transform the output.
   */
  public static final String PROPERTY_XSL_TEMPLATES = PROPERTY_PREFIX + "xslTemplates";

  /**
   * The property name to set the XSL templates used to transform the output.
   */
  public static final String PROPERTY_RETROSPECT = PROPERTY_PREFIX + "retrospect";

  // browser
  /**
   * The property name to set the supported {@link BrowserType}s (by their {@link BrowserType#getSymbol()}).
   */
  public static final String PROPERTY_BROWSER_TYPE = PROPERTY_PREFIX + "browser";
  private static final BrowserType DEFAULT_BROWSER_TYPE = BrowserType.FIREFOX_31;
  /**
   * The property name to set the 'Accept-Language' header of the browser.
   */
  public static final String PROPERTY_ACCEPT_LANGUAGE = PROPERTY_PREFIX + "acceptLanguage";
  private static final String DEFAULT_ACCEPT_LANGUAGE = "en-us,en;q=0.8,de-de;q=0.5,de;q=0.3";
  /**
   * The property name to set the user the browser uses for basic authentication.
   */
  public static final String PROPERTY_BASIC_AUTH_USER = PROPERTY_PREFIX + "basicAuthUser";
  private static final String PROPERTY_BASIC_AUTH_PASSWORD = PROPERTY_PREFIX + "basicAuthPassword";
  /**
   * The property name to set the user the browser uses for NTLM authentication.
   */
  public static final String PROPERTY_NTLM_USER = PROPERTY_PREFIX + "ntlmUser";
  private static final String PROPERTY_NTLM_PASSWORD = PROPERTY_PREFIX + "ntlmPassword";
  /**
   * The property name to set the workstation the browser uses for NTLM authentication.
   */
  public static final String PROPERTY_NTLM_WORKSTATION = PROPERTY_PREFIX + "ntlmWorkstation";
  /**
   * The property name to set the domain the browser uses for NTLM authentication.
   */
  public static final String PROPERTY_NTLM_DOMAIN = PROPERTY_PREFIX + "ntlmDomain";

  // proxy
  /**
   * The property name to set the proxy host the browser uses.
   */
  public static final String PROPERTY_PROXY_HOST = PROPERTY_PREFIX + "proxyHost";
  /**
   * The property name to set the proxy port the browser uses.
   */
  public static final String PROPERTY_PROXY_PORT = PROPERTY_PREFIX + "proxyPort";
  /**
   * The property name to set the hosts the the browser bypasses the proxy for.
   */
  public static final String PROPERTY_PROXY_HOSTS_TO_BYPASS = PROPERTY_PREFIX + "proxyHostsToBypass";
  /**
   * The property name to set the proxy user the browser uses.
   */
  public static final String PROPERTY_PROXY_USER = PROPERTY_PREFIX + "proxyUser";
  private static final String PROPERTY_PROXY_PASSWORD = PROPERTY_PREFIX + "proxyPassword";

  /**
   * The prefix identifying a property a variable.
   */
  public static final String VARIABLE_PREFIX = "$";
  /**
   * The prefix identifying a variable as secret. The property has to be prefixed by {@link #VARIABLE_PREFIX} and
   * {@link #SECRET_PREFIX}.
   */
  public static final String SECRET_PREFIX = "$";

  private List<IScripter> scripters;
  private List<ICommandSet> commandSets;
  private List<Class<? extends IControl>> controls;
  private String baseUrl;
  private int jsTimeoutInSeconds;
  private int httpTimeoutInSeconds;

  private String wpathSeparator;
  private File outputDir;
  private List<String> xslTemplates;

  private List<BrowserType> browserTypes;
  private String acceptLanaguage;

  private SecretString basicAuthUser;
  private SecretString basicAuthPassword;
  private SecretString ntlmUser;
  private SecretString ntlmPassword;
  private SecretString ntlmWorkstation;
  private SecretString ntlmDomain;

  private String proxyHost;
  private int proxyPort;
  private Set<String> proxyHostsToBypass;
  private SecretString proxyUser;
  private SecretString proxyPassword;

  private List<Variable> variables; // store them in defined order

  private boolean log;
  private int retrospect;

  /**
   * The constructor. It reads the the configuration properties from
   * <ol>
   * <li>the given property file</li>
   * <li>the system properties</li>
   * <li>the given external properties</li>
   * </ol>
   * If a property is set by multiple sources, the last source wins.
   * 
   * @param aConfigurationPropertyFile the configuration property file
   * @param anExternalPropertiesMap the external properties
   * @throws ConfigurationException in case of problems with the configuration
   */
  public WetatorConfiguration(final File aConfigurationPropertyFile, final Map<String, String> anExternalPropertiesMap) {
    LOG.info("Configuration: Configuration file is '" + aConfigurationPropertyFile.getAbsolutePath() + "'");
    // lets do some validations first
    if (!aConfigurationPropertyFile.exists()) {
      throw new ConfigurationException("The configuration file '" + aConfigurationPropertyFile.getAbsolutePath()
          + "' does not exist.");
    }
    if (!aConfigurationPropertyFile.canRead()) {
      throw new ConfigurationException("The configuration file '" + aConfigurationPropertyFile.getAbsolutePath()
          + "' is not readable.");
    }

    Properties tmpProperties;
    File tmpBaseDirectory;
    // ok, we can start to read the file
    try {
      final FileInputStream tmpFileInputStream = new FileInputStream(aConfigurationPropertyFile);
      try {
        tmpProperties = new Properties();
        tmpProperties.load(tmpFileInputStream);

        tmpBaseDirectory = aConfigurationPropertyFile.getParentFile();
        if (null == tmpBaseDirectory) {
          tmpBaseDirectory = new File(System.getProperty("user.dir"));
        }
      } finally {
        IOUtils.closeQuietly(tmpFileInputStream);
      }
    } catch (final IOException e) {
      throw new ConfigurationException("An error occured during read of the configuration file '"
          + aConfigurationPropertyFile.getAbsolutePath() + "'.", e);
    }

    initialize(tmpBaseDirectory, tmpProperties, anExternalPropertiesMap);
  }

  /**
   * The constructor. It reads the the configuration properties from
   * <ol>
   * <li>the given configuration properties</li>
   * <li>the system properties</li>
   * <li>the given external properties</li>
   * </ol>
   * If a property is set by multiple sources, the last source wins.
   * 
   * @param aBaseDirectory the base directory for all file I/O
   * @param aConfigurationProperties the configuration properties
   * @param anExternalPropertiesMap the external properties
   */
  public WetatorConfiguration(final File aBaseDirectory, final Properties aConfigurationProperties,
      final Map<String, String> anExternalPropertiesMap) {
    super();

    initialize(aBaseDirectory, aConfigurationProperties, anExternalPropertiesMap);
  }

  private void initialize(final File aBaseDirectory, final Properties aConfigurationProperties,
      final Map<String, String> anExternalPropertiesMap) {
    // lets do some validations first
    if (!aBaseDirectory.exists()) {
      throw new ConfigurationException("The base directory '" + aBaseDirectory.getAbsolutePath() + "' does not exist.");
    }
    if (!aBaseDirectory.isDirectory()) {
      throw new ConfigurationException("The base directory '" + aBaseDirectory.getAbsolutePath()
          + "' is not a directory.");
    }
    if (!aBaseDirectory.canRead()) {
      throw new ConfigurationException("The base directory '" + aBaseDirectory.getAbsolutePath() + "' is not readable.");
    }
    if (!aBaseDirectory.canWrite()) {
      throw new ConfigurationException("The base directory '" + aBaseDirectory.getAbsolutePath() + "' is not writable.");
    }
    LOG.info("Configuration: Base directory is '" + aBaseDirectory.getAbsolutePath() + "'");

    // we start with the given configuration properties
    final Properties tmpProperties = aConfigurationProperties;

    // overwrite with system properties if defined....
    final Set<Object> tmpSystemPropertyNames = System.getProperties().keySet();
    for (final Object tmpKey : tmpSystemPropertyNames) {
      final String tmpKeyName = (String) tmpKey;
      if (tmpKeyName.startsWith(PROPERTY_PREFIX) || tmpKeyName.startsWith(VARIABLE_PREFIX)) {
        final Object tmpPropertyValue = System.getProperty(tmpKeyName);
        if (null != tmpPropertyValue) {
          tmpProperties.put(tmpKeyName, tmpPropertyValue);
        }
      }
    }

    // overwrite with external properties if defined....
    if (null != anExternalPropertiesMap) {
      final Set<String> tmpExternalPropertiesNames = anExternalPropertiesMap.keySet();
      for (final String tmpKey : tmpExternalPropertiesNames) {
        final String tmpKeyName = tmpKey;
        if (tmpKeyName.startsWith(PROPERTY_PREFIX) || tmpKeyName.startsWith(VARIABLE_PREFIX)) {
          final Object tmpPropertyValue = anExternalPropertiesMap.get(tmpKeyName);
          if (null != tmpPropertyValue) {
            tmpProperties.put(tmpKeyName, tmpPropertyValue);
          }
        }
      }
    }

    // scripters
    scripters = new LinkedList<IScripter>();
    readScripters(tmpProperties);
    for (final IScripter tmpScripter : scripters) {
      tmpScripter.initialize(tmpProperties);
    }

    // command sets
    commandSets = new LinkedList<ICommandSet>();
    readCommandSets(tmpProperties);
    for (final ICommandSet tmpCommandSet : commandSets) {
      tmpCommandSet.initialize(tmpProperties);
    }

    // controls
    controls = new LinkedList<Class<? extends IControl>>();
    readControls(tmpProperties);

    String tmpValue;

    // wpath separator
    wpathSeparator = tmpProperties.getProperty(PROPERTY_WPATH_SEPARATOR, DEFAULT_WPATH_SEPARATOR);

    // outputDir
    tmpValue = tmpProperties.getProperty(PROPERTY_OUTPUT_DIR, DEFAULT_OUTPUT_DIR);
    outputDir = new File(tmpValue);
    if (!outputDir.isAbsolute()) {
      // output dir is relative to the base directory
      outputDir = new File(aBaseDirectory, tmpValue);
    }

    tmpValue = tmpProperties.getProperty(PROPERTY_DISTINCT_OUTPUT, DEFAULT_DISTINCT_OUTPUT);
    final boolean tmpDistinctOutput = Boolean.parseBoolean(tmpValue);
    LOG.info("Configuration: DistinctOutput is '" + tmpDistinctOutput + "'");
    if (tmpDistinctOutput) {
      final SimpleDateFormat tmpFormater = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
      outputDir = new File(outputDir, tmpFormater.format(new Date()));
    }

    try {
      FileUtil.createOutputDir(outputDir);
    } catch (final IOException e) {
      throw new ConfigurationException("Could not create output directory '" + outputDir.getAbsolutePath() + "'.", e);
    }
    LOG.info("Configuration: OutputDir is '" + outputDir.getAbsolutePath() + "'");

    // baseUrl
    tmpValue = tmpProperties.getProperty(PROPERTY_BASE_URL, "");
    tmpProperties.remove(PROPERTY_BASE_URL);
    if (StringUtils.isEmpty(tmpValue)) {
      throw new ConfigurationException("The required property '" + PROPERTY_BASE_URL + "' is not set.");
    }
    baseUrl = tmpValue;

    // jsTimeout
    tmpValue = tmpProperties.getProperty(PROPERTY_JAVASCRIPT_TIMEOUT, "1");
    try {
      jsTimeoutInSeconds = Integer.parseInt(tmpValue);
    } catch (final NumberFormatException e) {
      throw new ConfigurationException("The property '" + PROPERTY_JAVASCRIPT_TIMEOUT + "' is no integer.");
    }
    if (jsTimeoutInSeconds < 1) {
      throw new ConfigurationException("The property '" + PROPERTY_JAVASCRIPT_TIMEOUT + "' is less than 1.");
    }

    // httpTimeout
    tmpValue = tmpProperties.getProperty(PROPERTY_HTTP_TIMEOUT, "90");
    try {
      httpTimeoutInSeconds = Integer.parseInt(tmpValue);
    } catch (final NumberFormatException e) {
      throw new ConfigurationException("The property '" + PROPERTY_HTTP_TIMEOUT + "' is no integer.");
    }
    if (httpTimeoutInSeconds < 1) {
      throw new ConfigurationException("The property '" + PROPERTY_HTTP_TIMEOUT + "' is less than 1.");
    }

    // browserVersion
    tmpValue = tmpProperties.getProperty(PROPERTY_BROWSER_TYPE, "");
    tmpProperties.remove(PROPERTY_BROWSER_TYPE);

    browserTypes = new ArrayList<IBrowser.BrowserType>();

    List<String> tmpParts = StringUtil.extractStrings(tmpValue, ",", '\\');
    for (final String tmpString : tmpParts) {
      if (StringUtils.isNotBlank(tmpString)) {
        final IBrowser.BrowserType tmpBrowserType = BrowserType.getForSymbol(tmpString);
        if (null == tmpBrowserType) {
          LOG.warn("Unsupported browser '" + tmpString + "'.");
        } else {
          browserTypes.add(tmpBrowserType);
        }
      }
    }
    // if nothing configured fall back to default
    if (browserTypes.isEmpty()) {
      browserTypes.add(DEFAULT_BROWSER_TYPE);
    }

    // accept language
    tmpValue = tmpProperties.getProperty(PROPERTY_ACCEPT_LANGUAGE, DEFAULT_ACCEPT_LANGUAGE);
    tmpProperties.remove(PROPERTY_ACCEPT_LANGUAGE);
    acceptLanaguage = tmpValue;

    // proxy
    tmpValue = tmpProperties.getProperty(PROPERTY_PROXY_HOST, "");
    tmpProperties.remove(PROPERTY_PROXY_HOST);
    if (StringUtils.isNotEmpty(tmpValue)) {
      proxyHost = tmpValue;

      // read the rest only if needed
      tmpValue = tmpProperties.getProperty(PROPERTY_PROXY_PORT, "");
      tmpProperties.remove(PROPERTY_PROXY_PORT);
      try {
        proxyPort = Integer.parseInt(tmpValue);
      } catch (final NumberFormatException e) {
        throw new ConfigurationException("The property '" + PROPERTY_PROXY_PORT + "' is no integer.");
      }

      proxyHostsToBypass = new HashSet<String>();
      tmpValue = tmpProperties.getProperty(PROPERTY_PROXY_HOSTS_TO_BYPASS, "");
      tmpProperties.remove(PROPERTY_PROXY_HOSTS_TO_BYPASS);
      if (StringUtils.isNotBlank(tmpValue)) {
        // parsing
        final String[] tmpNonProxyHostArray = tmpValue.split("\\|");
        for (String tmpString : tmpNonProxyHostArray) {
          tmpString = tmpString.replaceAll("\\.", "\\\\.");
          tmpString = tmpString.replaceAll("^\\*", ".*");
          proxyHostsToBypass.add(tmpString);
        }
      }

      tmpValue = tmpProperties.getProperty(PROPERTY_PROXY_USER, "");
      tmpProperties.remove(PROPERTY_PROXY_USER);
      if (StringUtils.isNotEmpty(tmpValue)) {
        proxyUser = new SecretString(tmpValue);

        // read the rest only if needed
        tmpValue = tmpProperties.getProperty(PROPERTY_PROXY_PASSWORD, "");
        tmpProperties.remove(PROPERTY_PROXY_PASSWORD);
        proxyPassword = new SecretString().appendSecret(tmpValue);
      }
    }

    // basic auth
    tmpValue = tmpProperties.getProperty(PROPERTY_BASIC_AUTH_USER, "");
    tmpProperties.remove(PROPERTY_BASIC_AUTH_USER);
    if (StringUtils.isNotEmpty(tmpValue)) {
      basicAuthUser = new SecretString(tmpValue);

      // read the rest only if needed
      tmpValue = tmpProperties.getProperty(PROPERTY_BASIC_AUTH_PASSWORD, "");
      tmpProperties.remove(PROPERTY_BASIC_AUTH_PASSWORD);
      basicAuthPassword = new SecretString().appendSecret(tmpValue);
    }

    // NTLM
    tmpValue = tmpProperties.getProperty(PROPERTY_NTLM_USER, "");
    tmpProperties.remove(PROPERTY_NTLM_USER);
    if (StringUtils.isNotEmpty(tmpValue)) {
      ntlmUser = new SecretString(tmpValue);

      // read the rest only if needed
      tmpValue = tmpProperties.getProperty(PROPERTY_NTLM_PASSWORD, "");
      tmpProperties.remove(PROPERTY_NTLM_PASSWORD);
      ntlmPassword = new SecretString().appendSecret(tmpValue);

      tmpValue = tmpProperties.getProperty(PROPERTY_NTLM_WORKSTATION, "");
      tmpProperties.remove(PROPERTY_NTLM_WORKSTATION);
      ntlmWorkstation = new SecretString(tmpValue);

      tmpValue = tmpProperties.getProperty(PROPERTY_NTLM_DOMAIN, "");
      tmpProperties.remove(PROPERTY_NTLM_DOMAIN);
      ntlmDomain = new SecretString(tmpValue);
    }

    // xslTemplates
    tmpValue = tmpProperties.getProperty(PROPERTY_XSL_TEMPLATES, "");
    tmpProperties.remove(PROPERTY_XSL_TEMPLATES);

    xslTemplates = new LinkedList<String>();

    tmpParts = StringUtil.extractStrings(tmpValue, ",", '\\');
    for (final String tmpString : tmpParts) {
      if (StringUtils.isNotBlank(tmpString)) {
        File tmpTemplateFile = new File(tmpString);

        if (!tmpTemplateFile.isAbsolute()) {
          // template file is relative to the base directory
          tmpTemplateFile = new File(aBaseDirectory, tmpString);
        }
        if (!tmpTemplateFile.exists()) {
          throw new ConfigurationException("The configured XSL template '" + tmpTemplateFile.getAbsolutePath()
              + "' does not exist.");
        }
        if (!tmpTemplateFile.canRead()) {
          throw new ConfigurationException("The configured XSL template '" + tmpTemplateFile.getAbsolutePath()
              + "' is not readable.");
        }
        xslTemplates.add(tmpTemplateFile.getAbsolutePath());
      }
    }

    // read the rest only if needed
    tmpValue = tmpProperties.getProperty(PROPERTY_RETROSPECT, "-1");
    tmpProperties.remove(PROPERTY_RETROSPECT);
    try {
      retrospect = Integer.parseInt(tmpValue);
    } catch (final NumberFormatException e) {
      throw new ConfigurationException("The property '" + PROPERTY_RETROSPECT + "' is no integer.");
    }

    // all properties starting with $ are variables
    variables = new LinkedList<Variable>();
    final Set<Entry<Object, Object>> tmpOtherEntries = tmpProperties.entrySet();
    for (final Entry<Object, Object> tmpEntry : tmpOtherEntries) {
      String tmpKey = (String) tmpEntry.getKey();
      final String tmpVariableValue = (String) tmpEntry.getValue();
      if (tmpKey.startsWith(VARIABLE_PREFIX)) {
        // ok it is a variable
        tmpKey = tmpKey.substring(1);
        if (tmpKey.startsWith(SECRET_PREFIX)) {
          variables.add(new Variable(tmpKey.substring(1), tmpVariableValue, true));
        } else {
          variables.add(new Variable(tmpKey, tmpVariableValue));
        }
      }
    }

    LOG.debug("Configuration: Reading of the configuration finished");
  }

  private void readScripters(final Properties aProperties) {
    // add default scripters first
    IScripter tmpDefaultScripter;
    tmpDefaultScripter = new XMLScripter();
    scripters.add(tmpDefaultScripter);
    LOG.info("Configuration: Scripter '" + tmpDefaultScripter.getClass().getName() + "' registered.");
    tmpDefaultScripter = new LegacyXMLScripter();
    scripters.add(tmpDefaultScripter);
    LOG.info("Configuration: Scripter '" + tmpDefaultScripter.getClass().getName() + "' registered.");
    tmpDefaultScripter = new ExcelScripter();
    scripters.add(tmpDefaultScripter);
    LOG.info("Configuration: Scripter '" + tmpDefaultScripter.getClass().getName() + "' registered.");
    tmpDefaultScripter = new WikiTextScripter();
    scripters.add(tmpDefaultScripter);
    LOG.info("Configuration: Scripter '" + tmpDefaultScripter.getClass().getName() + "' registered.");

    final String tmpValue = aProperties.getProperty(PROPERTY_SCRIPTERS, "");
    final List<String> tmpScripterClassNames = StringUtil.extractStrings(tmpValue, ",", '\\');

    for (String tmpScripterClassName : tmpScripterClassNames) {
      tmpScripterClassName = tmpScripterClassName.trim();
      if (!StringUtils.isEmpty(tmpScripterClassName)) {
        Class<?> tmpClass = null;
        try {
          try {
            tmpClass = ClassUtils.getClass(tmpScripterClassName);
          } catch (final ClassNotFoundException e) {
            // make Ant happy
            tmpClass = ClassUtils.getClass(getClass().getClassLoader(), tmpScripterClassName);
          }
          @SuppressWarnings("unchecked")
          final Class<? extends IScripter> tmpScripterClass = (Class<? extends IScripter>) tmpClass;
          final IScripter tmpIScripter = tmpScripterClass.newInstance();
          scripters.add(tmpIScripter);
          LOG.info("Configuration: Scripter '" + tmpScripterClassName + "' registered.");
        } catch (final ClassNotFoundException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load scripter '" + tmpScripterClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load scripter '" + tmpScripterClassName + "' (" + e.toString() + ").");
          }
        } catch (final InstantiationException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load scripter '" + tmpScripterClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load scripter '" + tmpScripterClassName + "' (" + e.toString() + ").");
          }
        } catch (final IllegalAccessException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load scripter '" + tmpScripterClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load scripter '" + tmpScripterClassName + "' (" + e.toString() + ").");
          }
        } catch (final ClassCastException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load scripter '" + tmpScripterClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load scripter '" + tmpScripterClassName + "' (" + e.toString() + ").");
          }
          if (null != tmpClass) {
            ClassLoader tmpClassLoader = tmpClass.getClassLoader();
            LOG.error("         '" + tmpClass.getName() + "' loaded from "
                + tmpClassLoader.getResource(tmpClass.getName().replace('.', '/') + ".class").toString() + "' ("
                + tmpClassLoader.toString() + ").");

            tmpClass = ICommandSet.class;
            tmpClassLoader = tmpClass.getClassLoader();
            LOG.error("         '" + tmpClass.getName() + "' loaded from "
                + tmpClassLoader.getResource(tmpClass.getName().replace('.', '/') + ".class").toString() + "' ("
                + tmpClassLoader.toString() + ").");
          }
        }
      }
    }
  }

  private void readCommandSets(final Properties aProperties) {
    final String tmpValue = aProperties.getProperty(PROPERTY_COMMAND_SETS, "");
    final List<String> tmpCommandSetClassNames = StringUtil.extractStrings(tmpValue, ",", '\\');

    // add default command sets first
    tmpCommandSetClassNames.add(0, DefaultCommandSet.class.getName());

    for (String tmpCommandSetClassName : tmpCommandSetClassNames) {
      tmpCommandSetClassName = tmpCommandSetClassName.trim();
      if (!StringUtils.isEmpty(tmpCommandSetClassName)) {
        Class<?> tmpClass = null;
        try {
          try {
            tmpClass = ClassUtils.getClass(tmpCommandSetClassName);
          } catch (final ClassNotFoundException e) {
            // make Ant happy
            tmpClass = ClassUtils.getClass(getClass().getClassLoader(), tmpCommandSetClassName);
          }
          @SuppressWarnings("unchecked")
          final Class<? extends ICommandSet> tmpCommandSetClass = (Class<? extends ICommandSet>) tmpClass;
          final ICommandSet tmpCommandSet = tmpCommandSetClass.newInstance();
          commandSets.add(tmpCommandSet);
          LOG.info("Configuration: Command set '" + tmpCommandSetClassName + "' registered.");
        } catch (final ClassNotFoundException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load command set '" + tmpCommandSetClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load command set '" + tmpCommandSetClassName + "' (" + e.toString() + ").");
          }
        } catch (final InstantiationException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load command set '" + tmpCommandSetClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load command set '" + tmpCommandSetClassName + "' (" + e.toString() + ").");
          }
        } catch (final IllegalAccessException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load command set '" + tmpCommandSetClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load command set '" + tmpCommandSetClassName + "' (" + e.toString() + ").");
          }
        } catch (final ClassCastException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load command set '" + tmpCommandSetClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load command set '" + tmpCommandSetClassName + "' (" + e.toString() + ").");
          }
          if (null != tmpClass) {
            ClassLoader tmpClassLoader = tmpClass.getClassLoader();
            LOG.error("         '" + tmpClass.getName() + "' loaded from "
                + tmpClassLoader.getResource(tmpClass.getName().replace('.', '/') + ".class").toString() + "' ("
                + tmpClassLoader.toString() + ").");

            tmpClass = ICommandSet.class;
            tmpClassLoader = tmpClass.getClassLoader();
            LOG.error("         '" + tmpClass.getName() + "' loaded from "
                + tmpClassLoader.getResource(tmpClass.getName().replace('.', '/') + ".class").toString() + "' ("
                + tmpClassLoader.toString() + ").");
          }
        }
      }
    }
  }

  private void readControls(final Properties aProperties) {
    final String tmpValue = aProperties.getProperty(PROPERTY_CONTROLS, "");
    final List<String> tmpControlClassNames = StringUtil.extractStrings(tmpValue, ",", '\\');

    for (String tmpControlClassName : tmpControlClassNames) {
      tmpControlClassName = tmpControlClassName.trim();
      if (!StringUtils.isEmpty(tmpControlClassName)) {
        Class<?> tmpClass = null;
        try {
          try {
            tmpClass = ClassUtils.getClass(tmpControlClassName);
          } catch (final ClassNotFoundException e) {
            // make Ant happy
            tmpClass = ClassUtils.getClass(getClass().getClassLoader(), tmpControlClassName);
          }
          @SuppressWarnings("unchecked")
          final Class<? extends IControl> tmpControlClass = (Class<? extends IControl>) tmpClass;
          controls.add(tmpControlClass);
          LOG.info("Configuration: Control '" + tmpControlClassName + "' registered.");
        } catch (final ClassNotFoundException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load control '" + tmpControlClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load control '" + tmpControlClassName + "' (" + e.toString() + ").");
          }
        } catch (final ClassCastException e) {
          if (LOG.isDebugEnabled()) {
            LOG.error("Configuration: Can't load control '" + tmpControlClassName + "'.", e);
          } else {
            LOG.error("Configuration: Can't load control '" + tmpControlClassName + "' (" + e.toString() + ").");
          }
          if (null != tmpClass) {
            ClassLoader tmpClassLoader = tmpClass.getClassLoader();
            LOG.error("         '" + tmpClass.getName() + "' loaded from "
                + tmpClassLoader.getResource(tmpClass.getName().replace('.', '/') + ".class").toString() + "' ("
                + tmpClassLoader.toString() + ").");

            tmpClass = ICommandSet.class;
            tmpClassLoader = tmpClass.getClassLoader();
            LOG.error("         '" + tmpClass.getName() + "' loaded from "
                + tmpClassLoader.getResource(tmpClass.getName().replace('.', '/') + ".class").toString() + "' ("
                + tmpClassLoader.toString() + ").");
          }
        }
      }
    }
  }

  /**
   * @return a list containing the configured {@link ICommandSet}s
   */
  public List<ICommandSet> getCommandSets() {
    return commandSets;
  }

  /**
   * @return the controls
   */
  public List<Class<? extends IControl>> getControls() {
    return controls;
  }

  /**
   * @return a list containing the configured {@link IScripter}s
   */
  public List<IScripter> getScripters() {
    return scripters;
  }

  /**
   * @return the configured case URL
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * @return the configured javascript timeout
   */
  public int getJsTimeoutInSeconds() {
    return jsTimeoutInSeconds;
  }

  /**
   * @return the configured http timeout
   */
  public int getHttpTimeoutInSeconds() {
    return httpTimeoutInSeconds;
  }

  /**
   * @return the configured wpath separator
   */
  public String getWPathSeparator() {
    return wpathSeparator;
  }

  /**
   * @return the configured output directory
   */
  public File getOutputDir() {
    return outputDir;
  }

  /**
   * @return a list containing the configured {@link BrowserType}s
   */
  public List<IBrowser.BrowserType> getBrowserTypes() {
    return browserTypes;
  }

  /**
   * @return the configured accept language of the browser
   */
  public String getAcceptLanaguage() {
    return acceptLanaguage;
  }

  /**
   * @return the configured proxy host
   */
  public String getProxyHost() {
    return proxyHost;
  }

  /**
   * @return the configured proxy port
   */
  public int getProxyPort() {
    return proxyPort;
  }

  /**
   * @return a set of the configured hosts to be bypassed by the proxy
   */
  public Set<String> getProxyHostsToBypass() {
    return proxyHostsToBypass;
  }

  /**
   * @return the configured proxy user
   */
  public SecretString getProxyUser() {
    return proxyUser;
  }

  /**
   * @return the configured proxy password
   */
  public SecretString getProxyPassword() {
    return proxyPassword;
  }

  /**
   * @return the configured basic auth user
   */
  public SecretString getBasicAuthUser() {
    return basicAuthUser;
  }

  /**
   * @return the configured basic auth password
   */
  public SecretString getBasicAuthPassword() {
    return basicAuthPassword;
  }

  /**
   * @return the configured ntlm user
   */
  public SecretString getNtlmUser() {
    return ntlmUser;
  }

  /**
   * @return the configured ntlm password
   */
  public SecretString getNtlmPassword() {
    return ntlmPassword;
  }

  /**
   * @return the configured ntlm workstation
   */
  public SecretString getNtlmWorkstation() {
    return ntlmWorkstation;
  }

  /**
   * @return the configured ntlm domain
   */
  public SecretString getNtlmDomain() {
    return ntlmDomain;
  }

  /**
   * @return a list containing the configured XSL templates
   */
  public List<String> getXslTemplates() {
    return xslTemplates;
  }

  /**
   * @return a list containing the configured {@link Variable}s
   */
  public List<Variable> getVariables() {
    return variables;
  }

  /**
   * @return the configured proxy port
   */
  public int getRetrospect() {
    return retrospect;
  }

  /**
   * @return true if the (javascript) log is switched on
   */
  public boolean isLogEnabled() {
    return log;
  }

  /**
   * Switches the (javascript) log on.
   */
  public void enableLog() {
    log = true;
  }
}