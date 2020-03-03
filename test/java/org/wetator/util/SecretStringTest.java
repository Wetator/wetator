/*
 * Copyright (c) 2008-2020 wetator.org
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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.wetator.core.Variable;

/**
 * @author rbri
 * @author frank.danek
 */
public class SecretStringTest {

  @Test
  public void constructor() {
    final SecretString tmpSecret = new SecretString();

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void constructorNull() {
    final SecretString tmpSecret = new SecretString(null);

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void constructorEmpty() {
    final SecretString tmpSecret = new SecretString("");

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void constructorBlank() {
    final SecretString tmpSecret = new SecretString(" ");

    assertEquals(" ", tmpSecret.getValue());
    assertEquals(" ", tmpSecret.toString());
  }

  @Test
  public void constructorString() {
    final SecretString tmpSecret = new SecretString("public");

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void appendNull() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.append(null);

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void appendEmpty() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.append("");

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void appendBlank() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.append(" ");

    assertEquals(" ", tmpSecret.getValue());
    assertEquals(" ", tmpSecret.toString());
  }

  @Test
  public void appendString() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.append("public");

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void appendStringFluent() {
    final SecretString tmpSecret = new SecretString().append("public");

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void appendMultiple() {
    // @formatter:off
    final SecretString tmpSecret = new SecretString()
      .append("1")
      .append(null)
      .append("2")
      .append("")
      .append("3")
      .append(" ")
      .append("4")
      .append("\t")
      .append("5 ")
      .append(" 6")
      .append(" 7 ")
      .append("8");
    // @formatter:on

    assertEquals("123 4\t5  6 7 8", tmpSecret.getValue());
    assertEquals("123 4\t5  6 7 8", tmpSecret.toString());
  }

  @Test
  public void appendSecretNull() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.appendSecret(null);

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void appendSecretEmpty() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.appendSecret("");

    assertEquals("", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void appendSecretBlank() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.appendSecret(" ");

    assertEquals(" ", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void appendSecretString() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.appendSecret("hidden");

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void appendSecretStringFluent() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void appendSecretMultiple() {
    // @formatter:off
    final SecretString tmpSecret = new SecretString()
      .appendSecret("1")
      .appendSecret(null)
      .appendSecret("2")
      .appendSecret("")
      .appendSecret("3")
      .appendSecret(" ")
      .appendSecret("4")
      .appendSecret("\t")
      .appendSecret("5 ")
      .appendSecret(" 6")
      .appendSecret(" 7 ")
      .appendSecret("8");
    // @formatter:on

    assertEquals("123 4\t5  6 7 8", tmpSecret.getValue());
    assertEquals("********************************************", tmpSecret.toString());
  }

  @Test
  public void appendMixedEmptySecretFirst() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.appendSecret("");
    tmpSecret.append("public");

    assertEquals("public", tmpSecret.getValue());
    assertEquals("****public", tmpSecret.toString());
  }

  @Test
  public void appendMixedSecretFirst() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.appendSecret("hidden");
    tmpSecret.append("public");

    assertEquals("hiddenpublic", tmpSecret.getValue());
    assertEquals("****public", tmpSecret.toString());
  }

  @Test
  public void appendMixedEmptySecretLast() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.append("public");
    tmpSecret.appendSecret("");

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public****", tmpSecret.toString());
  }

  @Test
  public void appendMixedSecretLast() {
    final SecretString tmpSecret = new SecretString();
    tmpSecret.append("public");
    tmpSecret.appendSecret("hidden");

    assertEquals("publichidden", tmpSecret.getValue());
    assertEquals("public****", tmpSecret.toString());
  }

  @Test
  public void prefixEmptyWithNull() {
    final SecretString tmpSecret = new SecretString("");
    tmpSecret.prefixWith(null);

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void prefixEmptyWithEmpty() {
    final SecretString tmpSecret = new SecretString("");
    tmpSecret.prefixWith("");

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void prefixEmptyWithBlank() {
    final SecretString tmpSecret = new SecretString("");
    tmpSecret.prefixWith(" ");

    assertEquals(" ", tmpSecret.getValue());
    assertEquals(" ", tmpSecret.toString());
  }

  @Test
  public void prefixEmptyWithString() {
    final SecretString tmpSecret = new SecretString("");
    tmpSecret.prefixWith("abcd");

    assertEquals("abcd", tmpSecret.getValue());
    assertEquals("abcd", tmpSecret.toString());
  }

  @Test
  public void prefixWithNull() {
    final SecretString tmpSecret = new SecretString("public");
    tmpSecret.prefixWith(null);

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void prefixWithEmpty() {
    final SecretString tmpSecret = new SecretString("public");
    tmpSecret.prefixWith("");

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void prefixWithBlank() {
    final SecretString tmpSecret = new SecretString("public");
    tmpSecret.prefixWith(" ");

    assertEquals(" public", tmpSecret.getValue());
    assertEquals(" public", tmpSecret.toString());
  }

  @Test
  public void prefixWithString() {
    final SecretString tmpSecret = new SecretString("public");
    tmpSecret.prefixWith("abcd");

    assertEquals("abcdpublic", tmpSecret.getValue());
    assertEquals("abcdpublic", tmpSecret.toString());
  }

  @Test
  public void prefixWithStringFluent() {
    final SecretString tmpSecret = new SecretString("public").prefixWith("abcd");

    assertEquals("abcdpublic", tmpSecret.getValue());
    assertEquals("abcdpublic", tmpSecret.toString());
  }

  @Test
  public void prefixSecretWithNull() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");
    tmpSecret.prefixWith(null);

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void prefixSecretWithEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");
    tmpSecret.prefixWith("");

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void prefixSecretWithBlank() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");
    tmpSecret.prefixWith(" ");

    assertEquals(" hidden", tmpSecret.getValue());
    assertEquals(" ****", tmpSecret.toString());
  }

  @Test
  public void prefixSecretWithString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");
    tmpSecret.prefixWith("abcd");

    assertEquals("abcdhidden", tmpSecret.getValue());
    assertEquals("abcd****", tmpSecret.toString());
  }

  @Test
  public void trimNull() {
    final SecretString tmpSecret = new SecretString(null);
    tmpSecret.trim();

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void trimEmpty() {
    final SecretString tmpSecret = new SecretString("");
    tmpSecret.trim();

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void trimBlank() {
    final SecretString tmpSecret = new SecretString(" ");
    tmpSecret.trim();

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void trimString() {
    final SecretString tmpSecret = new SecretString("pub lic");
    tmpSecret.trim();

    assertEquals("pub lic", tmpSecret.getValue());
    assertEquals("pub lic", tmpSecret.toString());
  }

  @Test
  public void trimLeading() {
    final SecretString tmpSecret = new SecretString("  public");
    tmpSecret.trim();

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void trimTrailing() {
    final SecretString tmpSecret = new SecretString("public  ");
    tmpSecret.trim();

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void trimBoth() {
    final SecretString tmpSecret = new SecretString("  public  ");
    tmpSecret.trim();

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void trimWhitespace() {
    final SecretString tmpSecret = new SecretString("\t \n\rpublic\t \n\r");
    tmpSecret.trim();

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void trimFluent() {
    final SecretString tmpSecret = new SecretString("  public  ").trim();

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void trimSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret(null);
    tmpSecret.trim();

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void trimSecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("");
    tmpSecret.trim();

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void trimSecretBlank() {
    final SecretString tmpSecret = new SecretString().appendSecret(" ");
    tmpSecret.trim();

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void trimSecretString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hid den");
    tmpSecret.trim();

    assertEquals("hid den", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void trimSecretLeading() {
    final SecretString tmpSecret = new SecretString().appendSecret("  hidden");
    tmpSecret.trim();

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void trimSecretTrailing() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden  ");
    tmpSecret.trim();

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void trimSecretBoth() {
    final SecretString tmpSecret = new SecretString().appendSecret("  hidden  ");
    tmpSecret.trim();

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void trimSecretWhitespace() {
    final SecretString tmpSecret = new SecretString().appendSecret("\t \n\rhidden\t \n\r");
    tmpSecret.trim();

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void trimMixed() {
    final SecretString tmpSecret = new SecretString().append(" ab").appendSecret("hidden").append("cd ");
    tmpSecret.trim();

    assertEquals("abhiddencd", tmpSecret.getValue());
    assertEquals("ab****cd", tmpSecret.toString());
  }

  @Test
  public void substring() {
    final SecretString tmpSecret = new SecretString("public");
    final SecretString tmpSubSecret = tmpSecret.substring(2);

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
    assertEquals("blic", tmpSubSecret.getValue());
    assertEquals("blic", tmpSubSecret.toString());
  }

  @Test
  public void substringSecret() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");
    final SecretString tmpSubSecret = tmpSecret.substring(2);

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
    assertEquals("dden", tmpSubSecret.getValue());
    assertEquals("****", tmpSubSecret.toString());
  }

  @Test
  public void substringMixedSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden").append("public");
    final SecretString tmpSubSecret = tmpSecret.substring(2);

    assertEquals("hiddenpublic", tmpSecret.getValue());
    assertEquals("****public", tmpSecret.toString());
    assertEquals("ddenpublic", tmpSubSecret.getValue());
    assertEquals("****public", tmpSubSecret.toString());
  }

  @Test
  public void substringMixedSecretLast() {
    final SecretString tmpSecret = new SecretString("public").appendSecret("hidden");
    final SecretString tmpSubSecret = tmpSecret.substring(2);

    assertEquals("publichidden", tmpSecret.getValue());
    assertEquals("public****", tmpSecret.toString());
    assertEquals("blichidden", tmpSubSecret.getValue());
    assertEquals("blic****", tmpSubSecret.toString());
  }

  @Test
  public void substringWithEnd() {
    final SecretString tmpSecret = new SecretString("public");
    final SecretString tmpSubSecret = tmpSecret.substring(2, 4);

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
    assertEquals("bl", tmpSubSecret.getValue());
    assertEquals("bl", tmpSubSecret.toString());
  }

  @Test
  public void substringWithEndSecret() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");
    final SecretString tmpSubSecret = tmpSecret.substring(2, 4);

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
    assertEquals("dd", tmpSubSecret.getValue());
    assertEquals("****", tmpSubSecret.toString());
  }

  @Test
  public void substringWithEndMixedSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden").append("public");
    final SecretString tmpSubSecret = tmpSecret.substring(5, 7);

    assertEquals("hiddenpublic", tmpSecret.getValue());
    assertEquals("****public", tmpSecret.toString());
    assertEquals("np", tmpSubSecret.getValue());
    assertEquals("****p", tmpSubSecret.toString());
  }

  @Test
  public void substringWithEndMixedSecretLast() {
    final SecretString tmpSecret = new SecretString("public").appendSecret("hidden");
    final SecretString tmpSubSecret = tmpSecret.substring(5, 7);

    assertEquals("publichidden", tmpSecret.getValue());
    assertEquals("public****", tmpSecret.toString());
    assertEquals("ch", tmpSubSecret.getValue());
    assertEquals("c****", tmpSubSecret.toString());
  }

  @Test(expected = NullPointerException.class)
  public void startsWithNull() {
    final SecretString tmpSecret = new SecretString("public");

    tmpSecret.startsWith(null);
  }

  @Test
  public void startsWithEmpty() {
    final SecretString tmpSecret = new SecretString("public");

    assertTrue(tmpSecret.startsWith(""));
  }

  @Test
  public void startsWithString() {
    final SecretString tmpSecret = new SecretString("public");

    assertTrue(tmpSecret.startsWith("pu"));
  }

  @Test
  public void startsWithNot() {
    final SecretString tmpSecret = new SecretString("public");

    assertFalse(tmpSecret.startsWith("ab"));
  }

  @Test(expected = NullPointerException.class)
  public void startsWithSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    tmpSecret.startsWith(null);
  }

  @Test
  public void startsWithSecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.startsWith(""));
  }

  @Test
  public void startsWithSecretString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.startsWith("hi"));
  }

  @Test
  public void startsWithSecretNot() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertFalse(tmpSecret.startsWith("ab"));
  }

  @Test(expected = NullPointerException.class)
  public void startsWithIndexNull() {
    final SecretString tmpSecret = new SecretString("public");

    tmpSecret.startsWith(null, 2);
  }

  @Test
  public void startsWithMixedSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden").append("public");

    assertTrue(tmpSecret.startsWith("hiddenpu"));
  }

  @Test
  public void startsWithMixedSecretLast() {
    final SecretString tmpSecret = new SecretString("public").appendSecret("hidden");

    assertTrue(tmpSecret.startsWith("publichi"));
  }

  @Test
  public void startsWithIndexEmpty() {
    final SecretString tmpSecret = new SecretString("public");

    assertTrue(tmpSecret.startsWith("", 2));
  }

  @Test
  public void startsWithIndexString() {
    final SecretString tmpSecret = new SecretString("public");

    assertTrue(tmpSecret.startsWith("bl", 2));
  }

  @Test
  public void startsWithIndexNotByString() {
    final SecretString tmpSecret = new SecretString("public");

    assertFalse(tmpSecret.startsWith("ab", 2));
  }

  @Test
  public void startsWithIndexNotByIndex() {
    final SecretString tmpSecret = new SecretString("public");

    assertFalse(tmpSecret.startsWith("ic", 2));
  }

  @Test(expected = NullPointerException.class)
  public void startsWithIndexSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    tmpSecret.startsWith(null, 2);
  }

  @Test
  public void startsWithIndexSecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.startsWith("", 2));
  }

  @Test
  public void startsWithIndexSecretString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.startsWith("dd", 2));
  }

  @Test
  public void startsWithIndexSecretNotByString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertFalse(tmpSecret.startsWith("ab", 2));
  }

  @Test
  public void startsWithIndexSecretNotByIndex() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertFalse(tmpSecret.startsWith("hi", 2));
  }

