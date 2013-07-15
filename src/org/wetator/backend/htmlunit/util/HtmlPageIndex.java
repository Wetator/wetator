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


package org.wetator.backend.htmlunit.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.NormalizedString;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomComment;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlApplet;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlBreak;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHead;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlHeading4;
import com.gargoylesoftware.htmlunit.html.HtmlHeading5;
import com.gargoylesoftware.htmlunit.html.HtmlHeading6;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLegend;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlOrderedList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeader;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTitle;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.gargoylesoftware.htmlunit.html.SubmittableElement;

/**
 * The text representation of a page text. Indexed by form controls to speed up the calculation of text before and
 * after.
 * 
 * @author rbri
 * @author frank.danek
 */
public class HtmlPageIndex {

  private HtmlPage htmlPage;

  private NormalizedString text;
  private Map<DomNode, FindSpot> positions;
  private NormalizedString textWithoutFormControls;
  private Map<DomNode, FindSpot> positionsWithoutFormControls;

  private List<DomNode> nodes;
  private List<HtmlElement> visibleHtmlElementsBottomUp;
  private List<HtmlElement> visibleHtmlElements;

  /**
   * The constructor.
   * 
   * @param aHtmlPage the HtmlPage to index
   */
  public HtmlPageIndex(final HtmlPage aHtmlPage) {
    htmlPage = aHtmlPage;

    text = new NormalizedString();
    positions = new HashMap<DomNode, FindSpot>();
    textWithoutFormControls = new NormalizedString();
    positionsWithoutFormControls = new HashMap<DomNode, FindSpot>();

    nodes = new LinkedList<DomNode>();
    visibleHtmlElementsBottomUp = new LinkedList<HtmlElement>();
    visibleHtmlElements = new LinkedList<HtmlElement>();

    parseDomNode(aHtmlPage);
  }

  /**
   * @param <E> the type of the {@link HtmlElement}
   * @param aId the id
   * @return the {@link HtmlElement} for the id
   * @throws ElementNotFoundException if no element was found for the given id
   * @see com.gargoylesoftware.htmlunit.html.HtmlPage#getHtmlElementById(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public <E extends HtmlElement> E getHtmlElementById(final String aId) throws ElementNotFoundException {
    return (E) htmlPage.getHtmlElementById(aId);
  }

  /**
   * Returns the whole text trimmed.
   * 
   * @return the whole text
   */
  public String getText() {
    return text.toString();
  }

  /**
   * Returns the whole text without the form controls trimmed.
   * 
   * @return the whole text
   */
  public String getTextWithoutFormControls() {
    return textWithoutFormControls.toString();
  }

  /**
   * Returns a list of all visible HtmlElements.
   * 
   * @return the list of all visible HtmlElements
   */
  public List<HtmlElement> getAllVisibleHtmlElements() {
    return visibleHtmlElements;
  }

  /**
   * Returns the list of all visible html elements always starting with the leaves.
   * 
   * @return the list of all html elements
   */
  public List<HtmlElement> getAllVisibleHtmlElementsBottomUp() {
    return visibleHtmlElementsBottomUp;
  }

  /**
   * Returns the start and end position of this html element as FindSpot.
   * 
   * @param anHtmlElement the element
   * @return the position
   */
  public FindSpot getPosition(final HtmlElement anHtmlElement) {
    return positions.get(anHtmlElement);
  }

  /**
   * Returns the start and end position of the first occurrence of the string in the text.
   * 
   * @param aSearchPattern the search pattern
   * @return the position
   */
  public FindSpot firstOccurence(final SearchPattern aSearchPattern) {
    return aSearchPattern.firstOccurenceIn(text.toString(), 0);
  }

  /**
   * Returns the start and end position of the first occurrence of the string in the text.
   * 
   * @param aSearchPattern the search pattern
   * @param aStartPos the pos to start with the search
   * @return the position
   */
  public FindSpot firstOccurence(final SearchPattern aSearchPattern, final int aStartPos) {
    return aSearchPattern.firstOccurenceIn(text.toString(), aStartPos);
  }

  /**
   * Returns the whole (trimmed) text before the given dom node.
   * 
   * @param aDomNode the node to look at
   * @return the text before the node
   */
  public String getTextBefore(final DomNode aDomNode) {
    final FindSpot tmpFindSpot = positions.get(aDomNode);
    if (null == tmpFindSpot) {
      return null;
    }
    return text.substring(0, tmpFindSpot.startPos);
  }

  /**
   * Returns the whole (trimmed) text before the given dom node.
   * 
   * @param aDomNode the node to look at
   * @return the text before the node
   */
  public String getTextBeforeIncludingMyself(final DomNode aDomNode) {
    final FindSpot tmpFindSpot = positions.get(aDomNode);
    if (null == tmpFindSpot) {
      return null;
    }
    return text.substring(0, tmpFindSpot.endPos);
  }

