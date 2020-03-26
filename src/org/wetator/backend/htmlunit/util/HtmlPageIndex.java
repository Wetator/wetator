/*
 * Copyright (c) 2008-2020 wetator.org
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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;
import org.wetator.util.NormalizedString;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomComment;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlApplet;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlBreak;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlFrame;
import com.gargoylesoftware.htmlunit.html.HtmlHead;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlInlineQuotation;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlLegend;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlObject;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlOrderedList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTitle;
import com.gargoylesoftware.htmlunit.html.SubmittableElement;
import com.gargoylesoftware.htmlunit.javascript.host.css.CSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.css.ComputedCSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.css.StyleAttributes.Definition;
import com.gargoylesoftware.htmlunit.javascript.host.event.MouseEvent;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLObjectElement;

import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;

/**
 * The text representation of a page text. Indexed by form controls to speed up the calculation of text before and
 * after.
 *
 * @author rbri
 * @author frank.danek
 */
public class HtmlPageIndex {
  private static final Logger LOG = LogManager.getLogger(HtmlPageIndex.class);

  private static final String HIERARCHY_DELIMITER = ">";

  private static final String EVENT_NAME_CLICK = "on" + MouseEvent.TYPE_CLICK;
  private static final String EVENT_NAME_DBL_CLICK = "on" + MouseEvent.TYPE_DBL_CLICK;
  private static final String EVENT_NAME_CONTEXT_MENU = "on" + MouseEvent.TYPE_CONTEXT_MENU;
  private static final String EVENT_NAME_MOUSE_DOWN = "on" + MouseEvent.TYPE_MOUSE_DOWN;
  private static final String EVENT_NAME_MOUSE_UP = "on" + MouseEvent.TYPE_MOUSE_UP;
  private static final String EVENT_NAME_MOUSE_OVER = "on" + MouseEvent.TYPE_MOUSE_OVER;
  private static final String EVENT_NAME_MOUSE_MOVE = "on" + MouseEvent.TYPE_MOUSE_MOVE;
  private static final String EVENT_NAME_MOUSE_OUT = "on" + MouseEvent.TYPE_MOUSE_OUT;

  private HtmlPage htmlPage;

  private NormalizedString text;
  private Map<DomNode, FindSpot> positions;
  private NormalizedString textWithoutFormControls;
  private Map<DomNode, FindSpot> positionsWithoutFormControls;

  private List<DomNode> nodes;
  private Set<HtmlElement> visibleHtmlElementsBottomUp;
  private Set<HtmlElement> visibleHtmlElements;

  private Map<DomNode, String> hierarchies;

  private Set<HtmlElement> htmlElementsWithClickListener;
  private Map<MouseAction, Set<HtmlElement>> htmlElementsWithMouseActionListener;

  private boolean lastOneWasHtmlElement;

  /**
   * The constructor.
   *
   * @param aHtmlPage the {@link HtmlPage} to index
   */
  public HtmlPageIndex(final HtmlPage aHtmlPage) {
    htmlPage = aHtmlPage;

    text = new NormalizedString();
    positions = new HashMap<>(256);
    positionsWithoutFormControls = new HashMap<>(256);
    textWithoutFormControls = new NormalizedString();

    nodes = new LinkedList<>();
    // LinkedHashSets to preserve the order and have a fast contains
    visibleHtmlElementsBottomUp = new LinkedHashSet<>();
    visibleHtmlElements = new LinkedHashSet<>();

    hierarchies = new HashMap<>(256);

    htmlElementsWithClickListener = new HashSet<>();
    htmlElementsWithMouseActionListener = new HashMap<>();
    for (MouseAction tmpMouseAction : MouseAction.values()) {
      htmlElementsWithMouseActionListener.put(tmpMouseAction, new HashSet<HtmlElement>());
    }

    parseDomNode(aHtmlPage, null, EnumSet.noneOf(MouseAction.class));
  }

