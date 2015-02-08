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


package org.wetator.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbri
 */
public class CommandTest {

  @Test
  public void constructor() {
    Command tmpCommand = new Command("command", false);

    Assert.assertEquals("command", tmpCommand.getName());
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals(-1, tmpCommand.getLineNo());
    // TODO Assert.assertEquals("", tmpCommand.toPrintableString(aContext));
  }

  @Test
  public void constructor_comment() {
    Command tmpCommand = new Command("command", true);

    Assert.assertEquals("command", tmpCommand.getName());
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals(-1, tmpCommand.getLineNo());
    // TODO Assert.assertEquals("", tmpCommand.toPrintableString(aContext));
  }
}
