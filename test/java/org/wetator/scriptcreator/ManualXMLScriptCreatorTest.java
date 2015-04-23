/*
 * Copyright (c) 2008-2015 wetator.org
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
import org.wetator.core.Command;
import org.wetator.core.Parameter;

/**
 * @author frank.danek
 */
public class ManualXMLScriptCreatorTest {

  @Test
  public void manual() {
    final List<Command> tmpCommands = new ArrayList<Command>();
    Command tmpCommand;
    tmpCommand = new Command("assert-content", false);
    tmpCommand.setLineNo(1);
    tmpCommand.setFirstParameter(new Parameter("dies ist ein test���"));
    tmpCommands.add(tmpCommand);
    tmpCommand = new Command("click-on", true);
    tmpCommand.setLineNo(2);
    tmpCommand.setFirstParameter(new Parameter("blabla"));
    tmpCommands.add(tmpCommand);
    tmpCommand = new Command("", true);
    tmpCommand.setLineNo(3);
    tmpCommand.setFirstParameter(new Parameter("Sch�ner Kommentar"));
    tmpCommands.add(tmpCommand);
    tmpCommand = new Command("exec-sql", false);
    tmpCommand.setLineNo(4);
    tmpCommand.setFirstParameter(new Parameter("SELECT sysdate FROM dual"));
    tmpCommands.add(tmpCommand);

    final XMLScriptCreator tmpXMLScriptCreator = new XMLScriptCreator();
    tmpXMLScriptCreator.setOutputDir("build");
    tmpXMLScriptCreator.setFileName("test");
    tmpXMLScriptCreator.setCommands(tmpCommands);

    tmpXMLScriptCreator.createScript();
  }
}
