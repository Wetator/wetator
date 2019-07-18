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


package org.wetator.backend.htmlunit;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.util.Output;
import org.wetator.util.XMLUtil;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.BaseFrameElement;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomComment;
import com.gargoylesoftware.htmlunit.html.DomDocumentType;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAbbreviated;
import com.gargoylesoftware.htmlunit.html.HtmlAcronym;
import com.gargoylesoftware.htmlunit.html.HtmlArea;
import com.gargoylesoftware.htmlunit.html.HtmlBase;
import com.gargoylesoftware.htmlunit.html.HtmlBig;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlBreak;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCanvas;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlCitation;
import com.gargoylesoftware.htmlunit.html.HtmlCode;
import com.gargoylesoftware.htmlunit.html.HtmlDefinition;
import com.gargoylesoftware.htmlunit.html.HtmlDeletedText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement.DisplayStyle;
import com.gargoylesoftware.htmlunit.html.HtmlEmbed;
import com.gargoylesoftware.htmlunit.html.HtmlEmphasis;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlFont;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlFrame;
import com.gargoylesoftware.htmlunit.html.HtmlHead;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlHeading4;
import com.gargoylesoftware.htmlunit.html.HtmlHeading5;
import com.gargoylesoftware.htmlunit.html.HtmlHeading6;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlHorizontalRule;
import com.gargoylesoftware.htmlunit.html.HtmlHtml;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInlineQuotation;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlInsertedText;
import com.gargoylesoftware.htmlunit.html.HtmlItalic;
import com.gargoylesoftware.htmlunit.html.HtmlKeyboard;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlParameter;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlPreformattedText;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSample;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.gargoylesoftware.htmlunit.html.HtmlSmall;
import com.gargoylesoftware.htmlunit.html.HtmlStrong;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubscript;
import com.gargoylesoftware.htmlunit.html.HtmlSuperscript;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeader;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTeletype;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlTitle;
import com.gargoylesoftware.htmlunit.html.HtmlUnknownElement;
import com.gargoylesoftware.htmlunit.html.HtmlVariable;
import com.gargoylesoftware.htmlunit.javascript.host.Element;
import com.gargoylesoftware.htmlunit.javascript.host.css.CSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLCanvasElement;
import com.gargoylesoftware.htmlunit.svg.SvgCircle;
import com.gargoylesoftware.htmlunit.svg.SvgEllipse;
import com.gargoylesoftware.htmlunit.svg.SvgLine;
import com.gargoylesoftware.htmlunit.svg.SvgPath;
import com.gargoylesoftware.htmlunit.svg.SvgPolygon;
import com.gargoylesoftware.htmlunit.svg.SvgPolyline;
import com.gargoylesoftware.htmlunit.svg.SvgRect;

/**
 * Helper methods to write the HtmlUnit page as XHtml to a file.
 *
 * @author rbri
 * @author frank.danek
 */
public final class XHtmlOutputter {
  private static final Logger LOG = LogManager.getLogger(XHtmlOutputter.class);

  private static final Set<String> EMPTY_TAGS;
  private static final Set<String> SINGLE_LINE_TAGS;
  private static final Set<String> IGNORED_ATTRIBUTES;
  private static final Map<Class<? extends Object>, String> TAG_NAMES = new HashMap<Class<? extends Object>, String>(
      100);

  private HtmlPage htmlPage;
  private ResponseStore responseStore;
  private Output output;
  private XMLUtil xmlUtil;

  static {
    EMPTY_TAGS = new HashSet<String>();
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

    SINGLE_LINE_TAGS = new HashSet<String>();
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

    IGNORED_ATTRIBUTES = new HashSet<String>();
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
    final FileWriterWithEncoding tmpFileWriter = new FileWriterWithEncoding(aFile, htmlPage.getCharset()); // NOPMD
    writeTo(tmpFileWriter);
  }

