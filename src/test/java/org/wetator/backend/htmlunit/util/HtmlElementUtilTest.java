/*
 * Copyright (c) 2008-2025 wetator.org
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

import java.io.IOException;
import java.util.Iterator;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlBody;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlButtonInput;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlEmailInput;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlHiddenInput;
import org.htmlunit.html.HtmlImage;
import org.htmlunit.html.HtmlImageInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlNumberInput;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlOptionGroup;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlParagraph;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlResetInput;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSpan;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTelInput;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.html.HtmlTextInput;
import org.htmlunit.html.HtmlUrlInput;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitButton;
import org.wetator.backend.htmlunit.control.HtmlUnitImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.wetator.backend.htmlunit.control.HtmlUnitInputEmail;
import org.wetator.backend.htmlunit.control.HtmlUnitInputFile;
import org.wetator.backend.htmlunit.control.HtmlUnitInputImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputNumber;
import org.wetator.backend.htmlunit.control.HtmlUnitInputPassword;
import org.wetator.backend.htmlunit.control.HtmlUnitInputRadioButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputReset;
import org.wetator.backend.htmlunit.control.HtmlUnitInputSubmit;
import org.wetator.backend.htmlunit.control.HtmlUnitInputTel;
import org.wetator.backend.htmlunit.control.HtmlUnitInputText;
import org.wetator.backend.htmlunit.control.HtmlUnitInputUrl;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.control.HtmlUnitOptionGroup;
import org.wetator.backend.htmlunit.control.HtmlUnitSelect;
import org.wetator.backend.htmlunit.control.HtmlUnitTextArea;
import org.wetator.backend.htmlunit.control.HtmlUnitUnspecificControl;

/**
 * @author rbri
 */
public class HtmlElementUtilTest {
  protected WebClient webClient;

  /**
   * Creates a Wetator configuration.
   */
  @Before
  public void createWebClient() {
    webClient = new WebClient(BrowserVersion.FIREFOX_ESR);
  }

