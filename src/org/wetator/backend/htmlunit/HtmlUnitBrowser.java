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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitButton;
import org.wetator.backend.htmlunit.control.HtmlUnitImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.wetator.backend.htmlunit.control.HtmlUnitInputFile;
import org.wetator.backend.htmlunit.control.HtmlUnitInputHidden;
import org.wetator.backend.htmlunit.control.HtmlUnitInputImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputPassword;
import org.wetator.backend.htmlunit.control.HtmlUnitInputRadioButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputReset;
import org.wetator.backend.htmlunit.control.HtmlUnitInputSubmit;
import org.wetator.backend.htmlunit.control.HtmlUnitInputText;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.control.HtmlUnitOptionGroup;
import org.wetator.backend.htmlunit.control.HtmlUnitSelect;
import org.wetator.backend.htmlunit.control.HtmlUnitTextArea;
import org.wetator.backend.htmlunit.util.ContentTypeUtil;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorEngine;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.AssertionFailedException;
import org.wetator.exception.WetatorException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.ContentUtil;
import org.wetator.util.NormalizedString;
import org.wetator.util.SecretString;

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
public final class HtmlUnitBrowser implements IBrowser {
  private static final Log LOG = LogFactory.getLog(HtmlUnitBrowser.class);;

  /** The maximum history size. */
  protected static final int MAX_HISTORY_SIZE = 15;

  /** Htmlunit WebClient. */
  protected WebClient webClient;
  /** ResponseStore. */
  protected ResponseStore responseStore;
  /** WetatorEngine. */
  protected WetatorEngine wetatorEngine;
  /** The list of failures ({@link AssertionFailedException}s). */
  protected List<AssertionFailedException> failures;
  /** ImmediateJobsTimeout. */
  protected long immediateJobsTimeout;
  /** The map containing the bookmarks. */
  protected Map<String, URL> bookmarks;

  /**
   * This repository contains all additional controls supported by the backend (e.g. added by a command set).
   */
  protected HtmlUnitControlRepository controlRepository = new HtmlUnitControlRepository();

