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

import java.util.List;
import java.util.Properties;

import org.rbri.wet.core.Parameter;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

/**
 * Commands to make it possible to test other commands (e.g. error situations).
 * 
 * @author rbri
 */
public final class TestCommandSet extends AbstractCommandSet {

  public TestCommandSet() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.AbstractCommandSet#registerCommands()
   */
  @Override
  protected void registerCommands() {
    registerCommand("Assert Fail", new CommandAssertFail());
  }

  /**
   * The assert fail command.
   */
  public final class CommandAssertFail implements WetCommandImplementation {
    @Override
    public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException {

      List<Parameter.Part> tmpFirstParameters = aWetCommand.getFirstParameter().getParts();
      SecretString tmpExpected = tmpFirstParameters.get(1).getValue(aWetContext);

      SecretString tmpCommandParam = tmpFirstParameters.get(0).getValue(aWetContext);
      WetCommand tmpCommand = new WetCommand(tmpCommandParam.getValue(), false);
      tmpCommand.setLineNo(aWetCommand.getLineNo());

      tmpCommand.setFirstParameter(aWetCommand.getSecondParameter());
      if (null != aWetCommand.getThirdParameter()) {
        tmpCommand.setSecondParameter(aWetCommand.getThirdParameter());
      }

      try {
        aWetContext.determineAndExecuteCommandImpl(tmpCommand);
      } catch (AssertionFailedException e) {
        String tmpResult = e.getMessage();
        Assert.assertMatch(tmpExpected.toString(), tmpResult, "wrongErrorMessage", null);
        return;
      }

      Assert.fail("expectedErrorNotThrown", null);
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
    // nothing
  }
}
