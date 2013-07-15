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


package org.rbri.wet.backend.htmlunit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class HtmlUnitControlFinderGetFirstClickableTextElementTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(HtmlUnitControlFinderGetFirstClickableTextElementTest.class);
    }

    public void testGetFirstClickableTextElement_Empty() throws IOException {
        String tmpHtmlCode = "<html><body>" + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("Name", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(0, tmpFound.getElementsSorted().size());
    }

    public void testGetFirstClickableTextElement_ById() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<p id='MyId1'>First line</p>"
            + "<p id='MyId2'>Second line</p>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("MyId2", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlParagraph 'Second line' (id='MyId2')] found by: BY_ID coverage: 0 distance: 10", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetFirstClickableTextElement_ByIdAfter() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<p id='MyId1'>First line</p>"
            + "<p id='MyId2'>Second line</p>"
            + "<p>Marker</p>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("Marker", false));
        tmpSearch.add(new SecretString("MyId2", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(0, tmpFound.getElementsSorted().size());
    }


    public void testGetFirstClickableTextElement_ByTextExact() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<p>First line</p>"
            + "<p>Second line</p>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("First line", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlParagraph 'First line'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetFirstClickableTextElement_ByTextPart() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<p>First line</p>"
            + "<p>Second line</p>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("First l", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlParagraph 'First line'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetFirstClickableTextElement_ByTextPartAfter() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<p>First line</p>"
            + "<p>Second line</p>"
            + "<p>Marker</p>"
            + "<p>Last line</p>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("Marker", false));
        tmpSearch.add(new SecretString("line", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlParagraph 'Last line'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetFirstClickableTextElement_ByTextFormated() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<p>First line</p>"
            + "<p>This t<font color='red'>ex</font>t is <b>styled</b>.</p>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("This text * styled", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlParagraph 'This text is styled.'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetFirstClickableTextElement_ByTextWithBreak() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<p>First<br>line</p>"
            + "<p>Second<br>line</p>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("First line", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlParagraph 'First\r\nline'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetFirstClickableTextElement_TableCell() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<table>"
            + "<tr>"
            + "<td>cell 1 1</td>"
            + "<td>cell 1 2</td>"
            + "</tr>"
            + "<tr>"
            + "<td>cell 2 1</td>"
            + "<td>cell 2 2</td>"
            + "</tr>"
            + "</table>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("cell 2 1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        // TODO
        assertEquals("[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlTableDataCell'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetFirstClickableTextElement_TableRow() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<table>"
            + "<tr>"
            + "<td>cell 1 1</td>"
            + "<td>cell 1 2</td>"
            + "</tr>"
            + "<tr>"
            + "<td>cell 2 1</td>"
            + "<td>cell 2 2</td>"
            + "</tr>"
            + "</table>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("cell 1 1 cell 1 2", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        // TODO
        assertEquals("[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlTableRow'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetFirstClickableTextElement_Div() throws IOException {
        String tmpHtmlCode = "<html><body>"
            + "<div>"
            + "<h1>Headline</h1>"
            + "<p>some text</p>"
            + "</div>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("line some", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getFirstClickableTextElement(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        // TODO
        assertEquals("[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlDivision'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }
}