  /**
   * Constructor.
   * 
   * @param aWetatorEngine the engine to work with
   */
  public HtmlUnitBrowser(final WetatorEngine aWetatorEngine) {
    super();

    // setup the backend
    // httpclient should accept all cookies
    System.getProperties().put("apache.commons.httpclient.cookiespec", "COMPATIBILITY");

    failures = new LinkedList<AssertionFailedException>();
    wetatorEngine = aWetatorEngine;

    // response store
    final WetatorConfiguration tmpConfiguration = wetatorEngine.getConfiguration();
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
    controlRepository.add(HtmlUnitInputHidden.class);
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
   * @see org.wetator.backend.IBrowser#startNewSession(org.wetator.backend.IBrowser.BrowserType)
   */
  @Override
  public void startNewSession(final IBrowser.BrowserType aBrowserType) {
    final WetatorConfiguration tmpConfiguration = wetatorEngine.getConfiguration();

    // reset the bookmarks
    bookmarks = new HashMap<String, URL>();

    final BrowserVersion tmpBrowserVersion = determineBrowserVersionFor(aBrowserType);

    if (null != webClient) {
      // TODO maybe we have to do more here
      try {
        // unset the onbeforeunload handler to avoid it interfering
        webClient.setOnbeforeunloadHandler(null);

        webClient.closeAllWindows();
      } catch (final ScriptException e) {
        // TODO handle exception
        e.printStackTrace();
      }
    }

    final String tmpHost = tmpConfiguration.getProxyHost();
    if (StringUtils.isNotEmpty(tmpHost)) {
      LOG.info("Proxy configured");
      LOG.info("Proxy Host: '" + tmpHost + "'");
      final int tmpPort = tmpConfiguration.getProxyPort();
      LOG.info("Proxy Port: '" + tmpPort + "'");

      webClient = new WebClient(tmpBrowserVersion, tmpHost, tmpPort);

      if ((null != tmpConfiguration.getProxyUser())
          && StringUtils.isNotEmpty(tmpConfiguration.getProxyUser().getValue())) {
        final String tmpUser = tmpConfiguration.getProxyUser().getValue();
        LOG.info("Proxy User: '" + tmpUser + "'");
        final String tmpPassword = tmpConfiguration.getProxyPassword().getValue();
        final DefaultCredentialsProvider tmpCredentialProvider = new DefaultCredentialsProvider();
        tmpCredentialProvider.addCredentials(tmpUser, tmpPassword);
        webClient.setCredentialsProvider(tmpCredentialProvider);
      } else {
        LOG.info("Proxy no user defined");
      }

      final Set<String> tmpNonProxyHosts = tmpConfiguration.getProxyHostsToBypass();

      for (String tmpString : tmpNonProxyHosts) {
        if (StringUtils.isNotEmpty(tmpString)) {
          final String tmpHostsToProxyBypass = tmpString.trim();
          webClient.getProxyConfig().addHostsToProxyBypass(tmpHostsToProxyBypass);
          LOG.info("Proxy HostsToProxyBypass: '" + tmpHostsToProxyBypass + "'");
        }
      }
    } else {
      webClient = new WebClient(tmpBrowserVersion);

      if ((null != tmpConfiguration.getBasicAuthUser())
          && StringUtils.isNotEmpty(tmpConfiguration.getBasicAuthUser().getValue())) {
        final String tmpUser = tmpConfiguration.getBasicAuthUser().getValue();
        final String tmpPassword = tmpConfiguration.getBasicAuthPassword().getValue();
        final DefaultCredentialsProvider tmpCredentialProvider = new DefaultCredentialsProvider();
        tmpCredentialProvider.addCredentials(tmpUser, tmpPassword);
        webClient.setCredentialsProvider(tmpCredentialProvider);

        LOG.info("BasicAuth enabled  user '" + tmpUser + "'.");
      }

    }

    // setup our listener
    webClient.addWebWindowListener(new WebWindowListener(this));
    webClient.setAlertHandler(new AlertHandler(wetatorEngine));
    // javascript
    webClient.setJavaScriptEnabled(true);
    webClient.setThrowExceptionOnScriptError(false);
    webClient.setJavaScriptErrorListener(new JavaScriptErrorListener(this));

    // debug stuff
    // webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    // final HtmlUnitContextFactory tmpContextFactory = webClient.getJavaScriptEngine().getContextFactory();
    // tmpContextFactory.setDebugger(new DebuggerImpl());

    // set Accept-Language header
    webClient.addRequestHeader("Accept-Language", tmpConfiguration.getAcceptLanaguage());

    // trust all SSL-certificates
    try {
      webClient.setUseInsecureSSL(true);
    } catch (final GeneralSecurityException e) {
      // TODO handle exception
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#openUrl(java.net.URL)
   */
  @Override
  public void openUrl(final URL aUrl) throws AssertionFailedException {
    try {
      webClient.getPage(aUrl);
      waitForImmediateJobs();
    } catch (final ScriptException e) {
      addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      addFailure("javascriptError", new String[] { tmpScriptException.getMessage() }, tmpScriptException);
    } catch (final FailingHttpStatusCodeException e) {
      addFailure("openServerError", new String[] { aUrl.toString(), e.getMessage() }, e);
    } catch (final UnknownHostException e) {
      addFailure("unknownHostError", new String[] { aUrl.toString(), e.getMessage() }, e);
    } catch (final Throwable e) {
      LOG.error("OpenUrl '" + aUrl.toExternalForm() + "'fails. " + e.getMessage());
      addFailure("openServerError", new String[] { aUrl.toString(), e.getMessage() }, e);
    }

    final String tmpRef = aUrl.getRef();
    if (StringUtils.isNotEmpty(tmpRef)) {
      checkAnchor(tmpRef);
    }
  }

  /**
   * Our own alert handler.
   */
  public static final class AlertHandler implements com.gargoylesoftware.htmlunit.AlertHandler {
    private WetatorEngine wetatorEngine;

    /**
     * Constructor.
     * 
     * @param aWetatorEngine the engine to inform about the alert texts.
     */
    public AlertHandler(final WetatorEngine aWetatorEngine) {
      wetatorEngine = aWetatorEngine;
    }

    @Override
    public void handleAlert(final Page aPage, final String aMessage) {
      LOG.debug("handleAlert " + aMessage);

      String tmpMessage = "";
      if (StringUtils.isNotEmpty(aMessage)) {
        tmpMessage = aMessage;
      }
      String tmpUrl = "";
      try {
        tmpUrl = aPage.getWebResponse().getWebRequest().getUrl().toExternalForm();
      } catch (final NullPointerException e) {
        // ignore
      }

      wetatorEngine.informListenersInfo("javascriptAlert", new String[] { tmpMessage, tmpUrl });
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
    public JavaScriptErrorListener(final HtmlUnitBrowser aHtmlUnitBrowser) {
      htmlUnitBrowser = aHtmlUnitBrowser;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener#loadScriptError(com.gargoylesoftware.htmlunit.html.HtmlPage,
     *      java.net.URL, java.lang.Exception)
     */
    @Override
    public void loadScriptError(final HtmlPage aHtmlPage, final URL aScriptUrl, final Exception anException) {
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
    public void malformedScriptURL(final HtmlPage aHtmlPage, final String aUrl,
        final MalformedURLException aMalformedURLException) {
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
    public void scriptException(final HtmlPage aHtmlPage, final ScriptException aScriptException) {
      htmlUnitBrowser.addFailure("javascriptError", new String[] { aScriptException.getMessage() }, aScriptException);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener#timeoutError(com.gargoylesoftware.htmlunit.html.HtmlPage,
     *      long, long)
     */
    @Override
    public void timeoutError(final HtmlPage aHtmlPage, final long aAllowedTime, final long aExecutionTime) {
      htmlUnitBrowser.addFailure("javascriptTimeoutError",
          new String[] { Long.toString(aAllowedTime), Long.toString(aExecutionTime),
              aHtmlPage.getUrl().toExternalForm() }, null);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#closeWindow(org.wetator.util.SecretString)
   */
  @Override
  public void closeWindow(final SecretString aWindowName) throws AssertionFailedException {
    final List<WebWindow> tmpWebWindows = webClient.getWebWindows();
    if (tmpWebWindows.isEmpty()) {
      Assert.fail("noWindowToClose", null);
    }

    if (null == aWindowName || StringUtils.isEmpty(aWindowName.getValue())) {
      for (int i = tmpWebWindows.size() - 1; i >= 0; i--) {
        final WebWindow tmpWebWindow = tmpWebWindows.get(i);

        if (tmpWebWindow instanceof TopLevelWindow) {
          wetatorEngine.informListenersInfo("closeWindow", new String[] { tmpWebWindow.getName() });
          ((TopLevelWindow) tmpWebWindow).close();
          return;
        }
        if (tmpWebWindow instanceof DialogWindow) {
          wetatorEngine.informListenersInfo("closeDialogWindow", new String[] { tmpWebWindow.getName() });
          ((DialogWindow) tmpWebWindow).close();
          return;
        }
      }
      Assert.fail("noWindowToClose", null);
    }

    final SearchPattern tmpWindowNamePattern = aWindowName.getSearchPattern();
    for (int i = tmpWebWindows.size() - 1; i > 0; i--) {
      final WebWindow tmpWebWindow = tmpWebWindows.get(i);

      final String tmpWindowName = tmpWebWindow.getName();
      if (tmpWindowNamePattern.matches(tmpWindowName)) {
        if (tmpWebWindow instanceof TopLevelWindow) {
          wetatorEngine.informListenersInfo("closeWindow", new String[] { tmpWindowName });
          ((TopLevelWindow) tmpWebWindow).close();
          return;
        }
        if (tmpWebWindow instanceof DialogWindow) {
          wetatorEngine.informListenersInfo("closeDialogWindow", new String[] { tmpWindowName });
          ((DialogWindow) tmpWebWindow).close();
          return;
        }
      }
    }
    Assert.fail("noWindowByNameToClose", new String[] { aWindowName.toString() });
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#goBackInCurrentWindow(int)
   */
  @Override
  public void goBackInCurrentWindow(final int aSteps) throws AssertionFailedException {
    final WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

    if (null == tmpCurrentWindow) {
      Assert.fail("noWebWindow", null);
    }

    final History tmpHistory = tmpCurrentWindow.getHistory();

    final int tmpIndexPos = tmpHistory.getIndex() - aSteps;
    if (tmpIndexPos >= tmpHistory.getLength() || tmpIndexPos < 0) {
      Assert.fail(
          "outsideHistory",
          new String[] { Integer.toString(aSteps), Integer.toString(tmpIndexPos),
              Integer.toString(tmpHistory.getLength()) });
    }

    try {
      tmpHistory.go(-1 * aSteps);
    } catch (final IOException e) {
      Assert.fail("historyFailed", new String[] { e.getMessage() });
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#saveCurrentWindowToLog(org.wetator.backend.control.IControl[])
   */
  @Override
  public void saveCurrentWindowToLog(final IControl... aControls) {
    final WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

    if (null != tmpCurrentWindow) {
      try {
        final Page tmpPage = tmpCurrentWindow.getEnclosedPage();
        if (null != tmpPage) {
          for (IControl tmpControl : aControls) {
            tmpControl.addHighlightStyle(wetatorEngine.getConfiguration());
          }
          final String tmpPageFile = responseStore.storePage(webClient, tmpPage);
          wetatorEngine.informListenersResponseStored(tmpPageFile);
        }
      } catch (final WetatorException e) {
        LOG.fatal("Problem with window handling. Saving page failed!", e);
      }
    }
  }

  /**
   * Checks if the url contains a hash, that the matching anchor is on the page.
   * 
   * @param aRef the hash from the url
   * @throws AssertionFailedException if no matisching anchor found
   */
  protected void checkAnchor(final String aRef) throws AssertionFailedException {
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
     * The constructor.
     * 
     * @param anHtmlUnitBrowser the browser to inform
     */
    public WebWindowListener(final HtmlUnitBrowser anHtmlUnitBrowser) {
      super();
      htmlUnitBrowser = anHtmlUnitBrowser;
    }

    @Override
    public void webWindowOpened(final WebWindowEvent anEvent) {
      LOG.debug("webWindowOpened");
    }

    @Override
    public void webWindowClosed(final WebWindowEvent anEvent) {
      final Page tmpPage = anEvent.getWebWindow().getEnclosedPage();
      if (null == tmpPage) {
        LOG.debug("webWindowClosed: (page null)");
      } else {
        LOG.debug("webWindowClosed: (url '"
            + anEvent.getWebWindow().getEnclosedPage().getWebResponse().getWebRequest().getUrl() + "')");
      }
    }

    @Override
    public void webWindowContentChanged(final WebWindowEvent anEvent) {
      LOG.debug("webWindowContentChanged");
      final Page tmpNewPage = anEvent.getNewPage();
      // first load into a new window
      if (null != tmpNewPage && null == anEvent.getOldPage()) {
        final URL tmpUrl = tmpNewPage.getWebResponse().getWebRequest().getUrl();
        final String tmpRef = tmpUrl.getRef();
        if (StringUtils.isNotEmpty(tmpRef)) {
          try {
            PageUtil.checkAnchor(tmpRef, tmpNewPage);
          } catch (final AssertionFailedException e) {
            htmlUnitBrowser.addFailure(e);
          }
        }
      }
    }

  }

  private Page getCurrentPage() throws AssertionFailedException {
    final WebWindow tmpWebWindow = webClient.getCurrentWindow();
    if (null == tmpWebWindow) {
      Assert.fail("noWebWindow", null);
    }
    final Page tmpPage = tmpWebWindow.getEnclosedPage();
    if (null == tmpPage) {
      Assert.fail("noPageInWebWindow", null);
    }

    return tmpPage;
  }

  @SuppressWarnings("deprecation")
  private BrowserVersion determineBrowserVersionFor(final IBrowser.BrowserType aBrowserType) {
    if (IBrowser.BrowserType.FIREFOX_3 == aBrowserType) {
      return BrowserVersion.FIREFOX_3;
    }
    if (IBrowser.BrowserType.FIREFOX_3_6 == aBrowserType) {
      return BrowserVersion.FIREFOX_3_6;
    }
    if (IBrowser.BrowserType.INTERNET_EXPLORER_6 == aBrowserType) {
      return BrowserVersion.INTERNET_EXPLORER_6;
    }
    if (IBrowser.BrowserType.INTERNET_EXPLORER_7 == aBrowserType) {
      return BrowserVersion.INTERNET_EXPLORER_7;
    }
    if (IBrowser.BrowserType.INTERNET_EXPLORER_8 == aBrowserType) {
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
    final Page tmpPage = getCurrentPage();
    if (tmpPage instanceof HtmlPage) {
      return (HtmlPage) tmpPage;
    }

    Assert.fail("noHtmlPage", new String[] { tmpPage.getClass().toString() });
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#getControlFinder()
   */
  @Override
  public IControlFinder getControlFinder() throws AssertionFailedException {
    final HtmlPage tmpHtmlPage = getCurrentHtmlPage();

    return new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#waitForImmediateJobs()
   */
  @Override
  public void waitForImmediateJobs() throws AssertionFailedException {
    Page tmpPage = getCurrentPage();
    if (tmpPage instanceof HtmlPage) {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + immediateJobsTimeout;
      while (System.currentTimeMillis() < tmpEndTime) {
        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
        final JavaScriptJobManager tmpJobManager = tmpHtmlPage.getEnclosingWindow().getJobManager();

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

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#assertTitleInTimeFrame(java.util.List, long)
   */
  @Override
  public boolean assertTitleInTimeFrame(final List<SecretString> aTitleToWaitFor, final long aTimeoutInSeconds)
      throws AssertionFailedException {
    final long tmpWaitTime = Math.max(immediateJobsTimeout, aTimeoutInSeconds * 1000L);

    // remember the page at start to be able to detect page changes
    final Page tmpStartPage = getCurrentPage();

    Page tmpPage = getCurrentPage();
    if (tmpPage instanceof HtmlPage) {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + tmpWaitTime;
      while (System.currentTimeMillis() < tmpEndTime) {
        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
        final String tmpCurrentTitle = tmpHtmlPage.getTitleText();
        try {
          Assert.assertListMatch(aTitleToWaitFor, tmpCurrentTitle);
          return tmpStartPage != tmpPage;
        } catch (final AssertionFailedException e) {
          // ok, not found, maybe we have to be more patient
        }

        final JavaScriptJobManager tmpJobManager = tmpHtmlPage.getEnclosingWindow().getJobManager();
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

    final HtmlPage tmpHtmlPage = getCurrentHtmlPage();
    final String tmpCurrentTitle = tmpHtmlPage.getTitleText();
    Assert.assertListMatch(aTitleToWaitFor, tmpCurrentTitle);
    return tmpStartPage != tmpPage;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#assertContentInTimeFrame(java.util.List, long)
   */
  @Override
  public boolean assertContentInTimeFrame(final List<SecretString> aContentToWaitFor, final long aTimeoutInSeconds)
      throws AssertionFailedException {
    final long tmpWaitTime = Math.max(immediateJobsTimeout, aTimeoutInSeconds * 1000L);

    // remember the page at start to be able to detect page changes
    final Page tmpStartPage = getCurrentPage();

    Page tmpPage = getCurrentPage();
    if (tmpPage instanceof HtmlPage) {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + tmpWaitTime;
      while (System.currentTimeMillis() < tmpEndTime) {
        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
        final String tmpContentAsText = new HtmlPageIndex(tmpHtmlPage).getText();
        try {
          Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
          return tmpStartPage != tmpPage;
        } catch (final AssertionFailedException e) {
          // ok, not found, maybe we have to be more patient
        }

        final JavaScriptJobManager tmpJobManager = tmpHtmlPage.getEnclosingWindow().getJobManager();
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
      final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
      final String tmpContentAsText = new HtmlPageIndex(tmpHtmlPage).getText();
      Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
      return tmpStartPage != tmpPage;
    }

    if (tmpPage instanceof XmlPage) {
      final XmlPage tmpXmlPage = (XmlPage) tmpPage;
      final String tmpContentAsText = new NormalizedString(tmpXmlPage.getContent()).toString();
      Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
      return tmpStartPage != tmpPage;
    }

    if (tmpPage instanceof TextPage) {
      final TextPage tmpTextPage = (TextPage) tmpPage;
      final String tmpContentAsText = tmpTextPage.getContent();
      Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
      return tmpStartPage != tmpPage;
    }

    final ContentType tmpContentType = ContentTypeUtil.getContentType(tmpPage);

    if (ContentType.PDF == tmpContentType) {
      try {
        final String tmpContentAsText = ContentUtil
            .getPdfContentAsString(tmpPage.getWebResponse().getContentAsStream());
        Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
        return tmpStartPage != tmpPage;
      } catch (final IOException e) {
        Assert.fail("pdfConversionToTextFailed", new String[] { e.getMessage() });
        return tmpStartPage != tmpPage;
      }
    }

    if (ContentType.XLS == tmpContentType) {
      try {
        final String tmpContentAsText = ContentUtil
            .getXlsContentAsString(tmpPage.getWebResponse().getContentAsStream());
        Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
        return tmpStartPage != tmpPage;
      } catch (final IOException e) {
        Assert.fail("xlsConversionToTextFailed", new String[] { e.getMessage() });
        return tmpStartPage != tmpPage;
      }
    }

    if (ContentType.RTF == tmpContentType) {
      try {
        final String tmpContentAsText = ContentUtil
            .getRtfContentAsString(tmpPage.getWebResponse().getContentAsStream());
        Assert.assertListMatch(aContentToWaitFor, tmpContentAsText);
        return tmpStartPage != tmpPage;
      } catch (final IOException e) {
        Assert.fail("rtfConversionToTextFailed", new String[] { e.getMessage() });
        return tmpStartPage != tmpPage;
      } catch (final BadLocationException e) {
        Assert.fail("rtfConversionToTextFailed", new String[] { e.getMessage() });
        return tmpStartPage != tmpPage;
      }
    }

    Assert.fail("unsupportedPageType", new String[] { tmpPage.getWebResponse().getContentType() });
    return tmpStartPage != tmpPage;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#addFailure(java.lang.String, java.lang.Object[], java.lang.Throwable)
   */
  @Override
  public void addFailure(final String aMessageKey, final Object[] aParameterArray, final Throwable aCause) {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    final AssertionFailedException tmpFailure = new AssertionFailedException(tmpMessage, aCause);
    addFailure(tmpFailure);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#addFailure(java.lang.String, java.lang.Object[], java.lang.Throwable)
   */
  @Override
  public void addFailure(final AssertionFailedException aFailure) {
    failures.add(aFailure);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#checkAndResetFailures()
   */
  @Override
  public AssertionFailedException checkAndResetFailures() {
    if (failures.isEmpty()) {
      return null;
    }

    final AssertionFailedException tmpResult = failures.get(0);
    for (AssertionFailedException tmpException : failures) {
      final Throwable tmpCause = tmpException.getCause();
      if (null != tmpCause) {
        wetatorEngine.informListenersWarn("pageError",
            new String[] { tmpException.getMessage(), ExceptionUtils.getStackTrace(tmpCause) });
      }
    }
    failures = new LinkedList<AssertionFailedException>();
    return tmpResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#getBookmark(java.lang.String)
   */
  @Override
  public URL getBookmark(final String aBookmarkName) {
    return bookmarks.get(aBookmarkName);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IBrowser#saveBookmark(java.lang.String, java.net.URL)
   */
  @Override
  public void saveBookmark(final String aBookmarkName, final URL aBookmarkUrl) {
    bookmarks.put(aBookmarkName, aBookmarkUrl);
  }

  /**
   * {@inheritDoc}
   * 
   * @throws AssertionFailedException
   * @see org.wetator.backend.IBrowser#bookmarkPage(String)
   */
  @Override
  public void bookmarkPage(final String aBookmarkName) throws AssertionFailedException {
    final URL tmpUrl = getCurrentHtmlPage().getWebResponse().getWebRequest().getUrl();
    saveBookmark(aBookmarkName, tmpUrl);
  }
}
