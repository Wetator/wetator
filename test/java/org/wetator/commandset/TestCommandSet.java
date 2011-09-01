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

import java.util.List;
import java.util.Properties;

import org.wetator.core.Command;
import org.wetator.core.ICommandImplementation;
import org.wetator.core.Parameter;
import org.wetator.core.WetatorContext;
import org.wetator.exception.AssertionFailedException;
import org.wetator.exception.CommandExecutionException;
import org.wetator.util.Assert;
import org.wetator.util.NormalizedString;
import org.wetator.util.SecretString;

/**
 * Commands to make it possible to test other commands (e.g. error situations).
 * 
 * @author rbri
 * @author frank.danek
 */
public final class TestCommandSet extends AbstractCommandSet {

  public TestCommandSet() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.commandset.AbstractCommandSet#registerCommands()
   */
  @Override
  protected void registerCommands() {
    registerCommand("assert-fail", new CommandAssertFail());
  }

  /**
   * The assert fail command.
   */
  public final class CommandAssertFail implements ICommandImplementation {
    @Override
    public void execute(WetatorContext aContext, Command aCommand) throws CommandExecutionException {

      List<Parameter.Part> tmpFirstParameters = aCommand.getFirstParameter().getParts();
      SecretString tmpExpected = tmpFirstParameters.get(1).getValue(aContext);

      SecretString tmpCommandParam = tmpFirstParameters.get(0).getValue(aContext);
      String tmpCommandName = tmpCommandParam.getValue();
      // normalize command name
      tmpCommandName = tmpCommandName.replace(' ', '-').replace('_', '-').toLowerCase();
      Command tmpCommand = new Command(tmpCommandName, false);
      tmpCommand.setLineNo(aCommand.getLineNo());

      tmpCommand.setFirstParameter(aCommand.getSecondParameter());
      if (null != aCommand.getThirdParameter()) {
        tmpCommand.setSecondParameter(aCommand.getThirdParameter());
      }

      try {
        aContext.determineAndExecuteCommandImpl(tmpCommand);
      } catch (AssertionFailedException e) {
        NormalizedString tmpResult = new NormalizedString(e.getMessage());
        Assert.assertMatch(new NormalizedString(tmpExpected.toString()).toString(), tmpResult.toString(),
            "wrongErrorMessage", null);
        return;
      }

      Assert.fail("expectedErrorNotThrown", null);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.ICommandSet#initialize(java.util.Properties)
   */
  @Override
  public void initialize(Properties aConfiguration) {
    // nothing to do at the moment
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.ICommandSet#cleanup()
   */
  @Override
  public void cleanup() {
    // nothing
  }
}
