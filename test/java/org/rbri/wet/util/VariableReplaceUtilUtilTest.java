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


package org.rbri.wet.util;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rbri.wet.core.variable.Variable;

/**
 * @author rbri
 */
public class VariableReplaceUtilUtilTest extends TestCase {

  public static void main(String[] anArgsArray) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(VariableReplaceUtilUtilTest.class);
  }

  public void testReplaceVariable_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = null;
    tmpVariables = null;

    assertNull(VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_Empty_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "";
    tmpVariables = null;

    assertEquals("", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_Static_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "abc";
    tmpVariables = null;

    assertEquals("abc", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_OneVar_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "a ${var0} bc";
    tmpVariables = null;

    assertEquals("a ${var0} bc", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_OneStartSeq_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "a ${var0bc";
    tmpVariables = null;

    assertEquals("a ${var0bc", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_TwoEndSeq_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "a ${var}}0bc";
    tmpVariables = null;

    assertEquals("a ${var}}0bc", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_TwoVar_null() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;

    tmpStringWithPlaceholders = "a ${var0} b ${var1} c";
    tmpVariables = null;

    assertEquals("a ${var0} b ${var1} c", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables,
        false));
  }

  public void testReplaceVariable_OnlyOneVar() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    assertEquals("value0", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_VarAtStart() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}abc";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    assertEquals("value0abc", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_VarAtEnd() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}de";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    assertEquals("value0de", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_TwoVars() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}${var1}";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    assertEquals("value0value1", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }

  public void testReplaceVariable_ReusedVars() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}${var1} ${var0}";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    assertEquals("value0value1 value0", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables,
        false));
  }

  public void testReplaceVariable_VarMagic() {
    String tmpStringWithPlaceholders;
    List<Variable> tmpVariables;
    Variable tmpVariable;

    tmpStringWithPlaceholders = "${var0}";
    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "${var1}", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    assertEquals("value1", VariableReplaceUtil.replaceVariables(tmpStringWithPlaceholders, tmpVariables, false));
  }
}
