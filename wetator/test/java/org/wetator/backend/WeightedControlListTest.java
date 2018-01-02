/*
 * Copyright (c) 2008-2018 wetator.org
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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WeightedControlList.Entry;
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

    Assert.assertTrue(tmpWeightedControlList.isEmpty());
    Assert.assertTrue(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void oneEntry() throws IOException {
    final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11, 1, 0);

    Assert.assertFalse(tmpWeightedControlList.isEmpty());
    Assert.assertFalse(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void twoEntries() throws IOException {
    final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11, 1, 0);
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 200, 11, 1, 0);

    Assert.assertFalse(tmpWeightedControlList.isEmpty());

    Assert.assertFalse(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void getElementsSorted_Distance() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 12, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 10, 1, 0);

    final List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 100 distance: 10 start: 1 index: 0",
        tmpSorted.get(0).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 100 distance: 11 start: 1 index: 0",
        tmpSorted.get(1).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 100 distance: 12 start: 1 index: 0",
        tmpSorted.get(2).toString());
  }

  @Test
  public void getElementsSorted_Deviation() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 10, 12, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11, 1, 0);

    final List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 4 distance: 10 start: 1 index: 0",
        tmpSorted.get(0).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 4 distance: 11 start: 1 index: 0",
        tmpSorted.get(1).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 9 distance: 11 start: 1 index: 0",
        tmpSorted.get(2).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 10 distance: 12 start: 1 index: 0",
        tmpSorted.get(3).toString());
  }

  @Test
  public void getElementsSorted_FoundType() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_LABEL, 10, 12, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11, 1, 0);

    final List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 4 distance: 10 start: 1 index: 0",
        tmpSorted.get(0).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 4 distance: 11 start: 1 index: 0",
        tmpSorted.get(1).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 9 distance: 11 start: 1 index: 0",
        tmpSorted.get(2).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_LABEL deviation: 10 distance: 12 start: 1 index: 0",
        tmpSorted.get(3).toString());
  }

  @Test
  public void getElementsSorted_Start() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 1, 1, 3, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 1, 1, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 1, 1, 4, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 1, 1, 2, 0);

    final List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 1 index: 0",
        tmpSorted.get(0).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 2 index: 0",
        tmpSorted.get(1).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 3 index: 0",
        tmpSorted.get(2).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 1 distance: 1 start: 4 index: 0",
        tmpSorted.get(3).toString());
  }

  @Test
  public void getElementsSorted_SameControl() throws IOException {
    final WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11, 1, 0);
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_LABEL, 10, 12, 1, 0);
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10, 1, 0);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11, 1, 0);

    final List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 4 distance: 10 start: 1 index: 0",
        tmpSorted.get(0).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID deviation: 4 distance: 11 start: 1 index: 0",
        tmpSorted.get(1).toString());
  }

  private HtmlAnchor constructHtmlAnchor() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<a href='wet.html'>AnchorText</a>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    final HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();
    return tmpAnchor;
  }
}
