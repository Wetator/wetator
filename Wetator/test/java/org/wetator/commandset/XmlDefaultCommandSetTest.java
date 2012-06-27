/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.commandset;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.exception.InvalidInputException;
import org.wetator.test.AbstractWebServerTest;

/**
 * @author frank.danek
 */
public class XmlDefaultCommandSetTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/xml/";

  @Test
  public void assertContent() throws InvalidInputException {
    executeTestFile("assert_content.wet");

    Assert.assertEquals(75, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDeselectedCheckbox() throws InvalidInputException {
    executeTestFile("assert_deselected_checkbox.wet");

    Assert.assertEquals(39, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDeselectedMultipleSelect() throws InvalidInputException {
    executeTestFile("assert_deselected_multipleSelect.wet");

    Assert.assertEquals(55, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDeselectedRadio() throws InvalidInputException {
    executeTestFile("assert_deselected_radio.wet");

    Assert.assertEquals(29, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDeselectedSingleSelect() throws InvalidInputException {
    executeTestFile("assert_deselected_singleSelect.wet");

    Assert.assertEquals(44, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertDisabled() throws InvalidInputException {
    executeTestFile("assert_disabled.wet");

    Assert.assertEquals(54, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedCheckbox() throws InvalidInputException {
    executeTestFile("assert_selected_checkbox.wet");

    Assert.assertEquals(39, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedMultipleSelect() throws InvalidInputException {
    executeTestFile("assert_selected_multipleSelect.wet");

    Assert.assertEquals(55, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedRadio() throws InvalidInputException {
    executeTestFile("assert_selected_radio.wet");

    Assert.assertEquals(29, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSelectedSingleSelect() throws InvalidInputException {
    executeTestFile("assert_selected_singleSelect.wet");

    Assert.assertEquals(44, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertSet() throws InvalidInputException {
    executeTestFile("assert_set.wet");

    Assert.assertEquals(102, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void assertTitle() throws InvalidInputException {
    executeTestFile("assert_title.wet");

    Assert.assertEquals(21, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void clickOnAfterText() throws InvalidInputException {
    executeTestFile("click_on_after_text.wet");

    Assert.assertEquals(280, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void clickOnAnchorInsidePage() throws InvalidInputException {
    executeTestFile("click_on_anchor_inside_page.wet");

    Assert.assertEquals(5, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void clickOn() throws InvalidInputException {
    executeTestFile("click_on.wet");

    Assert.assertEquals(156, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void deselectCheckbox() throws InvalidInputException {
    executeTestFile("deselect_checkbox.wet");

    Assert.assertEquals(66, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void deselectMultipleSelect() throws InvalidInputException {
    executeTestFile("deselect_multipleSelect.wet");

    Assert.assertEquals(81, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void deselectRadio() throws InvalidInputException {
    executeTestFile("deselect_radio.wet");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void execJava() throws InvalidInputException {
    executeTestFile("exec_java.wet");

    Assert.assertEquals(29, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void goBack() throws InvalidInputException {
    executeTestFile("go_back.wet");

    Assert.assertEquals(10, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void mouseOverAfter() throws InvalidInputException {
    executeTestFile("mouse_over_after.wet");

    Assert.assertEquals(33, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void mouseOver() throws InvalidInputException {
    executeTestFile("mouse_over.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void openUrlAnchorInsidePage() throws InvalidInputException {
    executeTestFile("open_url_anchor_inside_page.wet");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void openUrlBasicAuth() throws InvalidInputException {
    executeTestFile("open_url_basicauth.wet");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void openUrlHeader() throws InvalidInputException {
    executeTestFile("open_url_header.wet");

    Assert.assertEquals(3, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void openUrlRedirect() throws InvalidInputException {
    executeTestFile("open_url_redirect.wet");

    Assert.assertEquals(12, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void openUrl() throws InvalidInputException {
    executeTestFile("open_url.wet");

    Assert.assertEquals(19, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectCheckboxAfter() throws InvalidInputException {
    executeTestFile("select_checkbox_after.wet");

    Assert.assertEquals(55, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectCheckbox() throws InvalidInputException {
    executeTestFile("select_checkbox.wet");

    Assert.assertEquals(66, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectMultipleSelect() throws InvalidInputException {
    executeTestFile("select_multipleSelect.wet");

    Assert.assertEquals(84, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectRadio() throws InvalidInputException {
    executeTestFile("select_radio.wet");

    Assert.assertEquals(48, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void selectSingleSelect() throws InvalidInputException {
    executeTestFile("select_singleSelect.wet");

    Assert.assertEquals(76, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void setUpload() throws InvalidInputException {
    executeTestFile("set_upload.wet");

    Assert.assertEquals(69, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void set() throws InvalidInputException {
    executeTestFile("set.wet");

    Assert.assertEquals(232, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  public void useModule() throws InvalidInputException {
    executeTestFile("use_module.wet");

    Assert.assertEquals(10, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(String aTestFileName) throws InvalidInputException {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
