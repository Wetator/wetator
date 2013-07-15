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
import com.gargoylesoftware.htmlunit.WebRequestSettings;
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

    private static long COUNTER = 9999;

    private File outputDir;
    private boolean overwrite;
    private WebClient webClient;

    private File storeDir;


    private long getUniqueId() {
        return ++COUNTER;
    }


    /**
     * Constructor
     */
    public ResponseStore(File anOutputDir, boolean anOverwriteFlag) {
        super();
        outputDir = anOutputDir;
        overwrite = anOverwriteFlag;

        initOutputDir();
    }


    /**
     * This method has to be called before any page is logged, because it creates the logdir.
     *
     * @param aStarttime The starttime is used to name the directory for the responses.
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
     * @param aPage the page to save
     * @throws WetException
     */
    public String storePage(WebClient aWebClient, Page aPage) throws WetException {
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


    public String storeContentFromUrl(URL aUrl) {
        try {
            WebResponse tmpWebResponse = webClient.loadWebResponse(new WebRequestSettings(aUrl));
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
}