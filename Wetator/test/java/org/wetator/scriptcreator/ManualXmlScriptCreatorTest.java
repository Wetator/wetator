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


package org.wetator.scriptcreator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.wetator.core.Parameter;
import org.wetator.core.WetCommand;
import org.wetator.exception.WetException;

/**
 * @author frank.danek
 */
public class ManualXmlScriptCreatorTest {

  /**
   * @throws WetException if something goes wrong
   */
  @Test
  public void manual() throws WetException {
    List<WetCommand> tmpCommands = new ArrayList<WetCommand>();
    WetCommand tmpCommand;
    tmpCommand = new WetCommand("assert-content", false);
    tmpCommand.setLineNo(1);
    tmpCommand.setFirstParameter(new Parameter("dies ist ein testäöü"));
    tmpCommands.add(tmpCommand);
    tmpCommand = new WetCommand("click-on", true);
    tmpCommand.setLineNo(2);
    tmpCommand.setFirstParameter(new Parameter("blabla"));
    tmpCommands.add(tmpCommand);
    tmpCommand = new WetCommand("", true);
    tmpCommand.setLineNo(3);
    tmpCommand.setFirstParameter(new Parameter("Schöner Kommentar"));
    tmpCommands.add(tmpCommand);
    tmpCommand = new WetCommand("exec-sql", false);
    tmpCommand.setLineNo(4);
    tmpCommand.setFirstParameter(new Parameter("SELECT sysdate FROM dual"));
    tmpCommands.add(tmpCommand);

    XmlScriptCreator tmpXmlScriptCreator = new XmlScriptCreator();
    tmpXmlScriptCreator.setOutputDir("build");
    tmpXmlScriptCreator.setFileName("test");
    tmpXmlScriptCreator.setCommands(tmpCommands);

    tmpXmlScriptCreator.createScript();
  }
}
