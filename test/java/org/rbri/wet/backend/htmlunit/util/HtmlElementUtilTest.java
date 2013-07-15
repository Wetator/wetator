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


package org.rbri.wet.backend.htmlunit.util;

import java.io.IOException;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rbri.wet.backend.htmlunit.HtmlUnitControl;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
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
public class HtmlElementUtilTest extends TestCase {

  public static void main(String[] anArgsArray) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(HtmlElementUtilTest.class);
  }

  public void testGetDescribingTextFor_HtmlAnchor() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a href='wet.html'>AnchorText</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpAnchor);
    assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlAnchor_FormatedText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a href='wet.html'>A<font>n</font>chor<b>Text</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpAnchor);
    assertEquals("[HtmlAnchor 'AnchorText']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlAnchor_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a href='wet.html' name='AnchorName'>AnchorText</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText' (name='AnchorName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpAnchor);
    assertEquals("[HtmlAnchor 'AnchorText' (name='AnchorName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlAnchor_Image() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a href='wet.html'><img src='wet.src'></a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'image: wet.src']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpAnchor);
    assertEquals("[HtmlAnchor 'image: wet.src']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlAnchor_ImageAndText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a href='wet.html'><img src='wet.src'>AnchorText</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'image: wet.src' 'AnchorText']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpAnchor);
    assertEquals("[HtmlAnchor 'image: wet.src' 'AnchorText']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlAnchor_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a href='wet.html' id='AnchorId'>AnchorText</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpAnchor);
    assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlAnchor_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a href='wet.html' name='AnchorName' id='AnchorId'>AnchorText</a>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpAnchor).getDescribingText();
    assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId') (name='AnchorName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlAnchor(tmpAnchor);
    assertEquals("[HtmlAnchor 'AnchorText' (id='AnchorId') (name='AnchorName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButton() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button type='button'>TestButton</button>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButton tmpButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpButton).getDescribingText();
    assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpButton);
    assertEquals("[HtmlButton 'TestButton']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButton_Value() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button type='button' value='TestButton' />"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButton tmpButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpButton).getDescribingText();
    assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpButton);
    assertEquals("[HtmlButton 'TestButton']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButton_FormatedText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button type='button'>T<font>e</font>st<b>Button</b></button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButton tmpButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpButton).getDescribingText();
    assertEquals("[HtmlButton 'TestButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpButton);
    assertEquals("[HtmlButton 'TestButton']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButton_Image() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button type='button'><img src='wet.src'></button>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButton tmpButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpButton).getDescribingText();
    assertEquals("[HtmlButton 'image: wet.src']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpButton);
    assertEquals("[HtmlButton 'image: wet.src']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButton_ImageAndText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button type='button'><img src='wet.src'>Text</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButton tmpButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpButton).getDescribingText();
    assertEquals("[HtmlButton 'image: wet.src' 'Text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpButton);
    assertEquals("[HtmlButton 'image: wet.src' 'Text']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButton_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button type='button' name='ButtonName'>Text</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButton tmpButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpButton).getDescribingText();
    assertEquals("[HtmlButton 'Text' (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpButton);
    assertEquals("[HtmlButton 'Text' (name='ButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButton_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button type='button' id='ButtonId'>Text</button>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButton tmpButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpButton).getDescribingText();
    assertEquals("[HtmlButton 'Text' (id='ButtonId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpButton);
    assertEquals("[HtmlButton 'Text' (id='ButtonId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButton_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button type='button' id='ButtonId' name='ButtonName'>Text</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButton tmpButton = (HtmlButton) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpButton).getDescribingText();
    assertEquals("[HtmlButton 'Text' (id='ButtonId') (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButton(tmpButton);
    assertEquals("[HtmlButton 'Text' (id='ButtonId') (name='ButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButtonInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='button' value='Button'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlButtonInput).getDescribingText();
    assertEquals("[HtmlButtonInput 'Button']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    assertEquals("[HtmlButtonInput 'Button']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButtonInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input name='ButtonName' type='button' value='Button'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlButtonInput).getDescribingText();
    assertEquals("[HtmlButtonInput 'Button' (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    assertEquals("[HtmlButtonInput 'Button' (name='ButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButtonInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='sb' type='button' value='Button'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlButtonInput).getDescribingText();
    assertEquals("[HtmlButtonInput 'Button' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    assertEquals("[HtmlButtonInput 'Button' (id='sb')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlButtonInput_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='sb' name='ButtonName' type='button' value='Button'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlButtonInput tmpHtmlButtonInput = (HtmlButtonInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlButtonInput).getDescribingText();
    assertEquals("[HtmlButtonInput 'Button' (id='sb') (name='ButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlButtonInput(tmpHtmlButtonInput);
    assertEquals("[HtmlButtonInput 'Button' (id='sb') (name='ButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlCheckBoxInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='checkbox' value='CheckBoxValue'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlCheckBoxInput tmpCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpCheckBoxInput).getDescribingText();
    assertEquals("[HtmlCheckBoxInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpCheckBoxInput);
    assertEquals("[HtmlCheckBoxInput]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlCheckBoxInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='checkbox' value='CheckBoxValue' name='CheckBoxName'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlCheckBoxInput tmpCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpCheckBoxInput).getDescribingText();
    assertEquals("[HtmlCheckBoxInput (name='CheckBoxName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpCheckBoxInput);
    assertEquals("[HtmlCheckBoxInput (name='CheckBoxName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlCheckBoxInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='checkbox' value='CheckBoxValue' id='CheckBoxId'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlCheckBoxInput tmpCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpCheckBoxInput).getDescribingText();
    assertEquals("[HtmlCheckBoxInput (id='CheckBoxId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpCheckBoxInput);
    assertEquals("[HtmlCheckBoxInput (id='CheckBoxId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlCheckBox_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='checkbox' value='CheckBoxValue' name='CheckBoxName' id='CheckBoxId'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlCheckBoxInput tmpCheckBoxInput = (HtmlCheckBoxInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpCheckBoxInput).getDescribingText();
    assertEquals("[HtmlCheckBoxInput (id='CheckBoxId') (name='CheckBoxName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpCheckBoxInput);
    assertEquals("[HtmlCheckBoxInput (id='CheckBoxId') (name='CheckBoxName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlFileInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='file'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlFileInput tmpFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpFileInput).getDescribingText();
    assertEquals("[HtmlFileInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpFileInput);
    assertEquals("[HtmlFileInput]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlFileInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='file' name='FileInputName'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlFileInput tmpFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpFileInput).getDescribingText();
    assertEquals("[HtmlFileInput (name='FileInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpFileInput);
    assertEquals("[HtmlFileInput (name='FileInputName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlFileInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='file' id='FileInputId'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlFileInput tmpFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpFileInput).getDescribingText();
    assertEquals("[HtmlFileInput (id='FileInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpFileInput);
    assertEquals("[HtmlFileInput (id='FileInputId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlFileInput_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='file' name='FileInputName' id='FileInputId'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlFileInput tmpFileInput = (HtmlFileInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpFileInput).getDescribingText();
    assertEquals("[HtmlFileInput (id='FileInputId') (name='FileInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlFileInput(tmpFileInput);
    assertEquals("[HtmlFileInput (id='FileInputId') (name='FileInputName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlImage() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<img src='wet.png'>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlImage tmpImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpImage).getDescribingText();
    assertEquals("[HtmlImage 'wet.png']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpImage);
    assertEquals("[HtmlImage 'wet.png']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlImage_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<img src='wet.png' name='ImageName'>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlImage tmpImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult;
    tmpResult = new HtmlUnitControl(tmpImage).getDescribingText();
    assertEquals("[HtmlImage 'wet.png' (name='ImageName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpImage);
    assertEquals("[HtmlImage 'wet.png' (name='ImageName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlImage_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<img src='wet.png' id='ImageId'>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlImage tmpImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpImage).getDescribingText();
    assertEquals("[HtmlImage 'wet.png' (id='ImageId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpImage);
    assertEquals("[HtmlImage 'wet.png' (id='ImageId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlImage_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<img src='wet.png' name='ImageName' id='ImageId'>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlImage tmpImage = (HtmlImage) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpImage).getDescribingText();
    assertEquals("[HtmlImage 'wet.png' (id='ImageId') (name='ImageName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImage(tmpImage);
    assertEquals("[HtmlImage 'wet.png' (id='ImageId') (name='ImageName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlImageInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='Image' value='ImageInput' src='sample.src'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlImageInput).getDescribingText();
    assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlImageInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='Image' name='ImageInputName' value='ImageInput' src='sample.src'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlImageInput).getDescribingText();
    assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (name='ImageInputName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (name='ImageInputName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlImageInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='Image' id='ImageInputId' value='ImageInput' src='sample.src'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlImageInput).getDescribingText();
    assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlImageInput_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='Image' id='ImageInputId' name='ImageInputName' value='ImageInput' src='sample.src'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlImageInput tmpHtmlImageInput = (HtmlImageInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlImageInput).getDescribingText();
    assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId') (name='ImageInputName')]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlImageInput(tmpHtmlImageInput);
    assertEquals("[HtmlImageInput 'ImageInput' (src='sample.src') (id='ImageInputId') (name='ImageInputName')]",
        tmpResult);
  }

  public void testGetDescribingTextFor_HtmlParagraph() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>test</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<DomNode> tmpChilds = tmpHtmlPage.getFirstChild().getChildren().iterator();
    tmpChilds.next();
    HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpChilds.next().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlParagraph).getDescribingText();
    assertEquals("[HtmlParagraph 'test']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    assertEquals("[HtmlParagraph 'test']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlParagraph_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p name='MyName' >test</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<DomNode> tmpChilds = tmpHtmlPage.getFirstChild().getChildren().iterator();
    tmpChilds.next();
    HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpChilds.next().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlParagraph).getDescribingText();
    assertEquals("[HtmlParagraph 'test' (name='MyName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    assertEquals("[HtmlParagraph 'test' (name='MyName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlParagraph_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p id='MyId'>test</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<DomNode> tmpChilds = tmpHtmlPage.getFirstChild().getChildren().iterator();
    tmpChilds.next();
    HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpChilds.next().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlParagraph).getDescribingText();
    assertEquals("[HtmlParagraph 'test' (id='MyId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    assertEquals("[HtmlParagraph 'test' (id='MyId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlParagraph_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p name='MyName' id='MyId'>test</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<DomNode> tmpChilds = tmpHtmlPage.getFirstChild().getChildren().iterator();
    tmpChilds.next();
    HtmlParagraph tmpHtmlParagraph = (HtmlParagraph) tmpChilds.next().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlParagraph).getDescribingText();
    assertEquals("[HtmlParagraph 'test' (id='MyId') (name='MyName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlParagraph(tmpHtmlParagraph);
    assertEquals("[HtmlParagraph 'test' (id='MyId') (name='MyName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlPasswordInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='password' value='Password'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlPasswordInput).getDescribingText();
    assertEquals("[HtmlPasswordInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    assertEquals("[HtmlPasswordInput]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlOption_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='optId' value='o_red'>red</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlOption tmpHtmlOption = (HtmlOption) tmpForm.getFirstChild().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlOption).getDescribingText();
    assertEquals("[HtmlOption 'red' (id='optId') part of [HtmlSelect (id='MyFirstSelectId')]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    assertEquals("[HtmlOption 'red' (id='optId') part of [HtmlSelect (id='MyFirstSelectId')]]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlOptionGroup_ValueOnly() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<option value='o_red'>red</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlOption tmpHtmlOption = (HtmlOption) tmpForm.getFirstChild().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlOption).getDescribingText();
    assertEquals("[HtmlOption 'red' part of [HtmlSelect (id='MyFirstSelectId')]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOption(tmpHtmlOption);
    assertEquals("[HtmlOption 'red' part of [HtmlSelect (id='MyFirstSelectId')]]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlOptionGroup_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' name='optgroup_colors'>" + "<option value='o_red'>red</option>" + "</optgroup>"
        + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpForm.getFirstChild().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlOptionGroup).getDescribingText();
    assertEquals("[HtmlOptionGroup 'colors' (name='optgroup_colors') part of [HtmlSelect (id='MyFirstSelectId')]]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals("[HtmlOptionGroup 'colors' (name='optgroup_colors') part of [HtmlSelect (id='MyFirstSelectId')]]",
        tmpResult);
  }

  public void testGetDescribingTextFor_HtmlOptionGroup_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>" + "<option value='o_red'>red</option>" + "</optgroup>"
        + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpForm.getFirstChild().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlOptionGroup).getDescribingText();
    assertEquals("[HtmlOptionGroup 'colors' (id='optgroup_colors') part of [HtmlSelect (id='MyFirstSelectId')]]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals("[HtmlOptionGroup 'colors' (id='optgroup_colors') part of [HtmlSelect (id='MyFirstSelectId')]]",
        tmpResult);
  }

  public void testGetDescribingTextFor_HtmlOptionGroup_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors' name='optgroup_name'>" + "<option value='o_red'>red</option>"
        + "</optgroup>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpForm.getFirstChild().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlOptionGroup).getDescribingText();
    assertEquals(
        "[HtmlOptionGroup 'colors' (id='optgroup_colors') (name='optgroup_name') part of [HtmlSelect (id='MyFirstSelectId')]]",
        tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals(
        "[HtmlOptionGroup 'colors' (id='optgroup_colors') (name='optgroup_name') part of [HtmlSelect (id='MyFirstSelectId')]]",
        tmpResult);
  }

  public void testGetDescribingTextFor_HtmlOptionGroup_LabelOnly() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors'>" + "<option value='o_red'>red</option>" + "</optgroup>" + "</select>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpForm.getFirstChild().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlOptionGroup).getDescribingText();
    assertEquals("[HtmlOptionGroup 'colors' part of [HtmlSelect (id='MyFirstSelectId')]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals("[HtmlOptionGroup 'colors' part of [HtmlSelect (id='MyFirstSelectId')]]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlOptionGroup_NoLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup>" + "<option value='o_red'>red</option>" + "</optgroup>" + "</select>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpForm.getFirstChild().getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlOptionGroup).getDescribingText();
    assertEquals("[HtmlOptionGroup '' part of [HtmlSelect (id='MyFirstSelectId')]]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlOptionGroup(tmpHtmlOptionGroup);
    assertEquals("[HtmlOptionGroup '' part of [HtmlSelect (id='MyFirstSelectId')]]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlPasswordInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input name='PasswordName' type='password' value='Password'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlPasswordInput).getDescribingText();
    assertEquals("[HtmlPasswordInput (name='PasswordName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    assertEquals("[HtmlPasswordInput (name='PasswordName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlPasswordInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='tx' type='password' value='Password'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlPasswordInput).getDescribingText();
    assertEquals("[HtmlPasswordInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    assertEquals("[HtmlPasswordInput (id='tx')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlPasswordInput_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='tx' name='PasswordName' type='password' value='Password'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlPasswordInput tmpHtmlPasswordInput = (HtmlPasswordInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlPasswordInput).getDescribingText();
    assertEquals("[HtmlPasswordInput (id='tx') (name='PasswordName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlPasswordInput(tmpHtmlPasswordInput);
    assertEquals("[HtmlPasswordInput (id='tx') (name='PasswordName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlRadioButtonInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='radio' value='RadioButton'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlRadioButtonInput).getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    assertEquals("[HtmlRadioButtonInput 'RadioButton']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlRadioButtonInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='radio' value='RadioButton' name='RadioButtonName'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlRadioButtonInput).getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (name='RadioButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (name='RadioButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlRadioButtonInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='radio' value='RadioButton' id='RadioButtonId'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlRadioButtonInput).getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlRadioButtonInput_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='radio' value='RadioButton' name='RadioButtonName' id='RadioButtonId'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlRadioButtonInput).getDescribingText();
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId') (name='RadioButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(tmpHtmlRadioButtonInput);
    assertEquals("[HtmlRadioButtonInput 'RadioButton' (id='RadioButtonId') (name='RadioButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlResetInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='reset' value='ResetButton'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlResetInput).getDescribingText();
    assertEquals("[HtmlResetInput 'ResetButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    assertEquals("[HtmlResetInput 'ResetButton']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlResetInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input name='ResetButtonName' type='reset' value='ResetButton'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlResetInput).getDescribingText();
    assertEquals("[HtmlResetInput 'ResetButton' (name='ResetButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    assertEquals("[HtmlResetInput 'ResetButton' (name='ResetButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlResetInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='sb' type='reset' value='ResetButton'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlResetInput).getDescribingText();
    assertEquals("[HtmlResetInput 'ResetButton' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    assertEquals("[HtmlResetInput 'ResetButton' (id='sb')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlResetInput_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='sb' name='ResetButtonName' type='reset' value='ResetButton'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlResetInput tmpHtmlResetInput = (HtmlResetInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlResetInput).getDescribingText();
    assertEquals("[HtmlResetInput 'ResetButton' (id='sb') (name='ResetButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlResetInput(tmpHtmlResetInput);
    assertEquals("[HtmlResetInput 'ResetButton' (id='sb') (name='ResetButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSelect() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select>" + "<option>Option1</option>"
        + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlSelect).getDescribingText();
    assertEquals("[HtmlSelect]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    assertEquals("[HtmlSelect]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSelect_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select name='SelectName'>"
        + "<option>Option1</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlSelect).getDescribingText();
    assertEquals("[HtmlSelect (name='SelectName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    assertEquals("[HtmlSelect (name='SelectName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSelect_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='SelectId'>"
        + "<option>Option1</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlSelect).getDescribingText();
    assertEquals("[HtmlSelect (id='SelectId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    assertEquals("[HtmlSelect (id='SelectId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSelect_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='SelectId' name='SelectName'>"
        + "<option>Option1</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlSelect).getDescribingText();
    assertEquals("[HtmlSelect (id='SelectId') (name='SelectName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSelect(tmpHtmlSelect);
    assertEquals("[HtmlSelect (id='SelectId') (name='SelectName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSpan() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<span class='abc'>some text</span>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan 'some text']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan 'some text']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSpan_Empty() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<span style='abc'></span>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan '']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan '']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSpan_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<span name='Spanname'>some text</span>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan 'some text' (name='Spanname')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan 'some text' (name='Spanname')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSpan_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<span id='SpanId'>some text</span>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan 'some text' (id='SpanId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan 'some text' (id='SpanId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSpan_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<span id='SpanId' name='Spanname'>some text</span>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlSpan tmpHtmlSpan = (HtmlSpan) tmpHtmlElements.next();

    String tmpResult = new HtmlUnitControl(tmpHtmlSpan).getDescribingText();
    assertEquals("[HtmlSpan 'some text' (id='SpanId') (name='Spanname')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSpan(tmpHtmlSpan);
    assertEquals("[HtmlSpan 'some text' (id='SpanId') (name='Spanname')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSubmitInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='submit' value='SubmitButton'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlSubmitInput).getDescribingText();
    assertEquals("[HtmlSubmitInput 'SubmitButton']", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    assertEquals("[HtmlSubmitInput 'SubmitButton']", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSubmitInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input name='SubmitButtonName' type='submit' value='SubmitButton'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlSubmitInput).getDescribingText();
    assertEquals("[HtmlSubmitInput 'SubmitButton' (name='SubmitButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    assertEquals("[HtmlSubmitInput 'SubmitButton' (name='SubmitButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSubmitInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='sb' type='submit' value='SubmitButton'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlSubmitInput).getDescribingText();
    assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlSubmitInput_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='sb' name='SubmitButtonName' type='submit' value='SubmitButton'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlSubmitInput tmpHtmlSubmitInput = (HtmlSubmitInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlSubmitInput).getDescribingText();
    assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb') (name='SubmitButtonName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlSubmitInput(tmpHtmlSubmitInput);
    assertEquals("[HtmlSubmitInput 'SubmitButton' (id='sb') (name='SubmitButtonName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlTextInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input type='text' value='Text'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlTextInput).getDescribingText();
    assertEquals("[HtmlTextInput]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    assertEquals("[HtmlTextInput]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlTextInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input name='TextName' type='text' value='Text'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlTextInput).getDescribingText();
    assertEquals("[HtmlTextInput (name='TextName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    assertEquals("[HtmlTextInput (name='TextName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlTextInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='tx' type='text' value='Text'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlTextInput).getDescribingText();
    assertEquals("[HtmlTextInput (id='tx')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    assertEquals("[HtmlTextInput (id='tx')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlTextInput_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='tx' name='TextName' type='text' value='Text'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlTextInput tmpHtmlTextInput = (HtmlTextInput) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlTextInput).getDescribingText();
    assertEquals("[HtmlTextInput (id='tx') (name='TextName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextInput(tmpHtmlTextInput);
    assertEquals("[HtmlTextInput (id='tx') (name='TextName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlTextArea() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<textarea></textarea>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlTextArea).getDescribingText();
    assertEquals("[HtmlTextArea]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    assertEquals("[HtmlTextArea]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlTextArea_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<textarea name='TextAreaName'></textarea>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlTextArea).getDescribingText();
    assertEquals("[HtmlTextArea (name='TextAreaName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    assertEquals("[HtmlTextArea (name='TextAreaName')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlTextArea_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<textarea id='TextAreaId'></textarea>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlTextArea).getDescribingText();
    assertEquals("[HtmlTextArea (id='TextAreaId')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    assertEquals("[HtmlTextArea (id='TextAreaId')]", tmpResult);
  }

  public void testGetDescribingTextFor_HtmlTextArea_Name_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<textarea name='TextAreaName' id='TextAreaId'></textarea>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlForm tmpForm = tmpHtmlPage.getForms().get(0);

    HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpForm.getFirstChild();

    String tmpResult = new HtmlUnitControl(tmpHtmlTextArea).getDescribingText();
    assertEquals("[HtmlTextArea (id='TextAreaId') (name='TextAreaName')]", tmpResult);

    tmpResult = HtmlElementUtil.getDescribingTextForHtmlTextArea(tmpHtmlTextArea);
    assertEquals("[HtmlTextArea (id='TextAreaId') (name='TextAreaName')]", tmpResult);
  }
}