  /**
   * Returns the whole (trimmed) text between this element and the preceding form element or the form start.
   * 
   * @param anHtmlElement the element to start from
   * @param aStartPos the start pos, text found before this will be not part of the result
   * @return the text before
   */
  public String getLabelTextBefore(final HtmlElement anHtmlElement, final int aStartPos) {
    final FindSpot tmpFindSpot = positions.get(anHtmlElement);
    if (null == tmpFindSpot) {
      return null;
    }

    final HtmlForm tmpCurrentForm = anHtmlElement.getEnclosingForm();
    int tmpStartPos = 0;
    final ListIterator<DomNode> tmpIter = nodes.listIterator(nodes.indexOf(anHtmlElement));
    while (tmpIter.hasPrevious()) {
      final DomNode tmpNode = tmpIter.previous();

      if (tmpNode instanceof HtmlBody) {
        // don't use the end pos of the body
        tmpStartPos = positions.get(tmpNode).startPos;
        break;
      }

      // we have to stop if we found some other (visible) form control
      if ((tmpNode instanceof SubmittableElement) && !(tmpNode instanceof HtmlHiddenInput)) {
        tmpStartPos = positions.get(tmpNode).endPos;
        final String tmpText = text.substring(Math.max(tmpStartPos, aStartPos), tmpFindSpot.startPos);
        if (StringUtils.isNotEmpty(tmpText)) {
          return tmpText;
        }
      }

      // we have to stop if we are reaching an element of another form
      if (tmpNode instanceof HtmlElement) {
        final HtmlForm tmpForm = ((HtmlElement) tmpNode).getEnclosingForm();
        // we are reaching another form
        if ((null != tmpForm) && (tmpForm != tmpCurrentForm)) {
          tmpStartPos = positions.get(tmpNode).endPos;
          break;
        }
      }
    }

    return text.substring(Math.max(tmpStartPos, aStartPos), tmpFindSpot.startPos);
  }

  /**
   * Returns the whole (trimmed) text between this element and the next form element or the form end.
   * 
   * @param anHtmlElement the element to start from
   * @return the text after
   */
  public String getLabelTextAfter(final HtmlElement anHtmlElement) {
    final FindSpot tmpFindSpot = positions.get(anHtmlElement);
    if (null == tmpFindSpot) {
      return null;
    }

    final HtmlForm tmpCurrentForm = anHtmlElement.getEnclosingForm();
    int tmpEndPos = text.length();
    final ListIterator<DomNode> tmpIter = nodes.listIterator(nodes.indexOf(anHtmlElement));
    // start with the next element
    tmpIter.next();

    while (tmpIter.hasNext()) {
      final DomNode tmpNode = tmpIter.next();

      // we have to stop if we found some other (visible) form control
      if ((tmpNode instanceof SubmittableElement) && !(tmpNode instanceof HtmlHiddenInput)) {
        tmpEndPos = positions.get(tmpNode).startPos;
        break;
      }

      // we have to stop if we are reaching an element of another form
      if (tmpNode instanceof HtmlElement) {
        final HtmlForm tmpForm = ((HtmlElement) tmpNode).getEnclosingForm();
        // we are reaching another form
        if ((null != tmpCurrentForm) && (tmpForm != tmpCurrentForm)) {
          tmpEndPos = positions.get(tmpNode).startPos;
          break;
        }
      }
    }

    return text.substring(tmpFindSpot.endPos, tmpEndPos);
  }

  /**
   * Returns the (trimmed) text of the given node and all its children.
   * 
   * @param aDomNode the node to look at
   * @return the text
   */
  public String getAsText(final DomNode aDomNode) {
    final FindSpot tmpFindSpot = positions.get(aDomNode);
    if (null == tmpFindSpot) {
      return null;
    }
    return text.substring(tmpFindSpot.startPos, tmpFindSpot.endPos);
  }

  /**
   * Returns the (trimmed) text of the given node and all its children.
   * All form controls are not part of this text.
   * 
   * @param aDomNode the node to look at
   * @return the text
   */
  public String getAsTextWithoutFormControls(final DomNode aDomNode) {
    final FindSpot tmpFindSpot = positionsWithoutFormControls.get(aDomNode);
    if (null == tmpFindSpot) {
      return null;
    }

    return textWithoutFormControls.substring(tmpFindSpot.startPos, tmpFindSpot.endPos);
  }

