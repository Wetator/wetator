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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.htmlunit.util.ContentTypeUtil;
import org.rbri.wet.exception.WetException;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Simple store that manages the storage
 * of the different responses.
 * 
 * @author rbri
 */
public final class ResponseStore {
  private static final Log LOG = LogFactory.getLog(ResponseStore.class);;

  private static long counter = 9999;

  private File outputDir;
  private boolean overwrite;
  private WebClient webClient;

  private File storeDir;

  private long getUniqueId() {
    return ++counter;
  }

  /**
   * Constructor
   * 
   * @param anOutputDir the outputDir to set
   * @param anOverwriteFlag the overwrite to set
   */
  public ResponseStore(File anOutputDir, boolean anOverwriteFlag) {
    super();
    outputDir = anOutputDir;
    overwrite = anOverwriteFlag;

    initOutputDir();
  }

  /**
   * This method has to be called before any page is logged, because it creates the logdir.
   */
  public void initOutputDir() {
    String tmpDirectoryName;
    if (overwrite) {
      tmpDirectoryName = "current";
    } else {
      SimpleDateFormat tmpFormater = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
      tmpDirectoryName = tmpFormater.format(new Date());
    }

    tmpDirectoryName = "responses_" + tmpDirectoryName;
    storeDir = new File(outputDir, tmpDirectoryName);

    try {
      FileUtils.forceMkdir(storeDir);
      FileUtils.cleanDirectory(storeDir);
    } catch (IOException e) {
      LOG.error("IO exception for dir: " + storeDir.getAbsolutePath(), e);
    }
  }

  /**
   * This method writes the page to a file with a unique name.
   * 
   * @param aWebClient the web client
   * @param aPage the page to save
   * @return the file name used for this page
   */
  public String storePage(WebClient aWebClient, Page aPage) {
    webClient = aWebClient;

    try {
      String tmpFileName = "response_" + getUniqueId();
      String tmpSuffix = ContentTypeUtil.getFileSuffix(aPage);

      tmpFileName = tmpFileName + "." + tmpSuffix;
      File tmpFile = new File(storeDir, tmpFileName);

      if (aPage instanceof HtmlPage) {
        XHtmlOutputter tmpHtmlOutputter = new XHtmlOutputter((HtmlPage) aPage, this);
        tmpHtmlOutputter.writeTo(tmpFile);
      } else {
        WebResponse tmpWebResponse = aPage.getWebResponse();
        InputStream tmpIn = tmpWebResponse.getContentAsStream();
        OutputStream tmpOutputStream = new FileOutputStream(tmpFile);

        byte[] tmpBuffer = new byte[1024];
        int tmpBytes;
        while ((tmpBytes = tmpIn.read(tmpBuffer)) > 0) {
          tmpOutputStream.write(tmpBuffer, 0, tmpBytes);
        }
        tmpOutputStream.close();
      }

      // to be sure to have the right slashes in the output
      String tmpLogDir = storeDir.getName();
      tmpLogDir = tmpLogDir.replaceAll("\\\\", "/");

      return tmpLogDir + "/" + tmpFileName;
    } catch (IOException e) {
      // TODO
      throw new WetException("xxx");
    }
  }

  /**
   * This method writes the page to a file with a unique name.
   * 
   * @param aUrl the url of the file to save
   * @return the file name used for this page
   */
  public String storeContentFromUrl(URL aUrl) {
    try {
      WebResponse tmpWebResponse = webClient.loadWebResponse(new WebRequest(aUrl));
      String tmpFileName = aUrl.getPath();
      String tmpQuery = aUrl.getQuery();
      if (null != tmpQuery) {
        tmpQuery = URLDecoder.decode(tmpQuery, "UTF-8");
        tmpFileName = tmpFileName + "?" + tmpQuery;
      }

      // fix special characters
      tmpFileName = tmpFileName.replaceAll(">", "__");
      tmpFileName = tmpFileName.replaceAll("<", "__");
      tmpFileName = tmpFileName.replaceAll(":", "__");
      tmpFileName = tmpFileName.replaceAll("\"", "__");
      tmpFileName = tmpFileName.replaceAll("\\|", "__");
      tmpFileName = tmpFileName.replaceAll("\\?", "__");
      tmpFileName = tmpFileName.replaceAll("\\*", "__");

      File tmpCssFile = new File(storeDir, tmpFileName);
      if (!tmpCssFile.exists()) {
        InputStream tmpInStream = tmpWebResponse.getContentAsStream();
        FileUtils.forceMkdir(tmpCssFile.getParentFile());
        FileOutputStream tmpOutStream = new FileOutputStream(tmpCssFile);
        try {
          IOUtils.copy(tmpInStream, tmpOutStream);
        } finally {
          tmpOutStream.close();
        }

        // System.out.println("" + aUrl + "  " + tmpWebResponse.getContentType());
        // if ("text/css".equalsIgnoreCase(tmpWebResponse.getContentType())) {
        // tmpInStream = tmpWebResponse.getContentAsStream();
        // parseCSS(new InputSource(new InputStreamReader(tmpInStream)));
        // }
      }
      // write our path
      String tmpResult;
      if (!tmpFileName.startsWith("/")) {
        tmpResult = "/" + tmpFileName;
      }
      tmpResult = "." + tmpFileName;
      return tmpResult;
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  // TODO handle background-image in css

  // static class ErrorHandler implements org.w3c.css.sac.ErrorHandler {
  // private boolean parsingSuccess = true;
  //
  // public boolean wasParsingSuccessful() {
  // return parsingSuccess;
  // }
  //
  // @Override
  // public void warning(CSSParseException aCSSParseException) throws CSSException {
  // // ignore
  // }
  //
  // @Override
  // public void fatalError(CSSParseException aArg0) throws CSSException {
  // parsingSuccess = false;
  // }
  //
  // @Override
  // public void error(CSSParseException aArg0) throws CSSException {
  // parsingSuccess = false;
  // }
  // };
  //
  // private void parseCSS(InputSource anInputSource) {
  // try {
  // final ErrorHandler tmpErrorHandler = new ErrorHandler();
  // final CSSOMParser tmpCSSOMParser = new CSSOMParser(new SACParserCSS21());
  // tmpCSSOMParser.setErrorHandler(tmpErrorHandler);
  // org.w3c.dom.css.CSSStyleSheet tmpCSSStyleSheet;
  // tmpCSSStyleSheet = tmpCSSOMParser.parseStyleSheet(anInputSource, null, null);
  //
  // if (tmpErrorHandler.wasParsingSuccessful()) {
  // CSSRuleList tmpRuleList = tmpCSSStyleSheet.getCssRules();
  // System.out.println("Number of rules: " + tmpRuleList.getLength());
  //
  // for (int i = 0; i < tmpRuleList.getLength(); i++) {
  // CSSRule rule = tmpRuleList.item(i);
  // if (rule instanceof CSSStyleRule) {
  // CSSStyleRule styleRule = (CSSStyleRule) rule;
  // System.out.println("selector:" + i + ": " + styleRule.getSelectorText());
  // CSSStyleDeclaration styleDeclaration = styleRule.getStyle();
  //
  // for (int j = 0; j < styleDeclaration.getLength(); j++) {
  // String property = styleDeclaration.item(j);
  // System.out.println("property: " + property);
  // System.out.println("value: " + styleDeclaration.getPropertyCSSValue(property).getCssText());
  // System.out.println("priority: " + styleDeclaration.getPropertyPriority(property));
  // }
  // }
  // }
  // }
  // } catch (final Exception e) {
  // // ignore
  // } catch (final Error e) {
  // // ignore
  // }
  // }
}