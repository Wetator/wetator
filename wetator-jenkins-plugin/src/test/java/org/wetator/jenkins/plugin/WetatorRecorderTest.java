/*
 * Copyright (c) 2008-2016 wetator.org
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


package org.wetator.jenkins.plugin;

import org.wetator.core.TestCase;
import org.wetator.jenkins.WetatorRecorder;
import org.wetator.jenkins.test.ResultXMLBuilder;

import hudson.model.Result;

/**
 * Tests for the {@link WetatorRecorder}.
 *
 * @author frank.danek
 */
public class WetatorRecorderTest extends AbstractPluginTest {

  public void testStable() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild("0", null);

    assertBuild(Result.SUCCESS);
  }

  public void testUnstable() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommandWithFailure();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild("0", null);

    assertBuild(Result.UNSTABLE);
  }

  public void testBelowUnstableThreshold() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommandWithFailure();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild("1", null);

    assertBuild(Result.SUCCESS);
  }

  public void testAboveUnstableThreshold() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommandWithFailure();
    builder.endTestRun();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.IE11);
    builder.writeCommandWithFailure();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild("1", null);

    assertBuild(Result.UNSTABLE);
  }

  public void testBelowFailueThreshold() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommandWithFailure();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild("0", "1");

    assertBuild(Result.UNSTABLE);
  }

  public void testAboveFailureThreshold() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommandWithFailure();
    builder.endTestRun();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.IE11);
    builder.writeCommandWithFailure();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild("0", "1");

    assertBuild(Result.FAILURE);
  }
}
