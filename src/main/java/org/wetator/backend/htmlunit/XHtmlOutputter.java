/*
 * Copyright (c) 2008-2024 wetator.org
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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlunit.Page;
import org.htmlunit.WebWindow;
import org.htmlunit.css.ComputedCssStyleDeclaration;
import org.htmlunit.html.BaseFrameElement;
import org.htmlunit.html.DomAttr;
import org.htmlunit.html.DomComment;
import org.htmlunit.html.DomDocumentType;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNamespaceNode;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomText;
import org.htmlunit.html.HtmlAbbreviated;
import org.htmlunit.html.HtmlAcronym;
import org.htmlunit.html.HtmlArea;
import org.htmlunit.html.HtmlBase;
import org.htmlunit.html.HtmlBig;
import org.htmlunit.html.HtmlBody;
import org.htmlunit.html.HtmlBold;
import org.htmlunit.html.HtmlBreak;
import org.htmlunit.html.HtmlButtonInput;
import org.htmlunit.html.HtmlCanvas;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlCitation;
import org.htmlunit.html.HtmlCode;
import org.htmlunit.html.HtmlDefinition;
import org.htmlunit.html.HtmlDeletedText;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlElement.DisplayStyle;
import org.htmlunit.html.HtmlEmbed;
import org.htmlunit.html.HtmlEmphasis;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlFont;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlFrame;
import org.htmlunit.html.HtmlHead;
import org.htmlunit.html.HtmlHeading1;
import org.htmlunit.html.HtmlHeading2;
import org.htmlunit.html.HtmlHeading3;
import org.htmlunit.html.HtmlHeading4;
import org.htmlunit.html.HtmlHeading5;
import org.htmlunit.html.HtmlHeading6;
import org.htmlunit.html.HtmlHiddenInput;
import org.htmlunit.html.HtmlHorizontalRule;
import org.htmlunit.html.HtmlHtml;
import org.htmlunit.html.HtmlImage;
import org.htmlunit.html.HtmlImageInput;
import org.htmlunit.html.HtmlInlineQuotation;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlInsertedText;
import org.htmlunit.html.HtmlItalic;
import org.htmlunit.html.HtmlKeyboard;
import org.htmlunit.html.HtmlLink;
import org.htmlunit.html.HtmlMeta;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlParagraph;
import org.htmlunit.html.HtmlParameter;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlPreformattedText;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlSample;
import org.htmlunit.html.HtmlScript;
import org.htmlunit.html.HtmlSmall;
import org.htmlunit.html.HtmlStrong;
import org.htmlunit.html.HtmlStyle;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlSubscript;
import org.htmlunit.html.HtmlSuperscript;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableDataCell;
import org.htmlunit.html.HtmlTableHeader;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.html.HtmlTeletype;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.html.HtmlTextInput;
import org.htmlunit.html.HtmlTitle;
import org.htmlunit.html.HtmlUnknownElement;
import org.htmlunit.html.HtmlVariable;
import org.htmlunit.javascript.host.html.HTMLCanvasElement;
import org.htmlunit.svg.SvgCircle;
import org.htmlunit.svg.SvgEllipse;
import org.htmlunit.svg.SvgLine;
import org.htmlunit.svg.SvgPath;
import org.htmlunit.svg.SvgPolygon;
import org.htmlunit.svg.SvgPolyline;
import org.htmlunit.svg.SvgRect;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.util.Output;
import org.wetator.util.XMLUtil;

/**
 * Helper methods to write the HtmlUnit page as XHtml to a file.
 *
 * @author rbri
 * @author frank.danek
 */
public final class XHtmlOutputter {
  private static final Logger LOG = LogManager.getLogger(XHtmlOutputter.class);

  public static final String JQUERY = "jquery-1.12.4.min.js";
  public static final String JQUERY_COLOR = "jquery.color-3.0.0.min.js";

  private static final Set<String> EMPTY_TAGS;
  private static final Set<String> SINGLE_LINE_TAGS;
  private static final Set<String> IGNORED_ATTRIBUTES;
  private static final Map<Class<? extends Object>, String> TAG_NAMES = new HashMap<>(100);