  private void parseDomNode(final DomNode aDomNode) {
    if (null == aDomNode) {
      return;
    }
    nodes.add(aDomNode);

    // mark pos before
    FindSpot tmpFindSpot = new FindSpot();
    tmpFindSpot.startPos = text.length();
    positions.put(aDomNode, tmpFindSpot);

    FindSpot tmpFindSpotWFC = new FindSpot();
    tmpFindSpotWFC.startPos = textWithoutFormControls.length();
    positionsWithoutFormControls.put(aDomNode, tmpFindSpotWFC);

    if (aDomNode.isDisplayed()) {
      if (aDomNode instanceof HtmlElement) {
        visibleHtmlElements.add((HtmlElement) aDomNode);
      }
      if (aDomNode instanceof HtmlHiddenInput || aDomNode instanceof HtmlApplet || aDomNode instanceof HtmlScript
          || aDomNode instanceof HtmlStyle || aDomNode instanceof HtmlFileInput || aDomNode instanceof DomComment

          || aDomNode instanceof HtmlHead || aDomNode instanceof HtmlTitle) {
        // nothing
      } else if (aDomNode instanceof DomText) {
        appendDomText((DomText) aDomNode);
      } else if (aDomNode instanceof HtmlInlineFrame) {
        appendHtmlInlineFrame((HtmlInlineFrame) aDomNode);
      } else if (aDomNode instanceof HtmlBreak) {
        text.append(" ");
        textWithoutFormControls.append(" ");
      } else if (aDomNode instanceof HtmlImage) {
        appendHtmlImage((HtmlImage) aDomNode);
      } else if (aDomNode instanceof HtmlSelect) {
        appendHtmlSelect((HtmlSelect) aDomNode);
      } else if (aDomNode instanceof HtmlOptionGroup) {
        appendHtmlOptionGroup((HtmlOptionGroup) aDomNode);
      } else if (aDomNode instanceof HtmlLegend) {
        appendHtmlLegend((HtmlLegend) aDomNode);
      } else if (aDomNode instanceof HtmlSubmitInput) {
        appendHtmlSubmitInput((HtmlSubmitInput) aDomNode);
      } else if (aDomNode instanceof HtmlResetInput) {
        appendHtmlResetInput((HtmlResetInput) aDomNode);
      } else if (aDomNode instanceof HtmlButtonInput) {
        appendHtmlButtonInput((HtmlButtonInput) aDomNode);
      } else if (aDomNode instanceof HtmlCheckBoxInput) {
        appendHtmlCheckBoxInput((HtmlCheckBoxInput) aDomNode);
      } else if (aDomNode instanceof HtmlRadioButtonInput) {
        appendHtmlRadioButtonInput((HtmlRadioButtonInput) aDomNode);
      } else if (aDomNode instanceof HtmlImageInput) {
        appendHtmlImageInput((HtmlImageInput) aDomNode);
      } else if (aDomNode instanceof HtmlInput) {
        appendHtmlInput((HtmlInput) aDomNode);
      } else if (aDomNode instanceof HtmlTextArea) {
        appendHtmlTextArea((HtmlTextArea) aDomNode);
      } else if (aDomNode instanceof HtmlButton) {
        appendHtmlButton((HtmlButton) aDomNode);
      } else if (aDomNode instanceof HtmlOrderedList) {
        appendHtmlOrderedList((HtmlOrderedList) aDomNode);
      } else {
        final boolean tmpIsBlock = (aDomNode instanceof HtmlDivision) || (aDomNode instanceof HtmlParagraph)
            || (aDomNode instanceof HtmlTable) || (aDomNode instanceof HtmlTableRow)
            || (aDomNode instanceof HtmlTableHeader) || (aDomNode instanceof HtmlTableDataCell)
            || (aDomNode instanceof HtmlTableCell) || (aDomNode instanceof HtmlUnorderedList)
            || (aDomNode instanceof HtmlHeading1) || (aDomNode instanceof HtmlHeading2)
            || (aDomNode instanceof HtmlHeading3) || (aDomNode instanceof HtmlHeading4)
            || (aDomNode instanceof HtmlHeading5) || (aDomNode instanceof HtmlHeading6)
            || (aDomNode instanceof HtmlListItem);
        if (tmpIsBlock) {
          text.append(" ");
          textWithoutFormControls.append(" ");
        }
        parseChildren(aDomNode);
        if (tmpIsBlock) {
          text.append(" ");
          textWithoutFormControls.append(" ");
        }
      }

      if (aDomNode instanceof HtmlElement) {
        visibleHtmlElementsBottomUp.add((HtmlElement) aDomNode);
      }
    }
    // mark pos after
    tmpFindSpot = positions.get(aDomNode);
    tmpFindSpot.endPos = text.length();

    tmpFindSpotWFC = positionsWithoutFormControls.get(aDomNode);
    tmpFindSpotWFC.endPos = textWithoutFormControls.length();
  }

  private void parseChildren(final DomNode aNode) {
    for (DomNode tmpChild : aNode.getChildren()) {
      parseDomNode(tmpChild);
    }
  }

  private void appendDomText(final DomText aDomText) {
    text.append(aDomText.getData());
    textWithoutFormControls.append(aDomText.getData());
  }

