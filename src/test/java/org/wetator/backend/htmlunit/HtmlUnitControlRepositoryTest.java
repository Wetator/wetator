/*
 * Copyright (c) 2008-2025 wetator.org
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


package org.wetator.backend.htmlunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlElement;
import org.junit.Test;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.util.PageUtil;

/**
 * @author frank.danek
 */
public class HtmlUnitControlRepositoryTest {

  @Test
  public void getForHtmlElementNotFound() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";

    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById("myId");

      final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

      assertNull(tmpRepository.getForHtmlElement(tmpHtmlElement));
    });
  }

  @Test
  public void getForHtmlElementByElement() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";

    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById("myId");

      final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();
      tmpRepository.add(HtmlUnitAnchor.class);

      assertEquals(HtmlUnitAnchor.class, tmpRepository.getForHtmlElement(tmpHtmlElement));
    });
  }

  @Test
  public void getForHtmlElementByElementAndAttribute() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "<a id='myId2' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById("myId2");

      final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();
      tmpRepository.add(HtmlUnitAnchor.class);
      tmpRepository.add(TestControl.class);

      assertEquals(TestControl.class, tmpRepository.getForHtmlElement(tmpHtmlElement));
    });
  }

  @Test
  public void getForHtmlElementByElementAndAttributeFallBack() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "<a id='myId2' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById("myId");

      final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();
      tmpRepository.add(HtmlUnitAnchor.class);
      tmpRepository.add(TestControl.class);

      assertEquals(HtmlUnitAnchor.class, tmpRepository.getForHtmlElement(tmpHtmlElement));
    });
  }

  /**
   * @author frank.danek
   */
  @ForHtmlElement(value = HtmlAnchor.class, attributeName = "id", attributeValues = { "myId2" })
  private class TestControl extends HtmlUnitBaseControl<HtmlAnchor> {

    /**
     * The constructor.
     *
     * @param anHtmlElement the {@link HtmlElement} from the backend
     */
    TestControl(final HtmlAnchor anHtmlElement) {
      super(anHtmlElement);
    }

    @Override
    public String getDescribingText() {
      return "TestControl";
    }
  }
}
