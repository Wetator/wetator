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


package org.wetator.core;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

/**
 * The Command;
 * our class that represents a command read from an source file.
 * 
 * @author rbri
 */
public final class Command {
  private String name;
  private boolean isComment;
  private Parameter firstParameter;
  private Parameter secondParameter;
  private Parameter thirdParameter;
  private int lineNo;

  /**
   * The constructor.
   * 
   * @param aName the name of the command
   * @param anIsCommentFlag true if the command is a comment
   */
  public Command(final String aName, final boolean anIsCommentFlag) {
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
  public void setLineNo(final int aLineNo) {
    lineNo = aLineNo;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
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
  public void setFirstParameter(final Parameter aFirstParameter) {
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
  public void setSecondParameter(final Parameter aSecondParameter) {
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
  public void setThirdParameter(final Parameter aThirdParameter) {
    thirdParameter = aThirdParameter;
  }

  /**
   * Creates a printable String from the command.
   * This takes care of secrets.
   * 
   * @param aWetContext the context
   * @return the string
   */
  public String toPrintableString(final WetContext aWetContext) {
    final StringBuilder tmpResult = new StringBuilder();
    tmpResult.append("[Command '");
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

  /**
   * Returns the list of secret strings parsed from the first parameter.
   * 
   * @param aWetContext the context
   * @return the list of secret strings (never null)
   */
  public List<SecretString> getFirstParameterValues(final WetContext aWetContext) {
    final Parameter tmpFirstParameter = getFirstParameter();
    final List<SecretString> tmpResult = new LinkedList<SecretString>();

    if (null == tmpFirstParameter) {
      return tmpResult;
    }

    final List<Parameter.Part> tmpParts = tmpFirstParameter.getParts();

    for (Parameter.Part tmpPart : tmpParts) {
      tmpResult.add(tmpPart.getValue(aWetContext));
    }

    return tmpResult;
  }

  /**
   * Returns the first parameter as secret string.
   * The parameter is taken at whole, not parsed.
   * 
   * @param aWetContext the context
   * @return a secret string or null if the first parameter was not set
   */
  public SecretString getFirstParameterValue(final WetContext aWetContext) {
    final Parameter tmpFirstParameter = getFirstParameter();

    if (null == tmpFirstParameter) {
      return null;
    }

    final SecretString tmpFirstValue = tmpFirstParameter.getValue(aWetContext);
    return tmpFirstValue;
  }

  /**
   * Returns the list of secret strings parsed from the first parameter.
   * 
   * @param aWetContext the context
   * @return the list of secret strings (never null)
   * @throws AssertionFailedException if the first parameter was not set
   */
  public List<SecretString> getRequiredFirstParameterValues(final WetContext aWetContext)
      throws AssertionFailedException {
    final Parameter tmpFirstParameter = getFirstParameter();

    if (null == tmpFirstParameter) {
      Assert.fail("emptyFirstParameter", new String[] { getName() });
    }

    return getFirstParameterValues(aWetContext);
  }

  /**
   * Returns the first parameter as secret string.
   * The parameter is taken at whole, not parsed.
   * 
   * @param aWetContext the context
   * @return a secret string
   * @throws AssertionFailedException if the first parameter was not set
   */
  public SecretString getRequiredFirstParameterValue(final WetContext aWetContext) throws AssertionFailedException {
    final Parameter tmpFirstParameter = getFirstParameter();

    if (null == tmpFirstParameter) {
      Assert.fail("emptyFirstParameter", new String[] { getName() });
    }

    final SecretString tmpFirstValue = tmpFirstParameter.getValue(aWetContext);
    return tmpFirstValue;
  }

  /**
   * Returns the list of secret strings parsed from the second parameter.
   * 
   * @param aWetContext the context
   * @return the list of secret strings (never null)
   * @throws AssertionFailedException if the first parameter was not set
   */
  public List<SecretString> getRequiredSecondParameterValues(final WetContext aWetContext)
      throws AssertionFailedException {
    final Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      Assert.fail("emptySecondParameter", new String[] { getName() });
    }

    return getSecondParameterValues(aWetContext);
  }

  /**
   * Returns the second parameter as secret string.
   * The parameter is taken at whole, not parsed.
   * 
   * @param aWetContext the context
   * @return a secret string
   * @throws AssertionFailedException if the second parameter was not set
   */
  public SecretString getRequiredSecondParameterValue(final WetContext aWetContext) throws AssertionFailedException {
    final Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      Assert.fail("emptySecondParameter", new String[] { getName() });
    }

    final SecretString tmpSecondValue = tmpSecondParameter.getValue(aWetContext);
    return tmpSecondValue;
  }

  /**
   * Returns the list of secret strings parsed from the second parameter.
   * 
   * @param aWetContext the context
   * @return the list of secret strings (never null)
   */
  public List<SecretString> getSecondParameterValues(final WetContext aWetContext) {
    final Parameter tmpSecondParameter = getSecondParameter();

    final List<SecretString> tmpResult = new LinkedList<SecretString>();
    if (null == tmpSecondParameter) {
      return tmpResult;
    }

    final List<Parameter.Part> tmpParts = tmpSecondParameter.getParts();

    for (Parameter.Part tmpPart : tmpParts) {
      tmpResult.add(tmpPart.getValue(aWetContext));
    }

    return tmpResult;
  }

  /**
   * Returns the second parameter as secret string.
   * The parameter is taken at whole, not parsed.
   * 
   * @param aWetContext the context
   * @return a secret string or null if the second parameter was not set
   */
  public SecretString getSecondParameterValue(final WetContext aWetContext) {
    final Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      return null;
    }

    final SecretString tmpSecondValue = tmpSecondParameter.getValue(aWetContext);
    return tmpSecondValue;
  }

  /**
   * Returns the second parameter as long.
   * 
   * @param aWetContext the context
   * @return a Long (or null if not set)
   * @throws AssertionFailedException if the second parameter is not convertable into a long
   */
  public Long getSecondParameterLongValue(final WetContext aWetContext) throws AssertionFailedException {
    final Parameter tmpSecondParameter = getSecondParameter();

    if (null == tmpSecondParameter) {
      return null;
    }

    final SecretString tmpSecondValue = tmpSecondParameter.getValue(aWetContext);
    if (StringUtils.isEmpty(tmpSecondValue.getValue())) {
      return null;
    }

    try {
      final BigDecimal tmpValue = new BigDecimal(tmpSecondValue.getValue());
      return Long.valueOf(tmpValue.longValueExact());
    } catch (final NumberFormatException e) {
      Assert.fail("integerParameterExpected", new String[] { getName(),
          tmpSecondParameter.getValue(aWetContext).toString(), "2" });
    } catch (final ArithmeticException e) {
      Assert.fail("integerParameterExpected", new String[] { getName(),
          tmpSecondParameter.getValue(aWetContext).toString(), "2" });
    }
    return null;
  }

  /**
   * Asserts that the second parameter is not set.
   * 
   * @param aWetContext the context
   * @throws AssertionFailedException if the second parameter is set.
   */
  public void assertNoUnusedSecondParameter(final WetContext aWetContext) throws AssertionFailedException {
    final Parameter tmpParameter = getSecondParameter();
    if (null != tmpParameter) {
      Assert.fail("unusedParameter", new String[] { getName(), tmpParameter.getValue(aWetContext).toString(), "2" });
    }
  }
}