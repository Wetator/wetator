/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.core;

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

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.backend.WetBackend.Browser;
import org.rbri.wet.backend.control.Control;
import org.rbri.wet.commandset.DefaultCommandSet;
import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.core.variable.Variable;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.scripter.Scripter;
import org.rbri.wet.scripter.WetScripter;
import org.rbri.wet.util.FileUtil;
import org.rbri.wet.util.SecretString;
import org.rbri.wet.util.StringUtil;

/**
 * The configuration file for Wetator.
 * 
 * @author rbri
 * @author frank.danek
 */
// TODO we have to split this into one common part and one part for configuring the backend
public final class WetConfiguration {
  private static final Log LOG = LogFactory.getLog(WetConfiguration.class);

  /**
   * The prefix for all wetator properties.
   */
  public static final String PROPERTY_PREFIX = "wetator.";

  // wetator
  /**
   * The property name to set the supported {@link WetCommandSet}s.
   */
  public static final String PROPERTY_COMMAND_SETS = PROPERTY_PREFIX + "commandSets";
  /**
   * The property name to set the supported {@link Control}s.
   */
  public static final String PROPERTY_CONTROLS = PROPERTY_PREFIX + "controls";
  /**
   * The property name to set the supported {@link WetScripter}s.
   */
  public static final String PROPERTY_SCRIPTERS = PROPERTY_PREFIX + "scripters";
  /**
   * The property name to set the base URL.
   */
  public static final String PROPERTY_BASE_URL = PROPERTY_PREFIX + "baseUrl";

  // output
  /**
   * The property name to set the output directory.
   */
  public static final String PROPERTY_OUTPUT_DIR = PROPERTY_PREFIX + "outputDir";
  private static final String DEFAULT_OUTPUT_DIR = "./logs";
  /**
   * The property name to set whether a distinct output directory should be used.
   */
  public static final String PROPERTY_DISTINCT_OUTPUT = PROPERTY_PREFIX + "distinctOutput";;
  private static final String DEFAULT_DISTINCT_OUTPUT = "false";
  /**
   * The property name to set the XSL templates used to transform the output.
   */
  public static final String PROPERTY_XSL_TEMPLATES = PROPERTY_PREFIX + "xslTemplates";

  // browser
  /**
   * The property name to set the supported {@link Browser}s (by their {@link Browser#getSymbol()}).
   */
  public static final String PROPERTY_BROWSER = PROPERTY_PREFIX + "browser";
  private static final Browser DEFAULT_BROWSER = Browser.FIREFOX_3_6;
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

  private List<WetScripter> scripters;
  private List<WetCommandSet> commandSets;
  private List<Class<? extends Control>> controls;
  private String baseUrl;

  private File outputDir;
  private List<String> xslTemplates;

  private List<Browser> browsers;
  private String acceptLanaguage;
  private SecretString basicAuthUser;
  private SecretString basicAuthPassword;

  private String proxyHost;
  private int proxyPort;
  private Set<String> proxyHostsToBypass;
  private SecretString proxyUser;
  private SecretString proxyPassword;

