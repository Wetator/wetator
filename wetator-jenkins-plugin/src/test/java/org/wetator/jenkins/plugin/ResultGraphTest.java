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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.wetator.core.TestCase;
import org.wetator.exception.InvalidInputException;
import org.wetator.jenkins.test.ResultXMLBuilder;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlArea;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Tests the result graph for different Wetator results.
 *
 * @author frank.danek
 */
public class ResultGraphTest extends AbstractPluginTest {

  @Test
  public void green() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeComment();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    rerunBuild();

    assertGraph(new int[] { 0, 0, 1 }, new int[] { 0, 0, 1 });
  }

  @Test
  public void blue() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithFailure();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    rerunBuild();

    assertGraph(new int[] { 1, 0, 1 }, new int[] { 1, 0, 1 });
  }

  @Test
  public void red() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithError();
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    rerunBuild();

    assertGraph(new int[] { 1, 0, 1 }, new int[] { 1, 0, 1 });
  }

  @Test
  public void exception() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.IE11);
    builder.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    builder.endTestRun();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    rerunBuild();

    assertGraph(new int[] { 1, 1, 2 }, new int[] { 1, 1, 2 });
  }

  @Test
  public void exceptionMultipleTestCases() throws Exception {
    builder.startEngine();

    builder.startTestCase();
    builder.progressListener.testRunStart(ResultXMLBuilder.IE11);
    builder.error(new NoClassDefFoundError("test error"));
    builder.progressListener.testRunEnd();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);
    builder.endTestCase();

    builder.startTestCase();
    builder.writeTestRunIgnored(ResultXMLBuilder.IE11);

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);
    builder.endTestCase();

    builder.endEngine();

    runBuild();

    rerunBuild();

    assertGraph(new int[] { 1, 3, 4 }, new int[] { 1, 3, 4 });
  }

  protected void assertGraph(int[]... anExpectedResultCounts) throws IOException, SAXException {
    HtmlPage tmpProjectPage = webClient.getPage(build.getProject());
    WebAssert.assertTextPresent(tmpProjectPage, "Wetator Test Result Trend");
    DomElement tmpMap = tmpProjectPage.getElementById("map");
    DomNodeList<HtmlElement> tmpAreas = tmpMap.getElementsByTagName("area");

    int tmpNumberOfBuilds = anExpectedResultCounts.length;
    assertEquals(tmpNumberOfBuilds * 3, tmpAreas.size());
    int j = tmpNumberOfBuilds;
    for (int i = 0; i < tmpNumberOfBuilds; i++) {
      j--;
      int[] tmpBuildCounts = anExpectedResultCounts[i];
      assertAreas(i + 1, tmpBuildCounts[0], tmpBuildCounts[1], tmpBuildCounts[2], tmpAreas.get(j),
          tmpAreas.get(j + tmpNumberOfBuilds), tmpAreas.get(j + 2 * tmpNumberOfBuilds));
    }
  }

  private void assertAreas(int aBuildNo, int anExpectedFailCount, int anExpectedSkipCount, int anExpectedTotalCount,
      HtmlElement anActualPassArea, HtmlElement anActualSkipArea, HtmlElement anActualFailArea) {
    HtmlArea tmpPassArea = (HtmlArea) anActualPassArea;
    HtmlArea tmpSkipArea = (HtmlArea) anActualSkipArea;
    HtmlArea tmpFailArea = (HtmlArea) anActualFailArea;

    int tmpPassCount = anExpectedTotalCount - anExpectedFailCount - anExpectedSkipCount;

    int tmpPassHeight = 0;
    int tmpFailHeight = 0;
    int tmpSkipHeight = 0;

    int tmpDeltaPerTest = 172 / anExpectedTotalCount;

    tmpPassHeight = tmpDeltaPerTest * tmpPassCount;
    tmpFailHeight = tmpDeltaPerTest * anExpectedFailCount;
    tmpSkipHeight = tmpDeltaPerTest * anExpectedSkipCount;

    assertArea(aBuildNo, anExpectedTotalCount, "passed", tmpPassHeight, tmpPassArea);
    assertArea(aBuildNo, anExpectedSkipCount, "skipped", tmpSkipHeight, tmpSkipArea);
    assertArea(aBuildNo, anExpectedFailCount, "failed", tmpFailHeight, tmpFailArea);
  }

  private void assertArea(int aBuildNo, int anExpectedResultCount, String anExpectedResult, int anExpectedHeight,
      HtmlArea anActualArea) {
    String[] tmpCoords = anActualArea.getCoordsAttribute().split(",");
    int tmpY1 = Integer.parseInt(tmpCoords[1]);
    int tmpY2 = Integer.parseInt(tmpCoords[3]);
    int tmpY3 = Integer.parseInt(tmpCoords[5]);
    int tmpY4 = Integer.parseInt(tmpCoords[7]);
    int tmpY5 = Integer.parseInt(tmpCoords[9]);
    int[] tmpY = new int[] { tmpY1, tmpY2, tmpY3, tmpY4, tmpY5 };
    Arrays.sort(tmpY);
    int tmpMinY = tmpY[0];
    int tmpMaxY = tmpY[4];
    int tmpDeltaY = tmpMaxY - tmpMinY;

    try {
      // on linux the results are 1px different :(
      assertEquals(anExpectedResult, anExpectedHeight + 1, tmpDeltaY);
    } catch (AssertionError e) {
      assertEquals(anExpectedResult, anExpectedHeight, tmpDeltaY);
    }

    String tmpTitle = anActualArea.getAttribute("title");

    String tmpStart = "#" + aBuildNo + ": " + anExpectedResultCount + " ";
    assertTrue(anExpectedResult + " expected start: [" + tmpStart + "] but was [" + tmpTitle + "]",
        tmpTitle.startsWith(tmpStart));

    String tmpEnd;
    if ("passed".equals(anExpectedResult)) {
      tmpEnd = "test" + (anExpectedResultCount != 1 ? "s" : "");
    } else if ("skipped".equals(anExpectedResult)) {
      tmpEnd = "test" + (anExpectedResultCount != 1 ? "s" : "") + " skipped";
    } else {
      tmpEnd = "failure" + (anExpectedResultCount != 1 ? "s" : "");
    }
    assertTrue(anExpectedResult + " expected end: [" + tmpEnd + "] but was [" + tmpTitle + "]",
        tmpTitle.endsWith(tmpEnd));
  }
}
