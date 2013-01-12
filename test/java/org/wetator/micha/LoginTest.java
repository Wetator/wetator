/*
 * Copyright (c) 2008-2013 wetator.org
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


package org.wetator.micha;

import java.io.File;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.wetator.exception.InvalidInputException;
import org.wetator.test.AbstractWebServerTest;

/**
 * @author frank.danek
 */
@Ignore
public class LoginTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/webpage/micha/";

  @Test
  public void assertContent() throws InvalidInputException {
    executeTestFile("login.wet");

    Assert.assertEquals(75, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void test() throws InvalidInputException {
    executeTestFile("test.wet");

    Assert.assertEquals(2, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void bug() throws InvalidInputException {
    executeTestFile("bug.wet");

    Assert.assertEquals(2, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(String aTestFileName) throws InvalidInputException {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
