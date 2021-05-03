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


package org.wetator.backend.htmlunit.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public class DescribingTextBuilderTest {

  private HtmlElement element;
  private HtmlElement elementWithId;
  private HtmlElement elementWithName;
  private HtmlElement elementWithIdAndName;

  @Before
  public void prepareElements() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div></div>"
        + "<div id='testId'></div>"
        + "<div name='testName'></div>"
        + "<div id='testId' name='testName'></div>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();
    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();
    element = tmpHtmlElements.next();
    elementWithId = tmpHtmlElements.next();
    elementWithName = tmpHtmlElements.next();
    elementWithIdAndName = tmpHtmlElements.next();
  }

  @Test
  public void createDefault() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createDefault(element).build());
  }

  @Test
  public void createDefaultWithId() {
    assertEquals("[HtmlDivision (id='testId')]", DescribingTextBuilder.createDefault(elementWithId).build());
  }

  @Test
  public void createDefaultWithName() {
    assertEquals("[HtmlDivision (name='testName')]", DescribingTextBuilder.createDefault(elementWithName).build());
  }

  @Test
  public void createDefaultWithIdAndName() {
    assertEquals("[HtmlDivision (id='testId') (name='testName')]",
        DescribingTextBuilder.createDefault(elementWithIdAndName).build());
  }

  @Test
  public void createCustom() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(elementWithIdAndName).build());
  }

  @Test
  public void addIdWithoutId() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addId().build());
  }

  @Test
  public void addId() {
    assertEquals("[HtmlDivision (id='testId')]",
        DescribingTextBuilder.createCustom(elementWithIdAndName).addId().build());
  }

  @Test
  public void addNameWithoutName() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addName().build());
  }

  @Test
  public void addName() {
    assertEquals("[HtmlDivision (name='testName')]",
        DescribingTextBuilder.createCustom(elementWithIdAndName).addName().build());
  }

  @Test
  public void addAttributeNameNull() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addAttribute(null, "val").build());
  }

  @Test
  public void addAttributeNameEmpty() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addAttribute("", "val").build());
  }

  @Test
  public void addAttributeNameBlank() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addAttribute(" ", "val").build());
  }

  @Test
  public void addAttributeValueNull() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addAttribute("att", null).build());
  }

  @Test
  public void addAttributeValueEmpty() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addAttribute("att", "").build());
  }

  @Test
  public void addAttributeValueBlank() {
    assertEquals("[HtmlDivision (att=' ')]",
        DescribingTextBuilder.createCustom(element).addAttribute("att", " ").build());
  }

  @Test
  public void addAttribute() {
    assertEquals("[HtmlDivision (att='val')]",
        DescribingTextBuilder.createCustom(element).addAttribute("att", "val").build());
  }

  @Test
  public void addTextNull() {
    assertEquals("[HtmlDivision 'null']", DescribingTextBuilder.createCustom(element).addText(null).build());
  }

  @Test
  public void addTextEmpty() {
    assertEquals("[HtmlDivision '']", DescribingTextBuilder.createCustom(element).addText("").build());
  }

  @Test
  public void addTextBlank() {
    assertEquals("[HtmlDivision ' ']", DescribingTextBuilder.createCustom(element).addText(" ").build());
  }

  @Test
  public void addText() {
    assertEquals("[HtmlDivision 'text']", DescribingTextBuilder.createCustom(element).addText("text").build());
  }

  @Test
  public void addPlainNull() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addPlain(null).build());
  }

  @Test
  public void addPlainEmpty() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addPlain("").build());
  }

  @Test
  public void addPlainBlank() {
    assertEquals("[HtmlDivision]", DescribingTextBuilder.createCustom(element).addPlain(" ").build());
  }

  @Test
  public void addPlain() {
    assertEquals("[HtmlDivision text]", DescribingTextBuilder.createCustom(element).addPlain("text").build());
  }
}
