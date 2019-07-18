/*
 * Copyright (c) 2008-2018 wetator.org
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


package org.wetator.core;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.IBrowser.BrowserType;

/**
 * Tests for {@link WetatorContext#addVariable(Variable)} and {@link WetatorContext#getVariables()}.
 *
 * @author frank.danek
 */
public class WetatorContextGetVariablesTest {

  private BrowserType browserType = BrowserType.FIREFOX_60;

  private File file1;
  private File file2;

  private String baseUrl;

  private WetatorConfiguration configuration;
  private WetatorEngine engine;

  @Before
  public void setupMocks() {
    file1 = new File("file1");
    file2 = new File("file2.xml");

    baseUrl = "http://baseurl";

    configuration = mock(WetatorConfiguration.class);
    when(configuration.getBaseUrl()).thenReturn(baseUrl);

    engine = mock(WetatorEngine.class);
    when(engine.getConfiguration()).thenReturn(configuration);
  }

  @Test
  public void implicit() throws Exception {
    // setup
    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);

    // run
    final List<Variable> tmpVariables = tmpContext.getVariables();

    // assert
    assertEquals("number of variables", 4, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
  }

  @Test
  public void own() throws Exception {
    // setup
    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    tmpContext.addVariable(new Variable("ctx", "ctx value"));

    // run
    final List<Variable> tmpVariables = tmpContext.getVariables();

    // assert
    assertEquals("number of variables", 5, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable("ctx", "ctx value", tmpVariables.get(4));
  }

  @Test
  public void fromConfiguration() throws Exception {
    // setup
    when(configuration.getVariables()).thenReturn(Arrays.asList(new Variable("conf", "conf value")));

    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);

    // run
    final List<Variable> tmpVariables = tmpContext.getVariables();

    // assert
    assertEquals("number of variables", 5, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable("conf", "conf value", tmpVariables.get(4));
  }

  @Test
  public void hierarchy() throws Exception {
    // setup
    when(configuration.getVariables()).thenReturn(Arrays.asList(new Variable("conf", "conf value")));

    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    tmpContext.addVariable(new Variable("ctx", "ctx value"));

    // run
    final List<Variable> tmpVariables = tmpContext.getVariables();

    // assert
    assertEquals("number of variables", 6, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable("ctx", "ctx value", tmpVariables.get(4));
    assertVariable("conf", "conf value", tmpVariables.get(5));
  }

  @Test
  public void shadowing() throws Exception {
    // setup
    when(configuration.getVariables())
        .thenReturn(Arrays.asList(new Variable(WetatorContext.VARIABLE_TESTFILE, "conf value")));

    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    tmpContext.addVariable(new Variable(WetatorContext.VARIABLE_TESTFILE, "ctx value"));

    // run
    final List<Variable> tmpVariables = tmpContext.getVariables();

    // assert
    assertEquals("number of variables", 6, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, "ctx value", tmpVariables.get(4));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, "conf value", tmpVariables.get(5));
  }

  @Test
  public void inSubContextImplicit() throws Exception {
    // setup
    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);

    // run
    final List<Variable> tmpVariables = tmpSubContext.getVariables();

    // assert
    assertEquals("number of variables", 8, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file2.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(4));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(5));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(6));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(7));
  }

  @Test
  public void inSubContextOwn() throws Exception {
    // setup
    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.addVariable(new Variable("subctx", "subctx value"));

    // run
    final List<Variable> tmpVariables = tmpSubContext.getVariables();

    // assert
    assertEquals("number of variables", 9, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file2.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable("subctx", "subctx value", tmpVariables.get(4));
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(5));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(6));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(7));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(8));
  }

  @Test
  public void inSubContextFromParent() throws Exception {
    // setup
    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    tmpContext.addVariable(new Variable("ctx", "ctx value"));
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);

    // run
    final List<Variable> tmpVariables = tmpSubContext.getVariables();

    // assert
    assertEquals("number of variables", 9, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file2.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(4));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(5));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(6));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(7));
    assertVariable("ctx", "ctx value", tmpVariables.get(8));
  }

  @Test
  public void inSubContextFromConfiguration() throws Exception {
    // setup
    when(configuration.getVariables()).thenReturn(Arrays.asList(new Variable("conf", "conf value")));

    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);

    // run
    final List<Variable> tmpVariables = tmpSubContext.getVariables();

    // assert
    assertEquals("number of variables", 9, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file2.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(4));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(5));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(6));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(7));
    assertVariable("conf", "conf value", tmpVariables.get(8));
  }

  @Test
  public void inSubContextHierarchy() throws Exception {
    // setup
    when(configuration.getVariables()).thenReturn(Arrays.asList(new Variable("conf", "conf value")));

    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    tmpContext.addVariable(new Variable("ctx", "ctx value"));
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.addVariable(new Variable("subctx", "subctx value"));

    // run
    final List<Variable> tmpVariables = tmpSubContext.getVariables();

    // assert
    assertEquals("number of variables", 11, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file2.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable("subctx", "subctx value", tmpVariables.get(4));
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(5));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(6));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(7));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(8));
    assertVariable("ctx", "ctx value", tmpVariables.get(9));
    assertVariable("conf", "conf value", tmpVariables.get(10));
  }

  @Test
  public void inSubContextShadowing() throws Exception {
    // setup
    when(configuration.getVariables())
        .thenReturn(Arrays.asList(new Variable(WetatorContext.VARIABLE_TESTFILE, "conf value")));

    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    tmpContext.addVariable(new Variable(WetatorContext.VARIABLE_TESTFILE, "ctx value"));
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.addVariable(new Variable(WetatorContext.VARIABLE_TESTFILE, "subctx value"));

    // run
    final List<Variable> tmpVariables = tmpSubContext.getVariables();

    // assert
    assertEquals("number of variables", 11, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file2.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, "subctx value", tmpVariables.get(4));
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(5));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(6));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(7));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(8));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, "ctx value", tmpVariables.get(9));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, "conf value", tmpVariables.get(10));
  }

  @Test
  public void afterSubContext() throws Exception {
    // setup
    final WetatorContext tmpContext = new WetatorContext(engine, file1.getName(), file1, browserType);
    tmpContext.addVariable(new Variable("ctx", "ctx value"));
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.addVariable(new Variable("subctx", "subctx value"));

    // run
    final List<Variable> tmpVariables = tmpContext.getVariables();

    // assert
    assertEquals("number of variables", 5, tmpVariables.size());
    assertVariable(WetatorContext.VARIABLE_TESTCASE, file1.getName(), tmpVariables.get(0));
    assertVariable(WetatorContext.VARIABLE_BROWSER, browserType.getLabel(), tmpVariables.get(1));
    assertVariable(WetatorContext.VARIABLE_TESTFILE, file1.getName(), tmpVariables.get(2));
    assertVariable(WetatorContext.VARIABLE_BASEURL, baseUrl, tmpVariables.get(3));
    assertVariable("ctx", "ctx value", tmpVariables.get(4));
  }

  private static void assertVariable(final String anExpectedName, final String anExpectedValue,
      final Variable anActualVariable) {
    assertEquals("variable name", anExpectedName, anActualVariable.getName());
    assertEquals("variable value", anExpectedValue, anActualVariable.getValue().getValue());
  }
}
