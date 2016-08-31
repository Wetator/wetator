/*
 * Copyright (c) 2008-2016 wetator.org
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.htmlunit.util.ContentTypeUtil;
import org.wetator.exception.ResourceException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

/**
 * Simple store that manages the storage of the different responses.
 *
 * @author rbri
 * @author frank.danek
 */
public final class ResponseStore {
  private static final Log LOG = LogFactory.getLog(ResponseStore.class);
  private static final Pattern CSS_URL_PATTERN = Pattern.compile("url\\(\\s*([\"']?)(.*?)([\"']?)\\s*\\)");
  private static final Pattern CSS_IMPORT_URL_PATTERN = Pattern.compile("@import\\s+([\"'])(.*?)([\"'])");
  private static final int MAX_FILE_NAME_LENGTH = 200;

  private static long counter = 99999;
  private Map<String, String> fileNames;

  private File outputDir;
  private boolean overwrite;
  private WebClient webClient;

  private File storeDir;
  private String relStoreDir;

  private static long getUniqueId() {
    return ++counter;
  }

  /**
   * The constructor.
   *
   * @param anOutputDir the outputDir to set
   * @param aBrowserSubdir the subdir for the specific browser this store is for
   * @param anOverwriteFlag the overwrite to set
   */
  public ResponseStore(final File anOutputDir, final String aBrowserSubdir, final boolean anOverwriteFlag) {
    super();
    outputDir = anOutputDir;
    overwrite = anOverwriteFlag;

    initOutputDir(aBrowserSubdir);
    fileNames = new HashMap<String, String>();
  }

  /**
   * This method has to be called before any page is logged, because it creates the logdir.
   *
   * @param aBrowserSubdir the subdir for the specific browser this store is for
   */
  protected void initOutputDir(final String aBrowserSubdir) {
    final String tmpDirectoryName;
    if (overwrite) {
      tmpDirectoryName = "responses_current";
    } else {
      final SimpleDateFormat tmpFormater = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss", Locale.ROOT);
      tmpDirectoryName = "responses_" + tmpFormater.format(new Date());
    }

    storeDir = new File(new File(outputDir, aBrowserSubdir.toLowerCase(Locale.ROOT)), tmpDirectoryName);
    relStoreDir = outputDir.toPath().relativize(storeDir.toPath()).toString();
    relStoreDir = relStoreDir.replaceAll("\\\\", "/");

    try {
      FileUtils.forceMkdir(storeDir);
      FileUtils.cleanDirectory(storeDir);
    } catch (final IOException e) {
      LOG.error("IO exception for dir: " + FilenameUtils.normalize(storeDir.getAbsolutePath()), e);
    }
  }

