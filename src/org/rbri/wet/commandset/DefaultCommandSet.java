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


package org.rbri.wet.commandset;

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
import org.rbri.wet.backend.Control;
import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.variable.Variable;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

/**
 * The implementation of all build in command that WeT
 * supports at the moment.
 * 
 * @author rbri
 */
public final class DefaultCommandSet extends AbstractCommandSet {
  // private static final Log LOG = LogFactory.getLog(DefaultCommandSet.class);

  /**
   * Constructor of the default command set
   */
  public DefaultCommandSet() {
    super();
  }

  @Override
  protected void registerCommands() {
    registerCommand("Open Url", new CommandOpenUrl());
    registerCommand("Use Module", new CommandUseModule());
    registerCommand("Click On", new CommandClickOn());
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
   * Command 'Open Url'
   */
  public final class CommandOpenUrl implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      SecretString tmpUrlParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      try {
        // create the complete URL
        String tmpUrlToLower = tmpUrlParam.toLowerCase(Locale.ENGLISH);
        if (tmpUrlToLower.startsWith("http://") || tmpUrlToLower.startsWith("https://")) {
          aWetContext.informListenersWarn("absoluteUrl", new String[] { tmpUrlParam.toString() });
        } else {
          if (!tmpUrlParam.startsWith("/")) {
            tmpUrlParam.prefixWith("/");
          }

          tmpUrlParam.prefixWith(aWetContext.getWetConfiguration().getBaseUrl());
        }

        URL tmpUrl = new URL(tmpUrlParam.getValue());
        aWetContext.informListenersInfo("openUrl", new String[] { tmpUrl.toString() });

        WetBackend tmpBackend = getWetBackend(aWetContext);
        tmpBackend.openUrl(tmpUrl);
      } catch (MalformedURLException e) {
        Assert.fail("invalidUrl", new String[] { tmpUrlParam.toString(), e.getMessage() });
      }
      getWetBackend(aWetContext).saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Use Module'
   */
  public final class CommandUseModule implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      SecretString tmpModuleParam = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      List<SecretString> tmpModuleParameters = aWetCommand.getSecondParameterValues(aWetContext);

      String tmpModule = tmpModuleParam.getValue();

      File tmpFile = new File(tmpModule);

      if (!tmpFile.isAbsolute()) {
        tmpFile = new File(aWetContext.getFile().getParent(), tmpModule);
        aWetContext.informListenersInfo("useModule", new String[] { tmpFile.getAbsolutePath() });
      }

      // setup the new context
      WetContext tmpWetContext = aWetContext.createSubContext(tmpFile);

      int i = 1;
      for (SecretString tmpSecretString : tmpModuleParameters) {
        Variable tmpVariable = new Variable("" + i, tmpSecretString);
        tmpWetContext.addVariable(tmpVariable);
        i++;
      }
      tmpWetContext.execute();
    }
  }

  /**
   * Command 'Set'
   */
  public final class CommandSet implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      List<SecretString> tmpSearchParam = aWetCommand.getFirstParameterValues(aWetContext);
      SecretString tmpValueParam = aWetCommand.getSecondParameterValue(aWetContext);
      if (null == tmpValueParam) {
        tmpValueParam = new SecretString("", "");
      }

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpElementFinder = tmpBackend.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      WeightedControlList tmpFoundElements = tmpElementFinder.getAllSetables(tmpSearchParam);

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
      if (tmpSearchParam.isEmpty()) {
        aWetContext.informListenersWarn("firstElementUsed", new String[] { tmpControl.getDescribingText() });
      }
      tmpControl.setValue(aWetContext, tmpValueParam, aWetContext.getFile().getParentFile());
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Click On'
   */
  public final class CommandClickOn implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // Buttons / Link / Image
      WeightedControlList tmpFoundElements = tmpControlFinder.getAllClickables(tmpSearchParam);

      // Text
      // tmpFoundElements.addAll(tmpControlFinder.getFirstClickableTextElement(tmpSearchParam));
      tmpFoundElements.addAll(tmpControlFinder.getAllElementsForText(tmpSearchParam));

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
      tmpControl.click(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Select'
   */
  public final class CommandSelect implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpSearchParam);

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
      tmpControl.select(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Deselect'
   */
  public final class CommandDeselect implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // (Select)Options / Checkboxes
      WeightedControlList tmpFoundElements = tmpControlFinder.getAllDeselectables(tmpSearchParam);

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
      tmpControl.deselect(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Mouse Over'
   */
  public final class CommandMouseOver implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      WeightedControlList tmpFoundElements = tmpControlFinder.getAllElementsForText(tmpSearchParam);

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);
      tmpControl.mouseOver(aWetContext);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'close Window'
   */
  public final class CommandCloseWindow implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      SecretString tmpWindowNameParam = aWetCommand.getFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.closeWindow(tmpWindowNameParam);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Go Back'
   */
  public final class CommandGoBack implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      SecretString tmpStepsParam = aWetCommand.getFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      int tmpSteps = 1;
      if (null != tmpStepsParam) {
        try {
          tmpSteps = Integer.parseInt(tmpStepsParam.getValue());
        } catch (Exception e) {
          aWetContext.informListenersWarn("stepsNotANumber", new String[] { tmpStepsParam.toString(), "" + tmpSteps });
        }
      }

      WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.goBackInCurrentWindow(tmpSteps);
      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Assert Title'
   */
  public final class CommandAssertTitle implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      List<SecretString> tmpExpected = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      Long tmpTimeout = aWetCommand.getSecondParameterLongValue(aWetContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.waitForTitle(tmpExpected, tmpTimeout);
    }
  }

  /**
   * Command 'Assert Content'
   */
  public final class CommandAssertContent implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      List<SecretString> tmpExpected = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      Long tmpTimeout = aWetCommand.getSecondParameterLongValue(aWetContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.waitForContent(tmpExpected, tmpTimeout);
    }
  }

  /**
   * Command 'Assert Disabled'
   */
  public final class CommandAssertDisabled implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      WeightedControlList tmpFoundElements = tmpControlFinder.getAllSetables(tmpSearchParam);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpSearchParam));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpSearchParam));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpSearchParam));

      // clickable Text
      // tmpFoundElements.addAll(tmpControlFinder.getFirstClickableTextElement(tmpSearchParam));
      tmpFoundElements.addAll(tmpControlFinder.getAllElementsForText(tmpSearchParam));

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);

      boolean tmpIsDisabled = tmpControl.isDisabled(aWetContext);
      Assert.assertTrue(tmpIsDisabled, "elementNotDisabled", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Assert Set'
   */
  public final class CommandAssertSet implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      SecretString tmpValueParam = aWetCommand.getRequiredSecondParameterValue(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      WeightedControlList tmpFoundElements = tmpControlFinder.getAllSetables(tmpSearchParam);

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);

      String tmpValue = tmpControl.getValue(aWetContext);

      Assert.assertEquals(tmpValueParam, tmpValue, "expectedValueNotFound", null);
    }
  }

  /**
   * Command 'Assert Selected'
   */
  public final class CommandAssertSelected implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpSearchParam);

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);

      boolean tmpIsSelected = tmpControl.isSelected(aWetContext);
      Assert.assertTrue(tmpIsSelected, "elementNotSelected", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Assert Deselected'
   */
  public final class CommandAssertDeselected implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      List<SecretString> tmpSearchParam = aWetCommand.getRequiredFirstParameterValues(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      WetBackend tmpBackend = getWetBackend(aWetContext);
      ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpSearchParam);

      Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpSearchParam);

      boolean tmpIsSelected = tmpControl.isSelected(aWetContext);
      Assert.assertFalse(tmpIsSelected, "elementNotDeselected", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Exec Java'
   */
  public final class CommandExecJava implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.rbri.wet.commandset.WetCommandImplementation#execute(org.rbri.wet.core.WetContext,
     *      org.rbri.wet.core.WetCommand)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {
      SecretString tmpCall = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      List<SecretString> tmpMethodParameters = aWetCommand.getSecondParameterValues(aWetContext);

      String tmpCallString = tmpCall.toString();
      int tmpLastDotPos = tmpCallString.lastIndexOf(".");
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
      Class<String>[] tmpParamTypes = new Class[tmpMethodParameters.size()];
      int i = 0;
      for (SecretString tmpSecret : tmpMethodParameters) {
        tmpParams[i] = tmpSecret.toString();
        tmpParamTypes[i] = String.class;
        i++;
      }

      String tmpMethodLabel = tmpMethodName;

      try {
        Class<?> tmpClass = ClassUtils.getClass(tmpClassName);
        Method tmpMethod = MethodUtils.getMatchingAccessibleMethod(tmpClass, tmpMethodName, tmpParamTypes);
        if (null == tmpMethod) {
          tmpMethod = MethodUtils.getMatchingAccessibleMethod(tmpClass, tmpMethodName, new Class[] { String[].class });
          tmpParams = new Object[] { tmpParams };
        }

        // build a more descriptive method name
        tmpMethodLabel += "(";
        for (int j = 0; j < tmpParamTypes.length; j++) {
          if (j > 0) {
            tmpMethodLabel += ", ";
          }
          tmpMethodLabel += "String";
        }
        tmpMethodLabel += ")";

        if (null == tmpMethod) {
          Assert.fail("javaExecMethodNotFound", new String[] { tmpClassName, tmpMethodLabel });
        }

        Object tmpReceiver = null;
        if (!Modifier.isStatic(tmpMethod.getModifiers())) {
          tmpReceiver = tmpClass.newInstance();
        }

        // time to execute
        Object tmpResult = tmpMethod.invoke(tmpReceiver, tmpParams);
        if (Void.TYPE != tmpMethod.getReturnType()) {
          if (null == tmpResult) {
            aWetContext.informListenersInfo("javaExecResult", new String[] { "null" });
          } else {
            aWetContext.informListenersInfo("javaExecResult", new String[] { tmpResult.toString() });
          }
        }
      } catch (ClassNotFoundException e) {
        aWetContext.informListenersInfo("javaExecClasspath", new String[] { System.getProperty("java.class.path") });
        Assert.fail("javaExecClassNotFound", new String[] { tmpClassName });
      } catch (IllegalArgumentException e) {
        Assert.fail("javaExecIllegalArgument", new String[] { tmpClassName, tmpMethodLabel,
            tmpMethodParameters.toString(), e.getMessage() });
      } catch (IllegalAccessException e) {
        aWetContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        Assert.fail("javaExecIllegalAccess", new String[] { tmpClassName, tmpMethodLabel,
            tmpMethodParameters.toString(), e.getMessage() });
      } catch (InvocationTargetException e) {
        aWetContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        if (null == e.getCause()) {
          Assert.fail("javaExecInvocationTarget", new String[] { tmpClassName, tmpMethodLabel,
              tmpMethodParameters.toString(), e.toString() });
        } else {
          Assert.fail("javaExecInvocationTarget", new String[] { tmpClassName, tmpMethodLabel,
              tmpMethodParameters.toString(), e.getCause().toString() });
        }
      } catch (InstantiationException e) {
        aWetContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        if (null == e.getCause()) {
          Assert.fail("javaExecInstantiation", new String[] { tmpClassName, tmpMethodLabel,
              tmpMethodParameters.toString(), e.toString() });
        } else {
          Assert.fail("javaExecInstantiation", new String[] { tmpClassName, tmpMethodLabel,
              tmpMethodParameters.toString(), e.getCause().toString() });
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.WetCommandSet#initialize(java.util.Properties)
   */
  @Override
  public void initialize(Properties aConfiguration) {
    // nothing to do at the moment
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.WetCommandSet#cleanup()
   */
  @Override
  public void cleanup() {
    // nothing to do at the moment
  }
}
