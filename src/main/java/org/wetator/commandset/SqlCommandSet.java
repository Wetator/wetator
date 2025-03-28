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


package org.wetator.commandset;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.backend.IBrowser;
import org.wetator.core.Command;
import org.wetator.core.ICommandImplementation;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.searchpattern.ContentPattern;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.CommandException;
import org.wetator.exception.ConfigurationException;
import org.wetator.exception.InvalidInputException;
import org.wetator.i18n.Messages;
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
  private static final String DB_NAME_PREFIX = "@";

  private static final Logger LOG = LogManager.getLogger(SqlCommandSet.class);

  private static final String PROPERTY_PREFIX = WetatorConfiguration.PROPERTY_PREFIX + "db.";
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

    connections = new HashMap<>();
  }

  @Override
  protected void registerCommands() {
    registerCommand("exec-sql", new CommandExecSql());
    registerCommand("assert-sql", new CommandAssertSql());
    registerCommand("assert-sql-in-content", new CommandAssertSqlInContent());
  }

  /**
   * Command 'exec-sql'.
   */
  public final class CommandExecSql implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final SecretString tmpSqlParam = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      tmpSqlParam.trim();
      final String tmpConnectionName = extractConnectionName(aContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      final Connection tmpConnection = connections.get(tmpConnectionName); // NOPMD

      try (Statement tmpStatement = tmpConnection.createStatement()) {
        tmpStatement.execute(tmpSql);
      } catch (final SQLException e) {
        final String tmpMessage = Messages.getMessage("sqlFailes", tmpSqlParam.toString(), e.getMessage());
        throw new ActionException(tmpMessage);
      }
    }
  }

  /**
   * Command 'assert-sql'.
   */
  public final class CommandAssertSql implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final SecretString tmpSqlParam = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      tmpSqlParam.trim();
      final String tmpConnectionName = extractConnectionName(aContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      final Connection tmpConnection = connections.get(tmpConnectionName); // NOPMD

      final StringBuilder tmpResult = new StringBuilder();
      try (Statement tmpStatement = tmpConnection.createStatement();
          ResultSet tmpResultSet = tmpStatement.executeQuery(tmpSql)) {
        final ResultSetMetaData tmpMetaData = tmpResultSet.getMetaData();

        while (tmpResultSet.next()) {
          for (int i = 1; i <= tmpMetaData.getColumnCount(); i++) {
            final String tmpValue = tmpResultSet.getString(i);
            if (tmpResultSet.wasNull()) {
              tmpResult.append("NULL");
            } else {
              tmpResult.append(tmpValue);
            }
            tmpResult.append(' ');
          }
        }
      } catch (final SQLException e) {
        final String tmpMessage = Messages.getMessage("sqlFailes", tmpSqlParam.toString(), e.getMessage());
        throw new AssertionException(tmpMessage, e);
      }

      final SecretString tmpExpected = aCommand.getRequiredSecondParameterValue(aContext);
      final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

      final String tmpResultString = tmpResult.toString().trim();
      tmpPattern.matches(tmpResultString, 10000);
    }
  }

  /**
   * Command 'assert-sql-in-content'.
   */
  public final class CommandAssertSqlInContent implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final SecretString tmpSqlParam = aCommand.getRequiredFirstParameterValue(aContext);
      Long tmpTimeout = aCommand.getSecondParameterLongValue(aContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }
      aCommand.checkNoUnusedThirdParameter(aContext);

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      tmpSqlParam.trim();
      final String tmpConnectionName = extractConnectionName(aContext, tmpSqlParam);

      String tmpSql = tmpSqlParam.getValue();
      tmpSql = removeConnectionName(tmpSql, tmpConnectionName);

      final Connection tmpConnection = connections.get(tmpConnectionName); // NOPMD

      final StringBuilder tmpExpected = new StringBuilder();
      try (Statement tmpStatement = tmpConnection.createStatement();
          ResultSet tmpResultSet = tmpStatement.executeQuery(tmpSql)) {
        final ResultSetMetaData tmpMetaData = tmpResultSet.getMetaData();

        while (tmpResultSet.next()) {
          for (int i = 1; i <= tmpMetaData.getColumnCount(); i++) {
            final String tmpValue = tmpResultSet.getString(i);
            if (tmpResultSet.wasNull()) {
              aContext.informListenersWarn("ignoringNullValue", tmpMetaData.getColumnName(i));
            } else {
              if (tmpExpected.length() > 0) {
                tmpExpected.append(ContentPattern.DELIMITER).append(' ');
              }
              tmpExpected.append(tmpValue);
            }
          }
        }
      } catch (final SQLException e) {
        final String tmpMessage = Messages.getMessage("sqlFailes", tmpSqlParam.toString(), e.getMessage());
        throw new AssertionException(tmpMessage, e);
      }

      final IBrowser tmpBrowser = getBrowser(aContext);
      final ContentPattern tmpPattern = new ContentPattern(new SecretString(tmpExpected.toString()));
      final boolean tmpContentChanged = tmpBrowser.assertContentInTimeFrame(tmpPattern, tmpTimeout);
      if (tmpContentChanged) {
        tmpBrowser.saveCurrentWindowToLog();
      }
    }
  }

  @Override
  public void initialize(final Properties aConfiguration) {
    // any connections defined?
    final String tmpPropConnections = aConfiguration.getProperty(PROPERTY_CONNECTIONS);

    if (StringUtils.isEmpty(tmpPropConnections)) {
      addInitializationMessage("No database connections defined (property '" + PROPERTY_CONNECTIONS + "' not set).");
      LOG.warn("No database connections defined (property '" + PROPERTY_CONNECTIONS + "' not set).");
      return;
    }

    final List<String> tmpConnectionNames = StringUtil.extractStrings(tmpPropConnections, ",", '\\');
    for (String tmpConnectionName : tmpConnectionNames) {
      tmpConnectionName = tmpConnectionName.trim();
      if (StringUtils.isEmpty(tmpConnectionName)) {
        continue;
      }

      final String tmpDriver = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_DRIVER);

      if (StringUtils.isEmpty(tmpDriver)) {
        LOG.warn("No database driver class specified for connection named '" + tmpConnectionName + "'.");
        throw new ConfigurationException(
            "No database driver class specified for connection named '" + tmpConnectionName + "'.");
      }

      try {
        Class.forName(tmpDriver);
      } catch (final Exception e) {
        LOG.warn("Error during load of database driver class '" + tmpDriver + "' for connection named '"
            + tmpConnectionName + "'.", e);
        throw new ConfigurationException("Error during load of database driver class '" + tmpDriver
            + "' for connection named '" + tmpConnectionName + "' (reason: " + e.toString() + ").");
      }

      final String tmpUrl = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_URL);
      final String tmpUser = aConfiguration.getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_USER);
      final String tmpPassword = aConfiguration
          .getProperty(PROPERTY_PREFIX + tmpConnectionName + PROPERTY_PART_PASSWORD);
      try {
        final Connection tmpConnection = DriverManager.getConnection(tmpUrl, tmpUser, tmpPassword); // NOPMD
        // to be sure
        tmpConnection.setAutoCommit(true);

        // ok register the connection
        connections.put(tmpConnectionName, tmpConnection);
        if (null == defaultConnectionName) {
          defaultConnectionName = tmpConnectionName;
        }

        // leave some info
        if (tmpConnectionName.equals(defaultConnectionName)) {
          addInitializationMessage("DB " + tmpConnectionName + " (default): " + tmpUrl);
        } else {
          addInitializationMessage("DB " + tmpConnectionName + ": " + tmpUrl);
        }
      } catch (final Exception e) {
        LOG.warn("Connection to database '" + tmpUrl + "' for connection named '" + tmpConnectionName + "' failed.", e);
        throw new ConfigurationException("Connection to database '" + tmpUrl + "' for connection named '"
            + tmpConnectionName + "' failed (reason: " + e.toString() + ").");
      }
    }
  }

  /**
   * Extract the connection name from a string.
   *
   * @param aContext the context
   * @param aParameter the parameter
   * @return the connection name
   * @throws InvalidInputException if no connection is defined within the given parameter and no default connection
   *         defined
   */
  protected String extractConnectionName(final WetatorContext aContext, final SecretString aParameter)
      throws InvalidInputException {
    // check for '@' at start for handling connections
    if (aParameter.startsWith(DB_NAME_PREFIX)) {
      for (final Map.Entry<String, Connection> tmpEntry : connections.entrySet()) {
        final String tmpConnectionName = tmpEntry.getKey();
        if (aParameter.startsWith(tmpConnectionName, 1)) {
          return tmpConnectionName;
        }
      }
      aContext.informListenersWarn("undefinedConnectionName", aParameter.toString());
    }

    if (null == defaultConnectionName) {
      final String tmpMessage = Messages.getMessage("noDefaultConnection");
      throw new InvalidInputException(tmpMessage);
    }
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

  @Override
  public void cleanup() {
    for (final Map.Entry<String, Connection> tmpEntry : connections.entrySet()) {
      try {
        tmpEntry.getValue().close();
      } catch (final Exception e) {
        LOG.warn("Error during close of connection to db '" + tmpEntry.getKey() + "'.", e);
      }
    }
    defaultConnectionName = null;
    connections.clear();
  }
}
