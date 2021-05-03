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


package org.wetator.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.util.PageUtil;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class WeightedControlListTest {

  @Test
  public void isEmpty() {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    assertTrue(tmpWeightedControlList.isEmpty());
    assertTrue(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void oneEntry() throws IOException {
    final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 100, 11, 1, "1", 0);

    assertFalse(tmpWeightedControlList.isEmpty());
    assertFalse(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void twoEntries() throws IOException {
    final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 100, 11, 1, "1", 0);
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 200, 11, 1, "1", 0);

    assertFalse(tmpWeightedControlList.isEmpty());

    assertFalse(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void getElementsSorted_FoundType() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 2, 2, 2, "2", 2);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_LABEL_ELEMENT, 1, 1, 1, "1", 1);

    final List<Entry> tmpEntriesSorted = tmpWeightedControlList.getEntriesSorted();

    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 2 distance: 2 start: 2 hierarchy: 2 index: 2",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlAnchor 'AnchorText'] found by: BY_LABEL_ELEMENT deviation: 1 distance: 1 start: 1 hierarchy: 1 index: 1",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void getElementsSorted_Deviation() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 2, 1, 1, "1", 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 3, 2, 2, "2", 2);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 3, 3, "3", 3);

    final List<Entry> tmpEntriesSorted = tmpWeightedControlList.getEntriesSorted();

    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 3 start: 3 hierarchy: 3 index: 3",
        tmpEntriesSorted.get(0).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 2 distance: 1 start: 1 hierarchy: 1 index: 1",
        tmpEntriesSorted.get(1).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 3 distance: 2 start: 2 hierarchy: 2 index: 2",
        tmpEntriesSorted.get(2).toString());
  }

  @Test
  public void getElementsSorted_Distance() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 2, 1, "1", 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 3, 2, "2", 2);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 3, "3", 3);

    final List<Entry> tmpEntriesSorted = tmpWeightedControlList.getEntriesSorted();

    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 3 hierarchy: 3 index: 3",
        tmpEntriesSorted.get(0).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 2 start: 1 hierarchy: 1 index: 1",
        tmpEntriesSorted.get(1).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 3 start: 2 hierarchy: 2 index: 2",
        tmpEntriesSorted.get(2).toString());
  }

  @Test
  public void getElementsSorted_Start() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 2, "1", 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 3, "2", 2);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "3", 3);

    final List<Entry> tmpEntriesSorted = tmpWeightedControlList.getEntriesSorted();

    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 3 index: 3",
        tmpEntriesSorted.get(0).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 2 hierarchy: 1 index: 1",
        tmpEntriesSorted.get(1).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 3 hierarchy: 2 index: 2",
        tmpEntriesSorted.get(2).toString());
  }

  @Test
  public void getElementsSorted_Hierarchy_Independent() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "2", 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "3", 2);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "1", 3);

    final List<Entry> tmpEntriesSorted = tmpWeightedControlList.getEntriesSorted();

    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 2 index: 1",
        tmpEntriesSorted.get(0).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 3 index: 2",
        tmpEntriesSorted.get(1).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 1 index: 3",
        tmpEntriesSorted.get(2).toString());
  }

  @Test
  public void getElementsSorted_Hierarchy() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "1", 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "1>2", 2);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "1>3", 3);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "1>3>4", 4);

    final List<Entry> tmpEntriesSorted = tmpWeightedControlList.getEntriesSorted();

    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 1>2 index: 2",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 1>3>4 index: 4",
        tmpEntriesSorted.get(1).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 1>3 index: 3",
        tmpEntriesSorted.get(2).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 1 index: 1",
        tmpEntriesSorted.get(3).toString());
  }

  @Test
  public void getElementsSorted_Index() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "2", 2);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "3", 3);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 1, 1, 1, "1", 1);

    final List<Entry> tmpEntriesSorted = tmpWeightedControlList.getEntriesSorted();

    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 1 index: 1",
        tmpEntriesSorted.get(0).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 2 index: 2",
        tmpEntriesSorted.get(1).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 hierarchy: 3 index: 3",
        tmpEntriesSorted.get(2).toString());
  }

  @Test
  public void getElementsSorted_SameControl() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 9, 11, 1, "1", 0);
    tmpWeightedControlList.add(tmpControl, FoundType.BY_LABEL_ELEMENT, 10, 12, 1, "1", 0);
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 4, 10, 1, "1", 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, FoundType.BY_ID, 4, 11, 1, "1", 0);

    final List<Entry> tmpEntriesSorted = tmpWeightedControlList.getEntriesSorted();

    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 4 distance: 10 start: 1 hierarchy: 1 index: 0",
        tmpEntriesSorted.get(0).toString());
    assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 4 distance: 11 start: 1 hierarchy: 1 index: 0",
        tmpEntriesSorted.get(1).toString());
  }

  private HtmlAnchor constructHtmlAnchor() throws IOException {
    final String tmpHtmlCode = "<html><body><a href='wet.html'>AnchorText</a></body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    return (HtmlAnchor) tmpHtmlElements.next();
  }
}
