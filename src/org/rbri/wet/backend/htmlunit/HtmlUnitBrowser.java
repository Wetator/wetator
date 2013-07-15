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

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Set;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.backend.htmlunit.util.ContentTypeUtil;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.ContentUtil;
import org.rbri.wet.util.SearchPattern;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.DialogWindow;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;


/**
 * The HtmlUnit backend.
 *
 * @author rbri
 */
public final class HtmlUnitBrowser implements WetBackend {
    private static final Log LOG = LogFactory.getLog(HtmlUnitBrowser.class);;

    protected static final int MAX_HISTORY_SIZE = 15;

    protected WebClient webClient;
    protected ResponseStore responseStore;
    protected WetEngine wetEngine;
    protected AssertionFailedException failure;


    public static void checkAnchor(String aRef, Page aPage) throws AssertionFailedException {
        if (null == aPage) {
            return;
        }

        if (    (aPage instanceof HtmlPage)
                && StringUtils.isNotEmpty(aRef) ) {
            HtmlPage tmpHtmlPage = (HtmlPage)aPage;
            try {
                // check first with id
                tmpHtmlPage.getHtmlElementById(aRef);
            } catch (ElementNotFoundException e) {
                // maybe there is an anchor with this name
                // the browser jumps to the first one
                try {
                    tmpHtmlPage.getAnchorByName(aRef);
                } catch (ElementNotFoundException eNF) {
                    Assert.fail("noAnchor", new String[] {aRef});
                }
            }
        }
    }


    // TODO implement close

    public HtmlUnitBrowser(WetEngine aWetEngine) {
        super();

        // setup the backend
        // httpclient should accept all cookies
        System.getProperties().put("apache.commons.httpclient.cookiespec", "COMPATIBILITY");

        wetEngine = aWetEngine;

        // response store
        WetConfiguration tmpConfiguration = wetEngine.getWetConfiguration();
        responseStore = new ResponseStore(tmpConfiguration.getOutputDir(), true);
    }


    public void stop() {
    }


