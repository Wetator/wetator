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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author rbri
 */
public class StringUtilTest extends TestCase {
    public StringUtilTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(StringUtilTest.class);
    }

    public void testExtractStrings_Null() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings(null, ",", 'x');
        assertEquals(0, tmpResult.size());
    }

    public void testExtractStrings1() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("", "", 'x');
        assertEquals(0, tmpResult.size());

        tmpResult = StringUtil.extractStrings("", "a", 'x');
        assertEquals(0, tmpResult.size());
    }

    public void testExtractStrings10() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("1xab2ab3ab4", "ab", 'x');
        assertEquals(3, tmpResult.size());
        assertEquals("1ab2", tmpResult.get(0));
        assertEquals("3", tmpResult.get(1));
        assertEquals("4", tmpResult.get(2));
    }

    public void testExtractStrings11() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("1ab2xab3ab4", "ab", 'x');
        assertEquals(3, tmpResult.size());
        assertEquals("1", tmpResult.get(0));
        assertEquals("2ab3", tmpResult.get(1));
        assertEquals("4", tmpResult.get(2));
    }

    public void testExtractStrings12() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("1ab2xab", "ab", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("1", tmpResult.get(0));
        assertEquals("2ab", tmpResult.get(1));
    }

    public void testExtractStrings13() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("1ab2xa", "ab", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("1", tmpResult.get(0));
        assertEquals("2xa", tmpResult.get(1));
    }

    public void testExtractStrings14() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("1ab2x", "ab", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("1", tmpResult.get(0));
        assertEquals("2x", tmpResult.get(1));
    }

    public void testExtractStrings15() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("1xxab2", "ab", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("1x", tmpResult.get(0));
        assertEquals("2", tmpResult.get(1));
    }

    public void testExtractStrings16() {
        try {
            StringUtil.extractStrings("1aa2", "a", 'a');
            assertTrue("IllegalArgumentException expected", false);
        } catch (IllegalArgumentException e) {
            assertEquals("Delimiter must be different from escape char.", e.getMessage());
        }
    }

    public void testExtractStrings17() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("1\\n2", "\\n", '\\');
        assertEquals(2, tmpResult.size());
        assertEquals("1", tmpResult.get(0));
        assertEquals("2", tmpResult.get(1));
    }

    public void testExtractStrings2() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("12a34", "a", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("12", tmpResult.get(0));
        assertEquals("34", tmpResult.get(1));
    }

    public void testExtractStrings3() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("12a34a", "a", 'x');
        assertEquals(3, tmpResult.size());
        assertEquals("12", tmpResult.get(0));
        assertEquals("34", tmpResult.get(1));
        assertEquals("", tmpResult.get(2));
    }

    public void testExtractStrings4() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("a", "a", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("", tmpResult.get(0));
        assertEquals("", tmpResult.get(1));
    }

    public void testExtractStrings5() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("aa", "a", 'x');
        assertEquals(3, tmpResult.size());
        assertEquals("", tmpResult.get(0));
        assertEquals("", tmpResult.get(1));
        assertEquals("", tmpResult.get(2));
    }

    public void testExtractStrings6() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("12aa34", "aa", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("12", tmpResult.get(0));
        assertEquals("34", tmpResult.get(1));
    }

    public void testExtractStrings7() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("12aa34a", "aa", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("12", tmpResult.get(0));
        assertEquals("34a", tmpResult.get(1));
    }

    public void testExtractStrings8() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("1a2aa34", "aa", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("1a2", tmpResult.get(0));
        assertEquals("34", tmpResult.get(1));
    }

    public void testExtractStrings9() {
        List<String> tmpResult;

        tmpResult = StringUtil.extractStrings("a12aa34", "aa", 'x');
        assertEquals(2, tmpResult.size());
        assertEquals("a12", tmpResult.get(0));
        assertEquals("34", tmpResult.get(1));
    }

    public void testForEmma() {
        new StringUtilEmma();
    }

    static class StringUtilEmma extends StringUtil {
        protected StringUtilEmma() {
            super();
        }
    }
}
