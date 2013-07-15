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


package org.rbri.wet.backend;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rbri.wet.backend.WeightedControlList.Entry;
import org.rbri.wet.backend.htmlunit.HtmlUnitControl;
import org.rbri.wet.backend.htmlunit.util.PageUtil;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class WeightedControlListTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(WeightedControlListTest.class);
    }


    public void testIsEmpty() {
        WeightedControlList tmpWeightedControlList = new WeightedControlList();

        assertTrue(tmpWeightedControlList.isEmpty());
        assertFalse(tmpWeightedControlList.hasManyEntires());

        assertTrue(tmpWeightedControlList.getElementsSorted().isEmpty());
    }


    public void testOneEntry() throws IOException {
        HtmlUnitControl tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        WeightedControlList tmpWeightedControlList = new WeightedControlList();

        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11);

        assertFalse(tmpWeightedControlList.isEmpty());
        assertFalse(tmpWeightedControlList.hasManyEntires());

        assertFalse(tmpWeightedControlList.getElementsSorted().isEmpty());
    }


    public void testTwoEntries() throws IOException {
        HtmlUnitControl tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        WeightedControlList tmpWeightedControlList = new WeightedControlList();

        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11);
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 200, 11);

        assertFalse(tmpWeightedControlList.isEmpty());
        assertTrue(tmpWeightedControlList.hasManyEntires());

        assertFalse(tmpWeightedControlList.getElementsSorted().isEmpty());
    }


    public void testGetElementsSorted_Distance() throws IOException {
        WeightedControlList tmpWeightedControlList = new WeightedControlList();

        HtmlUnitControl tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 12);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 10);

        List<Entry> tmpSorted = tmpWeightedControlList.getElementsSorted();

        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 100 distance: 10", tmpSorted.get(0).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 100 distance: 11", tmpSorted.get(1).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 100 distance: 12", tmpSorted.get(2).toString());
    }


    public void testGetElementsSorted_Coverage() throws IOException {
        WeightedControlList tmpWeightedControlList = new WeightedControlList();

        HtmlUnitControl tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 10, 12);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11);

        List<Entry> tmpSorted = tmpWeightedControlList.getElementsSorted();

        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 10", tmpSorted.get(0).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 11", tmpSorted.get(1).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 9 distance: 11", tmpSorted.get(2).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 10 distance: 12", tmpSorted.get(3).toString());
    }

    public void testGetElementsSorted_FoundType() throws IOException {
        WeightedControlList tmpWeightedControlList = new WeightedControlList();

        HtmlUnitControl tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_LABEL, 10, 12);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11);

        List<Entry> tmpSorted = tmpWeightedControlList.getElementsSorted();

        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 10", tmpSorted.get(0).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 11", tmpSorted.get(1).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 9 distance: 11", tmpSorted.get(2).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_LABEL coverage: 10 distance: 12", tmpSorted.get(3).toString());
    }


    public void testGetElementsSorted_SameControl() throws IOException {
        WeightedControlList tmpWeightedControlList = new WeightedControlList();

        HtmlUnitControl tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11);
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_LABEL, 10, 12);
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10);

        tmpControl = new HtmlUnitControl(constructHtmlAnchor());
        tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11);

        List<Entry> tmpSorted = tmpWeightedControlList.getElementsSorted();

        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 10", tmpSorted.get(0).toString());
        assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 11", tmpSorted.get(1).toString());
    }


    private HtmlAnchor constructHtmlAnchor() throws IOException {
        String tmpHtmlCode = "<html><body>" + "<a href='wet.html'>AnchorText</a>" + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

        tmpHtmlElements.next();
        tmpHtmlElements.next();
        tmpHtmlElements.next();

        HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();
        return tmpAnchor;
    }
}