  private List<Variable> variables; // store them in defined order

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
   */
  public WetConfiguration(File aConfigurationPropertyFile, Map<String, String> anExternalPropertiesMap) {
    super();

    LOG.info("Config  Configuration file is '" + aConfigurationPropertyFile.getAbsolutePath() + "'");
    // lets do some validations first
    if (!aConfigurationPropertyFile.exists()) {
      throw new WetException("The configuration file '" + aConfigurationPropertyFile.getAbsolutePath()
          + "' does not exist.");
    }

    if (!aConfigurationPropertyFile.canRead()) {
      throw new WetException("The configuration file '" + aConfigurationPropertyFile.getAbsolutePath()
          + "' is not readable.");
    }

    Properties tmpProperties;
    File tmpBaseDirectory;
    // ok, we can start to read the file
    try {
      FileInputStream tmpFileInputStream;

      tmpFileInputStream = new FileInputStream(aConfigurationPropertyFile);
      tmpProperties = new Properties();
      tmpProperties.load(tmpFileInputStream);

      tmpBaseDirectory = aConfigurationPropertyFile.getParentFile();
      if (null == tmpBaseDirectory) {
        tmpBaseDirectory = new File(System.getProperty("user.dir"));
      }
    } catch (IOException e) {
      throw new WetException("An error occured during read of the configuration file '"
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
  public WetConfiguration(File aBaseDirectory, Properties aConfigurationProperties,
      Map<String, String> anExternalPropertiesMap) {
    super();

    initialize(aBaseDirectory, aConfigurationProperties, anExternalPropertiesMap);
  }

  @SuppressWarnings("unchecked")
  private void initialize(File aBaseDirectory, Properties aConfigurationProperties,
      Map<String, String> anExternalPropertiesMap) {
    // lets do some validations first
    if (!aBaseDirectory.exists()) {
      throw new WetException("Config  The base directory '" + aBaseDirectory.getAbsolutePath() + "' does not exist.");
    }

    LOG.info("Config  Base directory is '" + aBaseDirectory.getAbsolutePath() + "'");

    String tmpValue;

    // we start with the given configuration properties
    Properties tmpProperties = aConfigurationProperties;

    // overwrite with system properties if defined....
    Set<Object> tmpSystemPropertyNames = System.getProperties().keySet();
    for (Object tmpKey : tmpSystemPropertyNames) {
      String tmpKeyName = (String) tmpKey;
      if (tmpKeyName.startsWith(PROPERTY_PREFIX) || tmpKeyName.startsWith(VARIABLE_PREFIX)) {
        Object tmpPropertyValue = System.getProperty(tmpKeyName);
        if (null != tmpPropertyValue) {
          tmpProperties.put(tmpKeyName, tmpPropertyValue);
        }
      }
    }

    // overwrite with external properties if defined....
    if (null != anExternalPropertiesMap) {
      Set<String> tmpExternalPropertiesNames = anExternalPropertiesMap.keySet();
      for (String tmpKey : tmpExternalPropertiesNames) {
        String tmpKeyName = tmpKey;
        if (tmpKeyName.startsWith(PROPERTY_PREFIX) || tmpKeyName.startsWith(VARIABLE_PREFIX)) {
          Object tmpPropertyValue = anExternalPropertiesMap.get(tmpKeyName);
          if (null != tmpPropertyValue) {
            tmpProperties.put(tmpKeyName, tmpPropertyValue);
          }
        }
      }
    }

    // scripters
    scripters = new LinkedList<WetScripter>();

    WetScripter tmpScripter;
    tmpScripter = Scripter.XML.getWetScripter();
    scripters.add(tmpScripter);
    LOG.info("Config  scripter '" + tmpScripter.getClass().getName() + "' registered.");
    tmpScripter = Scripter.XLS.getWetScripter();
    scripters.add(tmpScripter);
    LOG.info("Config  scripter '" + tmpScripter.getClass().getName() + "' registered.");

    tmpValue = tmpProperties.getProperty(PROPERTY_SCRIPTERS, "");
    List<String> tmpScripterClassNames = StringUtil.extractStrings(tmpValue, ",", '\\');

    for (String tmpScripterClassName : tmpScripterClassNames) {
      tmpScripterClassName = tmpScripterClassName.trim();
      if (!StringUtils.isEmpty(tmpScripterClassName)) {
        try {
          Class<? extends WetScripter> tmpClass;
          try {
            tmpClass = ClassUtils.getClass(tmpScripterClassName);
          } catch (ClassNotFoundException e) {
            // make Ant happy
            tmpClass = ClassUtils.getClass(getClass().getClassLoader(), tmpScripterClassName);
          }
          WetScripter tmpWetScripter = tmpClass.newInstance();
          scripters.add(tmpWetScripter);
          LOG.info("Config  scripter '" + tmpScripterClassName + "' registered.");
        } catch (ClassNotFoundException e) {
          LOG.error("Config  Can't load scripter '" + tmpScripterClassName + "'.", e);
        } catch (InstantiationException e) {
          LOG.error("Config  Can't load scripter '" + tmpScripterClassName + "'.", e);
        } catch (IllegalAccessException e) {
          LOG.error("Config  Can't load scripter '" + tmpScripterClassName + "'.", e);
        } catch (ClassCastException e) {
          LOG.error("Config  Can't load scripter '" + tmpScripterClassName + "'.", e);
        }
      }
    }
    for (WetScripter tmpWebScripter : scripters) {
      tmpWebScripter.initialize(tmpProperties);
    }

    // command sets
    commandSets = new LinkedList<WetCommandSet>();

    WetCommandSet tmpCommandSet;
    tmpCommandSet = new DefaultCommandSet();
    commandSets.add(tmpCommandSet);
    LOG.info("Config  command set '" + tmpCommandSet.getClass().getName() + "' registered.");

    tmpValue = tmpProperties.getProperty(PROPERTY_COMMAND_SETS, "");
    List<String> tmpCommandSetClassNames = StringUtil.extractStrings(tmpValue, ",", '\\');

    for (String tmpCommandSetClassName : tmpCommandSetClassNames) {
      tmpCommandSetClassName = tmpCommandSetClassName.trim();
      if (!StringUtils.isEmpty(tmpCommandSetClassName)) {
        try {
          Class<? extends WetCommandSet> tmpClass;
          try {
            tmpClass = ClassUtils.getClass(tmpCommandSetClassName);
          } catch (ClassNotFoundException e) {
            // make Ant happy
            tmpClass = ClassUtils.getClass(getClass().getClassLoader(), tmpCommandSetClassName);
          }
          WetCommandSet tmpWetCommandSet = tmpClass.newInstance();
          commandSets.add(tmpWetCommandSet);
          LOG.info("Config  command set '" + tmpCommandSetClassName + "' registered.");
        } catch (ClassNotFoundException e) {
          LOG.error("Config  Can't load command set '" + tmpCommandSetClassName + "'.", e);
        } catch (InstantiationException e) {
          LOG.error("Config  Can't load command set '" + tmpCommandSetClassName + "'.", e);
        } catch (IllegalAccessException e) {
          LOG.error("Config  Can't load command set '" + tmpCommandSetClassName + "'.", e);
        } catch (ClassCastException e) {
          LOG.error("Config  Can't load command set '" + tmpCommandSetClassName + "'.", e);
        }
      }
    }
    for (WetCommandSet tmpWetCommandSet : commandSets) {
      tmpWetCommandSet.initialize(tmpProperties);
    }

    // controls
    controls = new LinkedList<Class<? extends Control>>();

    tmpValue = tmpProperties.getProperty(PROPERTY_CONTROLS, "");
    List<String> tmpControlClassNames = StringUtil.extractStrings(tmpValue, ",", '\\');

    for (String tmpControlClassName : tmpControlClassNames) {
      tmpControlClassName = tmpControlClassName.trim();
      if (!StringUtils.isEmpty(tmpControlClassName)) {
        try {
          Class<? extends Control> tmpClass;
          try {
            tmpClass = ClassUtils.getClass(tmpControlClassName);
          } catch (ClassNotFoundException e) {
            // make Ant happy
            tmpClass = ClassUtils.getClass(getClass().getClassLoader(), tmpControlClassName);
          }
          controls.add(tmpClass);
          LOG.info("Config  control '" + tmpControlClassName + "' registered.");
        } catch (ClassNotFoundException e) {
          LOG.error("Config  Can't load control '" + tmpControlClassName + "'.", e);
        }
      }
    }

    // outputDir
    tmpValue = tmpProperties.getProperty(PROPERTY_OUTPUT_DIR, DEFAULT_OUTPUT_DIR);
    // output dir is relative to the base directory
    outputDir = new File(aBaseDirectory, tmpValue);
    FileUtil.createOutputDir(outputDir);

    tmpValue = tmpProperties.getProperty(PROPERTY_DISTINCT_OUTPUT, DEFAULT_DISTINCT_OUTPUT);
    boolean tmpDistinctOutput = Boolean.parseBoolean(tmpValue);
    LOG.info("Config  DistinctOutput is '" + tmpDistinctOutput + "'");
    if (tmpDistinctOutput) {
      SimpleDateFormat tmpFormater = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
      outputDir = new File(outputDir, tmpFormater.format(new Date()));
    }
    LOG.info("Config  OutputDir is '" + outputDir.getAbsolutePath() + "'");

    // baseUrl
    tmpValue = tmpProperties.getProperty(PROPERTY_BASE_URL, "");
    tmpProperties.remove(PROPERTY_BASE_URL);
    if (StringUtils.isEmpty(tmpValue)) {
      throw new WetException("The required property '" + PROPERTY_BASE_URL + "' is not set.");
    }
    baseUrl = tmpValue;

    // browserVersion
    tmpValue = tmpProperties.getProperty(PROPERTY_BROWSER, "");
    tmpProperties.remove(PROPERTY_BROWSER);

    browsers = new ArrayList<WetBackend.Browser>();

    List<String> tmpParts = StringUtil.extractStrings(tmpValue, ",", '\\');
    for (String tmpString : tmpParts) {
      if (StringUtils.isNotBlank(tmpString)) {
        WetBackend.Browser tmpBrowser = Browser.getForSymbol(tmpString);
        if (null == tmpBrowser) {
          LOG.warn("Unsupported browser '" + tmpString + "'.");
        } else {
          browsers.add(tmpBrowser);
        }
      }
    }
    // if nothing configured fall back to default
    if (browsers.isEmpty()) {
      browsers.add(DEFAULT_BROWSER);
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
      } catch (NumberFormatException e) {
        throw new WetException("The property '" + PROPERTY_PROXY_PORT + "' is no integer.");
      }

      proxyHostsToBypass = new HashSet<String>();
      tmpValue = tmpProperties.getProperty(PROPERTY_PROXY_HOSTS_TO_BYPASS, "");
      tmpProperties.remove(PROPERTY_PROXY_HOSTS_TO_BYPASS);
      if (StringUtils.isEmpty(tmpValue)) {
        // parsing
        String[] tmpNonProxyHostArray = tmpValue.split("\\|");
        for (String tmpString : tmpNonProxyHostArray) {
          tmpString = tmpString.replaceAll("\\.", "\\\\.");
          tmpString = tmpString.replaceAll("^\\*", ".*");
          proxyHostsToBypass.add(tmpString);
        }

      }

      tmpValue = tmpProperties.getProperty(PROPERTY_PROXY_USER, "");
      tmpProperties.remove(PROPERTY_PROXY_USER);
      if (StringUtils.isNotEmpty(tmpValue)) {
        proxyUser = new SecretString(tmpValue, false);

        tmpValue = tmpProperties.getProperty(PROPERTY_PROXY_PASSWORD, "");
        tmpProperties.remove(PROPERTY_PROXY_PASSWORD);
        proxyPassword = new SecretString(tmpValue, true);
      }
    }

    // basic auth
    tmpValue = tmpProperties.getProperty(PROPERTY_BASIC_AUTH_USER, "");
    tmpProperties.remove(PROPERTY_BASIC_AUTH_USER);
    if (StringUtils.isNotEmpty(tmpValue)) {
      basicAuthUser = new SecretString(tmpValue, false);

      // read the rest only if needed
      tmpValue = tmpProperties.getProperty(PROPERTY_BASIC_AUTH_PASSWORD, "");
      tmpProperties.remove(PROPERTY_BASIC_AUTH_PASSWORD);
      basicAuthPassword = new SecretString(tmpValue, true);
    }

    // TODO NTLM

    // xslTemplates
    tmpValue = tmpProperties.getProperty(PROPERTY_XSL_TEMPLATES, "");
    tmpProperties.remove(PROPERTY_XSL_TEMPLATES);

    xslTemplates = new LinkedList<String>();

    tmpParts = StringUtil.extractStrings(tmpValue, ",", '\\');
    for (String tmpString : tmpParts) {
      if (StringUtils.isNotBlank(tmpString)) {
        // template file is relative to the base directory
        File tmpTemplateFile = new File(aBaseDirectory, tmpString);
        if (tmpTemplateFile.exists()) {
          xslTemplates.add(tmpTemplateFile.getAbsolutePath());
        } else {
          throw new WetException("Configured XSL template '" + tmpTemplateFile.getAbsolutePath() + "' not found.");
        }
      }
    }

    // all properties starting with $ are variables
    variables = new LinkedList<Variable>();
    Set<Entry<Object, Object>> tmpOtherEntries = tmpProperties.entrySet();
    for (Entry<Object, Object> tmpEntry : tmpOtherEntries) {
      String tmpKey = (String) tmpEntry.getKey();
      String tmpVariableValue = (String) tmpEntry.getValue();
      if (tmpKey.startsWith(VARIABLE_PREFIX)) {
        // ok it is a variable
        tmpKey = tmpKey.substring(1);
        if (tmpKey.startsWith(SECRET_PREFIX)) {
          variables.add(new Variable(tmpKey.substring(1), tmpVariableValue, true));
        } else {
          variables.add(new Variable(tmpKey, tmpVariableValue, false));
        }
      }
    }

    LOG.debug("Config  Reading of the configuration finished'");
  }

  /**
   * @return a list containing the configured {@link WetCommandSet}s
   */
  public List<WetCommandSet> getCommandSets() {
    return commandSets;
  }

  /**
   * @return the controls
   */
  public List<Class<? extends Control>> getControls() {
    return controls;
  }

  /**
   * @return a list containing the configured {@link WetScripter}s
   */
  public List<WetScripter> getScripters() {
    return scripters;
  }

  /**
   * @return the configured case URL
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * @return the configured output directory
   */
  public File getOutputDir() {
    return outputDir;
  }

  /**
   * @return a list containing the configured {@link Browser}s
   */
  public List<WetBackend.Browser> getBrowsers() {
    return browsers;
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
}