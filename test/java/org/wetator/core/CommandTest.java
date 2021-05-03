/*
 * Copyright (c) 2008-2021 wetator.org
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class CommandTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private WetatorContext context;

  @Before
  public void setupMocks() {
    context = mock(WetatorContext.class);
    when(context.replaceVariables(anyString())).thenAnswer(anInvocation -> {
      return new SecretString((String) anInvocation.getArguments()[0]);
    });
  }

  @Test
  public void constructor() {
    final Command tmpCommand = new Command("command", false);

    assertEquals("command", tmpCommand.getName());
    assertFalse(tmpCommand.isComment());
    assertEquals(-1, tmpCommand.getLineNo());
  }

  @Test
  public void constructor_comment() {
    final Command tmpCommand = new Command("command", true);

    assertEquals("command", tmpCommand.getName());
    assertTrue(tmpCommand.isComment());
    assertEquals(-1, tmpCommand.getLineNo());
  }

  @Test
  public void getFirstParameterValue_null() throws Exception {
    final Command tmpCommand = new Command("command", false);

    assertEquals("", tmpCommand.getFirstParameterValue(context).toString());
  }

  @Test
  public void getFirstParameterValue_empty() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setFirstParameter(new Parameter(""));

    assertEquals("", tmpCommand.getFirstParameterValue(context).toString());
  }

  @Test
  public void getFirstParameterValue() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setFirstParameter(new Parameter("param"));

    assertEquals("param", tmpCommand.getFirstParameterValue(context).toString());
  }

  @Test
  public void getRequiredFirstParameterValue_null() throws Exception {
    thrown.expect(InvalidInputException.class);
    thrown.expectMessage("The command 'command' requires a first parameter.");

    final Command tmpCommand = new Command("command", false);

    tmpCommand.getRequiredFirstParameterValue(context);
  }

  @Test
  public void getRequiredFirstParameterValue_empty() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setFirstParameter(new Parameter(""));

    assertEquals("", tmpCommand.getRequiredFirstParameterValue(context).toString());
  }

  @Test
  public void getRequiredFirstParameterValue() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setFirstParameter(new Parameter("param"));

    assertEquals("param", tmpCommand.getRequiredFirstParameterValue(context).toString());
  }

  @Test
  public void getSecondParameterValue_null() throws Exception {
    final Command tmpCommand = new Command("command", false);

    assertEquals("", tmpCommand.getSecondParameterValue(context).toString());
  }

  @Test
  public void getSecondParameterValue_empty() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter(""));

    assertEquals("", tmpCommand.getSecondParameterValue(context).toString());
  }

  @Test
  public void getSecondParameterValue() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("param"));

    assertEquals("param", tmpCommand.getSecondParameterValue(context).toString());
  }

  @Test
  public void getRequiredSecondParameterValue_null() throws Exception {
    thrown.expect(InvalidInputException.class);
    thrown.expectMessage("The command 'command' requires a second parameter.");

    final Command tmpCommand = new Command("command", false);

    tmpCommand.getRequiredSecondParameterValue(context);
  }

  @Test
  public void getRequiredSecondParameterValue_empty() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter(""));

    assertEquals("", tmpCommand.getRequiredSecondParameterValue(context).toString());
  }

  @Test
  public void getRequiredSecondParameterValue() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("param"));

    assertEquals("param", tmpCommand.getRequiredSecondParameterValue(context).toString());
  }

  @Test
  public void getSecondParameterLongValue_null() throws Exception {
    final Command tmpCommand = new Command("command", false);

    assertEquals(null, tmpCommand.getSecondParameterLongValue(context));
  }

  @Test
  public void getSecondParameterLongValue_empty() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter(""));

    assertEquals(null, tmpCommand.getSecondParameterLongValue(context));
  }

  @Test
  public void getSecondParameterLongValue_noLong() throws Exception {
    thrown.expect(InvalidInputException.class);
    thrown.expectMessage("The command 'command' expects an integer parameter value 'param' as parameter 2.");

    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("param"));

    tmpCommand.getSecondParameterLongValue(context);
  }

  @Test
  public void getSecondParameterLongValue() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("10"));

    assertEquals(Long.valueOf(10), tmpCommand.getSecondParameterLongValue(context));
  }

  @Test
  public void getSecondParameterValues_null() throws Exception {
    final Command tmpCommand = new Command("command", false);

    assertEquals(0, tmpCommand.getSecondParameterValues(context).size());
  }

  @Test
  public void getSecondParameterValues_empty() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter(""));

    assertEquals(0, tmpCommand.getSecondParameterValues(context).size());
  }

  @Test
  public void getSecondParameterValues_single() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("param"));

    final List<SecretString> tmpValues = tmpCommand.getSecondParameterValues(context);
    assertEquals(1, tmpValues.size());
    assertEquals("param", tmpValues.get(0).toString());
  }

  @Test
  public void getSecondParameterValues_multiple() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("param,other"));

    final List<SecretString> tmpValues = tmpCommand.getSecondParameterValues(context);
    assertEquals(2, tmpValues.size());
    assertEquals("param", tmpValues.get(0).toString());
    assertEquals("other", tmpValues.get(1).toString());
  }

  @Test
  public void getRequiredSecondParameterValues_null() throws Exception {
    thrown.expect(InvalidInputException.class);
    thrown.expectMessage("The command 'command' requires a second parameter.");

    final Command tmpCommand = new Command("command", false);

    assertEquals(0, tmpCommand.getRequiredSecondParameterValues(context).size());
  }

  @Test
  public void getRequiredSecondParameterValues_empty() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter(""));

    assertEquals(0, tmpCommand.getRequiredSecondParameterValues(context).size());
  }

  @Test
  public void getRequiredSecondParameterValues_single() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("param"));

    final List<SecretString> tmpValues = tmpCommand.getRequiredSecondParameterValues(context);
    assertEquals(1, tmpValues.size());
    assertEquals("param", tmpValues.get(0).toString());
  }

  @Test
  public void getRequiredSecondParameterValues_multiple() throws Exception {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("param,other"));

    final List<SecretString> tmpValues = tmpCommand.getRequiredSecondParameterValues(context);
    assertEquals(2, tmpValues.size());
    assertEquals("param", tmpValues.get(0).toString());
    assertEquals("other", tmpValues.get(1).toString());
  }

  @Test
  public void checkNoUnusedSecondParameter_null() throws Exception {
    final Command tmpCommand = new Command("command", false);

    tmpCommand.checkNoUnusedSecondParameter(context);
  }

  @Test
  public void checkNoUnusedSecondParameter_empty() throws Exception {
    thrown.expect(InvalidInputException.class);
    thrown.expectMessage("The command 'command' does not use the value '' provided as parameter 2.");

    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter(""));

    tmpCommand.checkNoUnusedSecondParameter(context);
  }

  @Test
  public void checkNoUnusedSecondParameter() throws Exception {
    thrown.expect(InvalidInputException.class);
    thrown.expectMessage("The command 'command' does not use the value 'param' provided as parameter 2.");

    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("param"));

    tmpCommand.checkNoUnusedSecondParameter(context);
  }

  @Test
  public void checkNoUnusedThirdParameter_null() throws Exception {
    final Command tmpCommand = new Command("command", false);

    tmpCommand.checkNoUnusedThirdParameter(context);
  }

  @Test
  public void checkNoUnusedThirdParameter_empty() throws Exception {
    thrown.expect(InvalidInputException.class);
    thrown.expectMessage("The command 'command' does not use the value '' provided as parameter 3.");

    final Command tmpCommand = new Command("command", false);
    tmpCommand.setThirdParameter(new Parameter(""));

    tmpCommand.checkNoUnusedThirdParameter(context);
  }

  @Test
  public void checkNoUnusedThirdParameter() throws Exception {
    thrown.expect(InvalidInputException.class);
    thrown.expectMessage("The command 'command' does not use the value 'param' provided as parameter 3.");

    final Command tmpCommand = new Command("command", false);
    tmpCommand.setThirdParameter(new Parameter("param"));

    tmpCommand.checkNoUnusedThirdParameter(context);
  }

  @Test
  public void toPrintableString_empty() {
    final Command tmpCommand = new Command("command", false);

    assertEquals("[Command 'command' params: ()]", tmpCommand.toPrintableString(context));
  }

  @Test
  public void toPrintableString_comment() {
    final Command tmpCommand = new Command("command", true);

    assertEquals("[Command 'command' COMMENT params: ()]", tmpCommand.toPrintableString(context));
  }

  @Test
  public void toPrintableString_WithParameter1_empty() {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setFirstParameter(new Parameter(""));

    assertEquals("[Command 'command' params: (1: '')]", tmpCommand.toPrintableString(context));
  }

  @Test
  public void toPrintableString_WithParameter1() {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setFirstParameter(new Parameter("first"));

    assertEquals("[Command 'command' params: (1: 'first')]", tmpCommand.toPrintableString(context));
  }

  @Test
  public void toPrintableString_WithParameter2_empty() {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter(""));

    assertEquals("[Command 'command' params: (2: '')]", tmpCommand.toPrintableString(context));
  }

  @Test
  public void toPrintableString_WithParameter2() {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setSecondParameter(new Parameter("second"));

    assertEquals("[Command 'command' params: (2: 'second')]", tmpCommand.toPrintableString(context));
  }

  @Test
  public void toPrintableString_WithParameter3_empty() {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setThirdParameter(new Parameter(""));

    assertEquals("[Command 'command' params: (3: '')]", tmpCommand.toPrintableString(context));
  }

  @Test
  public void toPrintableString_WithParameter3() {
    final Command tmpCommand = new Command("command", false);
    tmpCommand.setThirdParameter(new Parameter("third"));

    assertEquals("[Command 'command' params: (3: 'third')]", tmpCommand.toPrintableString(context));
  }

  @Test
  public void toPrintableString_full() {
    final Command tmpCommand = new Command("command", true);
    tmpCommand.setFirstParameter(new Parameter("first"));
    tmpCommand.setSecondParameter(new Parameter("second"));
    tmpCommand.setThirdParameter(new Parameter("third"));

    assertEquals("[Command 'command' COMMENT params: (1: 'first' 2: 'second' 3: 'third')]",
        tmpCommand.toPrintableString(context));
  }
}