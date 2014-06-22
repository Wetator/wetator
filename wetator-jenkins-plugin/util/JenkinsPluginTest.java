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


package org.wetator.forjenkinsplugin;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.exception.InvalidInputException;
import org.wetator.test.AbstractWebServerTest;

/**
 * @author frank.danek
 */
public class JenkinsPluginTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/forJenkinsPlugin/";

  @Test
  public void ok() throws InvalidInputException {
    executeTestFile("ok.wet");

    Assert.assertEquals(1, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void failure() throws InvalidInputException {
    executeTestFile("failure.wet");

    Assert.assertEquals(2, getSteps());
    Assert.assertEquals(1, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void error() throws InvalidInputException {
    executeTestFile("error.wet");

    Assert.assertEquals(1, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(1, getErrors());
  }

  @Test
  public void moduleNotFound() throws InvalidInputException {
    executeTestFile("moduleNotFound.wet");

    Assert.assertEquals(1, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(1, getErrors());
  }

  @Test
  public void moduleOk() throws InvalidInputException {
    executeTestFile("moduleOk.wet");

    Assert.assertEquals(2, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void moduleFailure() throws InvalidInputException {
    executeTestFile("moduleFailure.wet");

    Assert.assertEquals(3, getSteps());
    Assert.assertEquals(1, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void moduleError() throws InvalidInputException {
    executeTestFile("moduleError.wet");

    Assert.assertEquals(2, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(1, getErrors());
  }

  @Test
  public void pathOk() throws InvalidInputException {
    executeTestFile("forJenkinsPlugin\\pathOk.wet", "pathOk.wet");

    Assert.assertEquals(1, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void pathFailure() throws InvalidInputException {
    executeTestFile("forJenkinsPlugin\\pathFailure.wet", "pathFailure.wet");

    Assert.assertEquals(2, getSteps());
    Assert.assertEquals(1, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void pathError() throws InvalidInputException {
    executeTestFile("forJenkinsPlugin\\pathError.wet", "pathError.wet");

    Assert.assertEquals(1, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(1, getErrors());
  }

  private void executeTestFile(String aTestFileName) throws InvalidInputException {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }

  private void executeTestFile(String aTestName, String aTestFileName) throws InvalidInputException {
    executeTestFile(aTestName, new File(BASE_FOLDER + aTestFileName));
  }
}