  @Test
  public void startsWithIndexMixedSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden").append("public");

    assertTrue(tmpSecret.startsWith("enpu", 4));
  }

  @Test
  public void startsWithIndexMixedSecretLast() {
    final SecretString tmpSecret = new SecretString("public").appendSecret("hidden");

    assertTrue(tmpSecret.startsWith("ichi", 4));
  }

  @Test(expected = NullPointerException.class)
  public void endsWithNull() {
    final SecretString tmpSecret = new SecretString("public");

    tmpSecret.endsWith(null);
  }

  @Test
  public void endsWithEmpty() {
    final SecretString tmpSecret = new SecretString("public");

    assertTrue(tmpSecret.endsWith(""));
  }

  @Test
  public void endsWithString() {
    final SecretString tmpSecret = new SecretString("public");

    assertTrue(tmpSecret.endsWith("ic"));
  }

  @Test
  public void endsWithNot() {
    final SecretString tmpSecret = new SecretString("public");

    assertFalse(tmpSecret.endsWith("cd"));
  }

  @Test(expected = NullPointerException.class)
  public void endsWithSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.endsWith(null));
  }

  @Test
  public void endsWithSecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.endsWith(""));
  }

  @Test
  public void endsWithSecretString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.endsWith("en"));
  }

  @Test
  public void endsWithSecretNot() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertFalse(tmpSecret.endsWith("cd"));
  }

  @Test
  public void endsWithMixedSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden").append("public");

    assertTrue(tmpSecret.endsWith("enpublic"));
  }

  @Test
  public void endsWithMixedSecretLast() {
    final SecretString tmpSecret = new SecretString("public").appendSecret("hidden");

    assertTrue(tmpSecret.endsWith("ichidden"));
  }

  @Test(expected = NullPointerException.class)
  public void containsNull() {
    final SecretString tmpSecret = new SecretString("public");

    tmpSecret.contains(null);
  }

  @Test
  public void containsEmpty() {
    final SecretString tmpSecret = new SecretString("public");

    assertTrue(tmpSecret.contains(""));
  }

  @Test
  public void containsString() {
    final SecretString tmpSecret = new SecretString("public");

    assertTrue(tmpSecret.contains("bl"));
  }

  @Test
  public void containsNot() {
    final SecretString tmpSecret = new SecretString("public");

    assertFalse(tmpSecret.contains("bc"));
  }

  @Test(expected = NullPointerException.class)
  public void containsSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.contains(null));
  }

  @Test
  public void containsSecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.contains(""));
  }

  @Test
  public void containsSecretString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertTrue(tmpSecret.contains("dd"));
  }

  @Test
  public void containsSecretNot() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertFalse(tmpSecret.contains("bc"));
  }

  @Test
  public void containsMixedSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden").append("public");

    assertTrue(tmpSecret.contains("enpu"));
  }

  @Test
  public void containsMixedSecretLast() {
    final SecretString tmpSecret = new SecretString("public").appendSecret("hidden");

    assertTrue(tmpSecret.contains("ichi"));
  }

  @Test
  public void splitNull() {
    final SecretString tmpSecret = new SecretString(null);

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(0, tmpParts.size());
  }

  @Test
  public void splitEmpty() {
    final SecretString tmpSecret = new SecretString("");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(0, tmpParts.size());
  }

  @Test
  public void splitNot() {
    final SecretString tmpSecret = new SecretString("public");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(1, tmpParts.size());
    final SecretString tmpPart = tmpParts.get(0);
    assertEquals("public", tmpPart.getValue());
    assertEquals("public", tmpPart.toString());
  }

  @Test
  public void splitAtStart() {
    final SecretString tmpSecret = new SecretString(",public");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("public", tmpPart.getValue());
    assertEquals("public", tmpPart.toString());
  }

  @Test
  public void splitInTheMiddle() {
    final SecretString tmpSecret = new SecretString("pub,lic");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("pub", tmpPart.getValue());
    assertEquals("pub", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("lic", tmpPart.getValue());
    assertEquals("lic", tmpPart.toString());
  }

  @Test
  public void splitAtEnd() {
    final SecretString tmpSecret = new SecretString("public,");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("public", tmpPart.getValue());
    assertEquals("public", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
  }

  @Test
  public void splitMultiple() {
    final SecretString tmpSecret = new SecretString(",pub,,lic,");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(5, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("pub", tmpPart.getValue());
    assertEquals("pub", tmpPart.toString());
    tmpPart = tmpParts.get(2);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
    tmpPart = tmpParts.get(3);
    assertEquals("lic", tmpPart.getValue());
    assertEquals("lic", tmpPart.toString());
    tmpPart = tmpParts.get(4);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
  }

  @Test
  public void splitSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret(null);

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(0, tmpParts.size());
  }

  @Test
  public void splitSecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(0, tmpParts.size());
  }

  @Test
  public void splitSecretNot() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(1, tmpParts.size());
    final SecretString tmpPart = tmpParts.get(0);
    assertEquals("hidden", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
  }

  @Test
  public void splitSecretAtStart() {
    final SecretString tmpSecret = new SecretString().appendSecret(",hidden");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("hidden", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
  }

  @Test
  public void splitSecretInTheMiddle() {
    final SecretString tmpSecret = new SecretString().appendSecret("hid,den");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("hid", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("den", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
  }

  @Test
  public void splitSecretAtEnd() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden,");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("hidden", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
  }

  @Test
  public void splitSecretMultiple() {
    final SecretString tmpSecret = new SecretString().appendSecret(",hid,,den,");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(5, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("hid", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(2);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
    tmpPart = tmpParts.get(3);
    assertEquals("den", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(4);
    assertEquals("", tmpPart.getValue());
    assertEquals("", tmpPart.toString());
  }

  @Test
  public void splitSecretMixedSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("hid,den").append("pub,lic");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(3, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("hid", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("denpub", tmpPart.getValue());
    assertEquals("****pub", tmpPart.toString());
    tmpPart = tmpParts.get(2);
    assertEquals("lic", tmpPart.getValue());
    assertEquals("lic", tmpPart.toString());
  }

  @Test
  public void splitSecretMixedSecretLast() {
    final SecretString tmpSecret = new SecretString("pub,lic").appendSecret("hid,den");

    final List<SecretString> tmpParts = tmpSecret.split(",", -1);

    assertEquals(3, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("pub", tmpPart.getValue());
    assertEquals("pub", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("lichid", tmpPart.getValue());
    assertEquals("lic****", tmpPart.toString());
    tmpPart = tmpParts.get(2);
    assertEquals("den", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
  }

  @Test(expected = NullPointerException.class)
  public void splitByNull() {
    final SecretString tmpSecret = new SecretString("public");

    tmpSecret.split(null, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void splitByEmpty() {
    final SecretString tmpSecret = new SecretString("public");

    tmpSecret.split("", -1);
  }

  @Test
  public void splitByMultiple() {
    final SecretString tmpSecret = new SecretString("public");

    final List<SecretString> tmpParts = tmpSecret.split("bl", -1);

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("pu", tmpPart.getValue());
    assertEquals("pu", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("ic", tmpPart.getValue());
    assertEquals("ic", tmpPart.toString());
  }

  @Test
  public void splitEscapedAtStart() {
    final SecretString tmpSecret = new SecretString("\\,pub,lic");

    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals(",pub", tmpPart.getValue());
    assertEquals(",pub", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("lic", tmpPart.getValue());
    assertEquals("lic", tmpPart.toString());
  }

  @Test
  public void splitEscapedInTheMiddle() {
    final SecretString tmpSecret = new SecretString("pu,b\\,l,ic");

    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    assertEquals(3, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("pu", tmpPart.getValue());
    assertEquals("pu", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("b,l", tmpPart.getValue());
    assertEquals("b,l", tmpPart.toString());
    tmpPart = tmpParts.get(2);
    assertEquals("ic", tmpPart.getValue());
    assertEquals("ic", tmpPart.toString());
  }

  @Test
  public void splitEscapedAtEnd() {
    final SecretString tmpSecret = new SecretString("pub,lic\\,");

    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("pub", tmpPart.getValue());
    assertEquals("pub", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("lic,", tmpPart.getValue());
    assertEquals("lic,", tmpPart.toString());
  }

  @Test
  public void splitEscapedSecretAtStart() {
    final SecretString tmpSecret = new SecretString().appendSecret("\\,hid,den");

    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals(",hid", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("den", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
  }

  @Test
  public void splitEscapedSecretInTheMiddle() {
    final SecretString tmpSecret = new SecretString().appendSecret("hi,d\\,d,en");

    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    assertEquals(3, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("hi", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("d,d", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(2);
    assertEquals("en", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
  }

  @Test
  public void splitEscapedSecretAtEnd() {
    final SecretString tmpSecret = new SecretString().appendSecret("hid,den\\,");

    final List<SecretString> tmpParts = tmpSecret.split(",", '\\');

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("hid", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("den,", tmpPart.getValue());
    assertEquals("****", tmpPart.toString());
  }

  @Test
  public void splitEscapedByMultiple() {
    final SecretString tmpSecret = new SecretString("pu\\blbl\\blic");

    final List<SecretString> tmpParts = tmpSecret.split("bl", '\\');

    assertEquals(2, tmpParts.size());
    SecretString tmpPart = tmpParts.get(0);
    assertEquals("publ", tmpPart.getValue());
    assertEquals("publ", tmpPart.toString());
    tmpPart = tmpParts.get(1);
    assertEquals("blic", tmpPart.getValue());
    assertEquals("blic", tmpPart.toString());
  }

  @Test
  public void lengthNull() {
    final SecretString tmpSecret = new SecretString(null);

    assertEquals(0, tmpSecret.length());
  }

  @Test
  public void lengthEmpty() {
    final SecretString tmpSecret = new SecretString("");

    assertEquals(0, tmpSecret.length());
  }

  @Test
  public void lengthBlank() {
    final SecretString tmpSecret = new SecretString(" ");

    assertEquals(1, tmpSecret.length());
  }

  @Test
  public void lengthString() {
    final SecretString tmpSecret = new SecretString("public");

    assertEquals(6, tmpSecret.length());
  }

  @Test
  public void lengthSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret(null);

    assertEquals(0, tmpSecret.length());
  }

  @Test
  public void lengthSecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("");

    assertEquals(0, tmpSecret.length());
  }

  @Test
  public void lengthSecretBlank() {
    final SecretString tmpSecret = new SecretString().appendSecret(" ");

    assertEquals(1, tmpSecret.length());
  }

  @Test
  public void lengthSecretString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertEquals(6, tmpSecret.length());
  }

  @Test
  public void isEmptyNull() {
    final SecretString tmpSecret = new SecretString(null);

    assertTrue(tmpSecret.isEmpty());
  }

  @Test
  public void isEmptyEmpty() {
    final SecretString tmpSecret = new SecretString("");

    assertTrue(tmpSecret.isEmpty());
  }

  @Test
  public void isEmptyBlank() {
    final SecretString tmpSecret = new SecretString(" ");

    assertFalse(tmpSecret.isEmpty());
  }

  @Test
  public void isEmptyString() {
    final SecretString tmpSecret = new SecretString("public");

    assertFalse(tmpSecret.isEmpty());
  }

  @Test
  public void isEmptySecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret(null);

    assertTrue(tmpSecret.isEmpty());
  }

  @Test
  public void isEmptySecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("");

    assertTrue(tmpSecret.isEmpty());
  }

  @Test
  public void isEmptySecretBlank() {
    final SecretString tmpSecret = new SecretString().appendSecret(" ");

    assertFalse(tmpSecret.isEmpty());
  }

  @Test
  public void isEmptySecretString() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    assertFalse(tmpSecret.isEmpty());
  }

  @Test
  public void toLowerCaseNull() {
    final SecretString tmpSecret = new SecretString(null);

    assertEquals("", tmpSecret.toLowerCase(Locale.ROOT));
  }

  @Test
  public void toLowerCaseEmpty() {
    final SecretString tmpSecret = new SecretString("");

    assertEquals("", tmpSecret.toLowerCase(Locale.ROOT));
  }

  @Test
  public void toLowerCaseBlank() {
    final SecretString tmpSecret = new SecretString(" ");

    assertEquals(" ", tmpSecret.toLowerCase(Locale.ROOT));
  }

  @Test
  public void toLowerCaseString() {
    final SecretString tmpSecret = new SecretString("PuBlIc123");

    assertEquals("public123", tmpSecret.toLowerCase(Locale.ROOT));
  }

  @Test
  public void toLowerCaseSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret(null);

    assertEquals("", tmpSecret.toLowerCase(Locale.ROOT));
  }

  @Test
  public void toLowerCaseecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("");

    assertEquals("", tmpSecret.toLowerCase(Locale.ROOT));
  }

  @Test
  public void toLowerCaseSecretBlank() {
    final SecretString tmpSecret = new SecretString().appendSecret(" ");

    assertEquals(" ", tmpSecret.toLowerCase(Locale.ROOT));
  }

  @Test
  public void toLowerCaseSecretString() {
    final SecretString tmpSecret = new SecretString().appendSecret("HiDdEn123");

    assertEquals("hidden123", tmpSecret.toLowerCase(Locale.ROOT));
  }

  @Test
  public void replaceVariablesStringNull() {
    final SecretString tmpSecret = new SecretString(null);

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretNull() {
    final SecretString tmpSecret = new SecretString().appendSecret(null);

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringEmpty() {
    final SecretString tmpSecret = new SecretString("");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringFix() {
    final SecretString tmpSecret = new SecretString("public");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("public", tmpSecret.getValue());
    assertEquals("public", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretFix() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsNull() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret.replaceVariables(null);

    assertEquals("${var0}", tmpSecret.getValue());
    assertEquals("${var0}", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsNull() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret.replaceVariables(null);

    assertEquals("${var0}", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsEmpty() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret.replaceVariables(new LinkedList<Variable>());

    assertEquals("${var0}", tmpSecret.getValue());
    assertEquals("${var0}", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret.replaceVariables(new LinkedList<Variable>());

    assertEquals("${var0}", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsUnknown() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var1", "value1", false)));

    assertEquals("${var0}", tmpSecret.getValue());
    assertEquals("${var0}", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsUnknown() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var1", "value1", false)));

    assertEquals("${var0}", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsMatchEmpty() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "", false)));

    assertEquals("", tmpSecret.getValue());
    assertEquals("", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsMatchEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "", false)));

    assertEquals("", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsSecretMatchEmpty() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "", true)));

    assertEquals("", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsSecretMatchEmpty() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "", true)));

    assertEquals("", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsMatchOnly() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("value0", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsMatchOnly() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsSecretMatchOnly() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsSecretMatchOnly() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsMatchAtStart() {
    final SecretString tmpSecret = new SecretString("${var0}public");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("value0public", tmpSecret.getValue());
    assertEquals("value0public", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsMatchAtStart() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}hidden");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("value0hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsSecretMatchAtStart() {
    final SecretString tmpSecret = new SecretString("${var0}public");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("value0public", tmpSecret.getValue());
    assertEquals("****public", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsSecretMatchAtStart() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}hidden");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("value0hidden", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsMatchInTheMiddle() {
    final SecretString tmpSecret = new SecretString("pub${var0}lic");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("pubvalue0lic", tmpSecret.getValue());
    assertEquals("pubvalue0lic", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsMatchInTheMiddle() {
    final SecretString tmpSecret = new SecretString().appendSecret("hid${var0}den");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("hidvalue0den", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsSecretMatchInTheMiddle() {
    final SecretString tmpSecret = new SecretString("pub${var0}lic");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("pubvalue0lic", tmpSecret.getValue());
    assertEquals("pub****lic", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsSecretMatchInTheMiddle() {
    final SecretString tmpSecret = new SecretString().appendSecret("hid${var0}den");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("hidvalue0den", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsMatchAtEnd() {
    final SecretString tmpSecret = new SecretString("public${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("publicvalue0", tmpSecret.getValue());
    assertEquals("publicvalue0", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsMatchAtEnd() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("hiddenvalue0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsSecretMatchAtEnd() {
    final SecretString tmpSecret = new SecretString("public${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("publicvalue0", tmpSecret.getValue());
    assertEquals("public****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsSecretMatchAtEnd() {
    final SecretString tmpSecret = new SecretString().appendSecret("hidden${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("hiddenvalue0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarTwiceVarsMatch() {
    final SecretString tmpSecret = new SecretString("${var0} ${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("value0 value0", tmpSecret.getValue());
    assertEquals("value0 value0", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarTwiceVarsMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("value0 value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarTwiceVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString("${var0} ${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("value0 value0", tmpSecret.getValue());
    assertEquals("**** ****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarTwiceVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true)));

    assertEquals("value0 value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsMatchMultiple() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var0", "valueX", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("value0", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsMatchMultiple() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var0", "valueX", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsSecretMatchMultiple() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var0", "valueX", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsSecretMatchMultiple() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var0", "valueX", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsMixedMatchMultipleSecretFirst() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var0", "valueX", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsMixedMatchMultipleSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var0", "valueX", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringOneVarVarsMixedMatchMultipleSecretLast() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var0", "valueX", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("value0", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretOneVarVarsMixedMatchMultipleSecretLast() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var0", "valueX", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarVarsNull() {
    final SecretString tmpSecret = new SecretString("${var0} ${var1}");

    tmpSecret.replaceVariables(null);

    assertEquals("${var0} ${var1}", tmpSecret.getValue());
    assertEquals("${var0} ${var1}", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarVarsNull() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var1}");

    tmpSecret.replaceVariables(null);

    assertEquals("${var0} ${var1}", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarVarsMatch() {
    final SecretString tmpSecret = new SecretString("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "value1", false)));

    assertEquals("value0 value1", tmpSecret.getValue());
    assertEquals("value0 value1", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarVarsMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "value1", false)));

    assertEquals("value0 value1", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "value1", true)));

    assertEquals("value0 value1", tmpSecret.getValue());
    assertEquals("**** ****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "value1", true)));

    assertEquals("value0 value1", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarVarsMixedMatch() {
    final SecretString tmpSecret = new SecretString("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "value1", false)));

    assertEquals("value0 value1", tmpSecret.getValue());
    assertEquals("**** value1", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarVarsMixedMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "value1", false)));

    assertEquals("value0 value1", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringMixedTwoVarVarsMatch() {
    final SecretString tmpSecret = new SecretString("${var0}").appendSecret("${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "value1", false)));

    assertEquals("value0value1", tmpSecret.getValue());
    assertEquals("value0****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringMixedTwoVarVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString("${var0}").appendSecret("${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "value1", true)));

    assertEquals("value0value1", tmpSecret.getValue());
    assertEquals("********", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedVarsMatch() {
    final SecretString tmpSecret = new SecretString("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "${var1}", false), new Variable("var1", "value1", false)));

    assertEquals("value1 value1", tmpSecret.getValue());
    assertEquals("value1 value1", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedVarsMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "${var1}", false), new Variable("var1", "value1", false)));

    assertEquals("value1 value1", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "${var1}", true), new Variable("var1", "value1", true)));

    assertEquals("value1 value1", tmpSecret.getValue());
    assertEquals("**** ****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "${var1}", true), new Variable("var1", "value1", true)));

    assertEquals("value1 value1", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedVarsMixedMatchSecretFirst() {
    final SecretString tmpSecret = new SecretString("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "${var1}", true), new Variable("var1", "value1", false)));

    assertEquals("value1 value1", tmpSecret.getValue());
    assertEquals("**** value1", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedVarsMixedMatchSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "${var1}", true), new Variable("var1", "value1", false)));

    assertEquals("value1 value1", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedVarsMixedMatchSecretLast() {
    final SecretString tmpSecret = new SecretString("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "${var1}", false), new Variable("var1", "value1", true)));

    assertEquals("value1 value1", tmpSecret.getValue());
    assertEquals("**** ****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedVarsMixedMatchSecretLast() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var0} ${var1}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "${var1}", false), new Variable("var1", "value1", true)));

    assertEquals("value1 value1", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedPartAtStartVarsMatch() {
    final SecretString tmpSecret = new SecretString("${${var1}0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "var", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("value0", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedPartAtStartVarsMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${${var1}0}");

    tmpSecret
        .replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "var", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedPartAtStartVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString("${${var1}0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "var", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedPartAtStartVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${${var1}0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "var", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedPartAtStartVarsMixedMatchSecretFirst() {
    final SecretString tmpSecret = new SecretString("${${var1}0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "var", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("value0", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedPartAtStartVarsMixedMatchSecretFirst() {
    final SecretString tmpSecret = new SecretString().appendSecret("${${var1}0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "var", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedPartAtStartVarsMixedMatchSecretLast() {
    final SecretString tmpSecret = new SecretString("${${var1}0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "var", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedPartAtStartVarsMixedMatchSecretLast() {
    final SecretString tmpSecret = new SecretString().appendSecret("${${var1}0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "var", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedPartAtEndVarsMatch() {
    final SecretString tmpSecret = new SecretString("${var${var1}}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "0", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("value0", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedPartAtEndVarsMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var${var1}}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false), new Variable("var1", "0", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedPartAtEndVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString("${var${var1}}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "0", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedPartAtEndVarsSecretMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var${var1}}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "0", true)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringTwoVarNestedPartAtEndVarsMixedMatch() {
    final SecretString tmpSecret = new SecretString("${var${var1}}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "0", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretTwoVarNestedPartAtEndVarsMixedMatch() {
    final SecretString tmpSecret = new SecretString().appendSecret("${var${var1}}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", true), new Variable("var1", "0", false)));

    assertEquals("value0", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesReplacementLengthMultipleSecretVars() {
    final List<Variable> tmpVariables = Arrays.asList(
    // @formatter:off
        new Variable("var0", "", true),
        new Variable("var1", "a", true),
        new Variable("var2", "ab", true),
        new Variable("var4", "abcd", true),
        new Variable("var11", "abcdefghijk", true)
    // @formatter:on
    );

    final SecretString tmpSecret = new SecretString("${var0} ${var1} ${var2} ${var4} ${var11}");
    tmpSecret.replaceVariables(tmpVariables);

    assertEquals(" a ab abcd abcdefghijk", tmpSecret.getValue());
    assertEquals("**** **** **** **** ****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesReplacementLengthMixedString() {
    final List<Variable> tmpVariables = Arrays.asList(
    // @formatter:off
        new Variable("var0", "", true),
        new Variable("var1", "a", true),
        new Variable("var2", "ab", true),
        new Variable("var4", "abcd", true),
        new Variable("var11", "abcdefghijk", true)
    // @formatter:on
    );

    final SecretString tmpSecret = new SecretString().appendSecret("${var0}").append("+").appendSecret("${var1}")
        .append("+").appendSecret("${var2}").append("+").appendSecret("${var4}").append("+").appendSecret("${var11}");
    tmpSecret.replaceVariables(tmpVariables);

    assertEquals("+a+ab+abcd+abcdefghijk", tmpSecret.getValue());
    assertEquals("****+****+****+****+****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringBrokenStartSeqOnly() {
    final SecretString tmpSecret = new SecretString("a ${var0bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a ${var0bc", tmpSecret.getValue());
    assertEquals("a ${var0bc", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretBrokenStartSeqOnly() {
    final SecretString tmpSecret = new SecretString().appendSecret("a ${var0bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a ${var0bc", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringBrokenOneVarOneStartSeqOnly() {
    final SecretString tmpSecret = new SecretString("a ${var0 ${var0}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a ${var0 value0bc", tmpSecret.getValue());
    assertEquals("a ${var0 value0bc", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretBrokenOneVarOneStartSeqOnly() {
    final SecretString tmpSecret = new SecretString().appendSecret("a ${var0 ${var0}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a ${var0 value0bc", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringBrokenStartSeqTwice() {
    final SecretString tmpSecret = new SecretString("a ${${var0}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a ${value0bc", tmpSecret.getValue());
    assertEquals("a ${value0bc", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretBrokenStartSeqTwice() {
    final SecretString tmpSecret = new SecretString().appendSecret("a ${${var0}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a ${value0bc", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringBrokenEndSeqOnly() {
    final SecretString tmpSecret = new SecretString("a var0}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a var0}bc", tmpSecret.getValue());
    assertEquals("a var0}bc", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretBrokenEndSeqOnly() {
    final SecretString tmpSecret = new SecretString().appendSecret("a var0}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a var0}bc", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringBrokenOneVarOneEndSeqOnly() {
    final SecretString tmpSecret = new SecretString("a ${var0}var0}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a value0var0}bc", tmpSecret.getValue());
    assertEquals("a value0var0}bc", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretBrokenOneVarOneEndSeqOnly() {
    final SecretString tmpSecret = new SecretString().appendSecret("a ${var0}var0}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a value0var0}bc", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringBrokenEndSeqTwice() {
    final SecretString tmpSecret = new SecretString("a ${var0}}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a value0}bc", tmpSecret.getValue());
    assertEquals("a value0}bc", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesStringSecretBrokenEndSeqTwice() {
    final SecretString tmpSecret = new SecretString().appendSecret("a ${var0}}bc");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "value0", false)));

    assertEquals("a value0}bc", tmpSecret.getValue());
    assertEquals("****", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesBrokenSyntax() {
    final List<Variable> tmpVariables = new LinkedList<>();
    Variable tmpVariable = new Variable("var0", "${var1}", true);
    tmpVariables.add(tmpVariable);
    tmpVariable = new Variable("var1", "value1", true);
    tmpVariables.add(tmpVariable);

    SecretString tmpSecret = new SecretString("${var0} ${var1");
    tmpSecret.replaceVariables(tmpVariables);

    assertEquals("value1 ${var1", tmpSecret.getValue());
    assertEquals("**** ${var1", tmpSecret.toString());

    tmpSecret = new SecretString("${var0} ${");
    tmpSecret.replaceVariables(tmpVariables);

    assertEquals("value1 ${", tmpSecret.getValue());
    assertEquals("**** ${", tmpSecret.toString());

    tmpSecret = new SecretString("${var0} ${}");
    tmpSecret.replaceVariables(tmpVariables);

    assertEquals("value1 ${}", tmpSecret.getValue());
    assertEquals("**** ${}", tmpSecret.toString());

    tmpSecret = new SecretString("${var0} ${7}");
    tmpSecret.replaceVariables(tmpVariables);

    assertEquals("value1 ${7}", tmpSecret.getValue());
    assertEquals("**** ${7}", tmpSecret.toString());

    tmpSecret = new SecretString("${var0${var1}}");
    tmpSecret.replaceVariables(tmpVariables);

    assertEquals("${var0value1}", tmpSecret.getValue());
    assertEquals("${var0****}", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesRecursionSelf() {
    final SecretString tmpSecret = new SecretString("${var0}");

    tmpSecret.replaceVariables(Arrays.asList(new Variable("var0", "${var0}", false)));

    assertEquals("${var0}", tmpSecret.getValue());
    assertEquals("${var0}", tmpSecret.toString());
  }

  @Test
  public void replaceVariablesBrokenRecursion() {
    final SecretString tmpSecret = new SecretString("${var1}");

    try {
      tmpSecret.replaceVariables(
          Arrays.asList(new Variable("var1", "${var2}", false), new Variable("var2", "${var1}", false)));
      fail("IllegalArgumentException expected");
    } catch (final IllegalArgumentException e) {
      assertEquals("Recursion during variable replacement (${var2}).", e.getMessage());
    }
  }
}
