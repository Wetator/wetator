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
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.commandset.DefaultCommandSet;
import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.core.variable.Variable;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.scripter.ExcelScripter;
import org.rbri.wet.scripter.WetScripter;
import org.rbri.wet.util.FileUtil;
import org.rbri.wet.util.SecretString;
import org.rbri.wet.util.StringUtil;


/**
 * The configuration file for Wetator.
 *
 * TODO we have to split this
 * into one common part and one
 * part for configuring the backend
 *
 * @author rbri
 */
public final class WetConfiguration {
    private static final Log LOG = LogFactory.getLog(WetConfiguration.class);

    public static final String PROPERTY_PREFIX = "wetator.";
    public static final String PROPERTY_COMMAND_SETS = PROPERTY_PREFIX + "commandSets";
    public static final String PROPERTY_SCRIPTERS = PROPERTY_PREFIX + "scripters";
    public static final String PROPERTY_BASE_URL = PROPERTY_PREFIX + "baseUrl";

    private static final String PROPERTY_BROWSER = PROPERTY_PREFIX + "browser";
    public static final String PROPERTY_ACCEPT_LANGUAGE = PROPERTY_PREFIX + "acceptLanguage";

    public static final String PROPERTY_OUTPUT_DIR = PROPERTY_PREFIX + "outputDir";
    private static final String DEFAULT_OUTPUT_DIR = "./logs";
    public static final String PROPERTY_DISTINCT_OUTPUT =  PROPERTY_PREFIX + "distinctOutput";;
    private static final String DEFAULT_DISTINCT_OUTPUT = "false";

    // proxy
    public static final String PROPERTY_PROXY_HOST = PROPERTY_PREFIX + "proxyHost";
    public static final String PROPERTY_PROXY_PORT = PROPERTY_PREFIX + "proxyPort";
    private static final String PROPERTY_PROXY_HOSTS_TO_BYPASS = PROPERTY_PREFIX + "proxyHostsToBypass";
    public static final String PROPERTY_PROXY_USER = PROPERTY_PREFIX + "proxyUser";
    private static final String PROPERTY_PROXY_PASSWORD = PROPERTY_PREFIX + "proxyPassword";
    public static final String PROPERTY_BASIC_AUTH_USER = PROPERTY_PREFIX + "basicAuthUser";
    private static final String PROPERTY_BASIC_AUTH_PASSWORD = PROPERTY_PREFIX + "basicAuthPassword";

    // output
    private static final String PROPERTY_XSL_TEMPLATES = PROPERTY_PREFIX + "xslTemplates";

    public static final String VARIABLE_PREFIX = "$";
    public static final String SECRET_PREFIX = "$";

    private List<WetScripter> scripters;
    private List<WetCommandSet> commandSets;

    private String baseUrl;
    private File outputDir;

    private WetBackend.Browser browser;
    private String acceptLanaguage;

    private String proxyHost;
    private int proxyPort;
    private Set<String> proxyHostsToBypass;
    private SecretString proxyUser;
    private SecretString proxyPassword;
    private SecretString basicAuthUser;
    private SecretString basicAuthPassword;

    private List<String> xslTemplates;

    private List<Variable> variables; // store them in defined order


    public WetConfiguration(File aConfigurationFile, Map<String, String> anExternalPropertiesMap) throws WetException {
        super();

        LOG.info("Config  file is '" + aConfigurationFile.getAbsolutePath() + "'");
        initializeFromFile(aConfigurationFile, anExternalPropertiesMap);
        LOG.debug("Config  reading of the config file finished'");
    }