  /**
   * The real worker; dumps the page as XHTML to this file.
   *
   * @param aWriter the writer to write on
   * @throws IOException in case of error
   */
  public void writeTo(final Writer aWriter) throws IOException {
    try {
      xmlUtil = new XMLUtil();
      output = new Output(aWriter, "  ");

      output.println("<?xml version=\"1.0\" encoding=\"" + htmlPage.getCharset().name() + "\"?>");
      output.println(
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");

      writeSubNodes(htmlPage);

      output.flush();
    } finally {
      aWriter.close();
    }
  }

  /**
   * Helper rot writing subnodes.
   *
   * @param aDomNode the parent node
   * @throws IOException in case of error
   */
  private void writeSubNodes(final DomNode aDomNode) throws IOException {
    DomNode tmpChild = aDomNode.getFirstChild();

    while (null != tmpChild) {
      // ignore script required; we build a screenshot
      // ignore HtmlBase because we rebuild relative css links to our local files
      if (!(tmpChild instanceof DomDocumentType) && !(tmpChild instanceof HtmlScript)
          && !(tmpChild instanceof DomComment) && !(tmpChild instanceof HtmlBase)) {

        if (tmpChild instanceof HtmlCanvas) {
          writeCanvasImage((HtmlCanvas) tmpChild);
        } else {
          writeStartTag(tmpChild);
          output.indent();
          writeSubNodes(tmpChild);
          output.unindent();
          writeEndTag(tmpChild);
        }
      }

      tmpChild = tmpChild.getNextSibling();
    }
  }

  /**
   * Writes the opening part of the tag.
   *
   * @param aDomNode the node to work on
   * @throws IOException in case of error
   */
  private void writeStartTag(final DomNode aDomNode) throws IOException {
    if (aDomNode instanceof HtmlHtml) {
      output.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
      output.print("<!-- Browser URL: ").print(htmlPage.getUrl().toExternalForm()).println(" -->");
    } else if (aDomNode instanceof HtmlUnknownElement) {
      output.print('<');
      output.print(((HtmlUnknownElement) aDomNode).getQualifiedName());
      writeAttributes(aDomNode);
      output.println(">");
    } else if (aDomNode instanceof DomElement) {
      output.print('<');
      output.print(determineTag(aDomNode));
      writeAttributes(aDomNode);

      if (aDomNode instanceof HtmlForm) {
        // disable form submit
        output.print(" onsubmit=\"return false;\"");
      }

      if (EMPTY_TAGS.contains(aDomNode.getClass().getName())) {
        output.print('/');
        if (HtmlElementUtil.isBlock(aDomNode)) {
          output.println(">");
        } else {
          output.print(">");
        }
      } else if (SINGLE_LINE_TAGS.contains(aDomNode.getClass().getName())) {
        output.print(">");
      } else {
        output.println(">");
      }

      if (aDomNode instanceof HtmlHead) {
        // inject some js libs for highlighting
        output.indent();
        output.println("<script src='../../resources/jquery-1.10.2.min.js'></script>");
        output.println("<script src='../../resources/jquery.color-2.1.2.min.js'></script>");
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
        } else if (SINGLE_LINE_TAGS.contains(tmpParentNode.getClass().getName())) {
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
   * @throws IOException in case of error
   */
  private void writeEndTag(final DomNode aDomNode) throws IOException {
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

      if (!EMPTY_TAGS.contains(aDomNode.getClass().getName())) {
        output.print("</");
        output.print(determineTag(aDomNode));

        if (HtmlElementUtil.isBlock(aDomNode)) {
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

      final Map<String, DomAttr> tmpAttributes = tmpDomElement.getAttributesMap();

      // some HtmlUnitControls are special
      if (tmpIsHtmlOption && ((HtmlOption) tmpDomElement).isSelected()) {
        output.print(" selected=\"selected\"");
      }

      // we are doing a screenshot here; reflect the current state of the control
      if (tmpIsChecked && ((HtmlInput) tmpDomElement).isChecked()) {
        output.print(" checked=\"checked\"");
      }

      boolean tmpStyleDefined = false;
      for (final DomAttr tmpAttribute : tmpAttributes.values()) {
        final String tmpAttributeName = tmpAttribute.getNodeName().toLowerCase(Locale.ROOT);

        if (!IGNORED_ATTRIBUTES.contains(tmpAttributeName)) {

          // selected attribute is handled based on the isSelected state already
          if (tmpIsHtmlOption && "selected".equals(tmpAttributeName)) {
            continue;
          }

          String tmpAttributeValue = tmpAttribute.getNodeValue();
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
                final Element tmpElemScript = aDomNode.getScriptableObject();
                final CSSStyleDeclaration tmpStyle = tmpElemScript.getWindow().getComputedStyle(tmpElemScript, null);
                // for the moment i have no better idea than always hard wire the display info
                tmpAttributeValue = new StringBuilder().append(tmpAttributeValue).append("; display: ")
                    .append(tmpStyle.getDisplay()).toString();
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
          // see com.gargoylesoftware.htmlunit.html.HtmlSubmitInput
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
          final Element tmpElemScript = aDomNode.getScriptableObject();
          final CSSStyleDeclaration tmpStyle = tmpElemScript.getWindow().getComputedStyle(tmpElemScript, null);
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

  private void writeCanvasImage(final HtmlCanvas aCanvas) throws IOException {
    output.print("<!-- ");
    writeStartTag(aCanvas);
    output.indent();
    writeSubNodes(aCanvas);
    output.unindent();
    writeEndTag(aCanvas);
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

}