  private void appendHtmlInlineFrame(final HtmlInlineFrame anHtmlInlineFrame) {
    final Page tmpPage = anHtmlInlineFrame.getEnclosedPage();
    if (tmpPage instanceof HtmlPage) {
      parseDomNode((HtmlPage) tmpPage);
    }
  }

  private void appendHtmlImageInput(final HtmlImageInput anHtmlImageInput) {
    text.append(anHtmlImageInput.getAltAttribute());
  }

  private void appendHtmlInput(final HtmlInput anHtmlInput) {
    text.append(anHtmlInput.getValueAttribute());
  }

  private void appendHtmlTextArea(final HtmlTextArea anHtmlTextArea) {
    textWithoutFormControls.disableAppend();
    parseChildren(anHtmlTextArea);
    textWithoutFormControls.enableAppend();
  }

  private void appendHtmlImage(final HtmlImage anHtmlImage) {
    text.append(anHtmlImage.getAltAttribute());
    textWithoutFormControls.append(anHtmlImage.getAltAttribute());
  }

  private void appendHtmlLegend(final HtmlLegend anHtmlLegend) {
    parseChildren(anHtmlLegend);
    text.append(" ");
    textWithoutFormControls.append(" ");
  }

  private void appendHtmlOptionGroup(final HtmlOptionGroup anHtmlOptionGroup) {
    final String tmpLabel = anHtmlOptionGroup.getLabelAttribute();
    text.append(tmpLabel);
  }

  private void appendHtmlButton(final HtmlButton anHtmlButton) {
    textWithoutFormControls.disableAppend();
    parseChildren(anHtmlButton);
    textWithoutFormControls.enableAppend();
    text.append(" ");
    textWithoutFormControls.append(" ");
  }

  private void appendHtmlSubmitInput(final HtmlSubmitInput anHtmlSubmitInput) {
    text.append(anHtmlSubmitInput.getValueAttribute());
    text.append(" ");
  }

  private void appendHtmlResetInput(final HtmlResetInput anHtmlResetInput) {
    text.append(anHtmlResetInput.getValueAttribute());
    text.append(" ");
  }

  private void appendHtmlButtonInput(final HtmlButtonInput anHtmlButtonInput) {
    text.append(anHtmlButtonInput.getValueAttribute());
    text.append(" ");
  }

  private void appendHtmlCheckBoxInput(final HtmlCheckBoxInput anHtmlCheckBoxInput) {
    textWithoutFormControls.disableAppend();
    parseChildren(anHtmlCheckBoxInput);
    textWithoutFormControls.enableAppend();
    text.append(" ");
    textWithoutFormControls.append(" ");
  }

  private void appendHtmlRadioButtonInput(final HtmlRadioButtonInput anHtmlRadioButtonInput) {
    textWithoutFormControls.disableAppend();
    parseChildren(anHtmlRadioButtonInput);
    textWithoutFormControls.enableAppend();
    text.append(" ");
    textWithoutFormControls.append(" ");
  }

  private void appendHtmlSelect(final HtmlSelect anHtmlSelect) {
    textWithoutFormControls.disableAppend();
    for (final DomNode tmpItem : anHtmlSelect.getHtmlElementDescendants()) {
      if ((tmpItem instanceof HtmlOption) || (tmpItem instanceof HtmlOptionGroup)) {
        text.append(" ");
        textWithoutFormControls.append(" ");
        parseDomNode(tmpItem);
      }
    }
    textWithoutFormControls.enableAppend();
    text.append(" ");
    textWithoutFormControls.append(" ");
  }

  /**
   * Appends a &lt;ol&gt; taking care to numerate it.
   * 
   * @param anHtmlOrderedList the OL element
   */
  private void appendHtmlOrderedList(final HtmlOrderedList anHtmlOrderedList) {
    text.append(" ");
    textWithoutFormControls.append(" ");

    int i = 1;
    for (final DomNode tmpItem : anHtmlOrderedList.getChildren()) {
      if (tmpItem instanceof HtmlListItem) {
        // hack for fixing the start pos
        final int tmpStartPos = text.length();
        text.append(String.valueOf(i++));
        text.append(". ");

        final int tmpStartPosWFC = textWithoutFormControls.length();
        textWithoutFormControls.append(String.valueOf(i));
        textWithoutFormControls.append(". ");

        parseDomNode(tmpItem);
        final FindSpot tmpFindSpot = positions.get(tmpItem);
        tmpFindSpot.startPos = tmpStartPos;

        final FindSpot tmpFindSpotWFC = positionsWithoutFormControls.get(tmpItem);
        tmpFindSpotWFC.startPos = tmpStartPosWFC;
      } else {
        parseDomNode(tmpItem);
      }
    }
    text.append(" ");
    textWithoutFormControls.append(" ");
  }
}
