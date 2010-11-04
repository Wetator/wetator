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


package org.rbri.wet.backend.htmlunit.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.core.searchpattern.SearchPattern;
import org.rbri.wet.util.NormalizedString;

import com.gargoylesoftware.htmlunit.html.DomComment;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlApplet;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlBreak;
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
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLegend;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlOrderedList;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeader;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTitle;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.gargoylesoftware.htmlunit.html.SubmittableElement;

/**
 * The text representation of a page text. Indexed by form controls to speed up the calculation of text before and
 * after.
 * 
 * @author rbri
 */
public class DomNodeText {

  private NormalizedString text;
  private List<DomNode> nodes;
  private List<HtmlElement> visibleHtmlElementsBottomUp;
  private List<HtmlElement> visibleHtmlElements;
  private Map<DomNode, FindSpot> positions;

  /**
   * Constructor
   * 
   * @param aDomNode the root node to start with
   */
  public DomNodeText(final DomNode aDomNode) {
    text = new NormalizedString();
    nodes = new LinkedList<DomNode>();
    visibleHtmlElementsBottomUp = new LinkedList<HtmlElement>();
    visibleHtmlElements = new LinkedList<HtmlElement>();
    positions = new HashMap<DomNode, FindSpot>();

    parseDomNode(aDomNode);
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
   * Returns a list of all visible HtmlElements
   * 
   * @return the list of all visible HtmlElements
   */
  public List<HtmlElement> getAllVisibleHtmlElements() {
    return visibleHtmlElements;
  }

  /**
   * Returns the list of all visible html elements always starting with the leaves
   * 
   * @return the list of all html elements
   */
  public List<HtmlElement> getAllVisibleHtmlElementsBottomUp() {
    return visibleHtmlElementsBottomUp;
  }

  /**
   * Returns the start and end position of this html element as FindSpot
   * 
   * @param anHtmlElement the element
   * @return the position
   */
  public FindSpot getPosition(HtmlElement anHtmlElement) {
    return positions.get(anHtmlElement);
  }

  /**
   * Returns the start and end position of the first occurrence of the string in the text
   * 
   * @param aSearchPattern the search pattern
   * @return the position
   */
  public FindSpot firstOccurence(SearchPattern aSearchPattern) {
    return aSearchPattern.firstOccurenceIn(text.toString(), 0);
  }

  /**
   * Returns the start and end position of the first occurrence of the string in the text
   * 
   * @param aSearchPattern the search pattern
   * @param aStartPos the pos to start with the search
   * @return the position
   */
  public FindSpot firstOccurence(SearchPattern aSearchPattern, int aStartPos) {
    return aSearchPattern.firstOccurenceIn(text.toString(), aStartPos);
  }

  /**
   * Returns the whole (trimmed) text before the given dom node
   * 
   * @param aDomNode the node to look at
   * @return the text before the node
   */
  public String getTextBefore(final DomNode aDomNode) {
    FindSpot tmpFindSpot = positions.get(aDomNode);
    if (null == tmpFindSpot) {
      return null;
    }
    return text.substring(0, tmpFindSpot.startPos);
  }

  /**
   * Returns the whole (trimmed) text before the given dom node
   * 
   * @param aDomNode the node to look at
   * @return the text before the node
   */
  public String getTextBeforeIncludingMyself(final DomNode aDomNode) {
    FindSpot tmpFindSpot = positions.get(aDomNode);
    if (null == tmpFindSpot) {
      return null;
    }
    return text.substring(0, tmpFindSpot.endPos);
  }

  /**
   * Returns the whole (trimmed) text between this element and the preceding form element or the form start
   * 
   * @param anHtmlElement the element to start from
   * @param aStartPos the start pos, text found before this will be not part of the result
   * @return the text before
   */
  public String getLabelTextBefore(final HtmlElement anHtmlElement, final int aStartPos) {
    FindSpot tmpFindSpot = positions.get(anHtmlElement);
    if (null == tmpFindSpot) {
      return null;
    }

    HtmlForm tmpCurrentForm = anHtmlElement.getEnclosingForm();
    int tmpStartPos = 0;
    ListIterator<DomNode> tmpIter = nodes.listIterator(nodes.indexOf(anHtmlElement));
    while (tmpIter.hasPrevious()) {
      DomNode tmpNode = tmpIter.previous();

      if (tmpNode instanceof HtmlBody) {
        // don't use the end pos of the body
        tmpStartPos = positions.get(tmpNode).startPos;
        break;
      }

      // we have to stop if we found some other (visible) form control
      if ((tmpNode instanceof SubmittableElement) && !(tmpNode instanceof HtmlHiddenInput)) {
        tmpStartPos = positions.get(tmpNode).endPos;
        String tmpText = text.substring(Math.max(tmpStartPos, aStartPos), tmpFindSpot.startPos);
        if (StringUtils.isNotEmpty(tmpText)) {
          return tmpText;
        }
      }

      // we have to stop if we are reaching an element of another form
      if (tmpNode instanceof HtmlElement) {
        HtmlForm tmpForm = ((HtmlElement) tmpNode).getEnclosingForm();
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
   * Returns the whole (trimmed) text between this element and the next form element or the form end
   * 
   * @param anHtmlElement the element to start from
   * @return the text after
   */
  public String getLabelTextAfter(final HtmlElement anHtmlElement) {
    FindSpot tmpFindSpot = positions.get(anHtmlElement);
    if (null == tmpFindSpot) {
      return null;
    }

    HtmlForm tmpCurrentForm = anHtmlElement.getEnclosingForm();
    int tmpEndPos = text.length();
    ListIterator<DomNode> tmpIter = nodes.listIterator(nodes.indexOf(anHtmlElement));
    // start with the next element
    tmpIter.next();

    while (tmpIter.hasNext()) {
      DomNode tmpNode = tmpIter.next();

      // we have to stop if we found some other (visible) form control
      if ((tmpNode instanceof SubmittableElement) && !(tmpNode instanceof HtmlHiddenInput)) {
        tmpEndPos = positions.get(tmpNode).startPos;
        break;
      }

      // we have to stop if we are reaching an element of another form
      if (tmpNode instanceof HtmlElement) {
        HtmlForm tmpForm = ((HtmlElement) tmpNode).getEnclosingForm();
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
   * Returns the (trimmed) text of the given node and all its children
   * 
   * @param aDomNode the node to look at
   * @return the text
   */
  public String getAsText(final DomNode aDomNode) {
    FindSpot tmpFindSpot = positions.get(aDomNode);
    if (null == tmpFindSpot) {
      return null;
    }
    return text.substring(tmpFindSpot.startPos, tmpFindSpot.endPos);
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
      } else if (aDomNode instanceof HtmlBreak) {
        text.append(" ");
      } else if (aDomNode instanceof HtmlImage) {
        appendHtmlImage((HtmlImage) aDomNode);
      } else if (aDomNode instanceof HtmlSelect) {
        appendHtmlSelect((HtmlSelect) aDomNode);
      } else if (aDomNode instanceof HtmlOptionGroup) {
        appendHtmlOptionGroup((HtmlOptionGroup) aDomNode);
      } else if (aDomNode instanceof HtmlLegend) {
        appendHtmlLegend((HtmlLegend) aDomNode);
      } else if (aDomNode instanceof HtmlCheckBoxInput) {
        appendHtmlCheckBoxInput((HtmlCheckBoxInput) aDomNode);
      } else if (aDomNode instanceof HtmlRadioButtonInput) {
        appendHtmlRadioButtonInput((HtmlRadioButtonInput) aDomNode);
      } else if (aDomNode instanceof HtmlImageInput) {
        appendHtmlImageInput((HtmlImageInput) aDomNode);
      } else if (aDomNode instanceof HtmlInput) {
        appendHtmlInput((HtmlInput) aDomNode);
      } else if (aDomNode instanceof HtmlOrderedList) {
        appendHtmlOrderedList((HtmlOrderedList) aDomNode);
      } else {
        boolean tmpIsBlock = (aDomNode instanceof HtmlDivision) || (aDomNode instanceof HtmlParagraph)
            || (aDomNode instanceof HtmlTable) || (aDomNode instanceof HtmlTableRow)
            || (aDomNode instanceof HtmlTableHeader) || (aDomNode instanceof HtmlTableDataCell)
            || (aDomNode instanceof HtmlTableCell) || (aDomNode instanceof HtmlUnorderedList)
            || (aDomNode instanceof HtmlHeading1) || (aDomNode instanceof HtmlHeading2)
            || (aDomNode instanceof HtmlHeading3) || (aDomNode instanceof HtmlHeading4)
            || (aDomNode instanceof HtmlHeading5) || (aDomNode instanceof HtmlHeading6)
            || (aDomNode instanceof HtmlListItem);
        if (tmpIsBlock) {
          text.append(" ");
        }
        parseChildren(aDomNode);
        if (tmpIsBlock) {
          text.append(" ");
        }
      }

      if (aDomNode instanceof HtmlElement) {
        visibleHtmlElementsBottomUp.add((HtmlElement) aDomNode);
      }
    }
    // mark pos after
    tmpFindSpot = positions.get(aDomNode);
    tmpFindSpot.endPos = text.length();
  }

  private void parseChildren(final DomNode aNode) {
    for (DomNode tmpChild : aNode.getChildren()) {
      parseDomNode(tmpChild);
    }
  }

  private void appendDomText(final DomText aDomText) {
    text.append(aDomText.getData());
  }

  private void appendHtmlImageInput(final HtmlImageInput anHtmlImageInput) {
    text.append(anHtmlImageInput.getAltAttribute());
  }

  private void appendHtmlInput(final HtmlInput anHtmlInput) {
    text.append(anHtmlInput.getValueAttribute());
  }

  private void appendHtmlImage(final HtmlImage anHtmlImage) {
    text.append(anHtmlImage.getAltAttribute());
  }

  private void appendHtmlLegend(final HtmlLegend anHtmlLegend) {
    parseChildren(anHtmlLegend);
    text.append(" ");
  }

  private void appendHtmlOptionGroup(final HtmlOptionGroup anHtmlOptionGroup) {
    String tmpLabel = anHtmlOptionGroup.getLabelAttribute();
    text.append(tmpLabel);
  }

  private void appendHtmlCheckBoxInput(final HtmlCheckBoxInput anHtmlCheckBoxInput) {
    parseChildren(anHtmlCheckBoxInput);
    text.append(" ");
  }

  private void appendHtmlRadioButtonInput(final HtmlRadioButtonInput anHtmlRadioButtonInput) {
    parseChildren(anHtmlRadioButtonInput);
    text.append(" ");
  }

  private void appendHtmlSelect(final HtmlSelect anHtmlSelect) {
    for (final DomNode tmpItem : anHtmlSelect.getHtmlElementDescendants()) {
      if ((tmpItem instanceof HtmlOption) || (tmpItem instanceof HtmlOptionGroup)) {
        text.append(" ");
        parseDomNode(tmpItem);
      }
    }
    text.append(" ");
  }

  /**
   * Appends a &lt;ol&gt; taking care to numerate it.
   * 
   * @param anHtmlOrderedList the OL element
   */
  private void appendHtmlOrderedList(final HtmlOrderedList anHtmlOrderedList) {
    text.append(" ");
    int i = 1;
    for (final DomNode tmpItem : anHtmlOrderedList.getChildren()) {
      if (tmpItem instanceof HtmlListItem) {
        // hack for fixing the start pos
        int tmpStartPos = text.length();
        text.append(String.valueOf(i++));
        text.append(". ");
        parseDomNode(tmpItem);
        FindSpot tmpFindSpot = positions.get(tmpItem);
        tmpFindSpot.startPos = tmpStartPos;
      } else {
        parseDomNode(tmpItem);
      }
    }
    text.append(" ");
  }
}
