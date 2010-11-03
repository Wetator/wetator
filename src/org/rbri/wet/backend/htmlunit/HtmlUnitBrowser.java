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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitAnchor;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitButton;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitImage;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputButton;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputFile;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputImage;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputPassword;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputRadioButton;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputReset;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputSubmit;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputText;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitOption;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitOptionGroup;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitSelect;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitTextArea;
import org.rbri.wet.backend.htmlunit.util.ContentTypeUtil;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.core.searchpattern.SearchPattern;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.i18n.Messages;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.ContentUtil;
import org.rbri.wet.util.NormalizedString;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.DialogWindow;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * The HtmlUnit backend.
 * 
 * @author rbri
 * @author frank.danek
 */
public final class HtmlUnitBrowser implements WetBackend {
  private static final Log LOG = LogFactory.getLog(HtmlUnitBrowser.class);;

  /** the maximum history size */
  protected static final int MAX_HISTORY_SIZE = 15;

  /** htmlunit WebClient */
  protected WebClient webClient;
  /** ResponseStore */
  protected ResponseStore responseStore;
  /** WetEngine */
  protected WetEngine wetEngine;
  /** AssertionFailedException */
  protected List<AssertionFailedException> failures;
  /** immediateJobsTimeout */
  protected long immediateJobsTimeout;

  /**
   * This repository contains all additional controls supported by the backend (e.g. added by a command set).
   */
  protected HtmlUnitControlRepository controlRepository = new HtmlUnitControlRepository();

