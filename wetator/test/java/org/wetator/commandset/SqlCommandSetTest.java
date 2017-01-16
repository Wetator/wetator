/*
 * Copyright (c) 2008-2017 wetator.org
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

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wetator.exception.ConfigurationException;
import org.wetator.test.AbstractWebServerTest;
import org.wetator.test.junit.BrowserRunner;

/**
 * @author rbri
 */
@RunWith(BrowserRunner.class)
public class SqlCommandSetTest extends AbstractWebServerTest {

  @Test
  public void initialize() {
    final SqlCommandSet tmpSql = new SqlCommandSet();
    // this should work without any exception
    tmpSql.initialize(new Properties());
  }

  @Test
  public void initializeNoDriver() {
    final Properties tmpProperties = new Properties();
    tmpProperties.put("wetator.db.connections", "wetdb");

    final SqlCommandSet tmpSql = new SqlCommandSet();
    try {
      tmpSql.initialize(tmpProperties);
      Assert.fail("ConfigurationException expected");
    } catch (final ConfigurationException e) {
      Assert.assertEquals("No database driver class specified for connection named 'wetdb'.", e.getMessage());
    }
  }

  @Test
  public void initializeUnknownDriver() {
    final Properties tmpProperties = new Properties();
    tmpProperties.put("wetator.db.connections", "wetdb");
    tmpProperties.put("wetator.db.wetdb.driver", "org.db.jdbcDriver");

    final SqlCommandSet tmpSql = new SqlCommandSet();
    try {
      tmpSql.initialize(tmpProperties);
      Assert.fail("ConfigurationException expected");
    } catch (final ConfigurationException e) {
      Assert.assertEquals(
          "Error during load of database driver class 'org.db.jdbcDriver' for"
              + " connection named 'wetdb' (reason: java.lang.ClassNotFoundException: org.db.jdbcDriver).",
          e.getMessage());
    }
  }

  @Test
  public void initializeConnectFailed() {
    final Properties tmpProperties = new Properties();
    tmpProperties.put("wetator.db.connections", "wetdb");
    tmpProperties.put("wetator.db.wetdb.driver", "org.hsqldb.jdbcDriver");
    tmpProperties.put("wetator.db.wetdb.url", "jdbc:hsqldb:mem:wetdb");
    tmpProperties.put("wetator.db.wetdb.user", "unknown");

    final SqlCommandSet tmpSql = new SqlCommandSet();
    try {
      tmpSql.initialize(tmpProperties);
      Assert.fail("ConfigurationException expected");
    } catch (final ConfigurationException e) {
      Assert.assertEquals("Connection to database 'jdbc:hsqldb:mem:wetdb' for connection named 'wetdb' failed"
          + " (reason: java.sql.SQLException: User not found: UNKNOWN).", e.getMessage());
    }
  }
}
