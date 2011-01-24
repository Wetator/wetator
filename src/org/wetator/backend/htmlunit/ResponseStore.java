/*
 * Copyright (c) 2008-2011 www.wetator.org
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.htmlunit.util.ContentTypeUtil;
import org.wetator.exception.WetException;

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
  private static final Pattern CSS_URL_PATTERN = Pattern.compile("url\\(([\"']?)(.*?)([\"']?)\\)");
  private static final int MAX_FILE_NAME_LENGTH = 200;

  private static long counter = 99999;
  private static Map<String, String> genericFileNames;

  private File outputDir;
  private boolean overwrite;
  private WebClient webClient;

  private File storeDir;

  private long getUniqueId() {
    return ++counter;
  }

  /**
   * The constructor.
   * 
   * @param anOutputDir the outputDir to set
   * @param anOverwriteFlag the overwrite to set
   */
  public ResponseStore(final File anOutputDir, final boolean anOverwriteFlag) {
    super();
    outputDir = anOutputDir;
    overwrite = anOverwriteFlag;

    initOutputDir();
    genericFileNames = new HashMap<String, String>();
  }

  /**
   * This method has to be called before any page is logged, because it creates the logdir.
   */
  public void initOutputDir() {
    String tmpDirectoryName;
    if (overwrite) {
      tmpDirectoryName = "responses_current";
    } else {
      final SimpleDateFormat tmpFormater = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
      tmpDirectoryName = "responses_" + tmpFormater.format(new Date());
    }

    storeDir = new File(outputDir, tmpDirectoryName);

    try {
      FileUtils.forceMkdir(storeDir);
      FileUtils.cleanDirectory(storeDir);
    } catch (final IOException e) {
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
  public String storePage(final WebClient aWebClient, final Page aPage) {
    webClient = aWebClient;

    return storePage(aPage);
  }

  /**
   * This method writes the page to a file with a unique name.
   * 
   * @param aPage the page to save
   * @return the file name used for this page
   */
  public String storePage(final Page aPage) {
    try {
      String tmpFileName = "response_" + getUniqueId();
      final String tmpSuffix = ContentTypeUtil.getFileSuffix(aPage);

      tmpFileName = tmpFileName + "." + tmpSuffix;
      final File tmpFile = new File(storeDir, tmpFileName);

      if (aPage instanceof HtmlPage) {
        final XHtmlOutputter tmpHtmlOutputter = new XHtmlOutputter((HtmlPage) aPage, this);
        tmpHtmlOutputter.writeTo(tmpFile);
      } else {
        final WebResponse tmpWebResponse = aPage.getWebResponse();
        final InputStream tmpIn = tmpWebResponse.getContentAsStream();
        final OutputStream tmpOutputStream = new FileOutputStream(tmpFile);

        final byte[] tmpBuffer = new byte[1024];
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
    } catch (final IOException e) {
      // TODO
      throw new WetException("xxx");
    }
  }

  /**
   * This method writes the page to a file with a unique name.
   * 
   * @param aContaingPage the page that contains the reference pointing to this file
   * @param aUrlString the url of the file to save
   * @param aSuffix to force a specific suffix for the file name
   * @return the file name used for this page
   */
  public String storeContentFromUrl(final HtmlPage aContaingPage, final String aUrlString, final String aSuffix) {
    try {
      final URL tmpPageUrl = aContaingPage.getWebResponse().getWebRequest().getUrl();
      final String tmpPageHost = tmpPageUrl.getHost();

      final URL tmpUrl = aContaingPage.getFullyQualifiedUrl(aUrlString);
      final String tmpHost = tmpUrl.getHost();
      if ((null == tmpHost) || !tmpHost.equals(tmpPageHost)) {
        LOG.info("Ignoring url '" + aUrlString + "' (wrong host).");
        return null;
      }

      final WebResponse tmpWebResponse = webClient.loadWebResponse(new WebRequest(tmpUrl));
      String tmpFileName = tmpUrl.getPath();
      String tmpQuery = tmpUrl.getQuery();
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

      // ensure the postfix
      // this helps if the result is browsed from a real server
      if (null != aSuffix && !tmpFileName.endsWith(aSuffix)) {
        tmpFileName = tmpFileName + aSuffix;
      }

      File tmpResourceFile = new File(storeDir, tmpFileName);

      if (tmpResourceFile.getAbsolutePath().length() > MAX_FILE_NAME_LENGTH) {
        // files with really long names
        String tmpGenericFileName = genericFileNames.get(tmpFileName);
        if (null == tmpGenericFileName) {
          tmpGenericFileName = "resource_" + getUniqueId();
          genericFileNames.put(tmpFileName, tmpGenericFileName);
        }
        tmpFileName = "resource/" + tmpGenericFileName;
        tmpResourceFile = new File(storeDir, tmpFileName);
      }

      if (!tmpResourceFile.exists()) {
        String tmpProcessed = null;
        if ("text/css".equalsIgnoreCase(tmpWebResponse.getContentType())) {
          final String tmpResponse = tmpWebResponse.getContentAsString();
          FileUtils.forceMkdir(tmpResourceFile.getParentFile());

          // process all url(....) inside
          tmpProcessed = processCSS(tmpResponse, aContaingPage);
          FileUtils.writeStringToFile(tmpResourceFile, tmpProcessed);
        }

        if (tmpProcessed == null) {
          final InputStream tmpInStream = tmpWebResponse.getContentAsStream();
          FileUtils.forceMkdir(tmpResourceFile.getParentFile());
          final FileOutputStream tmpOutStream = new FileOutputStream(tmpResourceFile);
          try {
            IOUtils.copy(tmpInStream, tmpOutStream);
          } finally {
            tmpOutStream.close();
          }
        }
      }
      // write our path
      String tmpResult;
      if (!(tmpFileName.charAt(0) == '/')) {
        tmpResult = "./" + tmpFileName;
        return tmpResult;
      }

      tmpResult = "." + tmpFileName;
      return tmpResult;
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  private String processCSS(final String aCssContent, final HtmlPage aContaingPage) {
    String tmpContent = aCssContent;
    int tmpStart = 0;
    Matcher tmpMatcher = CSS_URL_PATTERN.matcher(aCssContent);

    while (tmpMatcher.find(tmpStart)) {
      final String tmpNewUrl = storeContentFromUrl(aContaingPage, tmpMatcher.group(2), null);
      if (null == tmpNewUrl) {
        tmpStart = tmpMatcher.end();
      } else {
        final String tmpReplacement = "url(" + tmpMatcher.group(1) + tmpNewUrl + tmpMatcher.group(3) + ")";
        tmpContent = StringUtils.replace(tmpContent, tmpMatcher.group(0), tmpReplacement);
        tmpStart = tmpMatcher.start() + tmpReplacement.length();

        tmpMatcher = CSS_URL_PATTERN.matcher(aCssContent);
      }
    }

    return tmpContent;
  }
}