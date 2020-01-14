/*
 * Copyright (c) 2008-2020 wetator.org
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


package org.wetator.i18n;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbri
 * @author frank.danek
 */
public class MessagesTest {

  @Test
  public void getMessage() {
    final String tmpMessage = Messages.getMessage("emptyFirstParameter", "param1");
    Assert.assertEquals("The command 'param1' requires a first parameter.", tmpMessage);
  }

  @Test
  public void getMessageParamsNull() {
    final String tmpMessage = Messages.getMessage("emptyFirstParameter");
    Assert.assertEquals("The command '{0}' requires a first parameter.", tmpMessage);
  }

  @Test
  public void getMessageWrongResource() {
    String tmpMessage = Messages.getMessage("unknown", "param1");
    Assert.assertEquals("Unknown message key 'unknown' (param(s):  'param1').", tmpMessage);

    tmpMessage = Messages.getMessage("unknown", "param1", "param2");
    Assert.assertEquals("Unknown message key 'unknown' (param(s):  'param1' 'param2').", tmpMessage);

    tmpMessage = Messages.getMessage("unknown");
    Assert.assertEquals("Unknown message key 'unknown'.", tmpMessage);

    tmpMessage = Messages.getMessage("unknown");
    Assert.assertEquals("Unknown message key 'unknown'.", tmpMessage);
  }
}
