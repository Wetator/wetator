/*
 * Copyright (c) 2008-2016 wetator.org
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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public class ParameterTest {

  private WetatorContext context;

  @Before
  public void setupMocks() {
    context = mock(WetatorContext.class);
    when(context.replaceVariables(anyString())).thenAnswer(new Answer<SecretString>() {
      @Override
      public SecretString answer(final InvocationOnMock anInvocation) throws Throwable {
        final Object[] tmpArgs = anInvocation.getArguments();
        return new SecretString((String) tmpArgs[0]);
      }
    });
  }

  @Test
  public void null1() throws Exception {
    final Parameter tmpParameter = new Parameter(null);
    Assert.assertNull(tmpParameter.getValue());
    Assert.assertEquals(0, tmpParameter.getNumberOfParts());
    Assert.assertEquals(0, tmpParameter.getParts().size());
  }

  @Test
  public void onePartEmpty() throws Exception {
    final Parameter tmpParameter = new Parameter("");
    Assert.assertEquals("", tmpParameter.getValue());
    Assert.assertEquals(0, tmpParameter.getNumberOfParts());
    Assert.assertEquals(0, tmpParameter.getParts().size());
  }

  @Test
  public void onePartBlank() throws Exception {
    final Parameter tmpParameter = new Parameter(" ");
    Assert.assertEquals(" ", tmpParameter.getValue());
    Assert.assertEquals(1, tmpParameter.getNumberOfParts());
    Assert.assertEquals(1, tmpParameter.getParts().size());
    Assert.assertEquals("", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void onePart() throws Exception {
    final Parameter tmpParameter = new Parameter("A");
    Assert.assertEquals("A", tmpParameter.getValue());
    Assert.assertEquals(1, tmpParameter.getNumberOfParts());
    Assert.assertEquals(1, tmpParameter.getParts().size());
    Assert.assertEquals("A", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("A", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void onePartWithBlank() throws Exception {
    final Parameter tmpParameter = new Parameter("A B");
    Assert.assertEquals("A B", tmpParameter.getValue());
    Assert.assertEquals(1, tmpParameter.getNumberOfParts());
    Assert.assertEquals(1, tmpParameter.getParts().size());
    Assert.assertEquals("A B", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("A B", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void onePartWithDelimiter() throws Exception {
    final Parameter tmpParameter = new Parameter("A\\,B");
    Assert.assertEquals("A\\,B", tmpParameter.getValue());
    Assert.assertEquals(1, tmpParameter.getNumberOfParts());
    Assert.assertEquals(1, tmpParameter.getParts().size());
    Assert.assertEquals("A,B", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("A,B", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void onePartEmptyWithDelimiter() throws Exception {
    final Parameter tmpParameter = new Parameter("\\,");
    Assert.assertEquals("\\,", tmpParameter.getValue());
    Assert.assertEquals(1, tmpParameter.getNumberOfParts());
    Assert.assertEquals(1, tmpParameter.getParts().size());
    Assert.assertEquals(",", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals(",", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsBothEmpty() throws Exception {
    final Parameter tmpParameter = new Parameter(",");
    Assert.assertEquals(",", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsFirstEmpty() throws Exception {
    final Parameter tmpParameter = new Parameter(",B");
    Assert.assertEquals(",B", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("B", tmpParameter.getParts().get(1).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsSecondEmpty() throws Exception {
    final Parameter tmpParameter = new Parameter("A,");
    Assert.assertEquals("A,", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("A", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getParts().get(1).getValue(context).getValue());
    Assert.assertEquals("A", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsBothBlank() throws Exception {
    final Parameter tmpParameter = new Parameter(" , ");
    Assert.assertEquals(" , ", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsFirstBlank() throws Exception {
    final Parameter tmpParameter = new Parameter(" ,B");
    Assert.assertEquals(" ,B", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("B", tmpParameter.getParts().get(1).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsSecondBlank() throws Exception {
    final Parameter tmpParameter = new Parameter("A, ");
    Assert.assertEquals("A, ", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("A", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("", tmpParameter.getParts().get(1).getValue(context).getValue());
    Assert.assertEquals("A", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoParts() throws Exception {
    final Parameter tmpParameter = new Parameter("A,B");
    Assert.assertEquals("A,B", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("A", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("B", tmpParameter.getParts().get(1).getValue(context).getValue());
    Assert.assertEquals("A", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsWithBlank() throws Exception {
    final Parameter tmpParameter = new Parameter("A 1,B 2");
    Assert.assertEquals("A 1,B 2", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("A 1", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("B 2", tmpParameter.getParts().get(1).getValue(context).getValue());
    Assert.assertEquals("A 1", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsWithDelimiter() throws Exception {
    final Parameter tmpParameter = new Parameter("A\\,1,B\\,2");
    Assert.assertEquals("A\\,1,B\\,2", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals("A,1", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals("B,2", tmpParameter.getParts().get(1).getValue(context).getValue());
    Assert.assertEquals("A,1", tmpParameter.getFirstPart().getValue(context).getValue());
  }

  @Test
  public void twoPartsEmptyWithDelimiter() throws Exception {
    final Parameter tmpParameter = new Parameter("\\,,\\,");
    Assert.assertEquals("\\,,\\,", tmpParameter.getValue());
    Assert.assertEquals(2, tmpParameter.getNumberOfParts());
    Assert.assertEquals(2, tmpParameter.getParts().size());
    Assert.assertEquals(",", tmpParameter.getParts().get(0).getValue(context).getValue());
    Assert.assertEquals(",", tmpParameter.getParts().get(1).getValue(context).getValue());
    Assert.assertEquals(",", tmpParameter.getFirstPart().getValue(context).getValue());
  }
}
