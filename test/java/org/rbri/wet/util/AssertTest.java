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


package org.rbri.wet.util;

import java.util.LinkedList;
import java.util.List;

import org.rbri.wet.exception.AssertionFailedException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author rbri
 */
public class AssertTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(AssertTest.class);
    }

    public void testAssertListMatch_Dots() throws AssertionFailedException {
        List<SecretString> tmpExpected = new LinkedList<SecretString>();
        tmpExpected.add(new SecretString("def", "def"));
        tmpExpected.add(new SecretString("...", "..."));
        
        Assert.assertListMatch(tmpExpected, " abc def ghi ... xyz");
    }

    public void testAssertListMatch_1() throws AssertionFailedException {
        List<SecretString> tmpExpected = new LinkedList<SecretString>();
        tmpExpected.add(new SecretString("GET Parameters", "GET Parameters"));
        tmpExpected.add(new SecretString("Key", "Key"));
        tmpExpected.add(new SecretString("Value", "Value"));
        tmpExpected.add(new SecretString("inputText_Name_Value InputTextNameValueTest", "inputText_Name_Value InputTextNameValueTest"));
        tmpExpected.add(new SecretString("OK", "OK"));
        
        Assert.assertListMatch(tmpExpected, "Request Snoopy @ rbri.de / rbri.org Home Projects NewView ProjectX jRipper WeT Links Imprint GET Parameters Key Value inputText_Name_Value InputTextNameValueTest OK POST Parameters Key Value Headers Key Value Host www.rbri.org User-Agent Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1 Accept-Language en-us,en;q=0.8,de-de;q=0.5,de;q=0.3 Referer http://wet.rbri.org/testcases/set.html Accept */* © rbri 2007, 2008");
    }

    public void testAssertListMatch_WrongOrder() {
        List<SecretString> tmpExpected = new LinkedList<SecretString>();
        tmpExpected.add(new SecretString("Pferde", "Pferde"));
        tmpExpected.add(new SecretString("keinen", "keinen"));
        tmpExpected.add(new SecretString("fressen", "fressen"));
        tmpExpected.add(new SecretString("Gurkensalat", "Gurkensalat"));
        
        try {
            Assert.assertListMatch(tmpExpected, "Pferde fressen keinen Gurkensalat");
            fail("AssertionFailedException expected");
        } catch (AssertionFailedException e) {
            assertEquals("Expected content(s) {not found} or [in wrong order]: 'Pferde, keinen, [fressen], Gurkensalat' (content: 'Pferde fressen keinen Gurkensalat').", e.getMessage());
        }
    }
    

}
