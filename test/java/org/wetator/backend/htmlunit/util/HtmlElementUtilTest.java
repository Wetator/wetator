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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitButton;
import org.wetator.backend.htmlunit.control.HtmlUnitImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.wetator.backend.htmlunit.control.HtmlUnitInputFile;
import org.wetator.backend.htmlunit.control.HtmlUnitInputImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputPassword;
import org.wetator.backend.htmlunit.control.HtmlUnitInputRadioButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputReset;
import org.wetator.backend.htmlunit.control.HtmlUnitInputSubmit;
import org.wetator.backend.htmlunit.control.HtmlUnitInputText;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.control.HtmlUnitOptionGroup;
import org.wetator.backend.htmlunit.control.HtmlUnitSelect;
import org.wetator.backend.htmlunit.control.HtmlUnitTextArea;
import org.wetator.backend.htmlunit.control.HtmlUnitUnspecificControl;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author rbri
 */
public class HtmlElementUtilTest {

  @Test
  public void getDescribingTextFor_HtmlAnchor() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_FormatedText() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html'>A<font>n</font>chor<b>Text</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html' name='AnchorName'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText' (name='AnchorName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    assertEquals("[HtmlAnchor 'AnchorText' (name='AnchorName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Image() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html'><img src='wet.src'></a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'image: wet.src']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    assertEquals("[HtmlAnchor 'image: wet.src']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_ImageAndText() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html'><img src='wet.src'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'image: wet.src' 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    assertEquals("[HtmlAnchor 'image: wet.src' 'AnchorText']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html' id='AnchorId'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html' name='AnchorName' id='AnchorId'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId') (name='AnchorName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId') (name='AnchorName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlBody() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "some text"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlBody tmpBody = (HtmlBody) tmpHtmlPage.getBody();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpBody).getDescribingText();
    assertEquals("[HtmlBody]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlBody(tmpBody);
    assertEquals("[HtmlBody]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlBody_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body id='tx'>"
        + "some text"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlBody tmpBody = (HtmlBody) tmpHtmlPage.getBody();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpBody).getDescribingText();
    assertEquals("[HtmlBody (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlBody(tmpBody);
    assertEquals("[HtmlBody (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button'>TestButton</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'TestButton']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_Value() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button' value='TestButton'/>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'TestButton']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_PlainText() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button'>TestButton</b></button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'TestButton']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_FormatedText() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button'>T<font>e</font>st<b>Button</b></button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'TestButton']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_Image() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button'><img src='wet.src'></button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'image: wet.src']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'image: wet.src']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_ImageAndText() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button'><img src='wet.src'>Text</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'image: wet.src' 'Text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'image: wet.src' 'Text']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button' name='ButtonName'>Text</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'Text' (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'Text' (name='ButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button' id='ButtonId'>Text</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'Text' (id='ButtonId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'Text' (id='ButtonId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button' id='ButtonId' name='ButtonName'>Text</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    assertEquals("[HtmlButton 'Text' (id='ButtonId') (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    assertEquals("[HtmlButton 'Text' (id='ButtonId') (name='ButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButtonInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='button' value='Button'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    assertEquals("[HtmlButtonInput 'Button']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    assertEquals("[HtmlButtonInput 'Button']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButtonInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='ButtonName' type='button' value='Button'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    assertEquals("[HtmlButtonInput 'Button' (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    assertEquals("[HtmlButtonInput 'Button' (name='ButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButtonInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' type='button' value='Button'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    assertEquals("[HtmlButtonInput 'Button' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    assertEquals("[HtmlButtonInput 'Button' (id='sb')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButtonInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' name='ButtonName' type='button' value='Button'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    assertEquals("[HtmlButtonInput 'Button' (id='sb') (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    assertEquals("[HtmlButtonInput 'Button' (id='sb') (name='ButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlCheckBoxInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='checkbox' value='CheckBoxValue'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    assertEquals("[HtmlCheckBoxInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    assertEquals("[HtmlCheckBoxInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlCheckBoxInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='checkbox' value='CheckBoxValue' name='CheckBoxName'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    assertEquals("[HtmlCheckBoxInput (name='CheckBoxName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    assertEquals("[HtmlCheckBoxInput (name='CheckBoxName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlCheckBoxInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='checkbox' value='CheckBoxValue' id='CheckBoxId'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    assertEquals("[HtmlCheckBoxInput (id='CheckBoxId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    assertEquals("[HtmlCheckBoxInput (id='CheckBoxId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlCheckBoxInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='checkbox' value='CheckBoxValue' name='CheckBoxName' id='CheckBoxId'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    assertEquals("[HtmlCheckBoxInput (id='CheckBoxId') (name='CheckBoxName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    assertEquals("[HtmlCheckBoxInput (id='CheckBoxId') (name='CheckBoxName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlCheckBoxInput_ByLabel() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='LabelId'>"
        + "<input type='checkbox' value='CheckBoxValue' style='display: none;'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild().getFirstChild();

    final HtmlUnitInputCheckBox tmpHtmlUnitInputCheckBox = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput);
    tmpHtmlUnitInputCheckBox.setHtmlLabel((HtmlLabel) tmpForm.getFirstChild());

    final String tmpResult = tmpHtmlUnitInputCheckBox.getDescribingText();
    assertEquals("[HtmlCheckBoxInput] by [HtmlLabel 'unchecked' (id='LabelId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlFileInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='file'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    assertEquals("[HtmlFileInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    assertEquals("[HtmlFileInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlFileInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='file' name='FileInputName'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    assertEquals("[HtmlFileInput (name='FileInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    assertEquals("[HtmlFileInput (name='FileInputName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlFileInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='file' id='FileInputId'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    assertEquals("[HtmlFileInput (id='FileInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    assertEquals("[HtmlFileInput (id='FileInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlFileInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='file' name='FileInputName' id='FileInputId'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    assertEquals("[HtmlFileInput (id='FileInputId') (name='FileInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    assertEquals("[HtmlFileInput (id='FileInputId') (name='FileInputName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlHiddenInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='hidden' value='Hidden'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    assertEquals("[HtmlHiddenInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    assertEquals("[HtmlHiddenInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlHiddenInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='HiddenName' type='hidden' value='Hidden'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    assertEquals("[HtmlHiddenInput (name='HiddenName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    assertEquals("[HtmlHiddenInput (name='HiddenName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlHiddenInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='hidden' value='Hidden'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    assertEquals("[HtmlHiddenInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    assertEquals("[HtmlHiddenInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlHiddenInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' name='HiddenName' type='hidden' value='Hidden'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    assertEquals("[HtmlHiddenInput (id='tx') (name='HiddenName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    assertEquals("[HtmlHiddenInput (id='tx') (name='HiddenName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    assertEquals("[HtmlImage 'wet.png']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    assertEquals("[HtmlImage 'wet.png']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png' name='ImageName'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    assertEquals("[HtmlImage 'wet.png' (name='ImageName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    assertEquals("[HtmlImage 'wet.png' (name='ImageName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png' id='ImageId'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    assertEquals("[HtmlImage 'wet.png' (id='ImageId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    assertEquals("[HtmlImage 'wet.png' (id='ImageId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png' name='ImageName' id='ImageId'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    assertEquals("[HtmlImage 'wet.png' (id='ImageId') (name='ImageName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    assertEquals("[HtmlImage 'wet.png' (id='ImageId') (name='ImageName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImageInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='Image' value='ImageInput' src='sample.src'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    assertEquals("[HtmlImageInput 'ImageInput' (src='http://www.wetator.org/sample.src')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    assertEquals("[HtmlImageInput 'ImageInput' (src='http://www.wetator.org/sample.src')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImageInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='Image' name='ImageInputName' value='ImageInput' src='sample.src'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    assertEquals("[HtmlImageInput 'ImageInput' (src='http://www.wetator.org/sample.src') (name='ImageInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    assertEquals("[HtmlImageInput 'ImageInput' (src='http://www.wetator.org/sample.src') (name='ImageInputName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImageInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='Image' id='ImageInputId' value='ImageInput' src='sample.src'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    assertEquals("[HtmlImageInput 'ImageInput' (src='http://www.wetator.org/sample.src') (id='ImageInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    assertEquals("[HtmlImageInput 'ImageInput' (src='http://www.wetator.org/sample.src') (id='ImageInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImageInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='Image' id='ImageInputId' name='ImageInputName' value='ImageInput' src='sample.src'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    assertEquals("[HtmlImageInput 'ImageInput' (src='http://www.wetator.org/sample.src') (id='ImageInputId') (name='ImageInputName')]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    assertEquals("[HtmlImageInput 'ImageInput' (src='http://www.wetator.org/sample.src') (id='ImageInputId') (name='ImageInputName')]",
        tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label for='TextInputId'>Label</label>"
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    assertEquals("[HtmlLabel 'Label' (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    assertEquals("[HtmlLabel 'Label' (for='TextInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label name='LabelName' for='TextInputId'>Label</label>"
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    assertEquals("[HtmlLabel 'Label' (name='LabelName') (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    assertEquals("[HtmlLabel 'Label' (name='LabelName') (for='TextInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='tx' for='TextInputId'>Label</label>"
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    assertEquals("[HtmlLabel 'Label' (id='tx') (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    assertEquals("[HtmlLabel 'Label' (id='tx') (for='TextInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='tx' name='LabelName' for='TextInputId'>Label</label>"
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    assertEquals("[HtmlLabel 'Label' (id='tx') (name='LabelName') (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    assertEquals("[HtmlLabel 'Label' (id='tx') (name='LabelName') (for='TextInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_withChild() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label>Label "
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    assertEquals("[HtmlLabel 'Label Text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    assertEquals("[HtmlLabel 'Label Text']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_withChild_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label name='LabelName'>Label "
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    assertEquals("[HtmlLabel 'Label Text' (name='LabelName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    assertEquals("[HtmlLabel 'Label Text' (name='LabelName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_withChild_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='tx'>Label "
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    assertEquals("[HtmlLabel 'Label Text' (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    assertEquals("[HtmlLabel 'Label Text' (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_withChild_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='tx' name='LabelName'>Label "
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    assertEquals("[HtmlLabel 'Label Text' (id='tx') (name='LabelName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    assertEquals("[HtmlLabel 'Label Text' (id='tx') (name='LabelName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOption() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<option>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    assertEquals("[HtmlOption 'Option1' part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    assertEquals("[HtmlOption 'Option1' part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOption_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<option name='optionName'>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    assertEquals("[HtmlOption 'Option1' (name='optionName') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    assertEquals("[HtmlOption 'Option1' (name='optionName') part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOption_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<option id='optionId'>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    assertEquals("[HtmlOption 'Option1' (id='optionId') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    assertEquals("[HtmlOption 'Option1' (id='optionId') part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOption_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<option name='optionName' id='optionId'>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    assertEquals("[HtmlOption 'Option1' (id='optionId') (name='optionName') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    assertEquals("[HtmlOption 'Option1' (id='optionId') (name='optionName') part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOptionGroup() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<optgroup label='optGroupLabel'>"
        + "<option>Option1</option>"
        + "</optgroup>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    assertEquals("[HtmlOptionGroup 'optGroupLabel' part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals("[HtmlOptionGroup 'optGroupLabel' part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOptionGroup_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<optgroup label='optGroupLabel' name='optionName'>"
        + "<option>Option1</option>"
        + "</optgroup>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    assertEquals("[HtmlOptionGroup 'optGroupLabel' (name='optionName') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals("[HtmlOptionGroup 'optGroupLabel' (name='optionName') part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOptionGroup_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<optgroup label='optGroupLabel' id='optionId'>"
        + "<option>Option1</option>"
        + "</optgroup>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOptionGroup_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<optgroup label='optGroupLabel' name='optionName' id='optionId'>"
        + "<option>Option1</option>"
        + "</optgroup>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') (name='optionName') part of [HtmlSelect]]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') (name='optionName') part of [HtmlSelect]]",
        tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    assertEquals("[HtmlParagraph 'paragraph text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    assertEquals("[HtmlParagraph 'paragraph text']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p name='ParagraphName'>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    assertEquals("[HtmlParagraph 'paragraph text' (name='ParagraphName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    assertEquals("[HtmlParagraph 'paragraph text' (name='ParagraphName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='para'>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    assertEquals("[HtmlParagraph 'paragraph text' (id='para')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    assertEquals("[HtmlParagraph 'paragraph text' (id='para')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='para' name='ParagraphName'>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    assertEquals("[HtmlParagraph 'paragraph text' (id='para') (name='ParagraphName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    assertEquals("[HtmlParagraph 'paragraph text' (id='para') (name='ParagraphName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlPasswordInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='password' value='Password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    assertEquals("[HtmlPasswordInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    assertEquals("[HtmlPasswordInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlPasswordInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='PasswordName' type='password' value='Password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    assertEquals("[HtmlPasswordInput (name='PasswordName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    assertEquals("[HtmlPasswordInput (name='PasswordName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlPasswordInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='password' value='Password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    assertEquals("[HtmlPasswordInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    assertEquals("[HtmlPasswordInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlPasswordInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' name='PasswordName' type='password' value='Password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    assertEquals("[HtmlPasswordInput (id='tx') (name='PasswordName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    assertEquals("[HtmlPasswordInput (id='tx') (name='PasswordName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlRadioButtonInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='radio' value='RadioButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    assertEquals("[HtmlRadioButtonInput 'RadioButton']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlRadioButtonInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='radio' value='RadioButton' name='RadioButtonName'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (name='RadioButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (name='RadioButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlRadioButtonInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='radio' value='RadioButton' id='RadioButtonId'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlRadioButtonInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='radio' value='RadioButton' name='RadioButtonName' id='RadioButtonId'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId') (name='RadioButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId') (name='RadioButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlRadioButtonInput_ByLabel() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='LabelId'>"
        + "<input type='radio' value='RadioButton' style='display: none;'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild().getFirstChild();

    final HtmlUnitInputRadioButton tmpHtmlUnitInputRadioButton = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput);
    tmpHtmlUnitInputRadioButton.setHtmlLabel((HtmlLabel) tmpForm.getFirstChild());

    final String tmpResult = tmpHtmlUnitInputRadioButton.getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton'] by [HtmlLabel 'unchecked' (id='LabelId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlResetInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='reset' value='ResetButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    assertEquals("[HtmlResetInput 'ResetButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    assertEquals("[HtmlResetInput 'ResetButton']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlResetInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='ResetButtonName' type='reset' value='ResetButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    assertEquals("[HtmlResetInput 'ResetButton' (name='ResetButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    assertEquals("[HtmlResetInput 'ResetButton' (name='ResetButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlResetInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' type='reset' value='ResetButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    assertEquals("[HtmlResetInput 'ResetButton' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    assertEquals("[HtmlResetInput 'ResetButton' (id='sb')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlResetInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' name='ResetButtonName' type='reset' value='ResetButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    assertEquals("[HtmlResetInput 'ResetButton' (id='sb') (name='ResetButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    assertEquals("[HtmlResetInput 'ResetButton' (id='sb') (name='ResetButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSelect() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<option>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    assertEquals("[HtmlSelect]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    assertEquals("[HtmlSelect]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSelect_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select name='SelectName'>"
        + "<option>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    assertEquals("[HtmlSelect (name='SelectName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    assertEquals("[HtmlSelect (name='SelectName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSelect_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='SelectId'>"
        + "<option>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    assertEquals("[HtmlSelect (id='SelectId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    assertEquals("[HtmlSelect (id='SelectId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSelect_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='SelectId' name='SelectName'>"
        + "<option>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    assertEquals("[HtmlSelect (id='SelectId') (name='SelectName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    assertEquals("[HtmlSelect (id='SelectId') (name='SelectName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span class='abc'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan 'some text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan 'some text']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Empty() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span style='abc'></span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan '']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan '']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span name='Spanname'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan 'some text' (name='Spanname')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan 'some text' (name='Spanname')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='SpanId'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan 'some text' (id='SpanId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan 'some text' (id='SpanId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='SpanId' name='Spanname'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan 'some text' (id='SpanId') (name='Spanname')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan 'some text' (id='SpanId') (name='Spanname')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSubmitInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='submit' value='SubmitButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    assertEquals("[HtmlSubmitInput 'SubmitButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    assertEquals("[HtmlSubmitInput 'SubmitButton']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSubmitInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='SubmitButtonName' type='submit' value='SubmitButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    assertEquals("[HtmlSubmitInput 'SubmitButton' (name='SubmitButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    assertEquals("[HtmlSubmitInput 'SubmitButton' (name='SubmitButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSubmitInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' type='submit' value='SubmitButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSubmitInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' name='SubmitButtonName' type='submit' value='SubmitButton'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb') (name='SubmitButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb') (name='SubmitButtonName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='text' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    assertEquals("[HtmlTextInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    assertEquals("[HtmlTextInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='TextName' type='text' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    assertEquals("[HtmlTextInput (name='TextName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    assertEquals("[HtmlTextInput (name='TextName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='text' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    assertEquals("[HtmlTextInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    assertEquals("[HtmlTextInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' name='TextName' type='text' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    assertEquals("[HtmlTextInput (id='tx') (name='TextName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    assertEquals("[HtmlTextInput (id='tx') (name='TextName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextArea() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<textarea></textarea>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    assertEquals("[HtmlTextArea]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    assertEquals("[HtmlTextArea]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextArea_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<textarea name='TextAreaName'></textarea>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    assertEquals("[HtmlTextArea (name='TextAreaName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    assertEquals("[HtmlTextArea (name='TextAreaName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextArea_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<textarea id='TextAreaId'></textarea>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    assertEquals("[HtmlTextArea (id='TextAreaId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    assertEquals("[HtmlTextArea (id='TextAreaId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextArea_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<textarea name='TextAreaName' id='TextAreaId'></textarea>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    assertEquals("[HtmlTextArea (id='TextAreaId') (name='TextAreaName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    assertEquals("[HtmlTextArea (id='TextAreaId') (name='TextAreaName')]", tmpResult);
  }

  @Test
  public void isBlock() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='div1'>"
        + "<span id='span1'>Wetator</span>"
        + "<span id='span2' style='display: block'>Wetator</span>"
        + "<span id='span3' style='display: inline-block'>Wetator</span>"
        + "</div>"
        + "<ul id='ul1'>"
        + "  <li id='li1'>Wetator</li>"
        + "  <li id='li2' style='display: block'>Smart</li>"
        + "  <li id='li3' style='display: inline-block'>Web</li>"
        + "  <li id='li4' style='display: inline'>Testing</li>"
        + "</ul>"
        + "<select id='select1'>"
        + "  <option id='option1'>Wetator</option>"
        + "  <option id='option2' style='display: block'>Smart</option>"
        + "  <option id='option3' style='display: inline-block'>Web</option>"
        + "  <option id='option4' style='display: inline'>Testing</option>"
        + "</select>"
        + "<img id='img1' src='smiley.gif'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomElement tmpElement = tmpHtmlPage.getElementById("div1");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));

    tmpElement = tmpHtmlPage.getElementById("span1");
    assertFalse(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("span2");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("span3");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));

    tmpElement = tmpHtmlPage.getElementById("li1");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("li2");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("li3");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("li4");
    assertFalse(HtmlElementUtil.isBlock(tmpElement));

    // options are always block, see inline comment in
    // org.wetator.backend.htmlunit.util.HtmlElementUtil.isBlock(DomNode)
    tmpElement = tmpHtmlPage.getElementById("option1");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("option2");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("option3");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("option4");
    assertTrue(HtmlElementUtil.isBlock(tmpElement));

    tmpElement = tmpHtmlPage.getElementById("img1");
    assertFalse(HtmlElementUtil.isBlock(tmpElement));
  }
}
