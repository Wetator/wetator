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


package org.rbri.wet.commandset;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;
import org.rbri.wet.util.StringUtil;

/**
 * The implementation of all sql commands.
 * 
 * @author rbri
 */
public final class SqlCommandSet extends AbstractCommandSet {
  /**
   * The prefix used to mark the db name
   */
  protected static final String DB_NAME_PREFIX = "@";

  private final Log log = LogFactory.getLog(SqlCommandSet.class);

  private static final String PROPERTY_PREFIX = WetConfiguration.PROPERTY_PREFIX + "db.";
  private static final String PROPERTY_CONNECTIONS = PROPERTY_PREFIX + "connections";
  private static final String PROPERTY_PART_DRIVER = ".driver";
  private static final String PROPERTY_PART_URL = ".url";
  private static final String PROPERTY_PART_USER = ".user";
  private static final String PROPERTY_PART_PASSWORD = ".password";

  private Map<String, Connection> connections;
  private String defaultConnectionName;

  /**
   * The set of supported sql commands.
   */
  public SqlCommandSet() {
    super();

    connections = new HashMap<String, Connection>();
  }

  @Override
  protected void registerCommands() {
    registerCommand("Exec SQL", new CommandExecSql());
    registerCommand("Assert SQL", new CommandAssertSql());
    registerCommand("Assert SQL in Content", new CommandAssertSqlInContent());
  }

  /**
   * Command 'Exec Sql'
   */
  public final class CommandExecSql implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      SecretString tmpSqlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      tmpSqlParam.trim();

