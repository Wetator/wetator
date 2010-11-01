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


package org.rbri.wet.backend.htmlunit;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.backend.Control;
import org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitElementIdentifier;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.html.DisabledElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
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
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.impl.SelectableTextInput;
import com.gargoylesoftware.htmlunit.javascript.host.Event;
import com.gargoylesoftware.htmlunit.javascript.host.KeyboardEvent;

/**
 * The HtmlUnit Control implementation.
 * 
 * @author rbri
 */
public class HtmlUnitControl implements Control {
  private HtmlElement htmlElement;

  /**
   * Constructor
   * 
   * @param anHtmlElement the HtmlElement from the backend
   */
  public HtmlUnitControl(final HtmlElement anHtmlElement) {
    super();
    htmlElement = anHtmlElement;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#hasSameBackendControl(org.rbri.wet.backend.Control)
   */
  @Override
  public boolean hasSameBackendControl(final Control aControl) {
    if (aControl instanceof HtmlUnitControl) {
      HtmlUnitControl tmpHtmlUnitControl = (HtmlUnitControl) aControl;

      return getHtmlElement() == tmpHtmlUnitControl.getHtmlElement();
    }
    return false;
  }

  /**
   * Getter for the backing htmlElement from HtmlUnit
   * 
   * @return the html element
   */
  protected HtmlElement getHtmlElement() {
    return htmlElement;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#click(WetContext)
   */
  @Override
  public void click(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      tmpHtmlElement.click();
      aWetContext.getWetBackend().waitForImmediateJobs();

      if (tmpHtmlElement instanceof HtmlAnchor) {
        HtmlAnchor tmpHtmlAnchor = (HtmlAnchor) tmpHtmlElement;
        String tmpHref = tmpHtmlAnchor.getHrefAttribute();
        if (StringUtils.isNotBlank(tmpHref) && tmpHref.startsWith("#")) {
          tmpHref = tmpHref.substring(1);
          PageUtil.checkAnchor(tmpHref, tmpHtmlAnchor.getPage());
        }
      }
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#mouseOver(WetContext)
   */
  @Override
  public void mouseOver(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      tmpHtmlElement.mouseOver();
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#select(WetContext)
   */
  @Override
  public void select(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof DisabledElement) {
      DisabledElement tmpDisabledElement = (DisabledElement) tmpHtmlElement;
      Assert.assertTrue(!tmpDisabledElement.isDisabled(), "elementDisabled", new String[] { getDescribingText() });
    }

    try {
      if (tmpHtmlElement instanceof HtmlCheckBoxInput) {
        HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpHtmlElement;

        tmpHtmlCheckBoxInput.focus();
        if (!tmpHtmlCheckBoxInput.isChecked()) {
          tmpHtmlCheckBoxInput.click();
        }
      } else if (tmpHtmlElement instanceof HtmlRadioButtonInput) {
        HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpHtmlElement;
        if (!tmpHtmlRadioButtonInput.isChecked()) {
          tmpHtmlRadioButtonInput.click();
        }
      } else if (tmpHtmlElement instanceof HtmlOption) {
        HtmlOption tmpHtmlOption = (HtmlOption) tmpHtmlElement;
        Assert.assertTrue(!tmpHtmlOption.getEnclosingSelect().isDisabled(), "elementDisabled",
            new String[] { getDescribingText() });

        if (!tmpHtmlOption.isSelected()) {
          tmpHtmlOption.click();
        }
      } else {
        Assert.fail("selectNotSupported", new String[] { getDescribingText() });
      }

      // wait for silence
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#deselect(WetContext)
   */
  @Override
  public void deselect(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof DisabledElement) {
      DisabledElement tmpDisabledElement = (DisabledElement) tmpHtmlElement;
      Assert.assertTrue(!tmpDisabledElement.isDisabled(), "elementDisabled", new String[] { getDescribingText() });
    }

    try {
      if (tmpHtmlElement instanceof HtmlCheckBoxInput) {
        HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpHtmlElement;

        tmpHtmlCheckBoxInput.focus();
        if (tmpHtmlCheckBoxInput.isChecked()) {
          tmpHtmlCheckBoxInput.click();
        }
      } else if (tmpHtmlElement instanceof HtmlOption) {
        HtmlOption tmpHtmlOption = (HtmlOption) tmpHtmlElement;

        HtmlSelect tmpHtmpSelect = tmpHtmlOption.getEnclosingSelect();
        if (tmpHtmpSelect.isMultipleSelectEnabled()) {
          Assert.assertTrue(!tmpHtmpSelect.isDisabled(), "elementDisabled", new String[] { getDescribingText() });

          if (tmpHtmlOption.isSelected()) {
            // TODO event support
            tmpHtmlOption.setSelected(false);
            // tmpHtmlOption.click(false, true, false);
          }
        } else {
          Assert.fail("deselectNotSupported", new String[] { getDescribingText() });
        }
      } else {
        Assert.fail("deselectNotSupported", new String[] { getDescribingText() });
      }

      // wait for silence
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#setValue(WetContext, SecretString, File)
   */
  @Override
  public void setValue(final WetContext aWetContext, final SecretString aValue, final File aDirectory)
      throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof DisabledElement) {
      DisabledElement tmpDisabledElement = (DisabledElement) tmpHtmlElement;
      Assert.assertTrue(!tmpDisabledElement.isDisabled(), "elementDisabled", new String[] { getDescribingText() });
    }

    try {
      tmpHtmlElement.click();
    } catch (IOException e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      if (tmpHtmlElement instanceof HtmlFileInput) {
        HtmlFileInput tmpHtmlFileInput = (HtmlFileInput) tmpHtmlElement;

        String tmpValue = aValue.getValue();
        if (StringUtils.isBlank(tmpValue)) {
          tmpHtmlFileInput.setValueAttribute("");
        } else {
          // now we have to determine the correct absolute file path
          File tmpFile = new File(tmpValue);

          if (!tmpFile.isAbsolute() && (null != aDirectory)) {
            // relative paths are relative to the location of the calling file
            tmpFile = new File(aDirectory, aValue.getValue());
          }

          // validate file
          if (!tmpFile.exists()) {
            Assert.fail("fileNotFound", new String[] { tmpFile.getAbsolutePath() });
          }

          // simulate events during file selection via file dialog
          ((HtmlPage) tmpHtmlFileInput.getPage()).setFocusedElement(null);
          tmpHtmlFileInput.setValueAttribute(tmpFile.getAbsolutePath());
          tmpHtmlFileInput.focus();
        }
      } else if (tmpHtmlElement instanceof HtmlHiddenInput) {
        HtmlHiddenInput tmpHtmlHiddenInput = (HtmlHiddenInput) tmpHtmlElement;
        tmpHtmlHiddenInput.setValueAttribute(aValue.getValue());
      } else if (tmpHtmlElement instanceof SelectableTextInput) {
        SelectableTextInput tmpSelectableTextInput = (SelectableTextInput) tmpHtmlElement;

        String tmpValue = aValue.getValue();
        tmpSelectableTextInput.select();

        if (tmpHtmlElement instanceof HtmlTextArea) {
          HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpHtmlElement;

          if (StringUtils.isNotBlank(tmpValue)) {
            tmpHtmlTextArea.type(aValue.getValue());
          } else {
            tmpHtmlTextArea.setText("");
          }
        } else {
          HtmlInput tmpHtmlInput = (HtmlInput) tmpHtmlElement;

          int tmpMaxLength = -1;
          try {
            String tmpMaxLengthString = tmpHtmlInput.getMaxLengthAttribute();
            tmpMaxLength = Integer.parseInt(tmpMaxLengthString);
          } catch (NumberFormatException e) {
            // TODO warn
          }

          if (tmpMaxLength > -1) {
            tmpValue = tmpValue.substring(0, Math.min(tmpMaxLength, tmpValue.length()));
          }
          if (tmpValue.length() > 0) {
            tmpHtmlElement.type(tmpValue);
          } else {
            // no way to simulate type of the del key
            char tmpDel = (char) 46;

            Event tmpKeyDownEvent = new KeyboardEvent(tmpHtmlElement, Event.TYPE_KEY_DOWN, tmpDel, false, false, false);
            ScriptResult tmpKeyDownResult = tmpHtmlElement.fireEvent(tmpKeyDownEvent);

            Event tmpKeyPressEvent = new KeyboardEvent(tmpHtmlElement, Event.TYPE_KEY_PRESS, tmpDel, false, false,
                false);
            ScriptResult tmpKeyPressResult = tmpHtmlElement.fireEvent(tmpKeyPressEvent);

            if (!tmpKeyDownEvent.isAborted(tmpKeyDownResult) && !tmpKeyPressEvent.isAborted(tmpKeyPressResult)) {
              // do it this way to not trigger the onChange handler
              tmpHtmlInput.setAttribute("value", "");
            }

            Event tmpKeyUpEvent = new KeyboardEvent(tmpHtmlElement, Event.TYPE_KEY_UP, tmpDel, false, false, false);
            tmpHtmlElement.fireEvent(tmpKeyUpEvent);

          }
        }
      } else {
        Assert.fail("setNotSupported", new String[] { getDescribingText() });
      }

      // wait for silence
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#isDisabled(WetContext)
   */
  @Override
  public boolean isDisabled(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof DisabledElement) {
      DisabledElement tmpDisabledElement = (DisabledElement) tmpHtmlElement;

      return tmpDisabledElement.isDisabled();
    } else if (tmpHtmlElement instanceof HtmlTableDataCell) {
      return true;
    }
    Assert.fail("disabledCheckNotSupported", new String[] { getDescribingText() });

    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#isSelected(WetContext)
   */
  @Override
  public boolean isSelected(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof HtmlCheckBoxInput) {
      HtmlCheckBoxInput tmpHtmlCheckBoxInput = (HtmlCheckBoxInput) tmpHtmlElement;

      return tmpHtmlCheckBoxInput.isChecked();
    }
    if (tmpHtmlElement instanceof HtmlRadioButtonInput) {
      HtmlRadioButtonInput tmpHtmlRadioButtonInput = (HtmlRadioButtonInput) tmpHtmlElement;

      return tmpHtmlRadioButtonInput.isChecked();
    }
    if (tmpHtmlElement instanceof HtmlOption) {
      HtmlOption tmpOption = (HtmlOption) tmpHtmlElement;

      return tmpOption.isSelected();
    }
    Assert.fail("selectedCheckNotSupported", new String[] { getDescribingText() });

    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#getValue(WetContext)
   */
  @Override
  public String getValue(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof HtmlInput) {
      HtmlInput tmpHtmlInput = (HtmlInput) tmpHtmlElement;
      return tmpHtmlInput.getValueAttribute();
    }

    if (tmpHtmlElement instanceof HtmlTextArea) {
      HtmlTextArea tmpHtmlTextArea = (HtmlTextArea) tmpHtmlElement;
      return tmpHtmlTextArea.getText();
    }

    Assert.fail("valueNotSupported", new String[] { getDescribingText() });
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.Control#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof HtmlAnchor) {
      return HtmlElementUtil.getDescribingTextForHtmlAnchor((HtmlAnchor) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlButton) {
      return HtmlElementUtil.getDescribingTextForHtmlButton((HtmlButton) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlButtonInput) {
      return HtmlElementUtil.getDescribingTextForHtmlButtonInput((HtmlButtonInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlCheckBoxInput) {
      return HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput((HtmlCheckBoxInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlFileInput) {
      return HtmlElementUtil.getDescribingTextForHtmlFileInput((HtmlFileInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlImage) {
      return HtmlElementUtil.getDescribingTextForHtmlImage((HtmlImage) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlImageInput) {
      return HtmlElementUtil.getDescribingTextForHtmlImageInput((HtmlImageInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlOption) {
      return HtmlElementUtil.getDescribingTextForHtmlOption((HtmlOption) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlOptionGroup) {
      return HtmlElementUtil.getDescribingTextForHtmlOptionGroup((HtmlOptionGroup) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlParagraph) {
      return HtmlElementUtil.getDescribingTextForHtmlParagraph((HtmlParagraph) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlPasswordInput) {
      return HtmlElementUtil.getDescribingTextForHtmlPasswordInput((HtmlPasswordInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlRadioButtonInput) {
      return HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput((HtmlRadioButtonInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlResetInput) {
      return HtmlElementUtil.getDescribingTextForHtmlResetInput((HtmlResetInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlSelect) {
      return HtmlElementUtil.getDescribingTextForHtmlSelect((HtmlSelect) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlSpan) {
      return HtmlElementUtil.getDescribingTextForHtmlSpan((HtmlSpan) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlSubmitInput) {
      return HtmlElementUtil.getDescribingTextForHtmlSubmitInput((HtmlSubmitInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlTextArea) {
      return HtmlElementUtil.getDescribingTextForHtmlTextArea((HtmlTextArea) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlTextInput) {
      return HtmlElementUtil.getDescribingTextForHtmlTextInput((HtmlTextInput) tmpHtmlElement);
    }

    // handle things that are not implemented at the moment
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[Unknown HtmlElement '");
    tmpResult.append(tmpHtmlElement.getClass());
    tmpResult.append("'");

    addId(tmpResult, tmpHtmlElement);
    addName(tmpResult, tmpHtmlElement);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  private static void addId(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    String tmpId = anHtmlElement.getAttribute("id");
    if ((null != tmpId) && (tmpId.length() > 0)) {
      aStringBuilder.append(" (id='");
      aStringBuilder.append(tmpId);
      aStringBuilder.append("')");
    }
  }

  private static void addName(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    String tmpName = anHtmlElement.getAttribute("name");
    if ((null != tmpName) && (tmpName.length() > 0)) {
      aStringBuilder.append(" (name='");
      aStringBuilder.append(tmpName);
      aStringBuilder.append("')");
    }
  }

  /**
   * This annotation contains the identifier for the htmlunit control.
   * 
   * @author frank.danek
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Identifiers {
    /**
     * The identifiers.
     */
    Class<? extends AbstractHtmlUnitElementIdentifier>[] value();
  }
}