  /**
   * @param <E> the type of the {@link HtmlElement}
   * @param aId the id
   * @return the {@link HtmlElement} for the id
   * @throws ElementNotFoundException if no element was found for the given id
   * @see com.gargoylesoftware.htmlunit.html.HtmlPage#getHtmlElementById(java.lang.String)
   */
  public <E extends HtmlElement> E getHtmlElementById(final String aId) throws ElementNotFoundException {
    return htmlPage.getHtmlElementById(aId);
  }

  /**
   * Returns the whole normalized text.
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
   * Returns an ordered set of all visible {@link HtmlElement}s starting from the root.
   *
   * @return an ordered set of all visible {@link HtmlElement}s
   */
  public Set<HtmlElement> getAllVisibleHtmlElements() {
    return visibleHtmlElements;
  }

  /**
   * Returns an ordered set of all visible {@link HtmlElement}s starting with the last leaf.
   *
   * @return an ordered set of all visible {@link HtmlElement}s
   */
  public Set<HtmlElement> getAllVisibleHtmlElementsBottomUp() {
    return visibleHtmlElementsBottomUp;
  }

  /**
   * Returns the visibility of the given {@link HtmlElement}.
   *
   * @param anHtmlElement the {@link HtmlElement} to check
   * @return <code>true</code> if the given {@link HtmlElement} is visible, <code>false</code> otherwise
   */
  public boolean isVisible(final HtmlElement anHtmlElement) {
    return visibleHtmlElements.contains(anHtmlElement);
  }

  /**
   * Returns the index on the page.
   *
   * @param anHtmlElement the element
   * @return the position
   */
  public int getIndex(final HtmlElement anHtmlElement) {
    final int tmpResult = nodes.indexOf(anHtmlElement);
    if (tmpResult < 0) {
      LOG.error("No index found for HtmlElement: " + anHtmlElement.toString());
      dumpToLog();
    }
    return tmpResult;
  }

  /**
   * Returns the start and end position of this html element as FindSpot.
   *
   * @param anHtmlElement the element
   * @return the position
   */
  public FindSpot getPosition(final HtmlElement anHtmlElement) {
    final FindSpot tmpResult = positions.get(anHtmlElement);
    if (null == tmpResult) {
      LOG.error("No position found for HtmlElement: " + anHtmlElement.toString());
      dumpToLog();
    }
    return tmpResult;
  }

  /**
   * Returns the hierarchy of the given element.
   *
   * @param anHtmlElement the element
   * @return the position
   */
  public String getHierarchy(final HtmlElement anHtmlElement) {
    final String tmpResult = hierarchies.get(anHtmlElement);
    if (null == tmpResult) {
      LOG.error("No hierarchy found for HtmlElement: " + anHtmlElement.toString());
      dumpToLog();
    }
    return tmpResult;
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
    return textSubstring(0, tmpFindSpot.getStartPos());
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
    return textSubstring(0, tmpFindSpot.getEndPos());
  }

  /**
   * Returns the whole (trimmed) text between the given element and the preceding form element or the form start.
   *
   * @param anHtmlElement the element to start from
   * @param aStartPos the start position, text found before this will be not part of the result
   * @return the labeling text before
   */
  public String getLabelingTextBefore(final HtmlElement anHtmlElement, final int aStartPos) {
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
        tmpStartPos = positions.get(tmpNode).getStartPos();
        break;
      }

      // we have to stop if we found some other (visible) form control
      if (tmpNode instanceof SubmittableElement && !(tmpNode instanceof HtmlHiddenInput)) {
        tmpStartPos = positions.get(tmpNode).getEndPos();

        // the searched control is chained directly after a leading control or placed inside a button tag
        if (tmpStartPos <= tmpFindSpot.getStartPos()) {
          final String tmpText = textSubstring(Math.max(tmpStartPos, aStartPos), tmpFindSpot.getStartPos());
          if (StringUtils.isNotEmpty(tmpText)) {
            return tmpText;
          }
        }
      }

