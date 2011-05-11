/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.commandset;

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
import org.wetator.backend.WetBackend;
import org.wetator.core.WetCommand;
import org.wetator.core.WetConfiguration;
import org.wetator.core.WetContext;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;
import org.wetator.util.StringUtil;

/**
 * The implementation of all sql commands.
 * 
 * @author rbri
 * @author frank.danek
 */
public final class SqlCommandSet extends AbstractCommandSet {
  /**
   * The prefix used to mark the db name.
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
    registerCommand("exec-sql", new CommandExecSql());
    registerCommand("assert-sql", new CommandAssertSql());
    registerCommand("assert-sql-in-content", new CommandAssertSqlInContent());
  }

  /**
   * Command 'Exec Sql'.
   */
  public final class CommandExecSql implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final SecretString tmpSqlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      tmpSqlParam.trim();
      final String tmpConnectionName = extractConnectionName(aWetContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      final Connection tmpConnection = connections.get(tmpConnectionName);

      try {
        final Statement tmpStatement = tmpConnection.createStatement();
        try {
          tmpStatement.execute(tmpSql);
        } finally {
          tmpStatement.close();
        }
      } catch (final SQLException e) {
        Assert.fail("sqlFailes", new String[] { tmpSqlParam.toString(), e.getMessage() });
      }
    }
  }

  /**
   * Command 'Assert Sql'.
   */
  public final class CommandAssertSql implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final SecretString tmpSqlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      final List<SecretString> tmpExpected = aWetCommand.getRequiredSecondParameterValues(aWetContext);

      tmpSqlParam.trim();
      final String tmpConnectionName = extractConnectionName(aWetContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      final Connection tmpConnection = connections.get(tmpConnectionName);

      final StringBuilder tmpResult = new StringBuilder();
      try {
        final Statement tmpStatement = tmpConnection.createStatement();
        try {
          final ResultSet tmpResultSet = tmpStatement.executeQuery(tmpSql);

          final ResultSetMetaData tmpMetaData = tmpResultSet.getMetaData();

          while (tmpResultSet.next()) {
            for (int i = 1; i <= tmpMetaData.getColumnCount(); i++) {
              final String tmpValue = tmpResultSet.getString(i);
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
      } catch (final SQLException e) {
        Assert.fail("sqlFailes", new String[] { tmpSqlParam.toString(), e.getMessage() });
      }

      final String tmpResultString = tmpResult.toString().trim();
      Assert.assertListMatch(tmpExpected, tmpResultString);
    }
  }

  /**
   * Command 'Assert Sql in Content'.
   */
  public final class CommandAssertSqlInContent implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final SecretString tmpSqlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      Long tmpTimeout = aWetCommand.getSecondParameterLongValue(aWetContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      tmpSqlParam.trim();
      final String tmpConnectionName = extractConnectionName(aWetContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      final Connection tmpConnection = connections.get(tmpConnectionName);

      final List<SecretString> tmpExpected = new LinkedList<SecretString>();
      try {
        final Statement tmpStatement = tmpConnection.createStatement();
        try {
          final ResultSet tmpResultSet = tmpStatement.executeQuery(tmpSql);

          final ResultSetMetaData tmpMetaData = tmpResultSet.getMetaData();

          while (tmpResultSet.next()) {
            for (int i = 1; i <= tmpMetaData.getColumnCount(); i++) {
              final String tmpValue = tmpResultSet.getString(i);
              if (tmpResultSet.wasNull()) {
                // TODO maybe report column and row
                aWetContext.informListenersWarn("ignoringNullValue", new String[] { tmpMetaData.getColumnName(i) });
              } else {
                final SecretString tmpSecretString = new SecretString(tmpValue, false);
                tmpExpected.add(tmpSecretString);
              }
            }
          }
          tmpResultSet.close();
        } finally {
          tmpStatement.close();
        }
      } catch (final SQLException e) {
        Assert.fail("sqlFailes", new String[] { tmpSqlParam.toString(), e.getMessage() });
      }

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final boolean tmpContentChanged = tmpBackend.assertContentInTimeFrame(tmpExpected, tmpTimeout);
      if (tmpContentChanged) {
        tmpBackend.saveCurrentWindowToLog();
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.commandset.WetCommandSet#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // any connections defined?
    final String tmpPropConnections = aConfiguration.getProperty(PROPERTY_CONNECTIONS);

    if (StringUtils.isEmpty(tmpPropConnections)) {
      return;
    }

    final List<String> tmpConnectionNames = StringUtil.extractStrings(tmpPropConnections, ",", '\\');
    for (String tmpConnectionName : tmpConnectionNames) {
      tmpConnectionName = tmpConnectionName.trim();
      if (StringUtils.isEmpty(tmpConnectionName)) {
        continue;
      }

      final String tmpDriver = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_DRIVER);
      final String tmpUrl = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_URL);
      final String tmpUser = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_USER);
      final String tmpPassword = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName
          + PROPERTY_PART_PASSWORD);

      if (StringUtils.isEmpty(tmpDriver)) {
        addInitializationMessage("No database driver class specified for connection named '" + tmpConnectionName + "'.");
        log.warn("No database driver class specified for connection named '" + tmpConnectionName + "'.");
      } else {
        try {
          Class.forName(tmpDriver);
        } catch (final Exception e) {
          addInitializationMessage("Error during load of database driver class '" + tmpDriver
              + "' for connection named '" + tmpConnectionName + "' (reason: " + e.toString() + ").");
          log.warn("Error during load of database driver class '" + tmpDriver + "' for connection named '"
              + tmpConnectionName + "'.", e);
        }
      }

      try {
        final Connection tmpConnection = DriverManager.getConnection(tmpUrl, tmpUser, tmpPassword);
        // to be sure
        tmpConnection.setAutoCommit(true);

        // ok register the connection
        connections.put(tmpConnectionName, tmpConnection);
        if (null == defaultConnectionName) {
          defaultConnectionName = tmpConnectionName;
        }

        // leave some info
        if (tmpConnectionName == defaultConnectionName) {
          addInitializationMessage("DB " + tmpConnectionName + " (default): " + tmpUrl);
        } else {
          addInitializationMessage("DB " + tmpConnectionName + ": " + tmpUrl);
        }
      } catch (final Exception e) {
        addInitializationMessage("Error connection to database '" + tmpUrl + "' for connection named '"
            + tmpConnectionName + "' (reason: " + e.toString() + ").");
        log.warn("Error connection to database '" + tmpUrl + "' for connection named '" + tmpConnectionName + "'.", e);
      }
    }
  }

  /**
   * Extract the connection name from a string.
   * 
   * @param aWetContext the wet context
   * @param aParameter the parameter
   * @return the connection name
   * @throws AssertionFailedException if no default connection defined
   */
  protected String extractConnectionName(final WetContext aWetContext, final SecretString aParameter)
      throws AssertionFailedException {
    // check for '@' at start for handling connections
    if (aParameter.startsWith(DB_NAME_PREFIX)) {
      for (Map.Entry<String, Connection> tmpEntry : connections.entrySet()) {
        final String tmpConnectionName = tmpEntry.getKey();
        if (aParameter.startsWith(tmpConnectionName, 1)) {
          return tmpConnectionName;
        }
      }
      aWetContext.informListenersWarn("undefinedConnectionName", new String[] { aParameter.toString() });
    }

    Assert.assertNotNull(defaultConnectionName, "noDefaultConnection", null);
    return defaultConnectionName;
  }

  /**
   * Removes the connection name from an sql.
   * 
   * @param aSql the sql
   * @param aConnectionName the connection name
   * @return the connection name
   */
  protected String removeConnectionName(final String aSql, final String aConnectionName) {
    final String tmpConnectionName = DB_NAME_PREFIX + aConnectionName;
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
   * @see org.wetator.commandset.WetCommandSet#cleanup()
   */
  @Override
  public void cleanup() {
    for (Map.Entry<String, Connection> tmpEntry : connections.entrySet()) {
      try {
        tmpEntry.getValue().close();
      } catch (final Exception e) {
        log.warn("Error during close of connection to db '" + tmpEntry.getKey() + "'.", e);
      }
    }
    defaultConnectionName = null;
    connections.clear();
  }
}