  private HtmlPage htmlPage;
  private ResponseStore responseStore;
  private Output output;
  private XMLUtil xmlUtil;

  static {
    EMPTY_TAGS = new HashSet<>();
    EMPTY_TAGS.add(HtmlMeta.class.getName());
    EMPTY_TAGS.add(HtmlLink.class.getName());
    EMPTY_TAGS.add(HtmlFrame.class.getName());

    EMPTY_TAGS.add(HtmlImage.class.getName());

    EMPTY_TAGS.add(HtmlHiddenInput.class.getName());
    EMPTY_TAGS.add(HtmlTextInput.class.getName());
    EMPTY_TAGS.add(HtmlPasswordInput.class.getName());
    EMPTY_TAGS.add(HtmlSubmitInput.class.getName());
    EMPTY_TAGS.add(HtmlButtonInput.class.getName());
    EMPTY_TAGS.add(HtmlImageInput.class.getName());
    EMPTY_TAGS.add(HtmlCheckBoxInput.class.getName());
    EMPTY_TAGS.add(HtmlRadioButtonInput.class.getName());
    EMPTY_TAGS.add(HtmlFileInput.class.getName());

    EMPTY_TAGS.add(HtmlBreak.class.getName());
    EMPTY_TAGS.add(HtmlHorizontalRule.class.getName());

    EMPTY_TAGS.add(HtmlArea.class.getName());
    EMPTY_TAGS.add(HtmlBase.class.getName());
    // EMPTY_TAGS.add(HtmlCol.class.getName());
    EMPTY_TAGS.add(HtmlParameter.class.getName());

    EMPTY_TAGS.add(SvgCircle.class.getName());
    EMPTY_TAGS.add(SvgEllipse.class.getName());
    EMPTY_TAGS.add(SvgLine.class.getName());
    EMPTY_TAGS.add(SvgPath.class.getName());
    EMPTY_TAGS.add(SvgPolygon.class.getName());
    EMPTY_TAGS.add(SvgPolyline.class.getName());
    EMPTY_TAGS.add(SvgRect.class.getName());

    SINGLE_LINE_TAGS = new HashSet<>();
    SINGLE_LINE_TAGS.add(HtmlTitle.class.getName());
    SINGLE_LINE_TAGS.add(HtmlParagraph.class.getName());
    SINGLE_LINE_TAGS.add(HtmlBreak.class.getName());
    SINGLE_LINE_TAGS.add(HtmlHeading1.class.getName());
    SINGLE_LINE_TAGS.add(HtmlHeading2.class.getName());
    SINGLE_LINE_TAGS.add(HtmlHeading3.class.getName());
    SINGLE_LINE_TAGS.add(HtmlHeading4.class.getName());
    SINGLE_LINE_TAGS.add(HtmlHeading5.class.getName());
    SINGLE_LINE_TAGS.add(HtmlHeading6.class.getName());
    SINGLE_LINE_TAGS.add(HtmlOption.class.getName());
    // TextArea because the content is preformated
    SINGLE_LINE_TAGS.add(HtmlTextArea.class.getName());

    SINGLE_LINE_TAGS.add(HtmlFont.class.getName());
    SINGLE_LINE_TAGS.add(HtmlItalic.class.getName());
    SINGLE_LINE_TAGS.add(HtmlBold.class.getName());
    SINGLE_LINE_TAGS.add(HtmlBig.class.getName());
    SINGLE_LINE_TAGS.add(HtmlCanvas.class.getName());
    SINGLE_LINE_TAGS.add(HtmlEmphasis.class.getName());
    SINGLE_LINE_TAGS.add(HtmlSmall.class.getName());
    SINGLE_LINE_TAGS.add(HtmlStrong.class.getName());
    SINGLE_LINE_TAGS.add(HtmlSubscript.class.getName());
    SINGLE_LINE_TAGS.add(HtmlSuperscript.class.getName());
    SINGLE_LINE_TAGS.add(HtmlInsertedText.class.getName());
    SINGLE_LINE_TAGS.add(HtmlDeletedText.class.getName());
    SINGLE_LINE_TAGS.add(HtmlCode.class.getName());
    SINGLE_LINE_TAGS.add(HtmlKeyboard.class.getName());
    SINGLE_LINE_TAGS.add(HtmlSample.class.getName());
    SINGLE_LINE_TAGS.add(HtmlPreformattedText.class.getName());
    SINGLE_LINE_TAGS.add(HtmlTeletype.class.getName());
    SINGLE_LINE_TAGS.add(HtmlVariable.class.getName());
    SINGLE_LINE_TAGS.add(HtmlAbbreviated.class.getName());
    SINGLE_LINE_TAGS.add(HtmlInlineQuotation.class.getName());
    SINGLE_LINE_TAGS.add(HtmlCitation.class.getName());
    SINGLE_LINE_TAGS.add(HtmlAcronym.class.getName());
    SINGLE_LINE_TAGS.add(HtmlDefinition.class.getName());

    IGNORED_ATTRIBUTES = new HashSet<>();
    IGNORED_ATTRIBUTES.add("onclick");
    IGNORED_ATTRIBUTES.add("ondblclick");
    IGNORED_ATTRIBUTES.add("onfocus");
    IGNORED_ATTRIBUTES.add("onblur");
    IGNORED_ATTRIBUTES.add("onselect");
    IGNORED_ATTRIBUTES.add("onchange");
    IGNORED_ATTRIBUTES.add("onload");
    IGNORED_ATTRIBUTES.add("onerror");

    IGNORED_ATTRIBUTES.add("onmousemove");
    IGNORED_ATTRIBUTES.add("onmouseover");
    IGNORED_ATTRIBUTES.add("onmouseout");
    IGNORED_ATTRIBUTES.add("onmousedown");
    IGNORED_ATTRIBUTES.add("onmouseup");

    IGNORED_ATTRIBUTES.add("onkeydown");
    IGNORED_ATTRIBUTES.add("onkeyup");
    IGNORED_ATTRIBUTES.add("onkeypress");
  }

