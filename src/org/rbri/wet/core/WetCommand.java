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


package org.rbri.wet.core;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

/**
 * The Command
 * 
 * @author rbri
 */
public final class WetCommand {
  private String name;
  private boolean isComment;
  private Parameter firstParameter;
  private Parameter secondParameter;
  private Parameter thirdParameter;
  private int lineNo;

  /**
   * Constructor
   * 
   * @param aName the name of the command
   * @param anIsCommentFlag true if the command is a comment
   */
  public WetCommand(String aName, boolean anIsCommentFlag) {
    name = aName;
    isComment = anIsCommentFlag;

    // debug
    lineNo = -1;
  }

  /**
   * @return the lineNo
   */
  public int getLineNo() {
    return lineNo;
  }

  /**
   * @param aLineNo the lineNo to set
   */
  public void setLineNo(int aLineNo) {
    lineNo = aLineNo;
  }

  /**
   * Getter for the name
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for the isComment flag
   * 
   * @return the isComment flag
   */
  public boolean isComment() {
    return isComment;
  }

  /**
   * @return the firstParameter
   */
  public Parameter getFirstParameter() {
    return firstParameter;
  }

  /**
   * @param aFirstParameter the firstParameter to set
   */
  public void setFirstParameter(Parameter aFirstParameter) {
    firstParameter = aFirstParameter;
  }

  /**
   * @return the secondParameter
   */
  public Parameter getSecondParameter() {
    return secondParameter;
  }

  /**
   * @param aSecondParameter the secondParameter to set
   */
  public void setSecondParameter(Parameter aSecondParameter) {
    secondParameter = aSecondParameter;
  }

  /**
   * @return the thirdParameter
   */
  public Parameter getThirdParameter() {
    return thirdParameter;
  }

  /**
   * @param aThirdParameter the thirdParameter to set
   */
  public void setThirdParameter(Parameter aThirdParameter) {
    thirdParameter = aThirdParameter;
  }

  public String toPrintableString(WetContext aWetContext) {
    StringBuilder tmpResult = new StringBuilder();
    tmpResult.append("[WetCommand '");
    tmpResult.append(getName());
    tmpResult.append("'");
    if (isComment) {
      tmpResult.append(" COMMENT");
    }

    Parameter tmpParameter = getFirstParameter();
    tmpResult.append(" firstParam:");
    tmpResult.append(" '");
    if (null != tmpParameter) {
      tmpResult.append(tmpParameter.getValue(aWetContext).toString());
    }
    tmpResult.append("'");

    tmpParameter = getSecondParameter();
    tmpResult.append(" '");
    if (null != tmpParameter) {
      tmpResult.append(getSecondParameter().getValue(aWetContext).toString());
    }
    tmpResult.append("'");

    tmpParameter = getThirdParameter();
    if (null != tmpParameter) {
      tmpResult.append(" '");
      tmpResult.append(getThirdParameter().getValue(aWetContext).toString());
      tmpResult.append("'");
    }

    tmpResult.append("]");
    return tmpResult.toString();
  }

  public List<SecretString> getFirstParameterValues(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpFirstParameter = getFirstParameter();
    List<SecretString> tmpResult = new LinkedList<SecretString>();

    if (null == tmpFirstParameter) {
      return tmpResult;
    }

    List<Parameter.Part> tmpParts = tmpFirstParameter.getParts();

    for (Parameter.Part tmpPart : tmpParts) {
      tmpResult.add(tmpPart.getValue(aWetContext));
    }

    return tmpResult;
  }

  public SecretString getFirstParameterValue(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpFirstParameter = getFirstParameter();

    if (null == tmpFirstParameter) {
      return null;
    }

    SecretString tmpFirstValue = tmpFirstParameter.getValue(aWetContext);
    return tmpFirstValue;
  }

  public List<SecretString> getRequiredFirstParameterValues(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpFirstParameter = getFirstParameter();

    if (null == tmpFirstParameter) {
      Assert.fail("emptyFirstParameter", new String[] { getName() });
    }

    List<Parameter.Part> tmpParts = tmpFirstParameter.getParts();

    List<SecretString> tmpResult = new LinkedList<SecretString>();
    for (Parameter.Part tmpPart : tmpParts) {
      tmpResult.add(tmpPart.getValue(aWetContext));
    }

    return tmpResult;
  }

  public SecretString getRequiredFirstParameterValue(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpFirstParameter = getFirstParameter();

    if (null == tmpFirstParameter) {
      Assert.fail("emptyFirstParameter", new String[] { getName() });
    }

    SecretString tmpFirstValue = tmpFirstParameter.getValue(aWetContext);
    return tmpFirstValue;
  }

  public List<SecretString> getRequiredSecondParameterValues(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      Assert.fail("emptySecondParameter", new String[] { getName() });
    }

    List<Parameter.Part> tmpParts = tmpSecondParameter.getParts();

    List<SecretString> tmpResult = new LinkedList<SecretString>();
    for (Parameter.Part tmpPart : tmpParts) {
      tmpResult.add(tmpPart.getValue(aWetContext));
    }

    return tmpResult;
  }

  public SecretString getRequiredSecondParameterValue(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      Assert.fail("emptySecondParameter", new String[] { getName() });
    }

    SecretString tmpSecondValue = tmpSecondParameter.getValue(aWetContext);
    return tmpSecondValue;
  }

  public List<SecretString> getSecondParameterValues(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpSecondParameter = getSecondParameter();

    List<SecretString> tmpResult = new LinkedList<SecretString>();
    if (null == tmpSecondParameter) {
      return tmpResult;
    }

    List<Parameter.Part> tmpParts = tmpSecondParameter.getParts();

    for (Parameter.Part tmpPart : tmpParts) {
      tmpResult.add(tmpPart.getValue(aWetContext));
    }

    return tmpResult;
  }

  public SecretString getSecondParameterValue(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      return null;
    }

    SecretString tmpSecondValue = tmpSecondParameter.getValue(aWetContext);
    return tmpSecondValue;
  }

  public long getSecondParameterLongValue(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      return -1;
    }

    SecretString tmpSecondValue = tmpSecondParameter.getValue(aWetContext);
    if (StringUtils.isEmpty(tmpSecondValue.getValue())) {
      return -1;
    }

    try {
      BigDecimal tmpValue = new BigDecimal(tmpSecondValue.getValue());
      return Long.valueOf(tmpValue.longValueExact());
    } catch (NumberFormatException e) {
      Assert.fail("integerParameterExpected", new String[] { getName(),
          tmpSecondParameter.getValue(aWetContext).toString(), "2" });
    } catch (ArithmeticException e) {
      Assert.fail("integerParameterExpected", new String[] { getName(),
          tmpSecondParameter.getValue(aWetContext).toString(), "2" });
    }
    return -1;
  }

  public void assertNoUnusedSecondParameter(WetContext aWetContext) throws AssertionFailedException {
    Parameter tmpParameter = getSecondParameter();
    if (null != tmpParameter) {
      Assert.fail("unusedParameter", new String[] { getName(), tmpParameter.getValue(aWetContext).toString(), "2" });
    }
  }
}
