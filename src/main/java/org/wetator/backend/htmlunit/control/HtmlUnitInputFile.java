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


package org.wetator.backend.htmlunit.control;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlunit.ScriptException;
import org.htmlunit.corejs.javascript.WrappedException;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlPage;
import org.wetator.backend.control.IControl;
import org.wetator.backend.control.ISettable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputFileIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.BackendException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;
import org.wetator.util.StringUtil;

/**
 * This is the implementation of the HTML element 'input file' (&lt;input type="file"&gt;) using HtmlUnit as
 * backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlFileInput.class)
@IdentifiedBy(HtmlUnitInputFileIdentifier.class)
public class HtmlUnitInputFile extends HtmlUnitBaseControl<HtmlFileInput>
    implements ISettable, IHtmlUnitDisableable<HtmlFileInput>, IHtmlUnitFocusable<HtmlFileInput> {

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlFileInput} from the backend
   */
  public HtmlUnitInputFile(final HtmlFileInput anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlFileInput(getHtmlElement());
  }

  @Override
  public void setValue(final WetatorContext aWetatorContext, final SecretString aValue, final File aDirectory)
      throws ActionException {
    final HtmlFileInput tmpHtmlFileInput = getHtmlElement();

    if (tmpHtmlFileInput.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", getDescribingText());
      throw new ActionException(tmpMessage);
    }
    if (tmpHtmlFileInput.isReadOnly()) {
      final String tmpMessage = Messages.getMessage("elementReadOnly", getDescribingText());
      throw new ActionException(tmpMessage);
    }

    try {
      final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlFileInput.getPage();
      DomElement tmpFocusedElement = tmpHtmlPage.getFocusedElement();
      if (tmpFocusedElement == null || tmpHtmlFileInput != tmpFocusedElement) {
        tmpHtmlFileInput.mouseOver();
        tmpHtmlFileInput.mouseMove();

        tmpHtmlFileInput.click();

        tmpFocusedElement = tmpHtmlPage.getFocusedElement();
        if (tmpHtmlFileInput != tmpFocusedElement) {
          final IControl tmpFocusedControl = aWetatorContext.getBrowser().getFocusedControl();

          if (tmpFocusedControl == null) {
            aWetatorContext.informListenersInfo("focusRemoved", getDescribingText());
            throw new ActionException(
                "After clicking on the control '" + getDescribingText() + "' the focus was removed.");
          }

          final String tmpDesc = tmpFocusedControl.getDescribingText();
          aWetatorContext.informListenersInfo("focusChanged", getDescribingText(), tmpDesc);

          if (tmpFocusedControl instanceof ISettable) {
            ((ISettable) tmpFocusedControl).setValue(aWetatorContext, aValue, aDirectory);
            return;
          }
          throw new ActionException("Focused control '" + tmpDesc + "' is not settable.");
        }
      }
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("backendError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    } catch (final ActionException e) {
      throw e;
    } catch (final Throwable e) {
      final String tmpMessage = Messages.getMessage("serverError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    }

    try {
      final String tmpValue = aValue.getValue();

      if (StringUtils.isBlank(tmpValue)) {
        tmpHtmlFileInput.setValue("");
      } else {
        final List<String> tmpFilenames = StringUtil.extractStrings(tmpValue, ",", '\\');
        final List<File> tmpFiles = new ArrayList<>();
        for (String tmpFilename : tmpFilenames) {
          tmpFilename = tmpFilename.trim();
          // now we have to determine the correct absolute file path
          File tmpFile = new File(tmpFilename);

          if (!tmpFile.isAbsolute() && null != aDirectory) {
            // relative paths are relative to the location of the calling file
            tmpFile = new File(aDirectory, tmpFilename);
          }

          // validate file
          if (!tmpFile.exists()) {
            final String tmpMessage = Messages.getMessage("fileNotFound",
                FilenameUtils.normalize(tmpFile.getAbsolutePath()));
            throw new ActionException(tmpMessage);
          }

          tmpFiles.add(tmpFile);
        }

        // simulate events during file selection via file dialog
        tmpHtmlFileInput.mouseOut();
        tmpFiles.forEach(f -> aWetatorContext.informListenersInfo("setFile", f.getAbsolutePath()));
        tmpHtmlFileInput.setFiles(tmpFiles.toArray(new File[0]));
      }

      // wait for silence
      waitForImmediateJobs(aWetatorContext);
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("backendError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    } catch (final ActionException e) {
      throw e;
    } catch (final Throwable e) {
      final String tmpMessage = Messages.getMessage("serverError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    }
  }

  @Override
  public void assertValue(final WetatorContext aWetatorContext, final SecretString anExpectedValue)
      throws AssertionException {
    String tmpValue = "";

    if (getHtmlElement().getFiles() != null && getHtmlElement().getFiles().length > 0) {
      tmpValue = Arrays.stream(getHtmlElement().getFiles()).map(f -> f.getName()).collect(Collectors.joining(", "));
    }

    Assert.assertEquals(anExpectedValue, tmpValue, "expectedValueNotFound");
  }
}