    private void initializeFromFile(File aConfigurationFile, Map<String, String> anExternalPropertiesMap) throws WetException {
        // lets do some validations first
        if (!aConfigurationFile.exists()) {
            throw new WetException("The configuration file '" + aConfigurationFile.getAbsolutePath() + "' does not exist.");
        }

        if (!aConfigurationFile.canRead()) {
            throw new WetException("The configuration file '" + aConfigurationFile.getAbsolutePath() + "' is not readable.");
        }

        // ok, we can start to read the file
        try {
            FileInputStream tmpFileInputStream;
            Properties tmpProperties;
            String tmpValue;

            tmpFileInputStream = new FileInputStream(aConfigurationFile);
            tmpProperties = new Properties();
            tmpProperties.load(tmpFileInputStream);

            // overwrite with system tmpProperties if defined....
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

            // command sets
            scripters = new LinkedList<WetScripter>();

            WetScripter tmpScripter;
            tmpScripter = new ExcelScripter();
            scripters.add(tmpScripter);
            LOG.info("Config  scripter '" + tmpScripter.getClass().getName() + "' registered.");

            tmpValue = tmpProperties.getProperty(PROPERTY_SCRIPTERS, "");
            List<String> tmpScripterClassNames = StringUtil.extractStrings(tmpValue, ",", '\\');

            for (String tmpScripterClassName : tmpScripterClassNames) {
                tmpScripterClassName = tmpScripterClassName.trim();
                if (!StringUtils.isEmpty(tmpScripterClassName)) {
                    try {
                        Class< ? extends WetScripter > tmpClass;
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
                        Class< ? extends WetCommandSet > tmpClass;
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

            // outputDir
            tmpValue = tmpProperties.getProperty(PROPERTY_OUTPUT_DIR, DEFAULT_OUTPUT_DIR);
            outputDir = FileUtil.createOutputDir(tmpValue);

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
            browser = parseBrowser(tmpValue);

            // accept language
            tmpValue = tmpProperties.getProperty(PROPERTY_ACCEPT_LANGUAGE, "en-us,en;q=0.8,de-de;q=0.5,de;q=0.3");
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
            // TODO parse to list
            // TODO validate
            xslTemplates = new LinkedList<String>();
            xslTemplates.add(tmpValue);

            // all tmpProperties starting with $ are variables
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
        } catch (IOException e) {
            throw new WetException("An error occured during read of the configuration file '" + aConfigurationFile.getAbsolutePath() + "'.", e);
        }
    }


    private WetBackend.Browser parseBrowser(String aBrowser) {
        if (null == aBrowser) {
            return WetBackend.Browser.FIREFOX_2;
        }

        if ("IE_6".equalsIgnoreCase(aBrowser)) {
            return WetBackend.Browser.INTERNET_EXPLORER_6;
        }

        if ("IE_7".equalsIgnoreCase(aBrowser)) {
            return WetBackend.Browser.INTERNET_EXPLORER_7;
        }

        if ("IE_8".equalsIgnoreCase(aBrowser)) {
            return WetBackend.Browser.INTERNET_EXPLORER_8;
        }

        if ("Firefox_2".equalsIgnoreCase(aBrowser)) {
            return WetBackend.Browser.FIREFOX_2;
        }

        if ("Firefox_3".equalsIgnoreCase(aBrowser)) {
            return WetBackend.Browser.FIREFOX_3;
        }

        return WetBackend.Browser.FIREFOX_3;
    }


    public List<WetCommandSet> getCommandSets() {
        return commandSets;
    }

    public List<WetScripter> getScripters() {
        return scripters;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public WetBackend.Browser getBrowser() {
        return browser;
    }

    public String getAcceptLanaguage() {
        return acceptLanaguage;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public Set<String> getProxyHostsToBypass() {
        return proxyHostsToBypass;
    }

    public SecretString getProxyUser() {
        return proxyUser;
    }

    public SecretString getProxyPassword() {
        return proxyPassword;
    }

    public SecretString getBasicAuthUser() {
        return basicAuthUser;
    }

    public SecretString getBasicAuthPassword() {
        return basicAuthPassword;
    }

    public List<String> getXslTemplates() {
        return xslTemplates;
    }

    public List<Variable> getVariables() {
        return variables;
    }
}