/*
 * Copyright (c) 2008-2021 wetator.org
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

/**
 * @author frank.danek
 */
public class TestCaseTest {

  @Test
  public void values_null() {
    final TestCase tmpTestCase = new TestCase(null, null);

    assertNull(tmpTestCase.getName());
    assertNull(tmpTestCase.getFile());
  }

  @Test
  public void values() {
    final TestCase tmpTestCase = new TestCase("myName", new File("myFile"));

    assertEquals("myName", tmpTestCase.getName());
    assertEquals("myFile", tmpTestCase.getFile().getName());
  }
}