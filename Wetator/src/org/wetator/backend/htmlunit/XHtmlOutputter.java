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


package org.wetator.backend.htmlunit;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.util.Output;
import org.wetator.util.XMLUtil;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomComment;
import com.gargoylesoftware.htmlunit.html.DomDocumentType;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlArea;
import com.gargoylesoftware.htmlunit.html.HtmlBase;
import com.gargoylesoftware.htmlunit.html.HtmlBreak;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
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
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlParameter;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlTitle;
import com.gargoylesoftware.htmlunit.html.HtmlUnknownElement;

/**
 * Helper methods to write the HtmlUnit page as XHtml to a file.
 * 
 * @author rbri
 */
public final class XHtmlOutputter {
  private static final Log LOG = LogFactory.getLog(XHtmlOutputter.class);;

  private static final Set<String> EMPTY_TAGS;
  private static final Set<String> SINGLE_LINE_TAGS;
  private static final Set<String> IGNORED_ATTRIBUTES;

  private HtmlPage htmlPage;
  private ResponseStore responseStore;
  private Output output;
  private XMLUtil xMLUtil;

  static {
    EMPTY_TAGS = new HashSet<String>();
    EMPTY_TAGS.add(HtmlMeta.class.getName());
    EMPTY_TAGS.add(HtmlLink.class.getName());

    EMPTY_TAGS.add(HtmlImage.class.getName());

    EMPTY_TAGS.add(HtmlHiddenInput.class.getName());
    EMPTY_TAGS.add(HtmlTextInput.class.getName());
    EMPTY_TAGS.add(HtmlPasswordInput.class.getName());
    EMPTY_TAGS.add(HtmlSubmitInput.class.getName());
    EMPTY_TAGS.add(HtmlButtonInput.class.getName());
    EMPTY_TAGS.add(HtmlImageInput.class.getName());
    EMPTY_TAGS.add(HtmlCheckBoxInput.class.getName());
    EMPTY_TAGS.add(HtmlRadioButtonInput.class.getName());

    EMPTY_TAGS.add(HtmlBreak.class.getName());
    EMPTY_TAGS.add(HtmlHorizontalRule.class.getName());

    EMPTY_TAGS.add(HtmlArea.class.getName());
    EMPTY_TAGS.add(HtmlBase.class.getName());
    // EMPTY_TAGS.add(HtmlCol.class.getName());
    EMPTY_TAGS.add(HtmlParameter.class.getName());

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

    IGNORED_ATTRIBUTES = new HashSet<String>();
    IGNORED_ATTRIBUTES.add("onclick");
    IGNORED_ATTRIBUTES.add("ondblclick");
    IGNORED_ATTRIBUTES.add("onfocus");
    IGNORED_ATTRIBUTES.add("onblur");
    IGNORED_ATTRIBUTES.add("onselect");
    IGNORED_ATTRIBUTES.add("onchange");

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
    final FileWriterWithEncoding tmpFileWriter = new FileWriterWithEncoding(aFile, determineEncoding());
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
      final String tmpEncoding = determineEncoding();
      xMLUtil = new XMLUtil(tmpEncoding);
      output = new Output(aWriter, "  ");

      output.println("<?xml version=\"1.0\" encoding=\"" + tmpEncoding + "\"?>");
      output
          .println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");

      writeSubNodes(htmlPage);

      output.flush();
    } finally {
      aWriter.close();
    }
  }

  private String determineEncoding() {
    final String tmpEncoding = htmlPage.getPageEncoding();
    if (StringUtils.isBlank(tmpEncoding)) {
      return "UTF-8";
    }
    return tmpEncoding.toUpperCase();
  }

  /**
   * Helper rot writing subnodes.
   * 
   * @param aDomNode the parent node
   * @throws IOException in case of error
   */
  protected void writeSubNodes(final DomNode aDomNode) throws IOException {
    DomNode tmpChild;

    tmpChild = aDomNode.getFirstChild();

    while (null != tmpChild) {
      if ((tmpChild instanceof DomDocumentType) || (tmpChild instanceof HtmlScript) || (tmpChild instanceof DomComment)) {
        // ignore
      } else {
        writeStartTag(tmpChild);
        output.indent();
        writeSubNodes(tmpChild);
        output.unindent();
        writeEndTag(tmpChild);
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
  protected void writeStartTag(final DomNode aDomNode) throws IOException {
    if (aDomNode instanceof HtmlHtml) {
      output.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
    } else if (aDomNode instanceof HtmlUnknownElement) {
      output.print('<');
      output.print(((HtmlUnknownElement) aDomNode).getQualifiedName());
      writeAttributes(aDomNode);
      output.println(">");
    } else if (aDomNode instanceof HtmlElement) {
      output.print('<');
      output.print(determineTag(aDomNode));
      writeAttributes(aDomNode);
      if (EMPTY_TAGS.contains(aDomNode.getClass().getName())) {
        output.print('/');
      }
      if (SINGLE_LINE_TAGS.contains(aDomNode.getClass().getName())) {
        output.print(">");
      } else {
        output.println(">");
      }
    } else if (aDomNode instanceof DomText) {
      String tmpText = ((DomText) aDomNode).getData();
      if (StringUtils.isEmpty(tmpText)) {
        output.print(tmpText);
      } else {
        if (aDomNode.getParentNode() instanceof HtmlStyle) {
          output.indent();

          // process all url(....) inside
          final URL tmpBaseUrl = htmlPage.getWebResponse().getWebRequest().getUrl();
          tmpText = responseStore.processCSS(tmpBaseUrl, tmpText, 0);

          output.println(tmpText);
          output.unindent();
        } else if (SINGLE_LINE_TAGS.contains(aDomNode.getParentNode().getClass().getName())) {
          output.print(xMLUtil.normalizeBodyValue(tmpText));
        } else {
          output.println(xMLUtil.normalizeBodyValue(tmpText));
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
  protected void writeEndTag(final DomNode aDomNode) throws IOException {
    if (aDomNode instanceof HtmlHtml) {
      output.println("</html>");
    } else if (aDomNode instanceof HtmlUnknownElement) {
      output.print("</");
      output.print(((DomNamespaceNode) aDomNode).getQualifiedName());
      output.println(">");
    } else if (aDomNode instanceof HtmlElement) {
      if (!EMPTY_TAGS.contains(aDomNode.getClass().getName())) {
        output.print("</");
        output.print(determineTag(aDomNode));
        output.println(">");
      }
    } else if (aDomNode instanceof DomText) {
      // nothing to do
    }
    // ignore the unsupported ones because they are reported form the start tag handler
  }

  /**
   * Writes the attributes of the tag.
   * 
   * @param aDomNode the node to work on
   * @throws IOException in case of error
   */
  protected void writeAttributes(final DomNode aDomNode) throws IOException {
    if (aDomNode instanceof HtmlElement) {
      final HtmlElement tmpHtmlElement = (HtmlElement) aDomNode;

      boolean tmpIsCssLink = false;
      if (tmpHtmlElement instanceof HtmlLink) {
        final HtmlLink tmpHtmlLink = (HtmlLink) tmpHtmlElement;

        if ("text/css".equals(tmpHtmlLink.getTypeAttribute()) && "stylesheet".equals(tmpHtmlLink.getRelAttribute())) {
          tmpIsCssLink = true;
        }
      }

      final boolean tmpIsHtmlImage = tmpHtmlElement instanceof HtmlImage;
      final boolean tmpIsHtmlInlineFrame = tmpHtmlElement instanceof HtmlInlineFrame;
      final boolean tmpIsHtmlPasswordInput = tmpHtmlElement instanceof HtmlPasswordInput;
      final boolean tmpIsHtmlSubmitInput = tmpHtmlElement instanceof HtmlSubmitInput;
      final URL tmpBaseUrl = htmlPage.getWebResponse().getWebRequest().getUrl();

      final Map<String, DomAttr> tmpAttributes = tmpHtmlElement.getAttributesMap();

      // some HtmlUnitControls are special
      if (tmpHtmlElement instanceof HtmlOption) {
        final HtmlOption tmpHtmlOption = (HtmlOption) tmpHtmlElement;
        if (tmpHtmlOption.isSelected() && tmpHtmlOption.getAttribute("selected") == DomElement.ATTRIBUTE_NOT_DEFINED) {
          output.print(" selected");
        }
      }

      for (DomAttr tmpAttribute : tmpAttributes.values()) {
        final String tmpAttributeName = tmpAttribute.getNodeName().toLowerCase();

        if (!IGNORED_ATTRIBUTES.contains(tmpAttributeName)) {
          String tmpAttributeValue = tmpAttribute.getNodeValue();
          // no output of javascript actions
          if (StringUtils.startsWithIgnoreCase(tmpAttributeValue, "javascript:")) {
            continue;
          }

          if ("style".equals(tmpAttributeName)) {
            // process all url(....) inside
            tmpAttributeValue = responseStore.processCSS(tmpBaseUrl, tmpAttributeValue, 0);
          }

          if (tmpIsCssLink && ("href".equals(tmpAttributeName))) {
            final String tmpStoredFileName = responseStore
                .storeContentFromUrl(tmpBaseUrl, tmpAttributeValue, 0, ".css");
            if (null != tmpStoredFileName) {
              tmpAttributeValue = tmpStoredFileName;
            }
          }

          if (tmpIsHtmlImage && ("src".equals(tmpAttributeName))) {
            final String tmpStoredFileName = responseStore.storeContentFromUrl(tmpBaseUrl, tmpAttributeValue, 0, null);
            if (null != tmpStoredFileName) {
              tmpAttributeValue = tmpStoredFileName;
            }
          }

          if (tmpIsHtmlInlineFrame && ("src".equals(tmpAttributeName))) {
            final HtmlInlineFrame tmpInlineFrame = (HtmlInlineFrame) aDomNode;

            // prevent NPE
            final WebWindow tmpWebWindow = tmpInlineFrame.getEnclosedWindow();
            if (null == tmpWebWindow) {
              LOG.warn("HtmlInlineFrame with enclosed EnclosedWindow == null (" + tmpInlineFrame.toString() + ").");
            } else {
              final Page tmpPage = tmpWebWindow.getEnclosedPage();
              if (null == tmpPage) {
                LOG.warn("HtmlInlineFrame with enclosed EnclosedPage == null (" + tmpInlineFrame.toString() + ").");
              } else {
                final String tmpStoredFileName = responseStore.storePage(tmpPage);
                if (null != tmpStoredFileName) {
                  tmpAttributeValue = "../" + tmpStoredFileName;
                }
              }
            }
          }

          if (tmpIsHtmlPasswordInput && ("value".equals(tmpAttributeName))) {
            if (!StringUtils.isEmpty(tmpAttributeValue)) {
              tmpAttributeValue = "*******";
            }
          }

          // Don't print if value="Submit Query"
          // see com.gargoylesoftware.htmlunit.html.HtmlSubmitInput
          if (tmpIsHtmlSubmitInput && ("value".equals(tmpAttributeName) && "Submit Query".equals(tmpAttributeValue))) {
            continue;
          }

          // special cases
          if (("checked".equals(tmpAttributeName)) && StringUtils.isEmpty(tmpAttributeValue)) {
            tmpAttributeValue = "checked";
          }
          if (("multiple".equals(tmpAttributeName)) && StringUtils.isEmpty(tmpAttributeValue)) {
            tmpAttributeValue = "multiple";
          }

          output.print(' ');
          output.print(tmpAttributeName);
          output.print("=\"");
          output.print(xMLUtil.normalizeAttributeValue(tmpAttributeValue));
          output.print('"');
        }
      }
    }
  }

  private String determineTag(final DomNode aDomNode) {
    String tmpTag = null;
    Class<? extends Object> tmpNodeClass = aDomNode.getClass();

    while (tmpNodeClass != HtmlElement.class) {
      try {
        final Field tmpField = tmpNodeClass.getDeclaredField("TAG_NAME");
        tmpTag = (String) tmpField.get(null);
        return tmpTag;
      } catch (final Exception e) {
        // ignore
      }
      tmpNodeClass = tmpNodeClass.getSuperclass();
    }

    if (null == tmpTag) {
      LOG.warn("Unsupported element " + aDomNode);
      return aDomNode.getClass().getName();
    }

    return tmpTag;
  }
}