  /**
   * The constructor.
   *
   * @param anHtmlPage the page to be written
   * @param aResponseStore the response store that is responsible to store files
   *        linked from this page. The links are changed to point to this page
   */
  public XHtmlOutputter(final HtmlPage anHtmlPage, final ResponseStore aResponseStore) {
    super();
    htmlPage = anHtmlPage;
    responseStore = aResponseStore;
  }

  /**
   * The real worker; dumps the page as XHTML to this file.
   *
   * @param aFile the file to write to
   * @throws IOException in case of error
   */
  public void writeTo(final File aFile) throws IOException {
    final FileWriterWithEncoding tmpFileWriter = FileWriterWithEncoding.builder().setFile(aFile)
        .setCharset(htmlPage.getCharset()).setCharsetEncoder(null).setAppend(false).get();
    writeTo(tmpFileWriter);
  }

  /**
   * The real worker; dumps the page as XHTML to this file.
   *
   * @param aWriter the writer to write on
   * @throws IOException in case of error
   */
  public void writeTo(final Writer aWriter) throws IOException {
    try { // NOPMD
      xmlUtil = new XMLUtil();
      output = new Output(aWriter, "  ");

      output.println("<?xml version=\"1.0\" encoding=\"" + htmlPage.getCharset().name() + "\"?>");
      output.println(
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");

      writeSubNodes(htmlPage, new Context());
    } finally {
      // this closes the writer also
      output.close();
    }
  }

  /**
   * Helper rot writing subnodes.
   *
   * @param aDomNode the parent node
   * @param aContext the context of this process
   * @throws IOException in case of error
   */
  private void writeSubNodes(final DomNode aDomNode, final Context aContext) throws IOException {
    DomNode tmpChild = aDomNode.getFirstChild();

    while (null != tmpChild) {
      // ignore script required; we build a screenshot
      // ignore HtmlBase because we rebuild relative css links to our local files
      if (!(tmpChild instanceof DomDocumentType) && !(tmpChild instanceof HtmlScript)
          && !(tmpChild instanceof DomComment) && !(tmpChild instanceof HtmlBase)) {

        if (tmpChild instanceof HtmlCanvas) {
          writeCanvasImage((HtmlCanvas) tmpChild, aContext);
        } else {
          writeStartTag(tmpChild, aContext);
          output.indent();
          writeSubNodes(tmpChild, aContext);
          output.unindent();
          writeEndTag(tmpChild, aContext);
        }
      }

      tmpChild = tmpChild.getNextSibling();
    }
  }

  /**
   * Writes the opening part of the tag.
   *
   * @param aDomNode the node to work on
   * @param aContext the context of this process
   * @throws IOException in case of error
   */
  private void writeStartTag(final DomNode aDomNode, final Context aContext) throws IOException {
    if (aDomNode instanceof HtmlHtml) {
      output.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
      output.print("<!-- Browser URL: ").print(htmlPage.getUrl().toExternalForm()).println(" -->");
    } else if (aDomNode instanceof HtmlUnknownElement) {
      output.print('<');
      output.print(((HtmlUnknownElement) aDomNode).getQualifiedName());
      writeAttributes(aDomNode);
      output.println(">");
    } else if (aDomNode instanceof DomElement) {
      if (aDomNode instanceof HtmlPreformattedText) {
        aContext.insidePre++;
      }

      output.print('<');
      output.print(determineTag(aDomNode));
      writeAttributes(aDomNode);

      if (aDomNode instanceof HtmlForm) {
        // disable form submit
        output.print(" onsubmit=\"return false;\"");
      }

      if (EMPTY_TAGS.contains(aDomNode.getClass().getName())) {
        output.print('/');
        if (HtmlElementUtil.isBlock(aDomNode) && aContext.insidePre == 0) {
          output.println(">");
        } else {
          output.print(">");
        }
      } else if (SINGLE_LINE_TAGS.contains(aDomNode.getClass().getName()) || aContext.insidePre > 0) {
        output.print(">");
      } else {
        output.println(">");
      }

      if (aDomNode instanceof HtmlHead) {
        // inject some js libs for highlighting
        output.indent();
        output.println("<script src='../../resources/" + JQUERY + "'></script>");
        output.println("<script src='../../resources/" + JQUERY_COLOR + "'></script>");
        output.println("<script src='../../resources/wetator_report.js'></script>");
        output.unindent();
      }
    } else if (aDomNode instanceof DomText) {
      String tmpText = ((DomText) aDomNode).getData();
      if (StringUtils.isEmpty(tmpText)) {
        output.print(tmpText);
      } else {
        final DomNode tmpParentNode = aDomNode.getParentNode();
        if (tmpParentNode instanceof HtmlStyle) {
          output.indent();

          // process all url(....) inside
          final URL tmpBaseUrl = htmlPage.getWebResponse().getWebRequest().getUrl();
          tmpText = responseStore.processCSS(tmpBaseUrl, tmpText, 0);

          output.println(tmpText);
          output.unindent();
        } else if (SINGLE_LINE_TAGS.contains(tmpParentNode.getClass().getName()) || aContext.insidePre > 0) {
          output.print(xmlUtil.normalizeBodyValue(tmpText));
        } else {
          output.println(xmlUtil.normalizeBodyValue(tmpText));
        }
      }
    } else {
      LOG.warn("Unknown DomNode " + aDomNode);
    }
  }

  /**
   * Writes the closing part of the tag.
   *
   * @param aDomNode the node to work on
   * @param aContext the context of this process
   * @throws IOException in case of error
   */
  private void writeEndTag(final DomNode aDomNode, final Context aContext) throws IOException {
    if (aDomNode instanceof HtmlHtml) {
      output.println("</html>");
    } else if (aDomNode instanceof HtmlUnknownElement) {
      output.print("</");
      output.print(((DomNamespaceNode) aDomNode).getQualifiedName());
      output.println(">");
    } else if (aDomNode instanceof DomElement) {
      if (aDomNode instanceof HtmlBody) {
        // our highlighting code
        output.indent();
        output.println("<script>");
        output.indent();
        output.println("highlight();");
        output.unindent();
        output.println("</script>");
        output.unindent();
      }

      if (aDomNode instanceof HtmlPreformattedText) {
        aContext.insidePre--;
      }

      if (!EMPTY_TAGS.contains(aDomNode.getClass().getName())) {
        output.print("</");
        output.print(determineTag(aDomNode));

        if (HtmlElementUtil.isBlock(aDomNode) && aContext.insidePre == 0) {
          output.println(">");
        } else {
          output.print(">");
        }
      }
    }
    // ignore the unsupported ones because they are reported form the start tag handler
  }

  /**
   * Writes the attributes of the tag.
   *
   * @param aDomNode the node to work on
   * @throws IOException in case of error
   */
  private void writeAttributes(final DomNode aDomNode) throws IOException {
    if (aDomNode instanceof DomElement) {
      final DomElement tmpDomElement = (DomElement) aDomNode;

      boolean tmpIsCssLink = false;
      if (tmpDomElement instanceof HtmlLink) {
        final HtmlLink tmpHtmlLink = (HtmlLink) tmpDomElement;

        if ("stylesheet".equalsIgnoreCase(tmpHtmlLink.getRelAttribute())) {
          tmpIsCssLink = true;
        }
      }

      final boolean tmpIsHtmlImage = tmpDomElement instanceof HtmlImage;
      final boolean tmpIsHtmlImageInput = tmpDomElement instanceof HtmlImageInput;

      final boolean tmpIsChecked = tmpDomElement instanceof HtmlCheckBoxInput
          || tmpDomElement instanceof HtmlRadioButtonInput;
      final boolean tmpIsHtmlOption = tmpDomElement instanceof HtmlOption;

      final URL tmpBaseUrl = htmlPage.getWebResponse().getWebRequest().getUrl();

      final Map<String, DomAttr> tmpAttributes = new HashMap<>(tmpDomElement.getAttributesMap());

      // some HtmlUnitControls are special
      if (tmpIsHtmlOption && ((HtmlOption) tmpDomElement).isSelected()) {
        output.print(" selected=\"selected\"");
      }

      // we are doing a screenshot here; reflect the current state of the control
      if (tmpIsChecked && ((HtmlInput) tmpDomElement).isChecked()) {
        output.print(" checked=\"checked\"");
      }

      // fixed order for readability and testability
      final List<String> tmpSortedAttributeNames = tmpAttributes.keySet().stream().map(k -> k.toLowerCase(Locale.ROOT))
          .sorted().collect(Collectors.toList());

      if (tmpDomElement instanceof HtmlInput) {
        tmpSortedAttributeNames.remove("value");
        tmpSortedAttributeNames.add(0, "value");
        if (tmpSortedAttributeNames.remove("type")) {
          tmpSortedAttributeNames.add(0, "type");
        }
      }
      if (tmpSortedAttributeNames.remove("name")) {
        tmpSortedAttributeNames.add(0, "name");
      }
      if (tmpSortedAttributeNames.remove("id")) {
        tmpSortedAttributeNames.add(0, "id");
      }

      boolean tmpStyleDefined = false;
      for (final String tmpAttributeName : tmpSortedAttributeNames) {
        final DomAttr tmpAttribute = tmpAttributes.get(tmpAttributeName);

        if (!IGNORED_ATTRIBUTES.contains(tmpAttributeName)) {

          // selected attribute is handled based on the isSelected state already
          if (tmpIsHtmlOption && "selected".equals(tmpAttributeName)) {
            continue;
          }

          String tmpAttributeValue;
          if (tmpDomElement instanceof HtmlInput && "value".equals(tmpAttributeName)) {
            tmpAttributeValue = ((HtmlInput) tmpDomElement).getValue();
          } else {
            tmpAttributeValue = tmpAttribute.getNodeValue();
          }

          // no output of javascript actions
          if (StringUtils.startsWithIgnoreCase(tmpAttributeValue, "javascript:")) {
            continue;
          }

          if ("style".equals(tmpAttributeName)) {
            tmpStyleDefined = true;

            // process all url(....) inside
            if (responseStore != null) {
              tmpAttributeValue = responseStore.processCSS(tmpBaseUrl, tmpAttributeValue, 0);
            }

            // ignore options for the moment, IE reports display style inline for options
            if (aDomNode instanceof HtmlElement && !(aDomNode instanceof HtmlOption)) {
              final HtmlElement tmpElement = (HtmlElement) aDomNode;
              // hopefully no one will ever made tags like head visible
              if (!DisplayStyle.NONE.value().equals(tmpElement.getDefaultStyleDisplay().value())) {
                final ComputedCssStyleDeclaration tmpStyle = tmpElement.getPage().getEnclosingWindow()
                    .getComputedStyle(tmpElement, null);
                // for the moment i have no better idea than always hard wire the display info
                // but place display in front just in case HtmlUnit calculates a wrong display
                // the real value overwrites then
                tmpAttributeValue = new StringBuilder().append("display: ").append(tmpStyle.getDisplay()).append("; ")
                    .append(tmpAttributeValue).toString();
              }
            }
          }

          if (tmpIsCssLink && "href".equals(tmpAttributeName)) {
            final URL tmpUrl = tmpDomElement.getHtmlPageOrNull().getFullyQualifiedUrl(tmpAttributeValue);
            final String tmpStoredFileName = responseStore.storeContentFromUrl(tmpBaseUrl, tmpUrl,
                (HtmlLink) tmpDomElement, null, 0, ".css");
            if (null != tmpStoredFileName) {
              tmpAttributeValue = tmpStoredFileName;
            }
          }

          if (tmpIsHtmlImage && "src".equals(tmpAttributeName)) {
            final URL tmpUrl = tmpDomElement.getHtmlPageOrNull().getFullyQualifiedUrl(tmpAttributeValue);
            final String tmpStoredFileName = responseStore.storeContentFromUrl(tmpBaseUrl, tmpUrl, null,
                (HtmlImage) tmpDomElement, 0, null);
            if (null != tmpStoredFileName) {
              tmpAttributeValue = tmpStoredFileName;
            }
          }

          if ((tmpIsHtmlImageInput || tmpDomElement instanceof HtmlEmbed) && "src".equals(tmpAttributeName)) {
            final URL tmpUrl = tmpDomElement.getHtmlPageOrNull().getFullyQualifiedUrl(tmpAttributeValue);
            final String tmpStoredFileName = responseStore.storeContentFromUrl(tmpBaseUrl, tmpUrl, null, null, 0, null);
            if (null != tmpStoredFileName) {
              tmpAttributeValue = tmpStoredFileName;
            }
          }

          if (tmpDomElement instanceof BaseFrameElement && "src".equals(tmpAttributeName)) {
            final BaseFrameElement tmpFrame = (BaseFrameElement) aDomNode;

            // prevent NPE
            final WebWindow tmpWebWindow = tmpFrame.getEnclosedWindow();
            if (null == tmpWebWindow) {
              LOG.warn("Frame with enclosed EnclosedWindow == null (" + tmpFrame.toString() + ").");
            } else {
              final Page tmpPage = tmpWebWindow.getEnclosedPage();
              if (null == tmpPage) {
                LOG.warn("Frame with enclosed EnclosedPage == null (" + tmpFrame.toString() + ").");
              } else {
                final String tmpStoredFileName = responseStore.storePage(tmpPage);
                if (null != tmpStoredFileName) {
                  tmpAttributeValue = "../../" + tmpStoredFileName;
                }
              }
            }
          }

          if ("background".equals(tmpAttributeName)
              && (tmpDomElement instanceof HtmlTable || tmpDomElement instanceof HtmlTableHeader
                  || tmpDomElement instanceof HtmlTableRow || tmpDomElement instanceof HtmlTableDataCell)) {
            final URL tmpUrl = tmpDomElement.getHtmlPageOrNull().getFullyQualifiedUrl(tmpAttributeValue);
            final String tmpStoredFileName = responseStore.storeContentFromUrl(tmpBaseUrl, tmpUrl, null, null, 0, null);
            if (null != tmpStoredFileName) {
              tmpAttributeValue = tmpStoredFileName;
            }
          }

          if (tmpDomElement instanceof HtmlPasswordInput && "value".equals(tmpAttributeName)
              && StringUtils.isNotEmpty(tmpAttributeValue)) {
            tmpAttributeValue = StringUtils.repeat("*", tmpAttributeValue.length());
          }

          // Don't print if value="Submit Query"
          // see org.htmlunit.html.HtmlSubmitInput
          if (tmpDomElement instanceof HtmlSubmitInput && "value".equals(tmpAttributeName)
              && "Submit Query".equals(tmpAttributeValue)) {
            continue;
          }

          // special cases
          if ("checked".equals(tmpAttributeName)) {
            if (tmpIsChecked) {
              // do not pass the checked attribute, we are doing a screenshot here
              // we have to reflect the current state of the control
              continue;
            }
            if (StringUtils.isEmpty(tmpAttributeValue)) {
              tmpAttributeValue = "checked";
            }
          }
          if ("multiple".equals(tmpAttributeName) && StringUtils.isEmpty(tmpAttributeValue)) {
            tmpAttributeValue = "multiple";
          }

          output.print(' ');
          output.print(tmpAttributeName);
          output.print("=\"");
          output.print(xmlUtil.normalizeAttributeValue(tmpAttributeValue));
          output.print('"');
        }
      }

      // ignore options for the moment, IE reports display style inline for options
      if (!tmpStyleDefined && aDomNode instanceof HtmlElement && !(aDomNode instanceof HtmlOption)) {
        final HtmlElement tmpElem = (HtmlElement) aDomNode;
        // hopefully no one will ever made tags like head visible
        if (!DisplayStyle.NONE.value().equals(tmpElem.getDefaultStyleDisplay().value())) {
          final ComputedCssStyleDeclaration tmpStyle = tmpElem.getPage().getEnclosingWindow().getComputedStyle(tmpElem,
              null);
          // for the moment i have no better idea than always hard wire the display info
          output.print(" style=\"display: ");
          output.print(tmpStyle.getDisplay());
          output.print('"');
        }
      }
    }

  }

  private String determineTag(final DomNode aDomNode) {
    Class<? extends Object> tmpNodeClass = aDomNode.getClass();
    // String tmpTag = null;
    String tmpTag = TAG_NAMES.get(tmpNodeClass);
    if (null != tmpTag) {
      return tmpTag;
    }

    while (tmpNodeClass != HtmlElement.class) {
      try {
        final Field tmpField = tmpNodeClass.getDeclaredField("TAG_NAME");
        tmpTag = (String) tmpField.get(null);
        TAG_NAMES.put(tmpNodeClass, tmpTag);
        return tmpTag;
      } catch (final NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { // NOPMD
        // ignore
      }
      tmpNodeClass = tmpNodeClass.getSuperclass();
    }

    LOG.warn("Unsupported element " + aDomNode);
    return aDomNode.getClass().getName();
  }

  private void writeCanvasImage(final HtmlCanvas aCanvas, final Context aContext) throws IOException {
    output.print("<!-- ");
    writeStartTag(aCanvas, aContext);
    output.indent();
    writeSubNodes(aCanvas, aContext);
    output.unindent();
    writeEndTag(aCanvas, aContext);
    output.println("-->");

    final HTMLCanvasElement tmpCanvas = aCanvas.getScriptableObject();
    output.print("<img src='");
    output.print(tmpCanvas.toDataURL("png"));
    output.print("' height='" + tmpCanvas.getHeight());
    output.print("' width='" + tmpCanvas.getWidth());

    String tmpAttrib = aCanvas.getAttribute("style");
    if (DomElement.ATTRIBUTE_NOT_DEFINED != tmpAttrib) {
      output.print("' style='" + aCanvas.getAttribute("style"));
    }

    tmpAttrib = aCanvas.getAttribute("class");
    if (DomElement.ATTRIBUTE_NOT_DEFINED != tmpAttrib) {
      output.print("' class='" + aCanvas.getAttribute("class"));
    }

    output.print("'>");
  }

  private static final class Context {
    private int insidePre;
  }
}