    public void startNewSession() {
        WetConfiguration tmpConfiguration = wetEngine.getWetConfiguration();

        WetBackend.Browser tmpWetBrowser = tmpConfiguration.getBrowser();
        BrowserVersion tmpBrowserVersion = determineBrowserVersionFor(tmpWetBrowser);

        // TODO maybe we have to do more here
        if (null != webClient) {
        	try {
        		webClient.closeAllWindows();
        	} catch (ScriptException e) {
				// TODO: handle exception
                e.printStackTrace();
			}
        }


        if (StringUtils.isNotEmpty(tmpConfiguration.getProxyHost())) {
            String tmpHost = tmpConfiguration.getProxyHost();
            int tmpPort = tmpConfiguration.getProxyPort();

            webClient = new WebClient(tmpBrowserVersion, tmpHost, tmpPort);

            if ((null != tmpConfiguration.getBasicAuthUser())
                    && StringUtils.isNotEmpty(tmpConfiguration.getBasicAuthUser().getValue())) {
                String tmpUser = tmpConfiguration.getBasicAuthUser().getValue();
                String tmpPassword = tmpConfiguration.getBasicAuthPassword().getValue();
                DefaultCredentialsProvider credentialProvider = new DefaultCredentialsProvider();
                credentialProvider.addProxyCredentials(tmpUser, tmpPassword);
                webClient.setCredentialsProvider(credentialProvider);
                // TODO logging
            }

            if ((null != tmpConfiguration.getProxyUser())
                    && StringUtils.isNotEmpty(tmpConfiguration.getProxyUser().getValue())) {
                String tmpUser = tmpConfiguration.getProxyUser().getValue();
                String tmpPassword = tmpConfiguration.getProxyPassword().getValue();
                DefaultCredentialsProvider credentialProvider = new DefaultCredentialsProvider();
                credentialProvider.addProxyCredentials(tmpUser, tmpPassword);
                webClient.setCredentialsProvider(credentialProvider);
                // TODO logging
            }

            Set<String> tmpNonProxyHosts = tmpConfiguration.getProxyHostsToBypass();

            for (String tmpString : tmpNonProxyHosts) {
                webClient.getProxyConfig().addHostsToProxyBypass(tmpString);
            }
        } else {
            webClient = new WebClient(tmpBrowserVersion);
        }

        // setup our listener
        webClient.addWebWindowListener(new WebWindowListener(this));
        webClient.setAlertHandler(new AlertHandler(wetEngine));

        // set Accept-Language header
        webClient.addRequestHeader("Accept-Language", tmpConfiguration.getAcceptLanaguage());

        // trust all SSL-certificates
        try {
            webClient.setUseInsecureSSL(true);
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getCurrentContentAsString() throws AssertionFailedException {
        Page tmpPage = getCurrentPage();
        if (tmpPage instanceof HtmlPage) {
            HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
            String tmpContentAsText = new DomNodeText(tmpHtmlPage).getText();

            return tmpContentAsText;
        }

        if (tmpPage instanceof XmlPage) {
            XmlPage tmpXmlPage = (XmlPage) tmpPage;
            String tmpContentAsText = tmpXmlPage.getContent();
            return tmpContentAsText;
        }

        if (tmpPage instanceof TextPage) {
            TextPage tmpTextPage = (TextPage) tmpPage;
            String tmpContentAsText = tmpTextPage.getContent();
            return tmpContentAsText;
        }

        ContentType tmpContentType = ContentTypeUtil.getContentType(tmpPage);

        if (ContentType.PDF == tmpContentType) {
            try {
                String tmpContentAsText = ContentUtil.getPdfContentAsString(tmpPage.getWebResponse().getContentAsStream());
                return tmpContentAsText;
            } catch (IOException e) {
                Assert.fail("pdfConversionToTextFailed", new String[] {e.getMessage()});
                return null;
            }
        }

        if (ContentType.XLS == tmpContentType) {
            try {
                String tmpContentAsText = ContentUtil.getXlsContentAsString(tmpPage.getWebResponse().getContentAsStream());
                return tmpContentAsText;
            } catch (IOException e) {
                Assert.fail("xlsConversionToTextFailed", new String[] {e.getMessage()});
                return null;
            }
        }

        Assert.fail("unsupportedPageType", new String[] {tmpPage.getWebResponse().getContentType()});
        return null;
    }



    public void openUrl(URL aUrl) throws AssertionFailedException {
        Page tmpPage = null;
        try {
            tmpPage = webClient.getPage(aUrl);

            if (tmpPage instanceof SgmlPage) {
                PageUtil.waitForThreads((SgmlPage)tmpPage);
            }
        } catch (ScriptException e) {
            Assert.fail("javascriptError", new String[] {e.getMessage()});
        } catch (WrappedException e) {
            Assert.fail("javascriptError", new String[] {ExceptionUtil.getMessageFromScriptExceptionCauseIfPossible(e)});
        } catch (FailingHttpStatusCodeException e) {
            Assert.fail("openServerError", new String[] {aUrl.toString(), e.getMessage()});
        } catch (UnknownHostException e) {
            Assert.fail("unknownHostError", new String[] {aUrl.toString(), e.getMessage()});
        } catch (Throwable e) {
            LOG.fatal("OpenUrl '" + aUrl.toExternalForm() + "'fails", e);
            Assert.fail("openServerError", new String[] {aUrl.toString(), e.getMessage()});
        }

        String aRef = aUrl.getRef();
        if (StringUtils.isNotEmpty(aRef)) {
            checkAnchor(aRef);
        }
    }


    public static final class AlertHandler implements com.gargoylesoftware.htmlunit.AlertHandler {
        private WetEngine wetEngine;

        public AlertHandler(WetEngine aWetEngine) {
            wetEngine = aWetEngine;
        }

        public void handleAlert(Page aPage, String aMessage) {
            LOG.debug("handleAlert " + aMessage);

            String tmpMessage = "";
            if (StringUtils.isNotEmpty(aMessage)) {
                tmpMessage = aMessage;
            }
            String tmpUrl = "";
            try {
                tmpUrl = aPage.getWebResponse().getRequestSettings().getUrl().toExternalForm();
            } catch (NullPointerException e) {
                // ignore
            }

            wetEngine.informListenersInfo("javascriptAlert", new String[] {tmpMessage, tmpUrl});
        }
    }


    public void closeWindow(SecretString aWindowName) throws AssertionFailedException {
        List<WebWindow> tmpWebWindows = webClient.getWebWindows();
        if (tmpWebWindows.isEmpty()) {
            Assert.fail("noWindowToClose", null);
        }

        if (null == aWindowName || StringUtils.isEmpty(aWindowName.getValue())) {
            for (int i = tmpWebWindows.size() - 1; i > 0 ; i--) {
                WebWindow tmpWebWindow = tmpWebWindows.get(i);

                if (tmpWebWindow instanceof TopLevelWindow) {
                    wetEngine.informListenersInfo("closeWindow", new String[] {tmpWebWindow.getName()});
                    ((TopLevelWindow)tmpWebWindow).close();
                    return;
                }
                if (tmpWebWindow instanceof DialogWindow) {
                    wetEngine.informListenersInfo("closeDialogWindow", new String[] {tmpWebWindow.getName()});
                    ((DialogWindow)tmpWebWindow).close();
                    return;
                }
            }
            Assert.fail("noWindowToClose", null);
        }

        SearchPattern tmpWindowNamePattern = aWindowName.getSearchPattern();
        for (int i = tmpWebWindows.size() - 1; i > 0 ; i--) {
            WebWindow tmpWebWindow = tmpWebWindows.get(i);

            String tmpWindowName = tmpWebWindow.getName();
            if (tmpWindowNamePattern.matches(tmpWindowName)) {
                if (tmpWebWindow instanceof TopLevelWindow) {
                    wetEngine.informListenersInfo("closeWindow", new String[] {tmpWebWindow.getName()});
                    ((TopLevelWindow)tmpWebWindow).close();
                    return;
                }
                if (tmpWebWindow instanceof DialogWindow) {
                    wetEngine.informListenersInfo("closeDialogWindow", new String[] {tmpWebWindow.getName()});
                    ((DialogWindow)tmpWebWindow).close();
                    return;
                }
            }
        }
        Assert.fail("noWindowByNameToClose", new String[] {aWindowName.toString()});
    }


    public void goBackInCurrentWindow(int aSteps) throws AssertionFailedException {
        WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

        if (null == tmpCurrentWindow) {
            Assert.fail("noWebWindow", null);
        }

        History tmpHistory = tmpCurrentWindow.getHistory();

        final int tmpIndexPos = tmpHistory.getIndex() - aSteps;
        if (tmpIndexPos >= tmpHistory.getLength() || tmpIndexPos < 0) {
            Assert.fail("outsideHistory", new String[] {"" + aSteps, "" + tmpIndexPos, "" + tmpHistory.getLength()});
        }

        try {
            tmpHistory.go(-1 * aSteps);
        } catch (IOException e) {
            Assert.fail("historyFailed", new String[] {e.getMessage()});
        }
    }


    public void saveCurrentWindowToLog() {
        WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

        if (null != tmpCurrentWindow) {
            try {
                Page tmpPage = tmpCurrentWindow.getEnclosedPage();
                String tmpPageFile = responseStore.storePage(webClient, tmpPage);
                wetEngine.informListenersResponseStored(tmpPageFile);
            } catch (WetException e) {
                LOG.fatal("Problem with window handling. Saving page failed!", e);
            }
        }
    }


    protected void checkAnchor(String aRef) throws AssertionFailedException {
        // check the anchor part of the url
        final Page tmpPage = getCurrentPage();
        checkAnchor(aRef, tmpPage);
    }


    public static final class WebWindowListener implements com.gargoylesoftware.htmlunit.WebWindowListener {
        private HtmlUnitBrowser htmlUnitBrowser;

        public WebWindowListener(HtmlUnitBrowser aHtmlUnitBrowser) {
            super();
            htmlUnitBrowser = aHtmlUnitBrowser;
        }

        public void webWindowOpened(WebWindowEvent anEvent) {
            LOG.debug("webWindowOpened");
        }


        public void webWindowClosed(WebWindowEvent anEvent) {
        	Page tmpPage = anEvent.getWebWindow().getEnclosedPage();
        	if (null == tmpPage) {
                LOG.debug("webWindowClosed: (page null)");
        	} else {
	            LOG.debug("webWindowClosed: (url '"
	                    + anEvent.getWebWindow().getEnclosedPage().getWebResponse().getRequestSettings().getUrl() + "')");
        	}
        }


        public void webWindowContentChanged(WebWindowEvent anEvent) {
            LOG.debug("webWindowContentChanged");
            Page tmpNewPage = anEvent.getNewPage();
            // first load into a new window
            if (null != tmpNewPage && null == anEvent.getOldPage()) {
                URL tmpUrl = tmpNewPage.getWebResponse().getRequestUrl();
                String aRef = tmpUrl.getRef();
                if (StringUtils.isNotEmpty(aRef)) {
                    try {
                        checkAnchor(aRef, tmpNewPage);
                    } catch (AssertionFailedException e) {
                        htmlUnitBrowser.failure = e;
                    }
                }
            }
        }

    }


    private Page getCurrentPage() throws AssertionFailedException {
        WebWindow tmpWebWindow = webClient.getCurrentWindow();
        if (null == tmpWebWindow) {
            Assert.fail("noWebWindow", null);
        }
        Page tmpPage = tmpWebWindow.getEnclosedPage();
        if (null == tmpPage) {
            Assert.fail("noPageInWebWindow", null);
        }

        return tmpPage;
    }

    private BrowserVersion determineBrowserVersionFor(WetBackend.Browser aWetBrowser) {
        if (WetBackend.Browser.FIREFOX_2 == aWetBrowser) {
            return BrowserVersion.FIREFOX_2;
        }
        if (WetBackend.Browser.FIREFOX_3 == aWetBrowser) {
            return BrowserVersion.FIREFOX_3;
        }
        if (WetBackend.Browser.INTERNET_EXPLORER_6 == aWetBrowser) {
            return BrowserVersion.INTERNET_EXPLORER_6;
        }
        if (WetBackend.Browser.INTERNET_EXPLORER_7 == aWetBrowser) {
            return BrowserVersion.INTERNET_EXPLORER_7;
        }
        if (WetBackend.Browser.INTERNET_EXPLORER_8 == aWetBrowser) {
            return BrowserVersion.INTERNET_EXPLORER_8;
        }
        return BrowserVersion.INTERNET_EXPLORER_6;
    }


    public HtmlPage getCurrentHtmlPage() throws AssertionFailedException {
        Page tmpPage = getCurrentPage();
        if (tmpPage instanceof HtmlPage) {
            return (HtmlPage) tmpPage;
        }

        Assert.fail("noHtmlPage", new String[] {tmpPage.getClass().toString()});
        return null;
    }

    public ControlFinder getControlFinder() throws AssertionFailedException {
        HtmlPage tmpHtmlPage = getCurrentHtmlPage();

        return new HtmlUnitControlFinder(tmpHtmlPage);
    }

    public String getCurrentTitle() throws AssertionFailedException {
        HtmlPage tmpHtmlPage = getCurrentHtmlPage();
        return tmpHtmlPage.getTitleText();
    }

    public void checkFailure() throws AssertionFailedException {
        if (null == failure) {
            return;
        }
        AssertionFailedException tmpFailure = failure;
        failure = null;
        throw tmpFailure;
    }
}
