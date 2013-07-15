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

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.rbri.wet.core.Parameter;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.result.WetResultWriter;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
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

    protected void registerCommands() {
        registerCommand("Assert Fail", new CommandAssertFail());
    }

    public final class CommandAssertFail implements WetCommandImplementation {
        public void execute(WetContext aWetContext, WetCommand aWetCommand) throws WetException, AssertionFailedException {

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
                Assert.assertEquals(tmpExpected.toString(), tmpResult, "wrongErrorMessage", null);
                return;
            }

            Assert.fail("expectedErrorNotThrown", null);
        }
    }

    public void initialize(Properties aConfiguration) {
        // nothing to do at the moment
    }

    public void cleanup() {
        // nothing to do at the moment
    }

    public void printConfiguration(WetResultWriter wetResultWriter) throws IOException {
        // nothing to do at the moment
    }
}
