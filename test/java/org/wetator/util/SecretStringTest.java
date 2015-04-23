/*
 * Copyright (c) 2008-2015 wetator.org
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


package org.wetator.util;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.core.Variable;

/**
 * @author rbri
 */
public class SecretStringTest {

  @Test
  public void constructor() {
    final SecretString tmpSecret = new SecretString();
    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());
  }

  @Test
  public void emptyConstructor() {
    final SecretString tmpSecret = new SecretString("");
    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());
  }

  @Test
  public void stringConstructor() {
    final SecretString tmpSecret = new SecretString("Test");
    Assert.assertEquals("Test", tmpSecret.getValue());
    Assert.assertEquals("Test", tmpSecret.toString());
  }

  @Test
  public void simpleSecret() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");
    Assert.assertEquals("hidden", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void mixSecret() {
    SecretString tmpSecret = new SecretString().appendSecret("hidden").append("public");
    Assert.assertEquals("hiddenpublic", tmpSecret.getValue());
    Assert.assertEquals("****public", tmpSecret.toString());

    tmpSecret = new SecretString("public").appendSecret("hidden");
    Assert.assertEquals("publichidden", tmpSecret.getValue());
    Assert.assertEquals("public****", tmpSecret.toString());

    tmpSecret = new SecretString("public").appendSecret("");
    Assert.assertEquals("public", tmpSecret.getValue());
    Assert.assertEquals("public****", tmpSecret.toString());
  }

  @Test
  public void append() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.append("1");
    tmpSecret.append(null);
    tmpSecret.append("2");
    tmpSecret.append("");
    tmpSecret.append("3");
    tmpSecret.append(" ");
    tmpSecret.append("4");
    tmpSecret.append("\t");
    tmpSecret.append("5");
    tmpSecret.append(" 6");
    tmpSecret.append(" 7 ");
    tmpSecret.append("8");

    Assert.assertEquals("123 4\t5 6 7 8", tmpSecret.getValue());
    Assert.assertEquals("123 4\t5 6 7 8", tmpSecret.toString());
  }

  @Test
  public void appendSecret() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.appendSecret("1");
    tmpSecret.appendSecret(null);
    tmpSecret.appendSecret("2");
    tmpSecret.appendSecret("");
    tmpSecret.appendSecret("3");
    tmpSecret.appendSecret(" ");
    tmpSecret.appendSecret("4");
    tmpSecret.appendSecret("\t");
    tmpSecret.appendSecret("5");
    tmpSecret.appendSecret(" 6");
    tmpSecret.appendSecret(" 7 ");
    tmpSecret.appendSecret("8");

    Assert.assertEquals("123 4\t5 6 7 8", tmpSecret.getValue());
    Assert.assertEquals("********************************************", tmpSecret.toString());
  }

  @Test
  public void prefixWith() {
    SecretString tmpSecret = new SecretString("");
    tmpSecret.prefixWith("1");

    Assert.assertEquals("1", tmpSecret.getValue());
    Assert.assertEquals("1", tmpSecret.toString());

    tmpSecret = new SecretString("abcd");
    tmpSecret.prefixWith("1");

    Assert.assertEquals("1abcd", tmpSecret.getValue());
    Assert.assertEquals("1abcd", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("abcd");
    tmpSecret.prefixWith("1");

    Assert.assertEquals("1abcd", tmpSecret.getValue());
    Assert.assertEquals("1****", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("abcd");
    tmpSecret.prefixWith("");

    Assert.assertEquals("abcd", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("abcd");
    tmpSecret.prefixWith(null);

    Assert.assertEquals("abcd", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void startsWith() {
    SecretString tmpSecret = new SecretString("abcd");
    Assert.assertTrue(tmpSecret.startsWith("ab"));
    Assert.assertFalse(tmpSecret.startsWith("bc"));
    Assert.assertTrue(tmpSecret.startsWith("b", 1));
    Assert.assertFalse(tmpSecret.startsWith("b", 2));

    tmpSecret = new SecretString().appendSecret("abcd");
    Assert.assertTrue(tmpSecret.startsWith("ab"));
    Assert.assertFalse(tmpSecret.startsWith("bc"));
    Assert.assertTrue(tmpSecret.startsWith("b", 1));
    Assert.assertFalse(tmpSecret.startsWith("b", 2));
  }

  @Test
  public void endsWith() {
    SecretString tmpSecret = new SecretString("abcd");
    Assert.assertTrue(tmpSecret.endsWith("d"));
    Assert.assertFalse(tmpSecret.endsWith("c"));

    tmpSecret = new SecretString().appendSecret("abcd");
    Assert.assertTrue(tmpSecret.endsWith("d"));
    Assert.assertFalse(tmpSecret.endsWith("c"));
  }

  @Test
  public void trim() {
    SecretString tmpSecret = new SecretString();
    tmpSecret.trim();

    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());

    tmpSecret = new SecretString("");
    tmpSecret.trim();

    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());

    tmpSecret = new SecretString("x");
    tmpSecret.trim();

    Assert.assertEquals("x", tmpSecret.getValue());
    Assert.assertEquals("x", tmpSecret.toString());

    tmpSecret = new SecretString("abcd");
    tmpSecret.trim();

    Assert.assertEquals("abcd", tmpSecret.getValue());
    Assert.assertEquals("abcd", tmpSecret.toString());

    tmpSecret = new SecretString(" abcd");
    tmpSecret.trim();

    Assert.assertEquals("abcd", tmpSecret.getValue());
    Assert.assertEquals("abcd", tmpSecret.toString());

    tmpSecret = new SecretString("abcd ");
    tmpSecret.trim();

    Assert.assertEquals("abcd", tmpSecret.getValue());
    Assert.assertEquals("abcd", tmpSecret.toString());

    tmpSecret = new SecretString(" \t   abcd \n \t   ");
    tmpSecret.trim();

    Assert.assertEquals("abcd", tmpSecret.getValue());
    Assert.assertEquals("abcd", tmpSecret.toString());

    tmpSecret = new SecretString(" ").appendSecret("x");
    tmpSecret.trim();

    Assert.assertEquals("x", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpSecret = new SecretString(" ").appendSecret(" ab ");
    tmpSecret.trim();

    Assert.assertEquals("ab", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpSecret = new SecretString(" a").appendSecret("xy").append("c   ");
    tmpSecret.trim();

    Assert.assertEquals("axyc", tmpSecret.getValue());
    Assert.assertEquals("a****c", tmpSecret.toString());
  }

  @Test
  public void substring() {
    SecretString tmpSecret = new SecretString("abcd");
    tmpSecret = tmpSecret.substring(2);

    Assert.assertEquals("cd", tmpSecret.getValue());
    Assert.assertEquals("cd", tmpSecret.toString());

    tmpSecret = new SecretString("abcd").appendSecret("xyz");
    tmpSecret = tmpSecret.substring(2);

    Assert.assertEquals("cdxyz", tmpSecret.getValue());
    Assert.assertEquals("cd****", tmpSecret.toString());

    tmpSecret = new SecretString("abcd");
    tmpSecret = tmpSecret.substring(2, 3);

    Assert.assertEquals("c", tmpSecret.getValue());
    Assert.assertEquals("c", tmpSecret.toString());

    tmpSecret = new SecretString("abcd").appendSecret("xyz");
    tmpSecret = tmpSecret.substring(2, 6);

    Assert.assertEquals("cdxy", tmpSecret.getValue());
    Assert.assertEquals("cd****", tmpSecret.toString());
  }

  @Test
  public void split() {
    SecretString tmpSecret = new SecretString("ab,cd");
    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    Assert.assertEquals(2, tmpParts.size());
    tmpSecret = tmpParts.get(0);
    Assert.assertEquals("ab", tmpSecret.getValue());
    Assert.assertEquals("ab", tmpSecret.toString());
    tmpSecret = tmpParts.get(1);
    Assert.assertEquals("cd", tmpSecret.getValue());
    Assert.assertEquals("cd", tmpSecret.toString());
  }

  @Test
  public void splitAtStart() {
    SecretString tmpSecret = new SecretString(",cd");
    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    Assert.assertEquals(2, tmpParts.size());
    tmpSecret = tmpParts.get(0);
    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());
    tmpSecret = tmpParts.get(1);
    Assert.assertEquals("cd", tmpSecret.getValue());
    Assert.assertEquals("cd", tmpSecret.toString());
  }

  @Test
  public void splitAtEnd() {
    SecretString tmpSecret = new SecretString("cd,");
    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    Assert.assertEquals(2, tmpParts.size());
    tmpSecret = tmpParts.get(0);
    Assert.assertEquals("cd", tmpSecret.getValue());
    Assert.assertEquals("cd", tmpSecret.toString());
    tmpSecret = tmpParts.get(1);
    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());
  }

  @Test
  public void splitEscaped() {
    SecretString tmpSecret = new SecretString("ab,cd\\,de,xy");
    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    Assert.assertEquals(3, tmpParts.size());
    tmpSecret = tmpParts.get(0);
    Assert.assertEquals("ab", tmpSecret.getValue());
    Assert.assertEquals("ab", tmpSecret.toString());
    tmpSecret = tmpParts.get(1);
    Assert.assertEquals("cd,de", tmpSecret.getValue());
    Assert.assertEquals("cd,de", tmpSecret.toString());
    tmpSecret = tmpParts.get(2);
    Assert.assertEquals("xy", tmpSecret.getValue());
    Assert.assertEquals("xy", tmpSecret.toString());
  }

  @Test
  public void splitEscapedAtStart() {
    SecretString tmpSecret = new SecretString("\\,ab,xy");
    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    Assert.assertEquals(2, tmpParts.size());
    tmpSecret = tmpParts.get(0);
    Assert.assertEquals(",ab", tmpSecret.getValue());
    Assert.assertEquals(",ab", tmpSecret.toString());
    tmpSecret = tmpParts.get(1);
    Assert.assertEquals("xy", tmpSecret.getValue());
    Assert.assertEquals("xy", tmpSecret.toString());
  }

  @Test
  public void splitEscapedAtEnd() {
    SecretString tmpSecret = new SecretString("ab\\,");
    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    Assert.assertEquals(1, tmpParts.size());
    tmpSecret = tmpParts.get(0);
    Assert.assertEquals("ab,", tmpSecret.getValue());
    Assert.assertEquals("ab,", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_null() {
    final SecretString tmpSecret = new SecretString(null);
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());

    tmpSecret.replaceVariables(new LinkedList<Variable>());

    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_Empty_null() {
    final SecretString tmpSecret = new SecretString("");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());

    tmpSecret.replaceVariables(new LinkedList<Variable>());

    Assert.assertEquals("", tmpSecret.getValue());
    Assert.assertEquals("", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_Static_null() {
    SecretString tmpSecret = new SecretString("abc");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("abc", tmpSecret.getValue());
    Assert.assertEquals("abc", tmpSecret.toString());

    tmpSecret.replaceVariables(new LinkedList<Variable>());

    Assert.assertEquals("abc", tmpSecret.getValue());
    Assert.assertEquals("abc", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("abc");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("abc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpSecret.replaceVariables(new LinkedList<Variable>());

    Assert.assertEquals("abc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_OneVar_null() {
    SecretString tmpSecret = new SecretString("a ${var0} bc");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("a ${var0} bc", tmpSecret.getValue());
    Assert.assertEquals("a ${var0} bc", tmpSecret.toString());

    tmpSecret.replaceVariables(new LinkedList<Variable>());

    Assert.assertEquals("a ${var0} bc", tmpSecret.getValue());
    Assert.assertEquals("a ${var0} bc", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("a ${var0} bc");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("a ${var0} bc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpSecret.replaceVariables(new LinkedList<Variable>());

    Assert.assertEquals("a ${var0} bc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_OneVar_Unknown() {
    final List<Variable> tmpVariables = new LinkedList<Variable>();
    final Variable tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("a ${unknown} bc");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("a ${unknown} bc", tmpSecret.getValue());
    Assert.assertEquals("a ${unknown} bc", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("a ${unknown} bc");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("a ${unknown} bc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_OneStartSeq_null() {
    SecretString tmpSecret = new SecretString("a ${var0bc");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("a ${var0bc", tmpSecret.getValue());
    Assert.assertEquals("a ${var0bc", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("a ${var0bc");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("a ${var0bc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_TwoEndSeq_null() {
    SecretString tmpSecret = new SecretString("a ${var}}0bc");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("a ${var}}0bc", tmpSecret.getValue());
    Assert.assertEquals("a ${var}}0bc", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("a ${var}}0bc");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("a ${var}}0bc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_TwoVar_null() {
    SecretString tmpSecret = new SecretString("a ${var0} b ${var1} c");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("a ${var0} b ${var1} c", tmpSecret.getValue());
    Assert.assertEquals("a ${var0} b ${var1} c", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("a ${var0} b ${var1} c");
    tmpSecret.replaceVariables(null);

    Assert.assertEquals("a ${var0} b ${var1} c", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_OnlyOneVar() {
    final List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0", tmpSecret.getValue());
    Assert.assertEquals("value0", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpVariable = new Variable("var0", "value0", true);
    tmpVariables.add(tmpVariable);

    tmpSecret = new SecretString("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0", tmpSecret.getValue());
    Assert.assertEquals("value0", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_ReplacementLength() {
    final List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "a", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var2", "ab", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var4", "abcd", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var11", "abcdefghijk", true);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0} ${var1} ${var2} ${var4} ${var11}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals(" a ab abcd abcdefghijk", tmpSecret.getValue());
    Assert.assertEquals("**** **** **** **** ****", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}").append("+").appendSecret("${var1}").append("+")
        .appendSecret("${var2}").append("+").appendSecret("${var4}").append("+").appendSecret("${var11}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("+a+ab+abcd+abcdefghijk", tmpSecret.getValue());
    Assert.assertEquals("****+****+****+****+****", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var11}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("abcdefghijk", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_VarAtStart() {
    final List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0}abc");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0abc", tmpSecret.getValue());
    Assert.assertEquals("value0abc", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}abc");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0abc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpVariable = new Variable("var0", "value0", true);
    tmpVariables.add(tmpVariable);

    tmpSecret = new SecretString("${var0}abc");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0abc", tmpSecret.getValue());
    Assert.assertEquals("value0abc", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}abc");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0abc", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_VarAtEnd() {
    final List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("de${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("devalue0", tmpSecret.getValue());
    Assert.assertEquals("devalue0", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("de${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("devalue0", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpVariable = new Variable("var0", "value0", true);
    tmpVariables.add(tmpVariable);

    tmpSecret = new SecretString("de${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("devalue0", tmpSecret.getValue());
    Assert.assertEquals("devalue0", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("de${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("devalue0", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_TwoVars() {
    final List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0}${var1}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1", tmpSecret.getValue());
    Assert.assertEquals("value0value1", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}${var1}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpVariable = new Variable("var0", "value0", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    tmpSecret = new SecretString("${var0}${var1}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1", tmpSecret.getValue());
    Assert.assertEquals("value0value1", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}${var1}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", true);
    tmpVariables.add(tmpVariable);

    tmpSecret = new SecretString("${var0}${var1}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1", tmpSecret.getValue());
    Assert.assertEquals("value0value1", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}${var1}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_ReusedVars() {
    List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0}${var1} ${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1 value0", tmpSecret.getValue());
    Assert.assertEquals("value0value1 value0", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}${var1} ${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1 value0", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    tmpSecret = new SecretString("${var0}${var1} ${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1 value0", tmpSecret.getValue());
    Assert.assertEquals("****value1 ****", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}${var1} ${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1 value0", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "value0", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", true);
    tmpVariables.add(tmpVariable);

    tmpSecret = new SecretString("${var0}${var1} ${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1 value0", tmpSecret.getValue());
    Assert.assertEquals("value0**** value0", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}${var1} ${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value0value1 value0", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_VarMagic() {
    List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "${var1}", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value1", tmpSecret.getValue());
    Assert.assertEquals("value1", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value1", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpVariables = new LinkedList<Variable>();
    tmpVariable = new Variable("var0", "${var1}", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", false);
    tmpVariables.add(tmpVariable);

    tmpSecret = new SecretString("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value1", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());

    tmpSecret = new SecretString().appendSecret("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value1", tmpSecret.getValue());
    Assert.assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_brokenSyntax() {
    final List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "${var1}", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", true);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0} ${var1");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value1 ${var1", tmpSecret.getValue());
    Assert.assertEquals("**** ${var1", tmpSecret.toString());

    tmpSecret = new SecretString("${var0} ${");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value1 ${", tmpSecret.getValue());
    Assert.assertEquals("**** ${", tmpSecret.toString());

    tmpSecret = new SecretString("${var0} ${}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value1 ${}", tmpSecret.getValue());
    Assert.assertEquals("**** ${}", tmpSecret.toString());

    tmpSecret = new SecretString("${var0} ${7}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("value1 ${7}", tmpSecret.getValue());
    Assert.assertEquals("**** ${7}", tmpSecret.toString());

    tmpSecret = new SecretString("${var0${var1}}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("${var0value1}", tmpSecret.getValue());
    Assert.assertEquals("${var0****}", tmpSecret.toString());
  }

  @Test
  public void replaceVariable_recursion() {
    final List<Variable> tmpVariables = new LinkedList<Variable>();
    Variable tmpVariable = new Variable("var0", "${var0}", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "${var2}", false);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var2", "${var1}", false);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0}");
    tmpSecret.replaceVariables(tmpVariables);

    Assert.assertEquals("${var0}", tmpSecret.getValue());
    Assert.assertEquals("${var0}", tmpSecret.toString());

    tmpSecret = new SecretString("${var1}");
    try {
      tmpSecret.replaceVariables(tmpVariables);
      Assert.fail("IllegalArgumentException expected");
    } catch (final IllegalArgumentException e) {
      Assert.assertEquals("Recursion during variable replacement (${var2}).", e.getMessage());
    }
  }
}
