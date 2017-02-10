/*
 * Copyright (c) 2008-2016 wetator.org
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


package org.wetator.primefaces.backend.htmlunit.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.control.ISelectable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;
import org.wetator.i18n.Messages;
import org.wetator.primefaces.backend.htmlunit.control.identifier.HtmlUnitPrimeFacesOptionInSelectIdentifier;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

/**
 * Support for PrimeFaces SelectOne.
 *
 * @author rbri
 */
@IdentifiedBy({ HtmlUnitPrimeFacesOptionInSelectIdentifier.class })
public class HtmlUnitPrimeFacesOption extends HtmlUnitBaseControl<HtmlDivision> implements ISelectable {

  private static final Log LOG = LogFactory.getLog(HtmlUnitPrimeFacesOption.class);

  private final HtmlOption htmlOption;

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlDivision} from the backend
   */
  public HtmlUnitPrimeFacesOption(final HtmlDivision anHtmlElement, final HtmlOption anOption) {
    super(anHtmlElement);
    htmlOption = anOption;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return "Primefaces " + getHtmlElement();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.control.ISelectable#select(org.wetator.core.WetatorContext)
   */
  @Override
  public void select(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlOption tmpHtmlOption = htmlOption;

    if (tmpHtmlOption.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", new String[] { getDescribingText() });
      throw new ActionException(tmpMessage);
    }
    if (tmpHtmlOption.getEnclosingSelect().isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", new String[] { getDescribingText() });
      throw new ActionException(tmpMessage);
    }

    try {
      if (tmpHtmlOption.isSelected()) {
        aWetatorContext.informListenersWarn("elementAlreadySelected", new String[] { getDescribingText() });
      } else {
        final int tmpPos = tmpHtmlOption.getEnclosingSelect().getOptions().indexOf(tmpHtmlOption);

        String tmpId = tmpHtmlOption.getEnclosingSelect().getId();
        if (!tmpId.endsWith("_input")) {
            final String tmpMessage = Messages.getMessage("PrimefacesOption_unsupportedID", new String[] { tmpId });
            throw new ActionException(tmpMessage);
        }

        final HtmlPage tmpPage = tmpHtmlOption.getHtmlPageOrNull();
        if (tmpPage == null) {
            final String tmpMessage = Messages.getMessage("PrimefacesOption_pageNull", new String[] {});
            throw new ActionException(tmpMessage);
        }

        tmpId = tmpId.substring(0, tmpId.length() - "_input".length());

        LOG.debug("PF Select - '" + tmpId + "'");

        DomElement tmpTarget = tmpPage.getElementById(tmpId);
        if (tmpTarget == null) {
            final String tmpMessage = Messages.getMessage("PrimefacesOption_controlNull", new String[] { tmpId });
            throw new ActionException(tmpMessage);
        }

        DomElement tmpTrigger = null;
        for (DomElement tmpChild : tmpTarget.getChildElements()) {
            String tmpClass = tmpChild.getAttribute("class");
            if (tmpClass != null
                    && (tmpClass.equals("ui-selectonemenu-trigger")
                        || tmpClass.startsWith("ui-selectonemenu-trigger ")
                        || tmpClass.endsWith(" ui-selectonemenu-trigger")
                        || tmpClass.contains(" ui-selectonemenu-trigger "))) {
                tmpTrigger = tmpChild;
                break;
            }
        }

        if (tmpTrigger == null) {
            final String tmpMessage = Messages.getMessage("PrimefacesOption_triggerNull", new String[] { tmpId });
            throw new ActionException(tmpMessage);
        }
        tmpTrigger.click();

        // now it is time to click on the popup
        tmpId += "_" + Integer.toString(tmpPos);

        tmpTarget = tmpPage.getElementById(tmpId);
        if (tmpTarget == null) {
            final String tmpMessage = Messages.getMessage("PrimefacesOption_optionNull", new String[] { tmpId });
            throw new ActionException(tmpMessage);
        }
        tmpTarget.click();
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
      final String tmpMessage = Messages.getMessage("backendError",
          new String[] { e.getMessage(), getDescribingText() });
      throw new ActionException(tmpMessage, e);
    } catch (final Throwable e) {
      final String tmpMessage = Messages
          .getMessage("serverError", new String[] { e.getMessage(), getDescribingText() });
      throw new ActionException(tmpMessage, e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.control.ISelectable#isSelected(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isSelected(final WetatorContext aWetatorContext) {
    final HtmlOption tmpHtmlOption = htmlOption;

    return tmpHtmlOption.isSelected();
  }

  @Override
  public String getUniqueSelector() {
    return getUniqueSelector(getHtmlElement());
  }

  @Override
  public boolean canReceiveFocus(final WetatorContext aWetatorContext) {
    return !isDisabled(aWetatorContext);
  }
}
