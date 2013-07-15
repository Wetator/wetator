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

import java.util.LinkedList;
import java.util.List;

import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
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

    public WetCommand(String aName, boolean anIsCommentFlag) {
        name = aName;
        isComment = anIsCommentFlag;

        // debug
        lineNo = -1;
    }



    public String getName() {
        return name;
    }


    public boolean isComment() {
        return isComment;
    }


    public void setFirstParameter(Parameter aParameter) {
        firstParameter = aParameter;
    }


    public void setSecondParameter(Parameter aParameter) {
        secondParameter = aParameter;
    }


    public void setThirdParameter(Parameter aParameter) {
        thirdParameter = aParameter;
    }


    public Parameter getFirstParameter() {
        return firstParameter;
    }


    public Parameter getSecondParameter() {
        return secondParameter;
    }


    public Parameter getThirdParameter() {
        return thirdParameter;
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


    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int aLineNo) {
        lineNo = aLineNo;
    }


    public List<SecretString> getFirstParameterValues(WetContext aWetContext) throws AssertionFailedException, WetException {
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


    public SecretString getFirstParameterValue(WetContext aWetContext) throws AssertionFailedException, WetException {
        Parameter tmpFirstParameter = getFirstParameter();

        if (null == tmpFirstParameter) {
            return null;
        }

        SecretString tmpFirstValue = tmpFirstParameter.getValue(aWetContext);
        return tmpFirstValue;
    }


    public List<SecretString> getRequiredFirstParameterValues(WetContext aWetContext) throws AssertionFailedException, WetException {
        Parameter tmpFirstParameter = getFirstParameter();

        if (null == tmpFirstParameter) {
            Assert.fail("emptyFirstParameter", new String[] {getName()});
        }

        List<Parameter.Part> tmpParts = tmpFirstParameter.getParts();

        List<SecretString> tmpResult = new LinkedList<SecretString>();
        for (Parameter.Part tmpPart : tmpParts) {
            tmpResult.add(tmpPart.getValue(aWetContext));
        }

        return tmpResult;
    }


    public SecretString getRequiredFirstParameterValue(WetContext aWetContext) throws AssertionFailedException, WetException {
        Parameter tmpFirstParameter = getFirstParameter();

        if (null == tmpFirstParameter) {
            Assert.fail("emptyFirstParameter", new String[] {getName()});
        }

        SecretString tmpFirstValue = tmpFirstParameter.getValue(aWetContext);
        return tmpFirstValue;
    }


    public List<SecretString> getRequiredSecondParameterValues(WetContext aWetContext) throws AssertionFailedException, WetException {
        Parameter tmpSecondParameter = getSecondParameter();

        if (null == tmpSecondParameter) {
            Assert.fail("emptySecondParameter", new String[] {getName()});
        }

        List<Parameter.Part> tmpParts = tmpSecondParameter.getParts();

        List<SecretString> tmpResult = new LinkedList<SecretString>();
        for (Parameter.Part tmpPart : tmpParts) {
            tmpResult.add(tmpPart.getValue(aWetContext));
        }

        return tmpResult;
    }


    public SecretString getRequiredSecondParameterValue(WetContext aWetContext) throws AssertionFailedException, WetException {
        Parameter tmpSecondParameter = getSecondParameter();

        if (null == tmpSecondParameter) {
            Assert.fail("emptySecondParameter", new String[] {getName()});
        }

        SecretString tmpSecondValue = tmpSecondParameter.getValue(aWetContext);
        return tmpSecondValue;
    }


    public List<SecretString> getSecondParameterValues(WetContext aWetContext) throws AssertionFailedException, WetException {
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


    public SecretString getSecondParameterValue(WetContext aWetContext) throws AssertionFailedException, WetException {
        Parameter tmpSecondParameter = getSecondParameter();

        if (null == tmpSecondParameter) {
            return null;
        }

        SecretString tmpSecondValue = tmpSecondParameter.getValue(aWetContext);
        return tmpSecondValue;
    }


    public void assertNoUnusedSecondParameter(WetContext aWetContext) throws AssertionFailedException {
        Parameter tmpParameter = getSecondParameter();
        if (null != tmpParameter) {
            Assert.fail("unusedParameter", new String[] {getName(), tmpParameter.getValue(aWetContext).toString(), "2"});
        }
    }
}
