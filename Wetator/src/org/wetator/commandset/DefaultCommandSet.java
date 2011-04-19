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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.wetator.backend.ControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WetBackend;
import org.wetator.backend.control.Control;
import org.wetator.backend.control.Deselectable;
import org.wetator.backend.control.Selectable;
import org.wetator.backend.control.Settable;
import org.wetator.core.WetCommand;
import org.wetator.core.WetContext;
import org.wetator.core.variable.Variable;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

/**
 * The implementation of all build in command that Wetator
 * supports at the moment.
 * 
 * @author rbri
 */
public final class DefaultCommandSet extends AbstractCommandSet {

  @Override
  protected void registerCommands() {
    registerCommand("Open Url", new CommandOpenUrl());
    registerCommand("Use Module", new CommandUseModule());
    registerCommand("Click On", new CommandClickOn());
    registerCommand("Click Double On", new CommandClickDoubleOn());
    registerCommand("Click Right On", new CommandClickRightOn());
    registerCommand("Set", new CommandSet());
    registerCommand("Select", new CommandSelect());
    registerCommand("Deselect", new CommandDeselect());
    registerCommand("Mouse Over", new CommandMouseOver());
    registerCommand("Close Window", new CommandCloseWindow());
    registerCommand("Go Back", new CommandGoBack());

    registerCommand("Assert Title", new CommandAssertTitle());
    registerCommand("Assert Content", new CommandAssertContent());
    registerCommand("Assert Disabled", new CommandAssertDisabled());
    registerCommand("Assert Set", new CommandAssertSet());
    registerCommand("Assert Selected", new CommandAssertSelected());
    registerCommand("Assert Deselected", new CommandAssertDeselected());

    registerCommand("Exec Java", new CommandExecJava());
  }

  /**
   * Command 'Open Url'.
   */
  public final class CommandOpenUrl implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final SecretString tmpUrlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      try {
        // create the complete URL
        final String tmpUrlToLower = tmpUrlParam.toLowerCase(Locale.ENGLISH);
        if (tmpUrlToLower.startsWith("http://") || tmpUrlToLower.startsWith("https://")) {
          aWetContext.informListenersWarn("absoluteUrl", new String[] { tmpUrlParam.toString() });
        } else {
          if (!tmpUrlParam.startsWith("/")) {
            tmpUrlParam.prefixWith("/");
          }

          tmpUrlParam.prefixWith(aWetContext.getWetConfiguration().getBaseUrl());
        }

        final URL tmpUrl = new URL(tmpUrlParam.getValue());
        aWetContext.informListenersInfo("openUrl", new String[] { tmpUrl.toString() });

        final WetBackend tmpBackend = getWetBackend(aWetContext);
        tmpBackend.openUrl(tmpUrl);
      } catch (final MalformedURLException e) {
        Assert.fail("invalidUrl", new String[] { tmpUrlParam.toString(), e.getMessage() });
      }
      getWetBackend(aWetContext).saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Use Module'.
   */
  public final class CommandUseModule implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final SecretString tmpModuleParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      final List<SecretString> tmpModuleParameters = aWetCommand.getSecondParameterValues(aWetContext);

      final String tmpModule = tmpModuleParam.getValue();

      File tmpFile = new File(tmpModule);

      if (!tmpFile.isAbsolute()) {
        tmpFile = new File(aWetContext.getFile().getParent(), tmpModule);
        aWetContext.informListenersInfo("useModule", new String[] { tmpFile.getAbsolutePath() });
      }

      // setup the new context
      final WetContext tmpWetContext = aWetContext.createSubContext(tmpFile);