      // we have to stop if we are reaching an element of another form
      if (tmpNode instanceof HtmlElement) {
        final HtmlForm tmpForm = ((HtmlElement) tmpNode).getEnclosingForm();
        // we are reaching another form
        if (null != tmpForm && tmpForm != tmpCurrentForm) {
          tmpStartPos = positions.get(tmpNode).getEndPos();
          break;
        }
      }
    }

    return textSubstring(Math.max(tmpStartPos, aStartPos), tmpFindSpot.getStartPos());
  }

  /**
   * Returns the whole (trimmed) text between the given element and the next form element or the form end.
   *
   * @param anHtmlElement the element to start from
   * @return the labeling text after
   */
  public String getLabelingTextAfter(final HtmlElement anHtmlElement) {
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
      if (tmpNode instanceof SubmittableElement && !(tmpNode instanceof HtmlHiddenInput)) {
        tmpEndPos = positions.get(tmpNode).getStartPos();
        break;
      }

      // we have to stop if we are reaching an element of another form
      if (tmpNode instanceof HtmlElement) {
        final HtmlForm tmpForm = ((HtmlElement) tmpNode).getEnclosingForm();
        // we are reaching another form
        if (null != tmpCurrentForm && tmpForm != tmpCurrentForm) {
          tmpEndPos = positions.get(tmpNode).getStartPos();
          break;
        }
      }
    }

    return textSubstring(tmpFindSpot.getEndPos(), tmpEndPos);
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
    return textSubstring(tmpFindSpot.getStartPos(), tmpFindSpot.getEndPos());
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

    return textWithoutFormControls.substring(tmpFindSpot.getStartPos(), tmpFindSpot.getEndPos());
  }

  /**
   * Returns <code>true</code> if the given element has an event listener for the <code>click</code> event.
   *
   * @param anHtmlElement the element to check
   * @return <code>true</code> if the given element has an event listener for the <code>click</code> event
   */
  public boolean hasClickListener(final HtmlElement anHtmlElement) {
    return htmlElementsWithClickListener.contains(anHtmlElement);
  }

  /**
   * Returns <code>true</code> if the given element has an event listener for the given {@link MouseAction}.
   *
   * @param aMouseAction the mouse action to check
   * @param anHtmlElement the element to check
   * @return <code>true</code> if the given element has an event listener for the given {@link MouseAction}
   */
  public boolean hasMouseActionListener(final MouseAction aMouseAction, final HtmlElement anHtmlElement) {
    return htmlElementsWithMouseActionListener.get(aMouseAction).contains(anHtmlElement);
  }

  private void parseDomNode(final DomNode aDomNode, final String aParentHierarchy,
      final Set<MouseAction> aParentMouseActions) {
    if (null == aDomNode) {
      return;
    }
    nodes.add(aDomNode);

    FindSpot tmpFindSpot = new FindSpot();
    // mark start position of the DOM node
    tmpFindSpot.setStartPos(text.length());
    positions.put(aDomNode, tmpFindSpot);

    FindSpot tmpFindSpotWFC = new FindSpot();
    tmpFindSpotWFC.setStartPos(textWithoutFormControls.length());
    positionsWithoutFormControls.put(aDomNode, tmpFindSpotWFC);

    final String tmpHierarchy;
    if (aParentHierarchy == null) {
      tmpHierarchy = String.valueOf(nodes.indexOf(aDomNode));
    } else {
      tmpHierarchy = aParentHierarchy + HIERARCHY_DELIMITER + nodes.indexOf(aDomNode);
    }
    hierarchies.put(aDomNode, tmpHierarchy);

    Set<MouseAction> tmpMouseActions = aParentMouseActions;

    if (isDisplayed(aDomNode)) {
      final boolean tmpIsHtmlElement = aDomNode instanceof HtmlElement;
      if (tmpIsHtmlElement) {
        final HtmlElement tmpHtmlElement = (HtmlElement) aDomNode;
        visibleHtmlElements.add(tmpHtmlElement);

        tmpMouseActions = getAvailableMouseActions(tmpHtmlElement, tmpMouseActions);
        tmpMouseActions.forEach(a -> htmlElementsWithMouseActionListener.get(a).add(tmpHtmlElement));
        if (tmpMouseActions.contains(MouseAction.CLICK)) {
          htmlElementsWithClickListener.add(tmpHtmlElement);
        }

        if (HtmlElementUtil.isFormatElement(aDomNode) && lastOneWasHtmlElement) {
          // IE suppresses whitespace between two elements
          // e.g. <b>Hello</b> <i>World</i>
          text.appendBlank();
          textWithoutFormControls.appendBlank();
        }
      }

      if (aDomNode instanceof HtmlHiddenInput || aDomNode instanceof HtmlApplet || aDomNode instanceof HtmlScript
          || aDomNode instanceof HtmlStyle || aDomNode instanceof HtmlFileInput || aDomNode instanceof DomComment
          || aDomNode instanceof HtmlHead || aDomNode instanceof HtmlTitle) { // NOPMD
        // nothing
      } else if (aDomNode instanceof DomText) {
        appendDomText((DomText) aDomNode);
      } else if (aDomNode instanceof HtmlBreak) {
        text.appendBlank();
        textWithoutFormControls.appendBlank();
      } else if (aDomNode instanceof HtmlButtonInput) {
        appendHtmlButtonInput((HtmlButtonInput) aDomNode);
      } else if (aDomNode instanceof HtmlCheckBoxInput) {
        appendHtmlCheckBoxInput((HtmlCheckBoxInput) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlImageInput) {
        appendHtmlImageInput((HtmlImageInput) aDomNode);
      } else if (aDomNode instanceof HtmlRadioButtonInput) {
        appendHtmlRadioButtonInput((HtmlRadioButtonInput) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlResetInput) {
        appendHtmlResetInput((HtmlResetInput) aDomNode);
      } else if (aDomNode instanceof HtmlSubmitInput) {
        appendHtmlSubmitInput((HtmlSubmitInput) aDomNode);
      } else if (aDomNode instanceof HtmlInput) {
        appendHtmlInput((HtmlInput) aDomNode);
      } else if (aDomNode instanceof HtmlButton) {
        appendHtmlButton((HtmlButton) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlFrame) {
        appendHtmlFrame((HtmlFrame) aDomNode, tmpHierarchy);
      } else if (aDomNode instanceof HtmlImage) {
        appendHtmlImage((HtmlImage) aDomNode);
      } else if (aDomNode instanceof HtmlInlineFrame) {
        appendHtmlInlineFrame((HtmlInlineFrame) aDomNode, tmpHierarchy);
      } else if (aDomNode instanceof HtmlInlineQuotation) {
        appendHtmlInlineQuotation((HtmlInlineQuotation) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlLabel) {
        appendHtmlLabel((HtmlLabel) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlLegend) {
        appendHtmlLegend((HtmlLegend) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlObject) {
        appendHtmlObject((HtmlObject) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlOptionGroup) {
        appendHtmlOptionGroup((HtmlOptionGroup) aDomNode);
      } else if (aDomNode instanceof HtmlOrderedList) {
        appendHtmlOrderedList((HtmlOrderedList) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlSelect) {
        appendHtmlSelect((HtmlSelect) aDomNode, tmpHierarchy, tmpMouseActions);
      } else if (aDomNode instanceof HtmlTextArea) {
        appendHtmlTextArea((HtmlTextArea) aDomNode, tmpHierarchy, tmpMouseActions);
      } else {
        if (aDomNode instanceof HtmlAnchor
            && DomElement.ATTRIBUTE_NOT_DEFINED != ((HtmlAnchor) aDomNode).getHrefAttribute()
            && !tmpMouseActions.contains(MouseAction.CLICK)) {
          // the content of an anchor with href should also be marked as 'clickable'
          tmpMouseActions = copyAndAdd(tmpMouseActions, MouseAction.CLICK);
        }

        final boolean tmpIsBlock = HtmlElementUtil.isBlock(aDomNode);
        if (tmpIsBlock) {
          text.appendBlank();
          textWithoutFormControls.appendBlank();
        }
        parseChildren(aDomNode, tmpHierarchy, tmpMouseActions);
        if (tmpIsBlock) {
          text.appendBlank();
          textWithoutFormControls.appendBlank();
        }
      }

      lastOneWasHtmlElement = tmpIsHtmlElement;
      if (tmpIsHtmlElement) {
        visibleHtmlElementsBottomUp.add((HtmlElement) aDomNode);
      }
    }
    // mark end position of the DOM node
    tmpFindSpot = positions.get(aDomNode);
    tmpFindSpot.setEndPos(text.length());

    tmpFindSpotWFC = positionsWithoutFormControls.get(aDomNode);
    tmpFindSpotWFC.setEndPos(textWithoutFormControls.length());
  }

  private boolean isDisplayed(final DomNode aDomNode) {
    if (!aDomNode.isDisplayed()) {
      return false;
    }

    // RichFaces uses this to hide some entry fields
    // for performance do this check only for span elements at the moment
    if (aDomNode instanceof HtmlSpan) {
      final ScriptableObject tmpScriptableObject = ((HtmlElement) aDomNode).getScriptableObject();
      if (tmpScriptableObject instanceof HTMLElement) {
        final HTMLElement tmpHtmlElement = (HTMLElement) tmpScriptableObject;
        final ComputedCSSStyleDeclaration tmpStyle = tmpHtmlElement.getWindow().getComputedStyle(tmpHtmlElement, null);
        final String tmpPosition = tmpStyle.getStyleAttribute(Definition.POSITION);

        if ("absolute".equalsIgnoreCase(tmpPosition)) {
          final String tmpClip = tmpStyle.getStyleAttribute(Definition.CLIP);
          if ("rect(0px, 0px, 1px, 1px)".equals(tmpClip)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private Set<MouseAction> getAvailableMouseActions(final HtmlElement aHtmlElement,
      final Set<MouseAction> aParentMouseActions) {
    Set<MouseAction> tmpMouseActions = aParentMouseActions;

    // FIXME add support for CSS :hover pseudo class for mouse over

    // click: click, mousedown, mouseup
    // click-double: click, dblclick, mousedown, mouseup
    // click-right: contextmenu, mousedown, mouseup
    // mouse-over: mouseover, mousemove, mouseout
    if (!aParentMouseActions
        .containsAll(EnumSet.of(MouseAction.CLICK, MouseAction.CLICK_DOUBLE, MouseAction.CLICK_RIGHT))
        && (aHtmlElement.hasEventHandlers(EVENT_NAME_MOUSE_DOWN)
            || aHtmlElement.hasEventHandlers(EVENT_NAME_MOUSE_UP))) {
      tmpMouseActions = copyAndAdd(tmpMouseActions, MouseAction.CLICK, MouseAction.CLICK_DOUBLE,
          MouseAction.CLICK_RIGHT);
    } else {
      if (!aParentMouseActions.containsAll(EnumSet.of(MouseAction.CLICK, MouseAction.CLICK_DOUBLE))
          && aHtmlElement.hasEventHandlers(EVENT_NAME_CLICK)) {
        tmpMouseActions = copyAndAdd(tmpMouseActions, MouseAction.CLICK, MouseAction.CLICK_DOUBLE);
      } else if (!aParentMouseActions.contains(MouseAction.CLICK_DOUBLE)
          && aHtmlElement.hasEventHandlers(EVENT_NAME_DBL_CLICK)) {
        tmpMouseActions = copyAndAdd(tmpMouseActions, MouseAction.CLICK_DOUBLE);
      }

      if (!aParentMouseActions.contains(MouseAction.CLICK_RIGHT)
          && aHtmlElement.hasEventHandlers(EVENT_NAME_CONTEXT_MENU)) {
        tmpMouseActions = copyAndAdd(tmpMouseActions, MouseAction.CLICK_RIGHT);
      }
    }

    if (!aParentMouseActions.contains(MouseAction.MOUSE_OVER)
        && (aHtmlElement.hasEventHandlers(EVENT_NAME_MOUSE_OVER) || aHtmlElement.hasEventHandlers(EVENT_NAME_MOUSE_MOVE)
            || aHtmlElement.hasEventHandlers(EVENT_NAME_MOUSE_OUT))) {
      // mouseenter and mouseleave are currently not supported by HtmlUnit
      tmpMouseActions = copyAndAdd(tmpMouseActions, MouseAction.MOUSE_OVER);
    }

    return tmpMouseActions;
  }

  private Set<MouseAction> copyAndAdd(final Set<MouseAction> aCurrentMouseActions,
      final MouseAction... aNewMouseActions) {
    final Set<MouseAction> tmpMouseActions = EnumSet.copyOf(aCurrentMouseActions);
    for (MouseAction tmpNewMouseAction : aNewMouseActions) {
      tmpMouseActions.add(tmpNewMouseAction);
    }
    return tmpMouseActions;
  }

  private void parseChildren(final DomNode aNode, final String aHierarchy, final Set<MouseAction> aMouseActions) {
    for (final DomNode tmpChild : aNode.getChildren()) {
      parseDomNode(tmpChild, aHierarchy, aMouseActions);
    }
  }

  private void appendDomText(final DomText aDomText) {
    String tmpTxt = aDomText.getData();

    // check for style 'text-transform'
    DomNode tmpParent = aDomText.getParentNode();
    while (tmpParent != null && !(tmpParent instanceof HtmlElement)) {
      tmpParent = tmpParent.getParentNode();
    }

    if (tmpParent != null) {
      final HtmlElement tmpParentHtmlElement = (HtmlElement) tmpParent;
      final ScriptableObject tmpScriptableObject = tmpParentHtmlElement.getScriptableObject();
      if (tmpScriptableObject instanceof HTMLElement) {
        final CSSStyleDeclaration tmpStyle = ((HTMLElement) tmpScriptableObject).getCurrentStyle();
        if (tmpStyle != null) {
          final String tmpTransform = tmpStyle.getStyleAttribute(Definition.TEXT_TRANSFORM);

          // for the moment we do not depend on the html lang attribute
          if ("uppercase".equalsIgnoreCase(tmpTransform)) {
            tmpTxt = tmpTxt.toUpperCase(Locale.ROOT);
          } else if ("lowercase".equalsIgnoreCase(tmpTransform)) {
            tmpTxt = tmpTxt.toLowerCase(Locale.ROOT);
          } else if ("capitalize".equalsIgnoreCase(tmpTransform)) {
            tmpTxt = WordUtils.capitalize(tmpTxt);
          }
        }
      }
    }

    text.append(tmpTxt);
    textWithoutFormControls.append(tmpTxt);
  }

  private void appendHtmlButton(final HtmlButton anHtmlButton, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    Set<MouseAction> tmpMouseActions = aMouseActions;
    if (!tmpMouseActions.contains(MouseAction.CLICK)) {
      // the content of a button should also be marked as 'clickable'
      tmpMouseActions = copyAndAdd(tmpMouseActions, MouseAction.CLICK);
    }

    text.appendBlank();
    textWithoutFormControls.appendBlank();
    textWithoutFormControls.disableAppend();
    parseChildren(anHtmlButton, aHierarchy, tmpMouseActions);
    textWithoutFormControls.enableAppend();
    text.appendBlank();
    textWithoutFormControls.appendBlank();
  }

  private void appendHtmlButtonInput(final HtmlButtonInput anHtmlButtonInput) {
    text.appendBlank();
    textWithoutFormControls.appendBlank();
    text.append(anHtmlButtonInput.getValueAttribute());
    text.appendBlank();
  }

  private void appendHtmlCheckBoxInput(final HtmlCheckBoxInput anHtmlCheckBoxInput, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    textWithoutFormControls.disableAppend();
    parseChildren(anHtmlCheckBoxInput, aHierarchy, aMouseActions);
    textWithoutFormControls.enableAppend();
    text.appendBlank();
    textWithoutFormControls.appendBlank();
  }

  private void appendHtmlFrame(final HtmlFrame anHtmlFrame, final String aHierarchy) {
    final Page tmpPage = anHtmlFrame.getEnclosedPage();
    if (tmpPage instanceof HtmlPage) {
      // events are not propagated through the frame 'border' -> start with fresh mouse actions
      parseDomNode((HtmlPage) tmpPage, aHierarchy, EnumSet.noneOf(MouseAction.class));
    }
  }

  private void appendHtmlImage(final HtmlImage anHtmlImage) {
    text.appendBlank();
    textWithoutFormControls.appendBlank();
    text.append(anHtmlImage.getAltAttribute());
    text.appendBlank();
    textWithoutFormControls.append(anHtmlImage.getAltAttribute());
    textWithoutFormControls.appendBlank();
  }

  private void appendHtmlImageInput(final HtmlImageInput anHtmlImageInput) {
    text.appendBlank();
    textWithoutFormControls.appendBlank();
    text.append(anHtmlImageInput.getAltAttribute());
    text.appendBlank();
  }

  private void appendHtmlInlineFrame(final HtmlInlineFrame anHtmlInlineFrame, final String aHierarchy) {
    final Page tmpPage = anHtmlInlineFrame.getEnclosedPage();
    if (tmpPage instanceof HtmlPage) {
      // events are not propagated through the iframe 'border' -> start with fresh mouse actions
      parseDomNode((HtmlPage) tmpPage, aHierarchy, EnumSet.noneOf(MouseAction.class));
    }
  }

  private void appendHtmlInlineQuotation(final HtmlInlineQuotation anHtmlInlineQuotation, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    text.append(" \"");
    textWithoutFormControls.append(" \"");
    parseChildren(anHtmlInlineQuotation, aHierarchy, aMouseActions);
    text.append("\" ");
    textWithoutFormControls.append("\" ");
  }

  private void appendHtmlInput(final HtmlInput anHtmlInput) {
    String tmpValue = anHtmlInput.getValueAttribute();
    if (StringUtils.isEmpty(tmpValue)) {
      tmpValue = anHtmlInput.getAttribute("placeholder");
    }
    text.append(tmpValue);
  }

  private void appendHtmlLabel(final HtmlLabel anHtmlLabel, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    text.appendBlank();
    textWithoutFormControls.appendBlank();
    parseChildren(anHtmlLabel, aHierarchy, aMouseActions);
    text.appendBlank();
    textWithoutFormControls.appendBlank();
  }

  private void appendHtmlLegend(final HtmlLegend anHtmlLegend, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    parseChildren(anHtmlLegend, aHierarchy, aMouseActions);
    text.appendBlank();
    textWithoutFormControls.appendBlank();
  }

  private void appendHtmlObject(final HtmlObject anHtmlObject, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    text.append(" ");
    textWithoutFormControls.append(" ");

    // process childs only if the control is not supported
    final HTMLObjectElement tmpJsObject = anHtmlObject.getScriptableObject();
    if (null == tmpJsObject || null == tmpJsObject.unwrap()) {
      parseChildren(anHtmlObject, aHierarchy, aMouseActions);
    }

    text.append(" ");
    textWithoutFormControls.append(" ");
  }

  private void appendHtmlOptionGroup(final HtmlOptionGroup anHtmlOptionGroup) {
    final String tmpLabel = anHtmlOptionGroup.getLabelAttribute();
    text.append(tmpLabel);
  }

  /**
   * Appends a &lt;ol&gt; taking care to numerate it.
   *
   * @param anHtmlOrderedList the OL element
   */
  private void appendHtmlOrderedList(final HtmlOrderedList anHtmlOrderedList, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    text.appendBlank();
    textWithoutFormControls.appendBlank();

    int i = 1;
    for (final DomNode tmpItem : anHtmlOrderedList.getChildren()) {
      if (tmpItem instanceof HtmlListItem) {
        // hack for fixing the start pos
        final int tmpStartPos = text.length();
        text.append(String.valueOf(i));
        text.append(". ");

        final int tmpStartPosWFC = textWithoutFormControls.length();
        textWithoutFormControls.append(String.valueOf(i++));
        textWithoutFormControls.append(". ");

        parseDomNode(tmpItem, aHierarchy, aMouseActions);
        final FindSpot tmpFindSpot = positions.get(tmpItem);
        tmpFindSpot.setStartPos(tmpStartPos);

        final FindSpot tmpFindSpotWFC = positionsWithoutFormControls.get(tmpItem);
        tmpFindSpotWFC.setStartPos(tmpStartPosWFC);
      } else {
        parseDomNode(tmpItem, aHierarchy, aMouseActions);
      }
    }
    text.appendBlank();
    textWithoutFormControls.appendBlank();
  }

  private void appendHtmlRadioButtonInput(final HtmlRadioButtonInput anHtmlRadioButtonInput, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    textWithoutFormControls.disableAppend();
    parseChildren(anHtmlRadioButtonInput, aHierarchy, aMouseActions);
    textWithoutFormControls.enableAppend();
    text.appendBlank();
    textWithoutFormControls.appendBlank();
  }

  private void appendHtmlResetInput(final HtmlResetInput anHtmlResetInput) {
    text.appendBlank();
    textWithoutFormControls.appendBlank();
    text.append(anHtmlResetInput.getValueAttribute());
    text.appendBlank();
  }

  private void appendHtmlSelect(final HtmlSelect anHtmlSelect, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    textWithoutFormControls.disableAppend();
    for (final DomNode tmpItem : anHtmlSelect.getHtmlElementDescendants()) {
      if (tmpItem instanceof HtmlOption || tmpItem instanceof HtmlOptionGroup) {
        text.appendBlank();
        textWithoutFormControls.appendBlank();
        parseDomNode(tmpItem, aHierarchy, aMouseActions);
      }
    }
    textWithoutFormControls.enableAppend();
    text.appendBlank();
    textWithoutFormControls.appendBlank();
  }

  private void appendHtmlSubmitInput(final HtmlSubmitInput anHtmlSubmitInput) {
    text.appendBlank();
    textWithoutFormControls.appendBlank();
    text.append(anHtmlSubmitInput.getValueAttribute());
    text.appendBlank();
  }

  private void appendHtmlTextArea(final HtmlTextArea anHtmlTextArea, final String aHierarchy,
      final Set<MouseAction> aMouseActions) {
    textWithoutFormControls.disableAppend();
    parseChildren(anHtmlTextArea, aHierarchy, aMouseActions);
    textWithoutFormControls.enableAppend();
  }

  /**
   * Helper to generate log output in case our index seems to be wrong.
   * The usual java {@link IndexOutOfBoundsException} text has no param info.
   *
   * @param aStartPos The beginning index, inclusive.
   * @param anEndPos The ending index, exclusive.
   * @return The new sub string.
   */
  private String textSubstring(final int aStartPos, final int anEndPos) {
    try {
      return text.substring(aStartPos, anEndPos);
    } catch (final IndexOutOfBoundsException e) {
      LOG.error("Invalid position(s) provided for text.substring(); startPos: " + aStartPos + " endPos: " + anEndPos);
      dumpToLog();
      throw e;
    }
  }

  /**
   * Helper for debugging.
   */
  public void dumpToLog() {
    final StringBuilder tmpLog = new StringBuilder(400).append(
        "\n ---- HtmlPageIndex dump -------------------------------------------------------\n text                   : ")
        .append(text).append('\n');

    // nodes/positions
    for (final DomNode tmpDomNode : nodes) {
      final FindSpot tmpPos = positions.get(tmpDomNode);
      // @formatter:off
      tmpLog.append("  ")
          .append(tmpDomNode.getNodeName())
          .append("  [")
          .append(Integer.toString(tmpPos.getStartPos()))
          .append(", ")
          .append(Integer.toString(tmpPos.getEndPos()))
          .append("]  ")
          .append(tmpDomNode.getClass().getName());
      // @formatter:on

      final String tmpValue = tmpDomNode.getNodeValue();
      if (null != tmpValue) {
        tmpLog.append("  '").append(tmpValue).append('\'');
      }
      tmpLog.append('\n');
    }

    tmpLog.append(" ---- end HtmlPageIndex dump ---------------------------------------------------\n");
    LOG.error(tmpLog.toString());
  }
}