  /**
   * Closes the WebClient.
   */
  @After
  public void closeWebClient() {
    webClient.close();
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    Assert.assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    Assert.assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_FormatedText() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html'>A<font>n</font>chor<b>Text</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    Assert.assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    Assert.assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html' name='AnchorName'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    Assert.assertEquals("[HtmlAnchor 'AnchorText' (name='AnchorName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    Assert.assertEquals("[HtmlAnchor 'AnchorText' (name='AnchorName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Image() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html'><img src='wet.src'></a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    Assert.assertEquals("[HtmlAnchor 'image: wet.src']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    Assert.assertEquals("[HtmlAnchor 'image: wet.src']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_ImageAndText() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html'><img src='wet.src'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    Assert.assertEquals("[HtmlAnchor 'image: wet.src' 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    Assert.assertEquals("[HtmlAnchor 'image: wet.src' 'AnchorText']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html' id='AnchorId'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    Assert.assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    Assert.assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html' id='AnchorId' data-testid='dtid'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    Assert.assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    Assert.assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId') (data-testid='dtid')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlAnchor_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='wet.html' name='AnchorName' id='AnchorId'>AnchorText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitAnchor(tmpHtmlAnchor).getDescribingText();
    Assert.assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId') (name='AnchorName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpHtmlAnchor);
    Assert.assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId') (name='AnchorName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlBody() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "some text"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlBody tmpBody = tmpHtmlPage.getBody();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpBody).getDescribingText();
    Assert.assertEquals("[HtmlBody]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlBody(tmpBody);
    Assert.assertEquals("[HtmlBody]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlBody_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body id='tx'>"
        + "some text"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlBody tmpBody = tmpHtmlPage.getBody();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpBody).getDescribingText();
    Assert.assertEquals("[HtmlBody (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlBody(tmpBody);
    Assert.assertEquals("[HtmlBody (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlBody_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body id='tx' data-testid='dtid'>"
        + "some text"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlBody tmpBody = tmpHtmlPage.getBody();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpBody).getDescribingText();
    Assert.assertEquals("[HtmlBody (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlBody(tmpBody);
    Assert.assertEquals("[HtmlBody (id='tx') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'TestButton']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'TestButton']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'TestButton']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'TestButton']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'image: wet.src']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'image: wet.src']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'image: wet.src' 'Text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'image: wet.src' 'Text']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'Text' (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'Text' (name='ButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'Text' (id='ButtonId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'Text' (id='ButtonId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButton_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button type='button' id='ButtonId' data-testid='dtid'>Text</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'Text' (id='ButtonId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'Text' (id='ButtonId') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButton tmpHtmlButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitButton(tmpHtmlButton).getDescribingText();
    Assert.assertEquals("[HtmlButton 'Text' (id='ButtonId') (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpHtmlButton);
    Assert.assertEquals("[HtmlButton 'Text' (id='ButtonId') (name='ButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlButtonInput 'Button']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    Assert.assertEquals("[HtmlButtonInput 'Button']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlButtonInput 'Button' (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    Assert.assertEquals("[HtmlButtonInput 'Button' (name='ButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlButtonInput 'Button' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    Assert.assertEquals("[HtmlButtonInput 'Button' (id='sb')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlButtonInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' type='button' value='Button' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlButtonInput 'Button' (id='sb') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    Assert.assertEquals("[HtmlButtonInput 'Button' (id='sb') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputButton(tmpHtmlButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlButtonInput 'Button' (id='sb') (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    Assert.assertEquals("[HtmlButtonInput 'Button' (id='sb') (name='ButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    Assert.assertEquals("[HtmlCheckBoxInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    Assert.assertEquals("[HtmlCheckBoxInput]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    Assert.assertEquals("[HtmlCheckBoxInput (name='CheckBoxName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    Assert.assertEquals("[HtmlCheckBoxInput (name='CheckBoxName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    Assert.assertEquals("[HtmlCheckBoxInput (id='CheckBoxId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    Assert.assertEquals("[HtmlCheckBoxInput (id='CheckBoxId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlCheckBoxInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='checkbox' value='CheckBoxValue' id='CheckBoxId' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    Assert.assertEquals("[HtmlCheckBoxInput (id='CheckBoxId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    Assert.assertEquals("[HtmlCheckBoxInput (id='CheckBoxId') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput).getDescribingText();
    Assert.assertEquals("[HtmlCheckBoxInput (id='CheckBoxId') (name='CheckBoxName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput);
    Assert.assertEquals("[HtmlCheckBoxInput (id='CheckBoxId') (name='CheckBoxName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild().getFirstChild();

    final HtmlUnitInputCheckBox tmpHtmlUnitInputCheckBox = new HtmlUnitInputCheckBox(tmpHtmlCheckBoxInput);
    tmpHtmlUnitInputCheckBox.setHtmlLabel((HtmlLabel) tmpForm.getFirstChild());

    final String tmpResult = tmpHtmlUnitInputCheckBox.getDescribingText();
    Assert.assertEquals("[HtmlCheckBoxInput] by [HtmlLabel 'unchecked' (id='LabelId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlNumberInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='number' value='42'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlNumberInput tmpHtmlNumberInput = (HtmlNumberInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputNumber(tmpHtmlNumberInput).getDescribingText();
    Assert.assertEquals("[HtmlNumberInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlNumberInput(tmpHtmlNumberInput);
    Assert.assertEquals("[HtmlNumberInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlNumberInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='NumberName' type='number' value='42'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlNumberInput tmpHtmlNumberInput = (HtmlNumberInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputNumber(tmpHtmlNumberInput).getDescribingText();
    Assert.assertEquals("[HtmlNumberInput (name='NumberName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlNumberInput(tmpHtmlNumberInput);
    Assert.assertEquals("[HtmlNumberInput (name='NumberName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlNumberInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='number' value='42'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlNumberInput tmpHtmlNumberInput = (HtmlNumberInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputNumber(tmpHtmlNumberInput).getDescribingText();
    Assert.assertEquals("[HtmlNumberInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlNumberInput(tmpHtmlNumberInput);
    Assert.assertEquals("[HtmlNumberInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlNumberInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='number' value='42' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlNumberInput tmpHtmlNumberInput = (HtmlNumberInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputNumber(tmpHtmlNumberInput).getDescribingText();
    Assert.assertEquals("[HtmlNumberInput (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlNumberInput(tmpHtmlNumberInput);
    Assert.assertEquals("[HtmlNumberInput (id='tx') (data-testid='dtid')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlNumberInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' name='NumberName' type='number' value='42'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlNumberInput tmpHtmlNumberInput = (HtmlNumberInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputNumber(tmpHtmlNumberInput).getDescribingText();
    Assert.assertEquals("[HtmlNumberInput (id='tx') (name='NumberName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlNumberInput(tmpHtmlNumberInput);
    Assert.assertEquals("[HtmlNumberInput (id='tx') (name='NumberName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlEmailInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='email' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlEmailInput tmpHtmlEmailInput = (HtmlEmailInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputEmail(tmpHtmlEmailInput).getDescribingText();
    Assert.assertEquals("[HtmlEmailInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlEmailInput(tmpHtmlEmailInput);
    Assert.assertEquals("[HtmlEmailInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlEmailInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='EmailName' type='email' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlEmailInput tmpHtmlEmailInput = (HtmlEmailInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputEmail(tmpHtmlEmailInput).getDescribingText();
    Assert.assertEquals("[HtmlEmailInput (name='EmailName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlEmailInput(tmpHtmlEmailInput);
    Assert.assertEquals("[HtmlEmailInput (name='EmailName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlEmailInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='email' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlEmailInput tmpHtmlEmailInput = (HtmlEmailInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputEmail(tmpHtmlEmailInput).getDescribingText();
    Assert.assertEquals("[HtmlEmailInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlEmailInput(tmpHtmlEmailInput);
    Assert.assertEquals("[HtmlEmailInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlEmailInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='email' value='admin@wetator.org' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlEmailInput tmpHtmlEmailInput = (HtmlEmailInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputEmail(tmpHtmlEmailInput).getDescribingText();
    Assert.assertEquals("[HtmlEmailInput (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlEmailInput(tmpHtmlEmailInput);
    Assert.assertEquals("[HtmlEmailInput (id='tx') (data-testid='dtid')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlEmailInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' name='EmailName' type='email' value='@admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlEmailInput tmpHtmlEmailInput = (HtmlEmailInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputEmail(tmpHtmlEmailInput).getDescribingText();
    Assert.assertEquals("[HtmlEmailInput (id='tx') (name='EmailName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlEmailInput(tmpHtmlEmailInput);
    Assert.assertEquals("[HtmlEmailInput (id='tx') (name='EmailName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTelInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='tel' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTelInput tmpHtmlTelInput = (HtmlTelInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputTel(tmpHtmlTelInput).getDescribingText();
    Assert.assertEquals("[HtmlTelInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTelInput(tmpHtmlTelInput);
    Assert.assertEquals("[HtmlTelInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTelInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='TelName' type='tel' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTelInput tmpHtmlTelInput = (HtmlTelInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputTel(tmpHtmlTelInput).getDescribingText();
    Assert.assertEquals("[HtmlTelInput (name='TelName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTelInput(tmpHtmlTelInput);
    Assert.assertEquals("[HtmlTelInput (name='TelName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTelInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='tel' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTelInput tmpHtmlTelInput = (HtmlTelInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputTel(tmpHtmlTelInput).getDescribingText();
    Assert.assertEquals("[HtmlTelInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTelInput(tmpHtmlTelInput);
    Assert.assertEquals("[HtmlTelInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTelInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='tel' value='admin@wetator.org' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTelInput tmpHtmlTelInput = (HtmlTelInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputTel(tmpHtmlTelInput).getDescribingText();
    Assert.assertEquals("[HtmlTelInput (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTelInput(tmpHtmlTelInput);
    Assert.assertEquals("[HtmlTelInput (id='tx') (data-testid='dtid')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTelInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' name='TelName' type='tel' value='@admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTelInput tmpHtmlTelInput = (HtmlTelInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputTel(tmpHtmlTelInput).getDescribingText();
    Assert.assertEquals("[HtmlTelInput (id='tx') (name='TelName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTelInput(tmpHtmlTelInput);
    Assert.assertEquals("[HtmlTelInput (id='tx') (name='TelName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlUrlInput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='url' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlUrlInput tmpHtmlUrlInput = (HtmlUrlInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputUrl(tmpHtmlUrlInput).getDescribingText();
    Assert.assertEquals("[HtmlUrlInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlUrlInput(tmpHtmlUrlInput);
    Assert.assertEquals("[HtmlUrlInput]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlUrlInput_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input name='UrlName' type='url' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlUrlInput tmpHtmlUrlInput = (HtmlUrlInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputUrl(tmpHtmlUrlInput).getDescribingText();
    Assert.assertEquals("[HtmlUrlInput (name='UrlName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlUrlInput(tmpHtmlUrlInput);
    Assert.assertEquals("[HtmlUrlInput (name='UrlName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlUrlInput_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='url' value='admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlUrlInput tmpHtmlUrlInput = (HtmlUrlInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputUrl(tmpHtmlUrlInput).getDescribingText();
    Assert.assertEquals("[HtmlUrlInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlUrlInput(tmpHtmlUrlInput);
    Assert.assertEquals("[HtmlUrlInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlUrlInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='url' value='admin@wetator.org' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlUrlInput tmpHtmlUrlInput = (HtmlUrlInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputUrl(tmpHtmlUrlInput).getDescribingText();
    Assert.assertEquals("[HtmlUrlInput (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlUrlInput(tmpHtmlUrlInput);
    Assert.assertEquals("[HtmlUrlInput (id='tx') (data-testid='dtid')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlUrlInput_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' name='UrlName' type='url' value='@admin@wetator.org'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlUrlInput tmpHtmlUrlInput = (HtmlUrlInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputUrl(tmpHtmlUrlInput).getDescribingText();
    Assert.assertEquals("[HtmlUrlInput (id='tx') (name='UrlName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlUrlInput(tmpHtmlUrlInput);
    Assert.assertEquals("[HtmlUrlInput (id='tx') (name='UrlName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    Assert.assertEquals("[HtmlFileInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    Assert.assertEquals("[HtmlFileInput]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    Assert.assertEquals("[HtmlFileInput (name='FileInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    Assert.assertEquals("[HtmlFileInput (name='FileInputName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    Assert.assertEquals("[HtmlFileInput (id='FileInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    Assert.assertEquals("[HtmlFileInput (id='FileInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlFileInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='file' id='FileInputId' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    Assert.assertEquals("[HtmlFileInput (id='FileInputId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    Assert.assertEquals("[HtmlFileInput (id='FileInputId') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputFile(tmpHtmlFileInput).getDescribingText();
    Assert.assertEquals("[HtmlFileInput (id='FileInputId') (name='FileInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpHtmlFileInput);
    Assert.assertEquals("[HtmlFileInput (id='FileInputId') (name='FileInputName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    Assert.assertEquals("[HtmlHiddenInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    Assert.assertEquals("[HtmlHiddenInput]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    Assert.assertEquals("[HtmlHiddenInput (name='HiddenName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    Assert.assertEquals("[HtmlHiddenInput (name='HiddenName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    Assert.assertEquals("[HtmlHiddenInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    Assert.assertEquals("[HtmlHiddenInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlHiddenInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='hidden' value='Hidden' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    Assert.assertEquals("[HtmlHiddenInput (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    Assert.assertEquals("[HtmlHiddenInput (id='tx') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlHiddenInput).getDescribingText();
    Assert.assertEquals("[HtmlHiddenInput (id='tx') (name='HiddenName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlHiddenInput(tmpHtmlHiddenInput);
    Assert.assertEquals("[HtmlHiddenInput (id='tx') (name='HiddenName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    Assert.assertEquals("[HtmlImage 'wet.png']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    Assert.assertEquals("[HtmlImage 'wet.png']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png' name='ImageName'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    Assert.assertEquals("[HtmlImage 'wet.png' (name='ImageName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    Assert.assertEquals("[HtmlImage 'wet.png' (name='ImageName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png' id='ImageId'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    Assert.assertEquals("[HtmlImage 'wet.png' (id='ImageId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    Assert.assertEquals("[HtmlImage 'wet.png' (id='ImageId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png' id='ImageId' data-testid='dtid'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    Assert.assertEquals("[HtmlImage 'wet.png' (id='ImageId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    Assert.assertEquals("[HtmlImage 'wet.png' (id='ImageId') (data-testid='dtid')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImage_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img src='wet.png' name='ImageName' id='ImageId'>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlImage tmpHtmlImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitImage(tmpHtmlImage).getDescribingText();
    Assert.assertEquals("[HtmlImage 'wet.png' (id='ImageId') (name='ImageName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpHtmlImage);
    Assert.assertEquals("[HtmlImage 'wet.png' (id='ImageId') (name='ImageName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (name='ImageInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (name='ImageInputName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlImageInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='Image' id='ImageInputId' value='ImageInput' src='sample.src' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId') (data-testid='dtid')]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId') (data-testid='dtid')]",
        tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputImage(tmpHtmlImageInput).getDescribingText();
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId') (name='ImageInputName')]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    Assert.assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId') (name='ImageInputName')]",
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label' (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label' (for='TextInputId')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label' (name='LabelName') (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label' (name='LabelName') (for='TextInputId')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label' (id='tx') (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label' (id='tx') (for='TextInputId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='tx' for='TextInputId' data-testid='dtid'>Label</label>"
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label' (id='tx') (data-testid='dtid') (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label' (id='tx') (data-testid='dtid') (for='TextInputId')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label' (id='tx') (name='LabelName') (for='TextInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label' (id='tx') (name='LabelName') (for='TextInputId')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label Text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label Text']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label Text' (name='LabelName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label Text' (name='LabelName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label Text' (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label Text' (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlLabel_withChild_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='tx' data-testid='dtid'>Label "
        + "<input type='text' id='TextInputId' value='Text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label Text' (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label Text' (id='tx') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlLabel tmpHtmlLabel = (HtmlLabel) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<>(tmpHtmlLabel).getDescribingText();
    Assert.assertEquals("[HtmlLabel 'Label Text' (id='tx') (name='LabelName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlLabel(tmpHtmlLabel);
    Assert.assertEquals("[HtmlLabel 'Label Text' (id='tx') (name='LabelName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    Assert.assertEquals("[HtmlOption 'Option1' part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    Assert.assertEquals("[HtmlOption 'Option1' part of [HtmlSelect]]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    Assert.assertEquals("[HtmlOption 'Option1' (name='optionName') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    Assert.assertEquals("[HtmlOption 'Option1' (name='optionName') part of [HtmlSelect]]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    Assert.assertEquals("[HtmlOption 'Option1' (id='optionId') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    Assert.assertEquals("[HtmlOption 'Option1' (id='optionId') part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOption_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<option id='optionId' data-testid='dtid'>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    Assert.assertEquals("[HtmlOption 'Option1' (id='optionId') (data-testid='dtid') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    Assert.assertEquals("[HtmlOption 'Option1' (id='optionId') (data-testid='dtid') part of [HtmlSelect]]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOption tmpHtmlOption = tmpHtmlSelect.getOptions().get(0);

    String tmpResult;
    tmpResult = new HtmlUnitOption(tmpHtmlOption).getDescribingText();
    Assert.assertEquals("[HtmlOption 'Option1' (id='optionId') (name='optionName') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    Assert.assertEquals("[HtmlOption 'Option1' (id='optionId') (name='optionName') part of [HtmlSelect]]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' part of [HtmlSelect]]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' (name='optionName') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' (name='optionName') part of [HtmlSelect]]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') part of [HtmlSelect]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') part of [HtmlSelect]]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlOptionGroup_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select>"
        + "<optgroup label='optGroupLabel' id='optionId' data-testid='dtid'>"
        + "<option>Option1</option>"
        + "</optgroup>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') (data-testid='dtid') part of [HtmlSelect]]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') (data-testid='dtid') part of [HtmlSelect]]",
        tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlSelect.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitOptionGroup(tmpHtmlOptionGroup).getDescribingText();
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') (name='optionName') part of [HtmlSelect]]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    Assert.assertEquals("[HtmlOptionGroup 'optGroupLabel' (id='optionId') (name='optionName') part of [HtmlSelect]]",
        tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    Assert.assertEquals("[HtmlParagraph 'paragraph text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    Assert.assertEquals("[HtmlParagraph 'paragraph text']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p name='ParagraphName'>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    Assert.assertEquals("[HtmlParagraph 'paragraph text' (name='ParagraphName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    Assert.assertEquals("[HtmlParagraph 'paragraph text' (name='ParagraphName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='para'>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    Assert.assertEquals("[HtmlParagraph 'paragraph text' (id='para')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    Assert.assertEquals("[HtmlParagraph 'paragraph text' (id='para')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='para' data-testid='dtid'>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    Assert.assertEquals("[HtmlParagraph 'paragraph text' (id='para') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    Assert.assertEquals("[HtmlParagraph 'paragraph text' (id='para') (data-testid='dtid')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlParagraph_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='para' name='ParagraphName'>paragraph text</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlParagraph).getDescribingText();
    Assert.assertEquals("[HtmlParagraph 'paragraph text' (id='para') (name='ParagraphName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    Assert.assertEquals("[HtmlParagraph 'paragraph text' (id='para') (name='ParagraphName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    Assert.assertEquals("[HtmlPasswordInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    Assert.assertEquals("[HtmlPasswordInput]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    Assert.assertEquals("[HtmlPasswordInput (name='PasswordName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    Assert.assertEquals("[HtmlPasswordInput (name='PasswordName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    Assert.assertEquals("[HtmlPasswordInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    Assert.assertEquals("[HtmlPasswordInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlPasswordInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='password' value='Password' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    Assert.assertEquals("[HtmlPasswordInput (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    Assert.assertEquals("[HtmlPasswordInput (id='tx') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputPassword(tmpHtmlPasswordInput).getDescribingText();
    Assert.assertEquals("[HtmlPasswordInput (id='tx') (name='PasswordName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    Assert.assertEquals("[HtmlPasswordInput (id='tx') (name='PasswordName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton' (name='RadioButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton' (name='RadioButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlRadioButtonInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='radio' value='RadioButton' id='RadioButtonId' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput).getDescribingText();
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId') (name='RadioButtonName')]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId') (name='RadioButtonName')]",
        tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild().getFirstChild();

    final HtmlUnitInputRadioButton tmpHtmlUnitInputRadioButton = new HtmlUnitInputRadioButton(tmpHtmlRadioButtonInput);
    tmpHtmlUnitInputRadioButton.setHtmlLabel((HtmlLabel) tmpForm.getFirstChild());

    final String tmpResult = tmpHtmlUnitInputRadioButton.getDescribingText();
    Assert.assertEquals("[HtmlRadioButtonInput 'RadioButton'] by [HtmlLabel 'unchecked' (id='LabelId')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    Assert.assertEquals("[HtmlResetInput 'ResetButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    Assert.assertEquals("[HtmlResetInput 'ResetButton']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    Assert.assertEquals("[HtmlResetInput 'ResetButton' (name='ResetButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    Assert.assertEquals("[HtmlResetInput 'ResetButton' (name='ResetButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    Assert.assertEquals("[HtmlResetInput 'ResetButton' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    Assert.assertEquals("[HtmlResetInput 'ResetButton' (id='sb')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlResetInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' type='reset' value='ResetButton' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    Assert.assertEquals("[HtmlResetInput 'ResetButton' (id='sb') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    Assert.assertEquals("[HtmlResetInput 'ResetButton' (id='sb') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputReset(tmpHtmlResetInput).getDescribingText();
    Assert.assertEquals("[HtmlResetInput 'ResetButton' (id='sb') (name='ResetButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    Assert.assertEquals("[HtmlResetInput 'ResetButton' (id='sb') (name='ResetButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    Assert.assertEquals("[HtmlSelect]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    Assert.assertEquals("[HtmlSelect]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    Assert.assertEquals("[HtmlSelect (name='SelectName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    Assert.assertEquals("[HtmlSelect (name='SelectName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    Assert.assertEquals("[HtmlSelect (id='SelectId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    Assert.assertEquals("[HtmlSelect (id='SelectId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSelect_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='SelectId' data-testid='dtid'>"
        + "<option>Option1</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    Assert.assertEquals("[HtmlSelect (id='SelectId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    Assert.assertEquals("[HtmlSelect (id='SelectId') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitSelect(tmpHtmlSelect).getDescribingText();
    Assert.assertEquals("[HtmlSelect (id='SelectId') (name='SelectName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    Assert.assertEquals("[HtmlSelect (id='SelectId') (name='SelectName')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span class='abc'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    Assert.assertEquals("[HtmlSpan 'some text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    Assert.assertEquals("[HtmlSpan 'some text']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Empty() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span style='abc'></span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    Assert.assertEquals("[HtmlSpan '']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    Assert.assertEquals("[HtmlSpan '']", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Name() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span name='Spanname'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    Assert.assertEquals("[HtmlSpan 'some text' (name='Spanname')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    Assert.assertEquals("[HtmlSpan 'some text' (name='Spanname')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='SpanId'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    Assert.assertEquals("[HtmlSpan 'some text' (id='SpanId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    Assert.assertEquals("[HtmlSpan 'some text' (id='SpanId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='SpanId' data-testid='dtid'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    Assert.assertEquals("[HtmlSpan 'some text' (id='SpanId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    Assert.assertEquals("[HtmlSpan 'some text' (id='SpanId') (data-testid='dtid')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSpan_Name_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='SpanId' name='Spanname'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlSpan).getDescribingText();
    Assert.assertEquals("[HtmlSpan 'some text' (id='SpanId') (name='Spanname')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    Assert.assertEquals("[HtmlSpan 'some text' (id='SpanId') (name='Spanname')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton']", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton' (name='SubmitButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton' (name='SubmitButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlSubmitInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='sb' type='submit' value='SubmitButton' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputSubmit(tmpHtmlSubmitInput).getDescribingText();
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb') (name='SubmitButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    Assert.assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb') (name='SubmitButtonName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    Assert.assertEquals("[HtmlTextInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    Assert.assertEquals("[HtmlTextInput]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    Assert.assertEquals("[HtmlTextInput (name='TextName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    Assert.assertEquals("[HtmlTextInput (name='TextName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    Assert.assertEquals("[HtmlTextInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    Assert.assertEquals("[HtmlTextInput (id='tx')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextInput_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='tx' type='text' value='Text' data-testid='dtid'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    Assert.assertEquals("[HtmlTextInput (id='tx') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    Assert.assertEquals("[HtmlTextInput (id='tx') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitInputText(tmpHtmlTextInput).getDescribingText();
    Assert.assertEquals("[HtmlTextInput (id='tx') (name='TextName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    Assert.assertEquals("[HtmlTextInput (id='tx') (name='TextName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    Assert.assertEquals("[HtmlTextArea]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    Assert.assertEquals("[HtmlTextArea]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    Assert.assertEquals("[HtmlTextArea (name='TextAreaName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    Assert.assertEquals("[HtmlTextArea (name='TextAreaName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    Assert.assertEquals("[HtmlTextArea (id='TextAreaId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    Assert.assertEquals("[HtmlTextArea (id='TextAreaId')]", tmpResult);
  }

  @Test
  public void getDescribingTextFor_HtmlTextArea_Id_DataTestid() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<textarea id='TextAreaId' data-testid='dtid'></textarea>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    Assert.assertEquals("[HtmlTextArea (id='TextAreaId') (data-testid='dtid')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    Assert.assertEquals("[HtmlTextArea (id='TextAreaId') (data-testid='dtid')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    final HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult;
    tmpResult = new HtmlUnitTextArea(tmpHtmlTextArea).getDescribingText();
    Assert.assertEquals("[HtmlTextArea (id='TextAreaId') (name='TextAreaName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    Assert.assertEquals("[HtmlTextArea (id='TextAreaId') (name='TextAreaName')]", tmpResult);
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    DomElement tmpElement = tmpHtmlPage.getElementById("div1");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));

    tmpElement = tmpHtmlPage.getElementById("span1");
    Assert.assertFalse(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("span2");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("span3");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));

    tmpElement = tmpHtmlPage.getElementById("li1");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("li2");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("li3");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("li4");
    Assert.assertFalse(HtmlElementUtil.isBlock(tmpElement));

    // options are always block, see inline comment in
    // org.wetator.backend.htmlunit.util.HtmlElementUtil.isBlock(DomNode)
    tmpElement = tmpHtmlPage.getElementById("option1");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("option2");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("option3");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));
    tmpElement = tmpHtmlPage.getElementById("option4");
    Assert.assertTrue(HtmlElementUtil.isBlock(tmpElement));

    tmpElement = tmpHtmlPage.getElementById("img1");
    Assert.assertFalse(HtmlElementUtil.isBlock(tmpElement));
  }
}