      int i = 1;
      for (SecretString tmpSecretString : tmpModuleParameters) {
        final Variable tmpVariable = new Variable(Integer.toString(i), tmpSecretString);
        tmpWetContext.addVariable(tmpVariable);
        i++;
      }
      tmpWetContext.execute();
    }
  }

  /**
   * Command 'Set'.
   */
  public final class CommandSet implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aWetCommand.getFirstParameterValues(aWetContext));
      SecretString tmpValueParam = aWetCommand.getSecondParameterValue(aWetContext);
      if (null == tmpValueParam) {
        tmpValueParam = new SecretString("", "");
      }

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpElementFinder = tmpBackend.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpElementFinder.getAllSettables(tmpWPath);

      final Settable tmpControl = (Settable) getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath,
          "noSetableHtmlElmentFound");
      if (tmpWPath.isEmpty()) {
        aWetContext.informListenersWarn("firstElementUsed", new String[] { tmpControl.getDescribingText() });
      }
      tmpControl.setValue(aWetContext, tmpValueParam, aWetContext.getFile().getParentFile());
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Click On'.
   */
  public final class CommandClickOn implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // Buttons / Link / Image
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllClickables(tmpWPath);

      // Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath,
          "noClickableHtmlElmentFound");
      tmpControl.click(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Click Double On'.
   */
  public final class CommandClickDoubleOn implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath,
          "no2ClickableHtmlElmentFound");
      tmpControl.clickDouble(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Click Right On'.
   */
  public final class CommandClickRightOn implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath, "7");
      tmpControl.clickRight(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Select'.
   */
  public final class CommandSelect implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final Selectable tmpControl = (Selectable) getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements,
          tmpWPath, "noSelectableHtmlElmentFound");
      tmpControl.select(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Deselect'.
   */
  public final class CommandDeselect implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // (Select)Options / Checkboxes
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllDeselectables(tmpWPath);

      final Deselectable tmpControl = (Deselectable) getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements,
          tmpWPath, "noDeselectableHtmlElmentFound");
      tmpControl.deselect(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Mouse Over'.
   */
  public final class CommandMouseOver implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath,
          "noHtmlElementFound");
      tmpControl.mouseOver(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'close Window'.
   */
  public final class CommandCloseWindow implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final SecretString tmpWindowNameParam = aWetCommand.getFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.closeWindow(tmpWindowNameParam);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Go Back'.
   */
  public final class CommandGoBack implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {

      final SecretString tmpStepsParam = aWetCommand.getFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      int tmpSteps = 1;
      if (null != tmpStepsParam) {
        try {
          tmpSteps = Integer.parseInt(tmpStepsParam.getValue());
        } catch (final Exception e) {
          aWetContext.informListenersWarn("stepsNotANumber",
              new String[] { tmpStepsParam.toString(), Integer.toString(tmpSteps) });
        }
      }

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.goBackInCurrentWindow(tmpSteps);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Assert Title'.
   */
  public final class CommandAssertTitle implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final List<SecretString> tmpExpected = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      Long tmpTimeout = aWetCommand.getSecondParameterLongValue(aWetContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final boolean tmpContentChanged = tmpBackend.assertTitleInTimeFrame(tmpExpected, tmpTimeout);
      if (tmpContentChanged) {
        tmpBackend.saveCurrentWindowToLog();
      }
    }
  }

  /**
   * Command 'Assert Content'.
   */
  public final class CommandAssertContent implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final List<SecretString> tmpExpected = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      Long tmpTimeout = aWetCommand.getSecondParameterLongValue(aWetContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final boolean tmpContentChanged = tmpBackend.assertContentInTimeFrame(tmpExpected, tmpTimeout);
      if (tmpContentChanged) {
        tmpBackend.saveCurrentWindowToLog();
      }
    }
  }

  /**
   * Command 'Assert Disabled'.
   */
  public final class CommandAssertDisabled implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      // clickable Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath,
          "noHtmlElementFound");

      final boolean tmpIsDisabled = tmpControl.isDisabled(aWetContext);
      Assert.assertTrue(tmpIsDisabled, "elementNotDisabled", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Assert Set'.
   */
  public final class CommandAssertSet implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      SecretString tmpValueParam = aWetCommand.getSecondParameterValue(aWetContext);
      if (null == tmpValueParam) {
        tmpValueParam = new SecretString("", "");
      }

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);

      final Settable tmpControl = (Settable) getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath,
          "noSetableHtmlElmentFound");

      tmpControl.assertValue(aWetContext, tmpValueParam);
    }
  }

  /**
   * Command 'Assert Selected'.
   */
  public final class CommandAssertSelected implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final Selectable tmpControl = (Selectable) getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements,
          tmpWPath, "noSelectableHtmlElmentFound");

      final boolean tmpIsSelected = tmpControl.isSelected(aWetContext);
      Assert.assertTrue(tmpIsSelected, "elementNotSelected", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Assert Deselected'.
   */
  public final class CommandAssertDeselected implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final Selectable tmpControl = (Selectable) getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements,
          tmpWPath, "noDeselectableHtmlElmentFound");

      final boolean tmpIsSelected = tmpControl.isSelected(aWetContext);
      Assert.assertFalse(tmpIsSelected, "elementNotDeselected", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Exec Java'.
   */
  public final class CommandExecJava implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final SecretString tmpCall = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      final List<SecretString> tmpMethodParameters = aWetCommand.getSecondParameterValues(aWetContext);

      final String tmpCallString = tmpCall.toString();
      final int tmpLastDotPos = tmpCallString.lastIndexOf('.');
      if (tmpLastDotPos < 0) {
        Assert.fail("javaExecSyntax", new String[] { tmpCallString });
      }

      String tmpClassName = tmpCallString.substring(0, tmpLastDotPos);
      if (StringUtils.isEmpty(tmpClassName)) {
        Assert.fail("javaExecSyntax", new String[] { tmpCallString });
      }
      if (tmpClassName.endsWith("()")) {
        tmpClassName = tmpClassName.substring(0, tmpClassName.length() - 2);
      }

      String tmpMethodName = tmpCallString.substring(tmpLastDotPos + 1);
      if (StringUtils.isEmpty(tmpMethodName)) {
        Assert.fail("javaExecSyntax", new String[] { tmpCallString });
      }
      if (tmpMethodName.endsWith("()")) {
        tmpMethodName = tmpMethodName.substring(0, tmpMethodName.length() - 2);
      }

      Object[] tmpParams = new String[tmpMethodParameters.size()];
      final Class<String>[] tmpParamTypes = new Class[tmpMethodParameters.size()];
      int i = 0;
      for (SecretString tmpSecret : tmpMethodParameters) {
        tmpParams[i] = tmpSecret.toString();
        tmpParamTypes[i] = String.class;
        i++;
      }

      final StringBuffer tmpMethodLabel = new StringBuffer(tmpMethodName);

      try {
        final Class<?> tmpClass = ClassUtils.getClass(tmpClassName);
        Method tmpMethod = MethodUtils.getMatchingAccessibleMethod(tmpClass, tmpMethodName, tmpParamTypes);
        if (null == tmpMethod) {
          tmpMethod = MethodUtils.getMatchingAccessibleMethod(tmpClass, tmpMethodName, new Class[] { String[].class });
          tmpParams = new Object[] { tmpParams };
        }

        // build a more descriptive method name
        tmpMethodLabel.append('(');
        for (int j = 0; j < tmpParamTypes.length; j++) {
          if (j > 0) {
            tmpMethodLabel.append(", ");
          }
          tmpMethodLabel.append("String");
        }
        tmpMethodLabel.append(')');

        if (null == tmpMethod) {
          Assert.fail("javaExecMethodNotFound", new String[] { tmpClassName, tmpMethodLabel.toString() });
        }

        Object tmpReceiver = null;
        if (!Modifier.isStatic(tmpMethod.getModifiers())) {
          tmpReceiver = tmpClass.newInstance();
        }

        // time to execute
        final Object tmpResult = tmpMethod.invoke(tmpReceiver, tmpParams);
        if (Void.TYPE != tmpMethod.getReturnType()) {
          if (null == tmpResult) {
            aWetContext.informListenersInfo("javaExecResult", new String[] { "null" });
          } else {
            aWetContext.informListenersInfo("javaExecResult", new String[] { tmpResult.toString() });
          }
        }
      } catch (final NoClassDefFoundError e) {
        aWetContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        aWetContext.informListenersInfo("javaExecClasspath", new String[] { System.getProperty("java.class.path") });
        Assert.fail("javaExecClassNotFound", new String[] { tmpClassName, e.toString() });
      } catch (final ClassNotFoundException e) {
        aWetContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        aWetContext.informListenersInfo("javaExecClasspath", new String[] { System.getProperty("java.class.path") });
        Assert.fail("javaExecClassNotFound", new String[] { tmpClassName, e.toString() });
      } catch (final IllegalArgumentException e) {
        Assert.fail("javaExecIllegalArgument", new String[] { tmpClassName, tmpMethodLabel.toString(),
            tmpMethodParameters.toString(), e.getMessage() });
      } catch (final IllegalAccessException e) {
        aWetContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        Assert.fail("javaExecIllegalAccess", new String[] { tmpClassName, tmpMethodLabel.toString(),
            tmpMethodParameters.toString(), e.getMessage() });
      } catch (final InvocationTargetException e) {
        aWetContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        if (null == e.getCause()) {
          Assert.fail("javaExecInvocationTarget", new String[] { tmpClassName, tmpMethodLabel.toString(),
              tmpMethodParameters.toString(), e.toString() });
        } else {
          Assert.fail("javaExecInvocationTarget", new String[] { tmpClassName, tmpMethodLabel.toString(),
              tmpMethodParameters.toString(), e.getCause().toString() });
        }
      } catch (final InstantiationException e) {
        aWetContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        if (null == e.getCause()) {
          Assert.fail("javaExecInstantiation", new String[] { tmpClassName, tmpMethodLabel.toString(),
              tmpMethodParameters.toString(), e.toString() });
        } else {
          Assert.fail("javaExecInstantiation", new String[] { tmpClassName, tmpMethodLabel.toString(),
              tmpMethodParameters.toString(), e.getCause().toString() });
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.commandset.WetCommandSet#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do at the moment
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.commandset.WetCommandSet#cleanup()
   */
  @Override
  public void cleanup() {
    // nothing to do at the moment
  }
}