  /**
   * Constructor.
   * 
   * @param aWetEngine the engine to work with
   */
  public HtmlUnitBrowser(WetEngine aWetEngine) {
    super();

    // setup the backend
    // httpclient should accept all cookies
    System.getProperties().put("apache.commons.httpclient.cookiespec", "COMPATIBILITY");

    failures = new LinkedList<AssertionFailedException>();
    wetEngine = aWetEngine;

    // response store
    WetConfiguration tmpConfiguration = wetEngine.getWetConfiguration();
    responseStore = new ResponseStore(tmpConfiguration.getOutputDir(), true);

    // TODO read from config
    immediateJobsTimeout = 1000L;

    // add the default controls
    controlRepository.add(HtmlUnitAnchor.class);
    controlRepository.add(HtmlUnitButton.class);
    controlRepository.add(HtmlUnitImage.class);
    controlRepository.add(HtmlUnitInputButton.class);
    controlRepository.add(HtmlUnitInputCheckBox.class);
    controlRepository.add(HtmlUnitInputFile.class);
    controlRepository.add(HtmlUnitInputImage.class);
    controlRepository.add(HtmlUnitInputPassword.class);
    controlRepository.add(HtmlUnitInputRadioButton.class);
    controlRepository.add(HtmlUnitInputReset.class);
    controlRepository.add(HtmlUnitInputSubmit.class);
    controlRepository.add(HtmlUnitInputText.class);
    controlRepository.add(HtmlUnitOption.class);
    controlRepository.add(HtmlUnitOptionGroup.class);
    controlRepository.add(HtmlUnitSelect.class);
    controlRepository.add(HtmlUnitTextArea.class);

    // add the controls from the configuration
    controlRepository.addAll(tmpConfiguration.getControls());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.WetBackend#startNewSession(org.rbri.wet.backend.WetBackend.Browser)
   */
  @Override
  public void startNewSession(WetBackend.Browser aBrowser) {
    WetConfiguration tmpConfiguration = wetEngine.getWetConfiguration();

    BrowserVersion tmpBrowserVersion = determineBrowserVersionFor(aBrowser);

    // TODO maybe we have to do more here
    if (null != webClient) {
      try {
        webClient.closeAllWindows();
      } catch (ScriptException e) {
        // TODO handle exception
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
        DefaultCredentialsProvider tmpCredentialProvider = new DefaultCredentialsProvider();
        tmpCredentialProvider.addCredentials(tmpUser, tmpPassword);
        webClient.setCredentialsProvider(tmpCredentialProvider);
        // TODO logging
      }

      if ((null != tmpConfiguration.getProxyUser())
          && StringUtils.isNotEmpty(tmpConfiguration.getProxyUser().getValue())) {
        String tmpUser = tmpConfiguration.getProxyUser().getValue();
        String tmpPassword = tmpConfiguration.getProxyPassword().getValue();
        DefaultCredentialsProvider tmpCredentialProvider = new DefaultCredentialsProvider();
        tmpCredentialProvider.addCredentials(tmpUser, tmpPassword);
        webClient.setCredentialsProvider(tmpCredentialProvider);
        // TODO logging
      }

      Set<String> tmpNonProxyHosts = tmpConfiguration.getProxyHostsToBypass();

      for (String tmpString : tmpNonProxyHosts) {
        if (StringUtils.isNotEmpty(tmpString)) {
          webClient.getProxyConfig().addHostsToProxyBypass(tmpString.trim());
        }
      }
    } else {
      webClient = new WebClient(tmpBrowserVersion);
    }

    // setup our listener
    webClient.addWebWindowListener(new WebWindowListener(this));
    webClient.setAlertHandler(new AlertHandler(wetEngine));
    // javascript
    webClient.setJavaScriptEnabled(true);
    webClient.setThrowExceptionOnScriptError(false);
    webClient.setJavaScriptErrorListener(new JavaScriptErrorListener(this));

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

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.WetBackend#openUrl(java.net.URL)
   */
  @Override
  public void openUrl(URL aUrl) throws AssertionFailedException {
    try {
      webClient.getPage(aUrl);
      waitForImmediateJobs();
    } catch (ScriptException e) {
      addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      addFailure("javascriptError", new String[] { tmpScriptException.getMessage() }, tmpScriptException);
    } catch (FailingHttpStatusCodeException e) {
      addFailure("openServerError", new String[] { aUrl.toString(), e.getMessage() }, e);
    } catch (UnknownHostException e) {
      addFailure("unknownHostError", new String[] { aUrl.toString(), e.getMessage() }, e);
    } catch (Throwable e) {
      LOG.error("OpenUrl '" + aUrl.toExternalForm() + "'fails. " + e.getMessage());
      addFailure("openServerError", new String[] { aUrl.toString(), e.getMessage() }, e);
    }

    String tmpRef = aUrl.getRef();
    if (StringUtils.isNotEmpty(tmpRef)) {
      checkAnchor(tmpRef);
    }
  }

  /**
   * Our own alert handler.
   */
  public static final class AlertHandler implements com.gargoylesoftware.htmlunit.AlertHandler {
    private WetEngine wetEngine;

    /**
     * Constructor.
     * 
     * @param aWetEngine the engine to inform about the alert texts.
     */
    public AlertHandler(WetEngine aWetEngine) {
      wetEngine = aWetEngine;
    }

    @Override
    public void handleAlert(Page aPage, String aMessage) {
      LOG.debug("handleAlert " + aMessage);

      String tmpMessage = "";
      if (StringUtils.isNotEmpty(aMessage)) {
        tmpMessage = aMessage;
      }
      String tmpUrl = "";
      try {
        tmpUrl = aPage.getWebResponse().getWebRequest().getUrl().toExternalForm();
      } catch (NullPointerException e) {
        // ignore
      }

      wetEngine.informListenersInfo("javascriptAlert", new String[] { tmpMessage, tmpUrl });
    }
  }

  /**
   * Our own listener. We like to inform about javascript errors without
   * stopping the processing.
   */
  public static final class JavaScriptErrorListener implements
      com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener {
    private HtmlUnitBrowser htmlUnitBrowser;

    /**
     * Constructor.
     * 
     * @param aHtmlUnitBrowser the browser this listener informs
     */
    public JavaScriptErrorListener(HtmlUnitBrowser aHtmlUnitBrowser) {
      htmlUnitBrowser = aHtmlUnitBrowser;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener#loadScriptError(com.gargoylesoftware.htmlunit.html.HtmlPage,
     *      java.net.URL, java.lang.Exception)
     */
    @Override
    public void loadScriptError(HtmlPage aHtmlPage, URL aScriptUrl, Exception anException) {
      htmlUnitBrowser.addFailure("javascriptLoadError", new String[] { aScriptUrl.toExternalForm(),
          aHtmlPage.getUrl().toExternalForm(), anException.getMessage() }, anException);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener#malformedScriptURL(com.gargoylesoftware.htmlunit.html.HtmlPage,
     *      java.lang.String, java.net.MalformedURLException)
     */
    @Override
    public void malformedScriptURL(HtmlPage aHtmlPage, String aUrl, MalformedURLException aMalformedURLException) {
      htmlUnitBrowser.addFailure("javascriptLoadError", new String[] { aUrl, aHtmlPage.getUrl().toExternalForm(),
          aMalformedURLException.getMessage() }, aMalformedURLException);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener#scriptException(com.gargoylesoftware.htmlunit.html.HtmlPage,
     *      com.gargoylesoftware.htmlunit.ScriptException)
     */
    @Override
    public void scriptException(HtmlPage aHtmlPage, ScriptException aScriptException) {
      htmlUnitBrowser.addFailure("javascriptError", new String[] { aScriptException.getMessage() }, aScriptException);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener#timeoutError(com.gargoylesoftware.htmlunit.html.HtmlPage,
     *      long, long)
     */
    @Override
    public void timeoutError(HtmlPage aHtmlPage, long aAllowedTime, long aExecutionTime) {
      htmlUnitBrowser.addFailure("javascriptTimeoutError", new String[] { "" + aAllowedTime, "" + aExecutionTime,
          aHtmlPage.getUrl().toExternalForm() }, null);
    }
  }

  @Override
  public void closeWindow(SecretString aWindowName) throws AssertionFailedException {
    List<WebWindow> tmpWebWindows = webClient.getWebWindows();
    if (tmpWebWindows.isEmpty()) {
      Assert.fail("noWindowToClose", null);
    }

    if (null == aWindowName || StringUtils.isEmpty(aWindowName.getValue())) {
      for (int i = tmpWebWindows.size() - 1; i > 0; i--) {
        WebWindow tmpWebWindow = tmpWebWindows.get(i);

        if (tmpWebWindow instanceof TopLevelWindow) {
          wetEngine.informListenersInfo("closeWindow", new String[] { tmpWebWindow.getName() });
          ((TopLevelWindow) tmpWebWindow).close();
          return;
        }
        if (tmpWebWindow instanceof DialogWindow) {
          wetEngine.informListenersInfo("closeDialogWindow", new String[] { tmpWebWindow.getName() });
          ((DialogWindow) tmpWebWindow).close();
          return;
        }
      }
      Assert.fail("noWindowToClose", null);
    }

    SearchPattern tmpWindowNamePattern = aWindowName.getSearchPattern();
    for (int i = tmpWebWindows.size() - 1; i > 0; i--) {
      WebWindow tmpWebWindow = tmpWebWindows.get(i);

      String tmpWindowName = tmpWebWindow.getName();
      if (tmpWindowNamePattern.matches(tmpWindowName)) {
        if (tmpWebWindow instanceof TopLevelWindow) {
          wetEngine.informListenersInfo("closeWindow", new String[] { tmpWebWindow.getName() });
          ((TopLevelWindow) tmpWebWindow).close();
          return;
        }
        if (tmpWebWindow instanceof DialogWindow) {
          wetEngine.informListenersInfo("closeDialogWindow", new String[] { tmpWebWindow.getName() });
          ((DialogWindow) tmpWebWindow).close();
          return;
        }
      }
    }
    Assert.fail("noWindowByNameToClose", new String[] { aWindowName.toString() });
  }

  @Override
  public void goBackInCurrentWindow(int aSteps) throws AssertionFailedException {
    WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

    if (null == tmpCurrentWindow) {
      Assert.fail("noWebWindow", null);
    }

    History tmpHistory = tmpCurrentWindow.getHistory();

    final int tmpIndexPos = tmpHistory.getIndex() - aSteps;
    if (tmpIndexPos >= tmpHistory.getLength() || tmpIndexPos < 0) {
      Assert.fail("outsideHistory", new String[] { "" + aSteps, "" + tmpIndexPos, "" + tmpHistory.getLength() });
    }

    try {
      tmpHistory.go(-1 * aSteps);
    } catch (IOException e) {
      Assert.fail("historyFailed", new String[] { e.getMessage() });
    }
  }

  @Override
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

  /**
   * Check if the url contains a hash, that the matching anchor is on the page
   * 
   * @param aRef the hash from the url
   * @throws AssertionFailedException if no matisching anchor found
   */
  protected void checkAnchor(String aRef) throws AssertionFailedException {
    // check the anchor part of the url
    final Page tmpPage = getCurrentPage();
    PageUtil.checkAnchor(aRef, tmpPage);
  }

  /**
   * Our own listener for window content changes.
   */
  public static final class WebWindowListener implements com.gargoylesoftware.htmlunit.WebWindowListener {
    private HtmlUnitBrowser htmlUnitBrowser;

    /**
     * Constructor
     * 
     * @param anHtmlUnitBrowser the browser to inform
     */
    public WebWindowListener(HtmlUnitBrowser anHtmlUnitBrowser) {
      super();
      htmlUnitBrowser = anHtmlUnitBrowser;
    }

    @Override
    public void webWindowOpened(WebWindowEvent anEvent) {
      LOG.debug("webWindowOpened");
    }

    @Override
    public void webWindowClosed(WebWindowEvent anEvent) {
      Page tmpPage = anEvent.getWebWindow().getEnclosedPage();
      if (null == tmpPage) {
        LOG.debug("webWindowClosed: (page null)");
      } else {
        LOG.debug("webWindowClosed: (url '"
            + anEvent.getWebWindow().getEnclosedPage().getWebResponse().getWebRequest().getUrl() + "')");
      }
    }

    @Override
    public void webWindowContentChanged(WebWindowEvent anEvent) {
      LOG.debug("webWindowContentChanged");
      Page tmpNewPage = anEvent.getNewPage();
      // first load into a new window
      if (null != tmpNewPage && null == anEvent.getOldPage()) {
        URL tmpUrl = tmpNewPage.getWebResponse().getWebRequest().getUrl();
        String tmpRef = tmpUrl.getRef();
        if (StringUtils.isNotEmpty(tmpRef)) {
          try {
            PageUtil.checkAnchor(tmpRef, tmpNewPage);
          } catch (AssertionFailedException e) {
            htmlUnitBrowser.addFailure(e);
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

  @SuppressWarnings("deprecation")
  private BrowserVersion determineBrowserVersionFor(WetBackend.Browser aWetBrowser) {
    if (WetBackend.Browser.FIREFOX_3 == aWetBrowser) {
      return BrowserVersion.FIREFOX_3;
    }
    if (WetBackend.Browser.FIREFOX_3_6 == aWetBrowser) {
      return BrowserVersion.FIREFOX_3_6;
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

  /**
   * Returns the current HtmlPage.
   * 
   * @return the current HtmlPage
   * @throws AssertionFailedException if the current page is not a HtmlPage
   */
  public HtmlPage getCurrentHtmlPage() throws AssertionFailedException {
    Page tmpPage = getCurrentPage();
    if (tmpPage instanceof HtmlPage) {
      return (HtmlPage) tmpPage;
    }

    Assert.fail("noHtmlPage", new String[] { tmpPage.getClass().toString() });
    return null;
  }

  @Override
  public ControlFinder getControlFinder() throws AssertionFailedException {
    HtmlPage tmpHtmlPage = getCurrentHtmlPage();

    return new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
  }

  @Override
  public void waitForImmediateJobs() throws AssertionFailedException {
    Page tmpPage = getCurrentPage();
    if (tmpPage instanceof HtmlPage) {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + immediateJobsTimeout;
      while (System.currentTimeMillis() < tmpEndTime) {
        HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
        JavaScriptJobManager tmpJobManager = tmpHtmlPage.getEnclosingWindow().getJobManager();

        if (tmpJobManager.waitForJobsStartingBefore(tmpEndTime - System.currentTimeMillis()) > 0) {
          continue;
        }

        if (tmpPage == getCurrentPage()) {
          break;
        }

        // current page is changed, we have to make at least one try
        // reset the timeout
        tmpPage = getCurrentPage();
        if (!(tmpPage instanceof HtmlPage)) {
          break;
        }
        tmpEndTime = System.currentTimeMillis() + immediateJobsTimeout;
      }
    }
  }

  @Override
  public String waitForTitle(List<SecretString> aTitleToWaitFor, long aTimeoutInSeconds)
      throws AssertionFailedException {
    long tmpWaitTime = Math.max(immediateJobsTimeout, aTimeoutInSeconds * 1000L);

    Page tmpPage = getCurrentPage();
    if (tmpPage instanceof HtmlPage) {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + tmpWaitTime;
      while (System.currentTimeMillis() < tmpEndTime) {
        HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
        String tmpCurrentTitle = tmpHtmlPage.getTitleText();
        try {
          Assert.assertListMatch(aTitleToWaitFor, tmpCurrentTitle);
          return tmpCurrentTitle;
        } catch (AssertionFailedException e) {
          // ok, not found, maybe we have to be more patient
        }

        JavaScriptJobManager tmpJobManager = tmpHtmlPage.getEnclosingWindow().getJobManager();
        if (tmpJobManager.waitForJobsStartingBefore(tmpEndTime - System.currentTimeMillis()) > 0) {
          continue;
        }

        if (tmpPage == getCurrentPage()) {
          break;
        }

        // current page is changed, we have to make at least one try
        // reset the timeout
        tmpPage = getCurrentPage();
        if (!(tmpPage instanceof HtmlPage)) {
          break;
        }
        tmpEndTime = System.currentTimeMillis() + tmpWaitTime;
      }
    }

    HtmlPage tmpHtmlPage = getCurrentHtmlPage();
    String tmpCurrentTitle = tmpHtmlPage.getTitleText();
    Assert.assertListMatch(aTitleToWaitFor, tmpCurrentTitle);
    return tmpCurrentTitle;
  }

  @Override
  public String waitForContent(List<SecretString> aContentToWaitFor, long aTimeoutInSeconds)
      throws AssertionFailedException {
    long tmpWaitTime = Math.max(immediateJobsTimeout, aTimeoutInSeconds * 1000L);

    Page tmpPage = getCurrentPage();
    if (tmpPage instanceof HtmlPage) {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + tmpWaitTime;
      while (System.currentTimeMillis() < tmpEndTime) {
        HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
        String tmpContentAsText = new DomNodeText(tmpHtmlPage).getText();
        try {
          Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
          return tmpContentAsText;
        } catch (AssertionFailedException e) {
          // ok, not found, maybe we have to be more patient
        }

        JavaScriptJobManager tmpJobManager = tmpHtmlPage.getEnclosingWindow().getJobManager();
        if (tmpJobManager.waitForJobsStartingBefore(tmpEndTime - System.currentTimeMillis()) > 0) {
          continue;
        }

        // current page is changed, we have to make another try
        if (tmpPage == getCurrentPage()) {
          break;
        }

        // current page is changed, we have to make at least one try
        // reset the timeout
        tmpPage = getCurrentPage();
        if (!(tmpPage instanceof HtmlPage)) {
          break;
        }
        tmpEndTime = System.currentTimeMillis() + tmpWaitTime;
      }
    }

    tmpPage = getCurrentPage();

    if (tmpPage instanceof HtmlPage) {
      HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
      String tmpContentAsText = new DomNodeText(tmpHtmlPage).getText();
      Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
      return tmpContentAsText;
    }

    if (tmpPage instanceof XmlPage) {
      XmlPage tmpXmlPage = (XmlPage) tmpPage;
      String tmpContentAsText = new NormalizedString(tmpXmlPage.getContent()).toString();
      Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
      return tmpContentAsText;
    }

    if (tmpPage instanceof TextPage) {
      TextPage tmpTextPage = (TextPage) tmpPage;
      String tmpContentAsText = tmpTextPage.getContent();
      Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
      return tmpContentAsText;
    }

    ContentType tmpContentType = ContentTypeUtil.getContentType(tmpPage);

    if (ContentType.PDF == tmpContentType) {
      try {
        String tmpContentAsText = ContentUtil.getPdfContentAsString(tmpPage.getWebResponse().getContentAsStream());
        Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
        return tmpContentAsText;
      } catch (IOException e) {
        Assert.fail("pdfConversionToTextFailed", new String[] { e.getMessage() });
        return null;
      }
    }

    if (ContentType.XLS == tmpContentType) {
      try {
        String tmpContentAsText = ContentUtil.getXlsContentAsString(tmpPage.getWebResponse().getContentAsStream());
        Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
        return tmpContentAsText;
      } catch (IOException e) {
        Assert.fail("xlsConversionToTextFailed", new String[] { e.getMessage() });
        return null;
      }
    }

    Assert.fail("unsupportedPageType", new String[] { tmpPage.getWebResponse().getContentType() });
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.WetBackend#addFailure(java.lang.String, java.lang.Object[], java.lang.Throwable)
   */
  @Override
  public void addFailure(String aMessageKey, Object[] aParameterArray, Throwable aCause) {
    String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    AssertionFailedException tmpFailure = new AssertionFailedException(tmpMessage, aCause);
    addFailure(tmpFailure);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.WetBackend#addFailure(java.lang.String, java.lang.Object[], java.lang.Throwable)
   */
  @Override
  public void addFailure(AssertionFailedException aFailure) {
    failures.add(aFailure);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.WetBackend#checkAndResetFailures()
   */
  @Override
  public AssertionFailedException checkAndResetFailures() {
    if (failures.isEmpty()) {
      return null;
    }

    AssertionFailedException tmpResult = failures.get(0);
    for (AssertionFailedException tmpException : failures) {
      Throwable tmpCause = tmpException.getCause();
      if (null != tmpCause) {
        wetEngine.informListenersWarn("error",
            new String[] { tmpException.getMessage(), ExceptionUtils.getStackTrace(tmpCause) });
      }
    }
    failures = new LinkedList<AssertionFailedException>();
    return tmpResult;
  }
}