  /**
   * This method writes the the text to a file with a unique name.
   *
   * @param aContent the text content to save
   * @return the file name used for this page
   */
  public String storeTextContent(final String aContent) {
    File tmpFile = null;
    try {
      final StringBuilder tmpFileName = new StringBuilder("content_").append(getUniqueId()).append(".txt");
      tmpFile = new File(storeDir, tmpFileName.toString());

      FileUtils.write(tmpFile, aContent, StandardCharsets.UTF_8);
      return relStoreDir + "/" + tmpFileName;
    } catch (final IOException e) {
      throw new ResourceException("Could not write file '" + FilenameUtils.normalize(tmpFile.getAbsolutePath()) + "'.",
          e);
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
    File tmpFile = null;
    try {
      final StringBuilder tmpFileName = new StringBuilder("response_").append(getUniqueId());
      final String tmpSuffix = ContentTypeUtil.getFileSuffix(aPage);

      tmpFileName.append('.').append(tmpSuffix);
      tmpFile = new File(storeDir, tmpFileName.toString());

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

      return relStoreDir + "/" + tmpFileName;
    } catch (final IOException e) {
      throw new ResourceException("Could not write file '" + FilenameUtils.normalize(tmpFile.getAbsolutePath()) + "'.",
          e);
    }
  }

  /**
   * This method writes the content of a url to a file with a unique name.
   *
   * @param aBaseUrl the url of the page, this is referenced from
   * @param aFullContentUrl the url of the content to save
   * @param aLink if provided use this link to ask for the WebResponse
   * @param anImage if provided use this image to ask for the WebResponse
   * @param aDeep the deep of the parent file in the response store
   *        (file system). This is used to calculate always relative urls for the return value
   * @param aSuffix to force a specific suffix for the file name
   * @return the file name used for this page (as relative path);
   */
  public String storeContentFromUrl(final URL aBaseUrl, final URL aFullContentUrl, final HtmlLink aLink,
      final HtmlImage anImage, final int aDeep, final String aSuffix) {
    try {
      if ("data".equals(aFullContentUrl.getProtocol())) {
        return null;
      }

      final String tmpBaseHost = aBaseUrl.getHost();
      if (null == tmpBaseHost || !tmpBaseHost.equals(aFullContentUrl.getHost())) {
        LOG.info("Ignoring URL '" + aFullContentUrl.toExternalForm() + "' (wrong host).");
        return null;
      }

      // did we already download this
      String tmpFileName = fileNames.get(aFullContentUrl.toExternalForm());
      if (null == tmpFileName) {
        // read data form url
        final WebResponse tmpWebResponse;
        if (null != aLink) {
          tmpWebResponse = aLink.getWebResponse(true);

          // e.g. empty src attrib
          if (tmpWebResponse == null) {
            LOG.warn("Ignoring link '" + aLink.asXml() + "'");
            return null;
          }
        } else if (null != anImage) {
          tmpWebResponse = anImage.getWebResponse(true);

          // e.g. empty src attrib
          if (tmpWebResponse == null) {
            LOG.warn("Ignoring image '" + anImage.asXml() + "'");
            return null;
          }
        } else {
          // set the referer header like the browser does
          final WebRequest tmpRequest = new WebRequest(aFullContentUrl);
          tmpRequest.setAdditionalHeader("Referer", aBaseUrl.toExternalForm());
          tmpWebResponse = webClient.loadWebResponse(tmpRequest);

          // we have to check the result code
          // see Ticket #42
          try {
            webClient.throwFailingHttpStatusCodeExceptionIfNecessary(tmpWebResponse);
          } catch (final FailingHttpStatusCodeException e) {
            LOG.warn("Could not read url '" + aFullContentUrl.toExternalForm() + "'.", e);
            return null;
          }
        }

        // create path
        tmpFileName = aFullContentUrl.getPath();
        if (tmpFileName.charAt(0) == '/') {
          tmpFileName = tmpFileName.substring(1);
        }

        String tmpQuery = aFullContentUrl.getQuery();
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

        // ensure the suffix
        // this helps the browser and file server to find the correct content type
        if (null == aSuffix) {
          final String tmpFileSuffix = ContentTypeUtil.getFileSuffix(tmpWebResponse);
          if (!tmpFileName.endsWith(tmpFileSuffix)) {
            tmpFileName = tmpFileName + "." + tmpFileSuffix;
          }
        } else {
          if (!tmpFileName.endsWith(aSuffix)) {
            tmpFileName = tmpFileName + aSuffix;
          }
        }

        File tmpResourceFile = new File(storeDir, tmpFileName);

        if (tmpResourceFile.getAbsolutePath().length() > MAX_FILE_NAME_LENGTH) {
          // files with really long names
          tmpFileName = "resource/" + "resource_" + getUniqueId();
          if (null != aSuffix) {
            tmpFileName = tmpFileName + aSuffix;
          } else {
            tmpFileName = tmpFileName + "." + ContentTypeUtil.getFileSuffix(tmpWebResponse);
          }
          tmpResourceFile = new File(storeDir, tmpFileName);
        }

        // store the value already to prevent endless looping
        fileNames.put(aFullContentUrl.toExternalForm(), tmpFileName);

        if (!tmpResourceFile.exists()) {
          String tmpProcessed = null;
          final String tmpContentType = tmpWebResponse.getContentType();
          if ("text/css".equalsIgnoreCase(tmpContentType)) {
            final String tmpResponse = getContentAsStringWithoutBOM(tmpWebResponse);
            if (null != tmpResponse) {
              FileUtils.forceMkdir(tmpResourceFile.getParentFile());

              // process all url(....) inside
              tmpProcessed = processCSS(aFullContentUrl, tmpResponse, StringUtils.countMatches(tmpFileName, "/"));
              FileUtils.writeStringToFile(tmpResourceFile, tmpProcessed, StandardCharsets.UTF_8);
            }
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
      }

      // calculate the return value
      final StringBuilder tmpResult = new StringBuilder();
      if (aDeep <= 0) {
        tmpResult.append("./");
      } else {
        for (int i = 0; i < aDeep; i++) {
          tmpResult.append("../");
        }
      }
      tmpResult.append(tmpFileName);

      return tmpResult.toString();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * This method parses the given css content for url(...);
   * resolves the pictures and returns the content with
   * correct paths.
   *
   * @param aFullContentUrl the url of the page/css, this is referenced from
   * @param aCssContent the css to process
   * @param aDeep the deep of the parent file in the response store
   *        (file system). This is used to calculate always relative urls for the return value
   * @return the changed content;
   * @throws MalformedURLException in case of error
   */
  public String processCSS(final URL aFullContentUrl, final String aCssContent, final int aDeep)
      throws MalformedURLException {
    String tmpContent = aCssContent;
    int tmpStart = 0;
    Matcher tmpMatcher = CSS_URL_PATTERN.matcher(aCssContent);
    while (tmpMatcher.find(tmpStart)) {
      final URL tmpCssUrl = UrlUtils.toUrlUnsafe(UrlUtils.resolveUrl(aFullContentUrl, tmpMatcher.group(2)));
      final String tmpNewUrl = storeContentFromUrl(aFullContentUrl, tmpCssUrl, null, null, aDeep, null);
      if (null == tmpNewUrl) {
        tmpStart = tmpMatcher.end();
      } else {
        final String tmpReplacement = "url(" + tmpMatcher.group(1) + tmpNewUrl + tmpMatcher.group(3) + ")";
        tmpContent = tmpContent.substring(0, tmpMatcher.start()) + tmpReplacement
            + tmpContent.substring(tmpMatcher.end());
        tmpStart = tmpMatcher.start() + tmpReplacement.length();

        tmpMatcher = CSS_URL_PATTERN.matcher(tmpContent);
      }
    }

    tmpStart = 0;
    tmpMatcher = CSS_IMPORT_URL_PATTERN.matcher(tmpContent);
    while (tmpMatcher.find(tmpStart)) {
      final URL tmpCssUrl = UrlUtils.toUrlUnsafe(UrlUtils.resolveUrl(aFullContentUrl, tmpMatcher.group(2)));
      final String tmpNewUrl = storeContentFromUrl(aFullContentUrl, tmpCssUrl, null, null, aDeep, null);
      if (null == tmpNewUrl) {
        tmpStart = tmpMatcher.end();
      } else {
        final String tmpReplacement = "@import" + tmpMatcher.group(1) + tmpNewUrl + tmpMatcher.group(3);
        tmpContent = tmpContent.substring(0, tmpMatcher.start()) + tmpReplacement
            + tmpContent.substring(tmpMatcher.end());
        tmpStart = tmpMatcher.start() + tmpReplacement.length();

        tmpMatcher = CSS_IMPORT_URL_PATTERN.matcher(tmpContent);
      }
    }

    return tmpContent;
  }

  /**
   * Our own version to strip BOM bytes from css input if any.
   */
  private static String getContentAsStringWithoutBOM(final WebResponse aWebResponse) {
    final String tmpCharset = aWebResponse.getContentCharset();
    InputStream tmpIn = null;
    try {
      tmpIn = aWebResponse.getContentAsStream();
      if (null == tmpIn) {
        return null;
      }

      // tmpIn = new BOMInputStream(tmpIn);
      return IOUtils.toString(tmpIn, tmpCharset);
    } catch (final IOException e) {
      LOG.warn("", e);
      return null;
    } finally {
      if (tmpIn != null) {
        IOUtils.closeQuietly(tmpIn);
      }
    }
  }
}