      String tmpConnectionName = extractConnectionName(aWetContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      Connection tmpConnection = connections.get(tmpConnectionName);

      try {
        Statement tmpStatement = tmpConnection.createStatement();
        try {
          tmpStatement.execute(tmpSql);
        } finally {
          tmpStatement.close();
        }
      } catch (SQLException e) {
        Assert.fail("sqlFailes", new String[] { tmpSqlParam.toString(), e.getMessage() });
      }
    }
  }

  /**
   * Command 'Assert Sql'
   */
  public final class CommandAssertSql implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      SecretString tmpSqlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      List<SecretString> tmpExpected = aWetCommand.getRequiredSecondParameterValues(aWetContext);

      tmpSqlParam.trim();

      String tmpConnectionName = extractConnectionName(aWetContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      Connection tmpConnection = connections.get(tmpConnectionName);

      StringBuilder tmpResult = new StringBuilder();
      try {
        Statement tmpStatement = tmpConnection.createStatement();
        try {
          ResultSet tmpResultSet = tmpStatement.executeQuery(tmpSql);

          ResultSetMetaData tmpMetaData = tmpResultSet.getMetaData();

          while (tmpResultSet.next()) {
            for (int i = 1; i <= tmpMetaData.getColumnCount(); i++) {
              String tmpValue = tmpResultSet.getString(i);
              if (tmpResultSet.wasNull()) {
                tmpResult.append("NULL");
              } else {
                tmpResult.append(tmpValue);
              }
              tmpResult.append(" ");
            }
          }
          tmpResultSet.close();
        } finally {
          tmpStatement.close();
        }
      } catch (SQLException e) {
        Assert.fail("sqlFailes", new String[] { tmpSqlParam.toString(), e.getMessage() });
      }

      String tmpResultString = tmpResult.toString().trim();
      Assert.assertListMatch(tmpExpected, tmpResultString);
    }
  }

  /**
   * Command 'Assert Sql in Content'
   */
  public final class CommandAssertSqlInContent implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      SecretString tmpSqlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      long tmpTimeout = aWetCommand.getSecondParameterLongValue(aWetContext);

      tmpTimeout = Math.max(0, tmpTimeout);

      tmpSqlParam.trim();
      String tmpConnectionName = extractConnectionName(aWetContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      Connection tmpConnection = connections.get(tmpConnectionName);

      List<SecretString> tmpExpected = new LinkedList<SecretString>();
      try {
        Statement tmpStatement = tmpConnection.createStatement();
        try {
          ResultSet tmpResultSet = tmpStatement.executeQuery(tmpSql);

          ResultSetMetaData tmpMetaData = tmpResultSet.getMetaData();

          while (tmpResultSet.next()) {
            for (int i = 1; i <= tmpMetaData.getColumnCount(); i++) {
              String tmpValue = tmpResultSet.getString(i);
              if (tmpResultSet.wasNull()) {
                // TODO maybe report column and row
                aWetContext.informListenersWarn("ignoringNullValue", new String[] { tmpMetaData.getColumnName(i) });
              } else {
                SecretString tmpSecretString = new SecretString(tmpValue, false);
                tmpExpected.add(tmpSecretString);
              }
            }
          }
          tmpResultSet.close();
        } finally {
          tmpStatement.close();
        }
      } catch (SQLException e) {
        Assert.fail("sqlFailes", new String[] { tmpSqlParam.toString(), e.getMessage() });
      }

      WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.waitForContent(tmpExpected, tmpTimeout);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.WetCommandSet#initialize(java.util.Properties)
   */
  public void initialize(Properties aConfiguration) {
    // any connections defined?
    String tmpPropConnections = aConfiguration.getProperty(PROPERTY_CONNECTIONS);

    if (StringUtils.isEmpty(tmpPropConnections)) {
      return;
    }

    List<String> tmpConnectionNames = StringUtil.extractStrings(tmpPropConnections, ",", '\\');
    for (String tmpConnectionName : tmpConnectionNames) {
      tmpConnectionName = tmpConnectionName.trim();
      if (StringUtils.isEmpty(tmpConnectionName)) {
        continue;
      }
      if (null == defaultConnectionName) {
        defaultConnectionName = tmpConnectionName;
      }

      String tmpDriver = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_DRIVER);
      String tmpUrl = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_URL);
      String tmpUser = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_USER);
      String tmpPassword = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_PASSWORD);

      if (StringUtils.isEmpty(tmpDriver)) {
        log.warn("No database driver class specified for connection named '" + tmpConnectionName + "'.");
      } else {
        try {
          Class.forName(tmpDriver);
        } catch (Exception e) {
          log.warn("Error during load of database driver class '" + tmpDriver + "' for connection named '"
              + tmpConnectionName + "'.", e);
        }
      }

      try {
        Connection tmpConnection = DriverManager.getConnection(tmpUrl, tmpUser, tmpPassword);
        // to be sure
        tmpConnection.setAutoCommit(true);

        // ok register the connection
        connections.put(tmpConnectionName, tmpConnection);

        // leave some info
        if (tmpConnectionName == defaultConnectionName) {
          addInitializationMessage("DB " + tmpConnectionName + " (default): " + tmpUrl);
        } else {
          addInitializationMessage("DB " + tmpConnectionName + ": " + tmpUrl);
        }
      } catch (Exception e) {
        log.warn("Error connection to database '" + tmpUrl + "' for connection named '" + tmpConnectionName + "'.", e);
      }
    }
  }

  /**
   * extract the connection name from a string
   * 
   * @param aWetContext the wet context
   * @param aParameter the parameter
   * @return the connection name
   */
  protected String extractConnectionName(WetContext aWetContext, SecretString aParameter) {
    // check for '@' at start for handling connections
    if (aParameter.startsWith(DB_NAME_PREFIX)) {
      for (Map.Entry<String, Connection> tmpEntry : connections.entrySet()) {
        String tmpConnectionName = tmpEntry.getKey();
        if (aParameter.startsWith(tmpConnectionName, 1)) {
          return tmpConnectionName;
        }
      }
      aWetContext.informListenersWarn("undefinedConnectionName", new String[] { aParameter.toString() });
    }
    return defaultConnectionName;
  }

  /**
   * removes the connection name from an sql
   * 
   * @param aSql the sql
   * @param aConnectionName the connection name
   * @return the connection name
   */
  protected String removeConnectionName(String aSql, String aConnectionName) {
    String tmpConnectionName = DB_NAME_PREFIX + aConnectionName;
    if (aSql.startsWith(tmpConnectionName)) {
      String tmpResult = aSql.substring(tmpConnectionName.length(), aSql.length());
      tmpResult = tmpResult.trim();
      return tmpResult;
    }
    return aSql;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.WetCommandSet#cleanup()
   */
  public void cleanup() {
    for (Map.Entry<String, Connection> tmpEntry : connections.entrySet()) {
      try {
        tmpEntry.getValue().close();
      } catch (Exception e) {
        log.warn("Error during close of connection to db '" + tmpEntry.getKey() + "'.", e);
      }
    }
    defaultConnectionName = null;
    connections.clear();
  }
}
