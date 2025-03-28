/*
 * Copyright (c) 2008-2025 wetator.org
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
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.text.BadLocationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.htmlunit.BrowserVersion;
import org.htmlunit.DefaultCredentialsProvider;
import org.htmlunit.DialogWindow;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.History;
import org.htmlunit.Page;
import org.htmlunit.ScriptException;
import org.htmlunit.TextPage;
import org.htmlunit.TopLevelWindow;
import org.htmlunit.WaitingRefreshHandler;
import org.htmlunit.WebClient;
import org.htmlunit.WebResponse;
import org.htmlunit.WebWindow;
import org.htmlunit.WebWindowEvent;
import org.htmlunit.corejs.javascript.WrappedException;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.FrameWindow;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.javascript.DebuggerImpl;
import org.htmlunit.javascript.HtmlUnitContextFactory;
import org.htmlunit.javascript.JavaScriptEngine;
import org.htmlunit.javascript.background.JavaScriptJob;
import org.htmlunit.javascript.background.JavaScriptJobManager;
import org.htmlunit.javascript.host.Window;
import org.htmlunit.util.WebClientUtils;
import org.htmlunit.xml.XmlPage;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.control.HtmlUnitButton;
import org.wetator.backend.htmlunit.control.HtmlUnitImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.wetator.backend.htmlunit.control.HtmlUnitInputEmail;
import org.wetator.backend.htmlunit.control.HtmlUnitInputFile;
import org.wetator.backend.htmlunit.control.HtmlUnitInputImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputNumber;
import org.wetator.backend.htmlunit.control.HtmlUnitInputPassword;
import org.wetator.backend.htmlunit.control.HtmlUnitInputRadioButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputReset;
import org.wetator.backend.htmlunit.control.HtmlUnitInputSubmit;
import org.wetator.backend.htmlunit.control.HtmlUnitInputTel;
import org.wetator.backend.htmlunit.control.HtmlUnitInputText;
import org.wetator.backend.htmlunit.control.HtmlUnitInputUrl;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.control.HtmlUnitOptionGroup;
import org.wetator.backend.htmlunit.control.HtmlUnitSelect;
import org.wetator.backend.htmlunit.control.HtmlUnitTextArea;
import org.wetator.backend.htmlunit.util.ContentTypeUtil;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorEngine;
import org.wetator.core.searchpattern.ContentPattern;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.BackendException;
import org.wetator.exception.ResourceException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.ContentUtil;
import org.wetator.util.NormalizedString;
import org.wetator.util.SecretString;

/**
 * The HtmlUnit backend.
 *
 * @author rbri
 * @author frank.danek
 */
public final class HtmlUnitBrowser implements IBrowser {
  private static final Logger LOG = LogManager.getLogger(HtmlUnitBrowser.class);

  private static final int MAX_LENGTH = 4000;

  /** Htmlunit WebClient. */
  private WebClient webClient;
  /** Sometimes we like to ignore some jobs. */
  private JavaScriptJobFilter jobFilter;
  /** ResponseStore. */
  private Map<BrowserVersion, ResponseStore> responseStores;
  /** WetatorEngine. */
  private WetatorEngine wetatorEngine;
  /** The list of failures ({@link AssertionException}s). */
  private List<AssertionException> failures;
  /** The JavaScript timeout. */
  private long jsTimeoutInMillis;
  /** The map containing the bookmarks. */
  private Map<String, URL> bookmarks;
  /** Cache of saved pages. */
  private WeakHashMap<Page, String> savedPages;

  /**
   * This repository contains all additional controls supported by the backend (e.g. added by a command set).
   */
  private HtmlUnitControlRepository controlRepository = new HtmlUnitControlRepository();

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

    savedPages = new WeakHashMap<>();

    failures = new LinkedList<>();
    wetatorEngine = aWetatorEngine;

    // response store
    final WetatorConfiguration tmpConfiguration = wetatorEngine.getConfiguration();
    jsTimeoutInMillis = tmpConfiguration.getJsTimeoutInSeconds() * 1000L;
    responseStores = new HashMap<>();
    for (final BrowserType tmpBrowserType : tmpConfiguration.getBrowserTypes()) {
      final BrowserVersion tmpBrowserVersion = determineBrowserVersionFor(tmpBrowserType);
      // manipulate the browser version before using it as key for a map
      // because this manipulation will change the hash value
      for (final Map.Entry<String, String> tmpMapping : tmpConfiguration.getMimeTypes().entrySet()) {
        tmpBrowserVersion.registerUploadMimeType(tmpMapping.getKey(), tmpMapping.getValue());
      }

      final ResponseStore tmpStore = new ResponseStore(tmpConfiguration.getOutputDir(), tmpBrowserType.getLabel(),
          !tmpConfiguration.isAppendResultsEnabled());
      responseStores.put(tmpBrowserVersion, tmpStore);
    }

    if (tmpConfiguration.isAppendResultsEnabled()) {
      ResponseStore.updateCounter(tmpConfiguration.getOutputDir());
    }

    // add the default controls
    controlRepository.add(HtmlUnitAnchor.class);
    controlRepository.add(HtmlUnitButton.class);
    controlRepository.add(HtmlUnitImage.class);
    controlRepository.add(HtmlUnitInputButton.class);
    controlRepository.add(HtmlUnitInputCheckBox.class);
    controlRepository.add(HtmlUnitInputEmail.class);
    controlRepository.add(HtmlUnitInputFile.class);
    controlRepository.add(HtmlUnitInputImage.class);
    controlRepository.add(HtmlUnitInputNumber.class);
    controlRepository.add(HtmlUnitInputPassword.class);
    controlRepository.add(HtmlUnitInputRadioButton.class);
    controlRepository.add(HtmlUnitInputReset.class);
    controlRepository.add(HtmlUnitInputSubmit.class);
    controlRepository.add(HtmlUnitInputTel.class);
    controlRepository.add(HtmlUnitInputText.class);
    controlRepository.add(HtmlUnitInputUrl.class);
    controlRepository.add(HtmlUnitOption.class);
    controlRepository.add(HtmlUnitOptionGroup.class);
    controlRepository.add(HtmlUnitSelect.class);
    controlRepository.add(HtmlUnitTextArea.class);

    // add the controls from the configuration
    controlRepository.addAll(tmpConfiguration.getControls());
  }

  @Override
  public void close() {
    try {
      HtmlUnitFinderDelegator.shutdownThreadPool();
    } catch (final InterruptedException e) {
      LOG.warn("Could not shutdown the Thread Pool.", e);
    }
  }

  @Override
  public void startNewSession(final IBrowser.BrowserType aBrowserType) {
    // at first take care there is now other session active
    endSession();

    final WetatorConfiguration tmpConfiguration = wetatorEngine.getConfiguration();

    // reset the bookmarks
    bookmarks = new HashMap<>();

    final BrowserVersion tmpBrowserVersion = determineBrowserVersionFor(aBrowserType);

    DefaultCredentialsProvider tmpCredentialProvider = null;

    final String tmpHost = tmpConfiguration.getProxyHost();
    if (StringUtils.isNotEmpty(tmpHost)) {
      LOG.info("Proxy configured");
      LOG.info("Proxy Host: '" + tmpHost + "'");
      final int tmpPort = tmpConfiguration.getProxyPort();
      LOG.info("Proxy Port: '" + tmpPort + "'");

      webClient = new WebClient(tmpBrowserVersion, tmpHost, tmpPort);

      final SecretString tmpProxyUser = tmpConfiguration.getProxyUser();
      if (null != tmpProxyUser && !tmpProxyUser.isEmpty()) {
        final String tmpUser = tmpProxyUser.getValue();
        LOG.info("Proxy User: '" + tmpUser + "'");
        final String tmpPassword = tmpConfiguration.getProxyPassword().getValue();
        tmpCredentialProvider = new DefaultCredentialsProvider();
        webClient.setCredentialsProvider(tmpCredentialProvider);

        tmpCredentialProvider.addCredentials(tmpUser, tmpPassword.toCharArray(), tmpHost, tmpPort, null);
      } else {
        LOG.info("Proxy no user defined");
      }

      final Set<String> tmpNonProxyHosts = tmpConfiguration.getProxyHostsToBypass();

      for (final String tmpString : tmpNonProxyHosts) {
        String tmpHostsToProxyBypass = tmpString.trim();
        tmpHostsToProxyBypass = tmpHostsToProxyBypass.replaceAll("\\.", "\\\\.");
        tmpHostsToProxyBypass = tmpHostsToProxyBypass.replaceAll("^\\*", ".*");

        webClient.getOptions().getProxyConfig().addHostsToProxyBypass(tmpHostsToProxyBypass);
        LOG.info("Proxy HostsToProxyBypass: '" + tmpHostsToProxyBypass + "'");
      }
    } else {
      webClient = new WebClient(tmpBrowserVersion);
    }

    final SecretString tmpBasicAuthUser = tmpConfiguration.getBasicAuthUser();
    if (null != tmpBasicAuthUser && !tmpBasicAuthUser.isEmpty()) {
      final String tmpUser = tmpBasicAuthUser.getValue();
      final String tmpPassword = tmpConfiguration.getBasicAuthPassword().getValue();

      if (null == tmpCredentialProvider) {
        tmpCredentialProvider = new DefaultCredentialsProvider();
        webClient.setCredentialsProvider(tmpCredentialProvider);
      }

      tmpCredentialProvider.addCredentials(tmpUser, tmpPassword.toCharArray());
      webClient.setCredentialsProvider(tmpCredentialProvider);

      LOG.info("BasicAuth enabled  user '" + tmpUser + "'.");
    }

    final SecretString tmpNtlmUser = tmpConfiguration.getNtlmUser();
    if (null != tmpNtlmUser && !tmpNtlmUser.isEmpty()) {
      final String tmpUser = tmpNtlmUser.getValue();
      final String tmpPassword = tmpConfiguration.getNtlmPassword().getValue();
      final String tmpWorkstation = tmpConfiguration.getNtlmWorkstation().getValue();
      final String tmpDomain = tmpConfiguration.getNtlmDomain().getValue();

      if (null == tmpCredentialProvider) {
        tmpCredentialProvider = new DefaultCredentialsProvider();
        webClient.setCredentialsProvider(tmpCredentialProvider);
      }

      tmpCredentialProvider.addNTLMCredentials(tmpUser, tmpPassword.toCharArray(), null, -1, tmpWorkstation, tmpDomain);

      LOG.info("NTLM enabled  user '" + tmpUser + "' workstation '" + tmpWorkstation + "' domain '" + tmpDomain + "'.");
    }

    // setup our listener
    webClient.addWebWindowListener(new WebWindowListener(this));
    webClient.setAttachmentHandler(new AttachmentHandler());
    webClient.setAlertHandler(new AlertHandler(wetatorEngine));
    webClient.getWebConsole().setLogger(new WebConsoleLogger(wetatorEngine));
    webClient.setIncorrectnessListener(new IncorrectnessListener(wetatorEngine));
    webClient.setConfirmHandler(new ConfirmHandler(wetatorEngine));

    // refresh handler - behave like the browser does
    webClient.setRefreshHandler(new WaitingRefreshHandler());

    // javascript
    webClient.getOptions().setJavaScriptEnabled(true);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.setJavaScriptErrorListener(new JavaScriptErrorListener(this));

    // webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    // webClient.getOptions().setUseInsecureSSL(true);

    final Set<SearchPattern> tmpFilters = tmpConfiguration.getJsJobFilterPatterns();
    if (tmpFilters.isEmpty()) {
      jobFilter = null;
    } else {
      jobFilter = new JavaScriptJobFilter();
      for (final SearchPattern tmpSearchPattern : tmpFilters) {
        jobFilter.addPattern(tmpSearchPattern);
      }
    }

    // set Accept-Language header
    webClient.addRequestHeader("Accept-Language", tmpConfiguration.getAcceptLanaguage());

    // trust all SSL-certificates
    webClient.getOptions().setUseInsecureSSL(true);

    // use client certificate key store
    final String tmpClientCertKeyStoreUrl = tmpConfiguration.getClientCertificateKeyStoreUrl();
    if (StringUtils.isNotEmpty(tmpClientCertKeyStoreUrl)) {
      final String tmpClientCertKeyStoreType = tmpConfiguration.getClientCertificateKeyStoreType();
      final SecretString tmpClientCertKeyStorePassword = tmpConfiguration.getClientCertificateKeyStorePassword();

      LOG.info("ClientCertificateKeyStore configured");
      LOG.info("    URL:      " + tmpClientCertKeyStoreUrl);
      LOG.info("    Type:     " + tmpClientCertKeyStoreType);
      LOG.info("    Password: " + tmpClientCertKeyStorePassword);

      try {
        final URL tmpKeyStoreURL = new URL(tmpClientCertKeyStoreUrl);
        webClient.getOptions().setSSLClientCertificateKeyStore(tmpKeyStoreURL, tmpClientCertKeyStorePassword.getValue(),
            tmpClientCertKeyStoreType);
      } catch (final Exception e) {
        LOG.error("Failed to use configured ClientCertificateKeyStore.", e.getCause());
      }
    }

    // set the timeout
    webClient.getOptions().setTimeout(tmpConfiguration.getHttpTimeoutInSeconds() * 1000);

    // debug stuff
    if (tmpConfiguration.isDebugLoggingEnabled()) {
      final HtmlUnitContextFactory tmpContextFactory = ((JavaScriptEngine) webClient.getJavaScriptEngine())
          .getContextFactory();
      tmpContextFactory.setDebugger(new DebuggerImpl());
    }
    // webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    if (tmpConfiguration.startJsDebugger()) {
      WebClientUtils.attachVisualDebugger(webClient);
    }
  }

  @Override
  public void endSession() {
    if (null != webClient) {
      try {
        // unset the onbeforeunload handler to avoid it interfering
        webClient.setOnbeforeunloadHandler(null);

        webClient.close();
      } catch (final ScriptException e) {
        LOG.warn("Could not close previous window.", e);
      }
    }
  }

  @Override
  public void openUrl(final URL aUrl) throws ActionException {
    try {
      webClient.getPage(aUrl);
      waitForImmediateJobs();

      final String tmpRequestedUrl = aUrl.toExternalForm();
      final String tmpCurrentUrl = getCurrentPage().getUrl().toExternalForm();
      if (!tmpRequestedUrl.equals(tmpCurrentUrl)) {
        wetatorEngine.informListenersInfo("openUrlRedirected", tmpRequestedUrl, tmpCurrentUrl);
      }
    } catch (final ScriptException e) {
      addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      addFailure("javascriptError", new String[] { tmpScriptException.getMessage() }, tmpScriptException);
    } catch (final FailingHttpStatusCodeException e) {
      final String tmpMessage = Messages.getMessage("openServerError", aUrl, e.getMessage());
      throw new ActionException(tmpMessage, e);
    } catch (final UnknownHostException e) {
      final String tmpMessage = Messages.getMessage("unknownHostError", aUrl, e.getMessage());
      throw new ActionException(tmpMessage, e);
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("openBackendError", e.getMessage());
      throw new ActionException(tmpMessage, e);
    } catch (final Throwable e) {
      LOG.error("OpenUrl '" + aUrl.toExternalForm() + "' fails. " + e.getMessage());
      final String tmpMessage = Messages.getMessage("openServerError", aUrl, e.getMessage());
      throw new ActionException(tmpMessage, e);
    }
  }

  /**
   * Our own alert handler.
   */
  public static final class AlertHandler implements org.htmlunit.AlertHandler {

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
      if (LOG.isDebugEnabled()) {
        LOG.debug("handleAlert " + aMessage);
      }

      String tmpMessage = "";
      if (StringUtils.isNotEmpty(aMessage)) {
        tmpMessage = aMessage;
      }
      String tmpUrl = "";
      try {
        tmpUrl = aPage.getWebResponse().getWebRequest().getUrl().toExternalForm();
      } catch (final NullPointerException e) { // NOPMD
        // ignore
      }
      wetatorEngine.informListenersInfo("javascriptAlert", tmpMessage, tmpUrl);
    }
  }

  /**
   * Our own alert handler.
   */
  public static final class AttachmentHandler implements org.htmlunit.attachment.AttachmentHandler {

    @Override
    public void handleAttachment(final Page aPage, final String anAttachmentFilename) {
      // Wetator likes to switch to the new window;
      // if we are finished with asserts against the attachment
      // we have to close the window.
      aPage.getEnclosingWindow().getWebClient().setCurrentWindow(aPage.getEnclosingWindow());
    }
  }

  /**
   * Our own confirm handler.
   */
  public static final class ConfirmHandler implements org.htmlunit.ConfirmHandler {

    private WetatorEngine wetatorEngine;
    private ContentPattern message;
    private boolean result;

    /**
     * Constructor.
     *
     * @param aWetatorEngine the engine to inform about the alert texts.
     */
    public ConfirmHandler(final WetatorEngine aWetatorEngine) {
      wetatorEngine = aWetatorEngine;
      chooseOkOnNextConfirmFor(null);
    }

    @Override
    public boolean handleConfirm(final Page aPage, final String aConfirmationMessage) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("handleConfirm " + aConfirmationMessage);
      }
      String tmpConfirmationMessage = "";
      if (StringUtils.isNotEmpty(aConfirmationMessage)) {
        tmpConfirmationMessage = aConfirmationMessage;
      }
      String tmpUrl = "";
      try {
        tmpUrl = aPage.getWebResponse().getWebRequest().getUrl().toExternalForm();
      } catch (final NullPointerException e) { // NOPMD
        // ignore
      }
      wetatorEngine.informListenersInfo("javascriptConfirm", tmpConfirmationMessage, tmpUrl);

      try {
        if (null != message) {
          message.matches(aConfirmationMessage, MAX_LENGTH);
        }

        if (result) {
          wetatorEngine.informListenersInfo("javascriptConfirmOk", tmpConfirmationMessage);
        } else {
          wetatorEngine.informListenersInfo("javascriptConfirmCancel", tmpConfirmationMessage);
        }
        return result;
      } catch (final AssertionException e) {
        final String tmpMessage = Messages.getMessage("confirmationMessageDoesNotMatch", e.getMessage());
        wetatorEngine.getBrowser().addFailure(new AssertionException(tmpMessage, e));

        wetatorEngine.informListenersInfo("javascriptConfirmOk", tmpConfirmationMessage);
        return false;
      } finally {
        // reset for the next call
        chooseOkOnNextConfirmFor(null);
      }
    }

    /**
     * Prepare for the next confirm handler.
     *
     * @param aMessagePattern the expected text
     */
    void chooseOkOnNextConfirmFor(final ContentPattern aMessagePattern) {
      message = aMessagePattern;
      result = true;
    }

    /**
     * Prepare for the next confirm handler.
     *
     * @param aMessagePattern the expected text
     */
    void chooseCancelOnNextConfirmFor(final ContentPattern aMessagePattern) {
      message = aMessagePattern;
      result = false;
    }
  }

  /**
   * Our own WebConsole logger.
   */
  public static class WebConsoleLogger implements org.htmlunit.WebConsole.Logger {
    private WetatorEngine wetatorEngine;

    /**
     * Constructor.
     *
     * @param aWetatorEngine the engine to inform about the alert texts.
     */
    public WebConsoleLogger(final WetatorEngine aWetatorEngine) {
      wetatorEngine = aWetatorEngine;
    }

    @Override
    public void trace(final Object aMessage) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Console [trace]: " + aMessage);
      }

      wetatorEngine.informListenersInfo("ConsoleTrace", aMessage);
    }

    @Override
    public void debug(final Object aMessage) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Console [debug]: " + aMessage);
      }

      wetatorEngine.informListenersInfo("ConsoleDebug", aMessage);
    }

    @Override
    public void info(final Object aMessage) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Console [info]: " + aMessage);
      }

      wetatorEngine.informListenersInfo("ConsoleInfo", aMessage);
    }

    @Override
    public void warn(final Object aMessage) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Console [warn]: " + aMessage);
      }

      wetatorEngine.informListenersInfo("ConsoleWarn", aMessage);
    }

    @Override
    public void error(final Object aMessage) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Console [error]: " + aMessage);
      }

      wetatorEngine.informListenersInfo("ConsoleError", aMessage);
    }

    @Override
    public boolean isTraceEnabled() {
      return true;
    }

    @Override
    public boolean isDebugEnabled() {
      return true;
    }

    @Override
    public boolean isInfoEnabled() {
      return true;
    }

    @Override
    public boolean isWarnEnabled() {
      return true;
    }

    @Override
    public boolean isErrorEnabled() {
      return true;
    }
  }

  /**
   * Our own IncorrectnessListener.
   */
  public static class IncorrectnessListener implements org.htmlunit.IncorrectnessListener {
    private WetatorEngine wetatorEngine;

    /**
     * Constructor.
     *
     * @param aWetatorEngine the engine to inform about the alert texts.
     */
    public IncorrectnessListener(final WetatorEngine aWetatorEngine) {
      wetatorEngine = aWetatorEngine;
    }

    @Override
    public void notify(final String aMessage, final Object anOrigin) {
      LOG.warn("Incorrectness: " + aMessage + " (detected by: " + anOrigin + ")");

      wetatorEngine.informListenersWarn("Incorrectness", new Object[] { aMessage, anOrigin }, (String) null);
    }
  }

  /**
   * Prepare for the next confirm handler.
   *
   * @param aMessagePattern the expected text
   */
  public void chooseOkOnNextConfirmFor(final ContentPattern aMessagePattern) {
    final ConfirmHandler tmpHandler = (ConfirmHandler) webClient.getConfirmHandler();
    tmpHandler.chooseOkOnNextConfirmFor(aMessagePattern);
  }

  /**
   * Prepare for the next confirm handler.
   *
   * @param aMessagePattern the expected text
   */
  public void chooseCancelOnNextConfirmFor(final ContentPattern aMessagePattern) {
    final ConfirmHandler tmpHandler = (ConfirmHandler) webClient.getConfirmHandler();
    tmpHandler.chooseCancelOnNextConfirmFor(aMessagePattern);
  }

  @Override
  public void closeWindow(final SecretString aWindowName) throws ActionException {
    final List<WebWindow> tmpWebWindows = webClient.getWebWindows();
    if (tmpWebWindows.isEmpty()) {
      final String tmpMessage = Messages.getMessage("noWindowToClose");
      throw new ActionException(tmpMessage);
    }

    if (null == aWindowName || aWindowName.isEmpty()) {
      for (int i = tmpWebWindows.size() - 1; i >= 0; i--) {
        final WebWindow tmpWebWindow = tmpWebWindows.get(i);

        if (tmpWebWindow instanceof TopLevelWindow) {
          wetatorEngine.informListenersInfo("closeWindow", tmpWebWindow.getName());
          ((TopLevelWindow) tmpWebWindow).close();
          return;
        }
        if (tmpWebWindow instanceof DialogWindow) {
          wetatorEngine.informListenersInfo("closeDialogWindow", tmpWebWindow.getName());
          ((DialogWindow) tmpWebWindow).close();
          return;
        }
      }
      final String tmpMessage = Messages.getMessage("noWindowToClose");
      throw new ActionException(tmpMessage);
    }

    final SearchPattern tmpWindowNamePattern = aWindowName.getSearchPattern();
    for (int i = tmpWebWindows.size() - 1; i > 0; i--) {
      final WebWindow tmpWebWindow = tmpWebWindows.get(i);

      final String tmpWindowName = tmpWebWindow.getName();
      if (tmpWindowNamePattern.matches(tmpWindowName)) {
        if (tmpWebWindow instanceof TopLevelWindow) {
          wetatorEngine.informListenersInfo("closeWindow", tmpWindowName);
          ((TopLevelWindow) tmpWebWindow).close();
          return;
        }
        if (tmpWebWindow instanceof DialogWindow) {
          wetatorEngine.informListenersInfo("closeDialogWindow", tmpWindowName);
          ((DialogWindow) tmpWebWindow).close();
          return;
        }
      }
    }
    final String tmpMessage = Messages.getMessage("noWindowByNameToClose", aWindowName);
    throw new ActionException(tmpMessage);
  }

  @Override
  public void goBackInCurrentWindow(final int aSteps) throws ActionException {
    WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

    if (null == tmpCurrentWindow) {
      final String tmpMessage = Messages.getMessage("noWebWindow");
      throw new ActionException(tmpMessage);
    }

    // sometimes the current window in HtmlUnit is an
    // iFrame; but we need the topmost one
    tmpCurrentWindow = tmpCurrentWindow.getTopWindow();
    final History tmpHistory = tmpCurrentWindow.getHistory();

    final int tmpIndexPos = tmpHistory.getIndex() - aSteps;
    if (tmpIndexPos >= tmpHistory.getLength() || tmpIndexPos < 0) {
      final String tmpMessage = Messages.getMessage("outsideHistory", aSteps, tmpIndexPos, tmpHistory.getLength());
      throw new ActionException(tmpMessage);
    }

    try {
      tmpHistory.go(-1 * aSteps);
    } catch (final IOException e) {
      final String tmpMessage = Messages.getMessage("historyFailed", e.getMessage());
      throw new ActionException(tmpMessage);
    }
  }

  @Override
  public void saveCurrentWindowToLog(final IControl... aControls) {
    WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

    if (null != tmpCurrentWindow) {
      try {
        // sometimes the current window in HtmlUnit is an
        // iFrame; but we need the topmost one
        tmpCurrentWindow = tmpCurrentWindow.getTopWindow();
        final Page tmpPage = tmpCurrentWindow.getEnclosedPage();
        if (null != tmpPage) {
          final ResponseStore tmpResponseStore = getResponseStore(webClient.getBrowserVersion());
          String tmpPageFile = tmpResponseStore.storePage(webClient, tmpPage);
          savedPages.put(tmpPage, tmpPageFile);

          // highlight changed control if possible
          final StringBuilder tmpParam = new StringBuilder();
          if (null != aControls) {
            String tmpDelim = "";
            for (final IControl tmpControl : aControls) {
              @SuppressWarnings("unchecked")
              final HtmlUnitBaseControl<HtmlElement> tmpHtmlUnitControl = (HtmlUnitBaseControl<HtmlElement>) tmpControl;
              if (tmpHtmlUnitControl.isPartOf(tmpPage)) {
                final String tmpSelector = tmpControl.getUniqueSelector();
                if (null != tmpSelector) {
                  // @formatter:off
                  tmpParam.append("highlight=")
                          .append(URLEncoder.encode(tmpControl.getUniqueSelector(), "ASCII"))
                          .append(tmpDelim);
                  tmpDelim = "&";
                  // @formatter:on
                }
              }
            }
          }

          if (tmpParam.length() > 0) {
            tmpPageFile = new StringBuilder().append(tmpPageFile).append('?').append(tmpParam).toString();
          }
          wetatorEngine.informListenersResponseStored(tmpPageFile);
        }
      } catch (final ResourceException e) {
        LOG.warn("Saving page failed!", e);
      } catch (final Throwable e) {
        LOG.fatal("Problem with window handling. Saving page failed!", e);
      }
    }
  }

  @Override
  public void markControls(final IControl... aControls) {
    WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

    if (null != tmpCurrentWindow) {
      try {
        // sometimes the current window in HtmlUnit is an
        // iFrame; but we need the topmost one
        tmpCurrentWindow = tmpCurrentWindow.getTopWindow();
        final Page tmpPage = tmpCurrentWindow.getEnclosedPage();
        if (null != tmpPage) {
          String tmpPageFile = savedPages.get(tmpPage);
          if (null != tmpPageFile) {
            final StringBuilder tmpParam = new StringBuilder();
            if (null != aControls) {
              String tmpDelim = "";
              for (final IControl tmpControl : aControls) {
                // TODO do we have to make sure the control is related to the page?
                // @formatter:off
                tmpParam.append("highlight=")
                        .append(tmpDelim)
                        .append(URLEncoder.encode(tmpControl.getUniqueSelector(), "ASCII"));
                tmpDelim = "&";
                // @formatter:on
              }
            }

            if (tmpParam.length() > 0) {
              tmpPageFile = new StringBuilder().append(tmpPageFile).append('?').append(tmpParam).toString();
            }
            wetatorEngine.informListenersHighlightedResponse(tmpPageFile);
          }
        }
      } catch (final ResourceException e) {
        LOG.warn("Saving page failed!", e);
      } catch (final Throwable e) {
        LOG.fatal("Problem with window handling. Saving page failed!", e);
      }
    }
  }

  private ResponseStore getResponseStore(final BrowserVersion aBrowserVersion) {
    return responseStores.get(aBrowserVersion);
  }

  /**
   * Our own listener for window content changes.
   */
  public static final class WebWindowListener implements org.htmlunit.WebWindowListener {
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
        htmlUnitBrowser.savedPages.remove(tmpPage);
        if (LOG.isDebugEnabled()) {
          LOG.debug("webWindowClosed: (url '"
              + anEvent.getWebWindow().getEnclosedPage().getWebResponse().getWebRequest().getUrl() + "')");
        }
      }
    }

    @Override
    public void webWindowContentChanged(final WebWindowEvent anEvent) {
      LOG.debug("webWindowContentChanged");
      if (null != anEvent.getOldPage()) {
        htmlUnitBrowser.savedPages.remove(anEvent.getOldPage());
      }
    }
  }

  /**
   * Ignore some jobs (like heartbeat).
   */
  public static final class JavaScriptJobFilter implements JavaScriptJobManager.JavaScriptJobFilter {

    @SuppressWarnings("hiding")
    private static final Logger LOG = LogManager.getLogger(JavaScriptJobFilter.class);

    private List<SearchPattern> patterns;
    private boolean isDebugEnabled;

    /**
     * The constructor.
     */
    public JavaScriptJobFilter() {
      super();
      patterns = new ArrayList<>();

    }

    /**
     * Add a pattern to the list of suppression patterns.
     *
     * @param aPattern the pattern to add
     */
    public void addPattern(final SearchPattern aPattern) {
      patterns.add(aPattern);
    }

    @Override
    public boolean passes(final JavaScriptJob aJob) {
      final String tmpJob = aJob.toString().replace("\n", "").replace("\r", "");
      for (final SearchPattern tmpPattern : patterns) {
        if (tmpPattern.matches(tmpJob)) {
          if (isDebugEnabled && LOG.isDebugEnabled()) {
            LOG.debug("JsJob filtered out: '" + tmpJob + "'"); // NOPMD
          }
          return false;
        }
      }
      return true;
    }
  }

  private Page getCurrentPage() throws BackendException {
    WebWindow tmpWebWindow = webClient.getCurrentWindow();
    if (null == tmpWebWindow) {
      final String tmpMessage = Messages.getMessage("noWebWindow");
      throw new BackendException(tmpMessage);
    }

    // sometimes the current window in HtmlUnit is an
    // iFrame; but we need the topmost one
    tmpWebWindow = tmpWebWindow.getTopWindow();
    final Page tmpPage = tmpWebWindow.getEnclosedPage();
    if (null == tmpPage) {
      final String tmpMessage = Messages.getMessage("noPageInWebWindow");
      throw new BackendException(tmpMessage);
    }

    return tmpPage;
  }

  private BrowserVersion determineBrowserVersionFor(final IBrowser.BrowserType aBrowserType) {
    if (IBrowser.BrowserType.FIREFOX_ESR == aBrowserType) {
      return BrowserVersion.FIREFOX_ESR;
    }
    if (IBrowser.BrowserType.FIREFOX == aBrowserType) {
      return BrowserVersion.FIREFOX;
    }
    if (IBrowser.BrowserType.CHROME == aBrowserType) {
      return BrowserVersion.CHROME;
    }
    if (IBrowser.BrowserType.EDGE == aBrowserType) {
      return BrowserVersion.EDGE;
    }

    return BrowserVersion.FIREFOX_ESR;
  }

  /**
   * Returns the current HtmlPage.
   *
   * @return the current HtmlPage
   * @throws BackendException if there is no current page or the current page is not an HtmlPage
   */
  public HtmlPage getCurrentHtmlPage() throws BackendException {
    final Page tmpPage = getCurrentPage();
    if (tmpPage.isHtmlPage()) {
      return (HtmlPage) tmpPage;
    }

    final String tmpMessage = Messages.getMessage("noHtmlPage", tmpPage.getClass());
    throw new BackendException(tmpMessage);
  }

  @Override
  public IControlFinder getControlFinder() throws BackendException {
    final HtmlPage tmpHtmlPage = getCurrentHtmlPage();

    return new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
  }

  @Override
  public IControl getFocusedControl() throws BackendException {
    final DomElement tmpDomElement = getCurrentHtmlPage().getFocusedElement();
    if (tmpDomElement instanceof HtmlElement) {
      final HtmlElement tmpHtmlElement = (HtmlElement) tmpDomElement;
      final Class<? extends HtmlUnitBaseControl<?>> tmpControlClass = controlRepository
          .getForHtmlElement(tmpHtmlElement);
      if (tmpControlClass != null) {
        try {
          return tmpControlClass.getConstructor(tmpHtmlElement.getClass()).newInstance(tmpHtmlElement);
        } catch (final Exception e) {
          wetatorEngine.informListenersWarn("createFocusedControlError",
              new String[] { tmpControlClass.getName(), tmpHtmlElement.getClass().getName() }, e);
        }
      }
    }
    return null;
  }

  @Override
  public boolean waitForImmediateJobs() throws BackendException {
    return waitForImmediateJobs(jsTimeoutInMillis);
  }

  @Override
  public boolean waitForImmediateJobs(final long aTimeoutInMillis) throws BackendException {
    boolean tmpPendingJobs = false;

    Page tmpPage = getCurrentPage();
    long tmpEndTime = System.currentTimeMillis() + aTimeoutInMillis;
    if (tmpPage.isHtmlPage()) {
      // try with wait
      long tmpNow;
      while ((tmpNow = System.currentTimeMillis()) < tmpEndTime) {
        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;

        tmpPendingJobs = areJobsPendig(tmpHtmlPage, tmpEndTime - tmpNow);
        if (tmpPendingJobs) {
          continue;
        }

        if (tmpPage == getCurrentPage()) {
          break;
        }

        // current page is changed, we have to make at least one try
        // reset the timeout
        tmpPage = getCurrentPage();
        if (!tmpPage.isHtmlPage()) {
          break;
        }
        tmpEndTime = System.currentTimeMillis() + aTimeoutInMillis;
      }
    }

    // handle animationFrames
    final Window tmpWin = tmpPage.getEnclosingWindow().getTopWindow().getScriptableObject();

    // TODO replace the hard coded second
    final long tmpTimeout = Math.max(1000, tmpEndTime - System.currentTimeMillis());
    tmpEndTime = System.currentTimeMillis() + tmpTimeout;
    int tmpPendingAnimationFrames = tmpWin.animateAnimationsFrames();
    while (tmpPendingAnimationFrames > 0 && System.currentTimeMillis() < tmpEndTime) {
      tmpPendingAnimationFrames = tmpWin.animateAnimationsFrames();
    }

    if (tmpPendingAnimationFrames > 0) {
      wetatorEngine.informListenersWarn("stillAnimataionFramesPending",
          new Object[] { tmpTimeout / 1000d, tmpPendingAnimationFrames }, (String) null);
    }

    if (tmpPendingJobs && tmpPage.isHtmlPage()) {
      wetatorEngine.informListenersWarn("stillJobsPending", new Object[] { aTimeoutInMillis / 1000d },
          ((HtmlPage) tmpPage).getEnclosingWindow().getJobManager().jobStatusDump(jobFilter));
      return true;
    }
    return false;
  }

  private boolean areJobsPendig(final HtmlPage aHtmlPage, final long anDuration) {
    final JavaScriptJobManager tmpJobManager = aHtmlPage.getEnclosingWindow().getJobManager();

    final int tmpJobCount = tmpJobManager.waitForJobsStartingBefore(anDuration, jobFilter);
    if (tmpJobCount > 0) {
      return true;
    }

    for (final FrameWindow tmpFrameWindow : aHtmlPage.getFrames()) {
      final Page tmpPage = tmpFrameWindow.getEnclosedPage();
      if (tmpPage.isHtmlPage() && areJobsPendig((HtmlPage) tmpPage, anDuration)) {
        return true;
      }
    }
    return false;
  }

  private int areJobsActive(final HtmlPage aHtmlPage) {
    final JavaScriptJobManager tmpJobManager = aHtmlPage.getEnclosingWindow().getJobManager();

    int tmpJobCount = tmpJobManager.getJobCount(jobFilter);
    if (tmpJobCount > 0) {
      return tmpJobCount;
    }

    for (final FrameWindow tmpFrameWindow : aHtmlPage.getFrames()) {
      final Page tmpPage = tmpFrameWindow.getEnclosedPage();
      if (tmpPage.isHtmlPage()) {
        tmpJobCount = areJobsActive((HtmlPage) tmpFrameWindow.getEnclosedPage());
        if (tmpJobCount > 0) {
          return tmpJobCount;
        }
      }
    }
    return 0;
  }

  @Override
  public boolean assertTitleInTimeFrame(final ContentPattern aTitleToWaitFor, final long aTimeoutInSeconds)
      throws AssertionException {
    final long tmpWaitTime = Math.max(jsTimeoutInMillis, aTimeoutInSeconds * 1000L);

    boolean tmpPageChanged = false;

    try {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + tmpWaitTime;

      Page tmpPage;

      // only true if the user has specified a wait time
      while (System.currentTimeMillis() < tmpEndTime) {
        tmpPage = getCurrentPage();
        if (!tmpPage.isHtmlPage()) {
          break;
        }

        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;
        final String tmpCurrentTitle = tmpHtmlPage.getTitleText();

        try {
          aTitleToWaitFor.matches(tmpCurrentTitle, MAX_LENGTH);
          // warn also in case of match to be consistent
          final int tmpJobCount = areJobsActive(tmpHtmlPage);
          if (tmpJobCount > 0) {
            wetatorEngine.informListenersWarn("stillJobsActive",
                new Object[] { jsTimeoutInMillis / 1000d, tmpJobCount },
                ((HtmlPage) tmpPage).getEnclosingWindow().getJobManager().jobStatusDump(jobFilter));
          }
          return tmpPageChanged;
        } catch (final AssertionException e) { // NOPMD
          // ok, not found, maybe we have to be more patient
        }

        tmpPageChanged = true;

        // wait for the jobs running in the next second
        if (areJobsPendig(tmpHtmlPage, 1)) {
          // page was changed in between, the new page has
          // at least a timeout of jsTimeout
          if (tmpPage != getCurrentPage()) {
            tmpEndTime = Math.max(tmpEndTime, System.currentTimeMillis() + jsTimeoutInMillis);
          }
          continue;
        }

        if (tmpPage == getCurrentPage()) {
          break;
        }
        // page was changed in between, the new page has
        // at least a timeout of jsTimeout
        tmpEndTime = Math.max(tmpEndTime, System.currentTimeMillis() + jsTimeoutInMillis);
      }

      tmpPage = getCurrentPage();

      if (tmpPage.isHtmlPage()) {
        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;

        // inform if there are still pending js jobs
        final int tmpJobCount = areJobsActive(tmpHtmlPage);
        if (tmpJobCount > 0) {
          wetatorEngine.informListenersWarn("stillJobsActive", new Object[] { jsTimeoutInMillis / 1000d, tmpJobCount },
              ((HtmlPage) tmpPage).getEnclosingWindow().getJobManager().jobStatusDump(jobFilter));
        }

        final String tmpCurrentTitle = tmpHtmlPage.getTitleText();
        aTitleToWaitFor.matches(tmpCurrentTitle, MAX_LENGTH);
      }

      final ContentType tmpContentType = ContentTypeUtil.getContentType(tmpPage);
      final WebResponse tmpResponse = tmpPage.getWebResponse();

      if (ContentType.PDF == tmpContentType) {
        try {
          final String tmpNormalizedTitle = ContentUtil.getPdfTitleAsString(tmpResponse.getContentAsStream());
          aTitleToWaitFor.matches(tmpNormalizedTitle, MAX_LENGTH);
          return tmpPageChanged;
        } catch (final IOException e) {
          Assert.fail("pdfConversionToTextFailed", e.getMessage());
          return tmpPageChanged;
        }
      }

      // content type without title
      Assert.fail("assertTitleUnsupportedContent", tmpContentType);

    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("browserBackendError", e.getMessage());
      throw new AssertionException(tmpMessage, e);
    }

    return tmpPageChanged;
  }

  @Override
  public boolean assertContentInTimeFrame(final ContentPattern aContentToWaitFor, final long aTimeoutInSeconds)
      throws AssertionException {
    final long tmpWaitTime = Math.max(jsTimeoutInMillis, aTimeoutInSeconds * 1000L);

    boolean tmpPageChanged = false;

    try {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + tmpWaitTime;

      Page tmpPage;

      // only true if the user has specified a wait time
      while (System.currentTimeMillis() < tmpEndTime) {
        tmpPage = getCurrentPage();
        if (!tmpPage.isHtmlPage()) {
          break;
        }

        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;

        try {
          final String tmpNormalizedContent = new HtmlPageIndex(tmpHtmlPage).getText();
          try {
            aContentToWaitFor.matches(tmpNormalizedContent, MAX_LENGTH);

            // warn also in case of match to be consistent
            final int tmpJobCount = areJobsActive(tmpHtmlPage);
            if (tmpJobCount > 0) {
              wetatorEngine.informListenersWarn("stillJobsActive",
                  new Object[] { jsTimeoutInMillis / 1000d, tmpJobCount },
                  ((HtmlPage) tmpPage).getEnclosingWindow().getJobManager().jobStatusDump(jobFilter));
            }
            return tmpPageChanged;
          } catch (final AssertionException e) { // NOPMD
            // ok, not found, maybe we have to be more patient
          }
        } catch (final IllegalStateException e) {
          // no javascript running/scheduled, so this is a real problem
          // Note: there is no API to ask for the currently running jobs only
          if (areJobsActive(tmpHtmlPage) > 0) {
            throw e;
          }

          // in some cases the page will be right now replaced by javascript;
          // ignore the exception and make another try
          wetatorEngine.informListenersWarn("pageIndexFailed", new String[] { e.getMessage() }, e);
        }

        tmpPageChanged = true;
        // wait for the jobs running in the next second
        if (areJobsPendig(tmpHtmlPage, 1)) {
          // page was changed in between, the new page has
          // at least a timeout of jsTimeout
          if (tmpPage != getCurrentPage()) {
            tmpEndTime = Math.max(tmpEndTime, System.currentTimeMillis() + jsTimeoutInMillis);
          }
          continue;
        }

        if (tmpPage == getCurrentPage()) {
          // current page is unchanged, another wait cycle makes
          // no sense because there is no javascript that will run
          break;
        }
        // page was changed in between, the new page has
        // at least a timeout of jsTimeout
        tmpEndTime = Math.max(tmpEndTime, System.currentTimeMillis() + jsTimeoutInMillis);
      }

      tmpPage = getCurrentPage();

      if (tmpPage.isHtmlPage()) {
        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;

        // inform if there are still pending js jobs
        final int tmpJobCount = areJobsActive(tmpHtmlPage);
        if (tmpJobCount > 0) {
          wetatorEngine.informListenersWarn("stillJobsActive", new Object[] { jsTimeoutInMillis / 1000d, tmpJobCount },
              ((HtmlPage) tmpPage).getEnclosingWindow().getJobManager().jobStatusDump(jobFilter));
        }

        final String tmpNormalizedContent = new HtmlPageIndex(tmpHtmlPage).getText();
        matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
        return tmpPageChanged;
      }

      if (tmpPage instanceof XmlPage) {
        final XmlPage tmpXmlPage = (XmlPage) tmpPage;
        final String tmpXmlContent = tmpXmlPage.getWebResponse().getContentAsString();
        final String tmpNormalizedContent = new NormalizedString(tmpXmlContent).toString();
        matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
        return tmpPageChanged;
      }

      if (tmpPage instanceof TextPage) {
        final TextPage tmpTextPage = (TextPage) tmpPage;
        final String tmpNormalizedContent = tmpTextPage.getContent();
        matchesWithLog(aContentToWaitFor, new NormalizedString(tmpNormalizedContent).toString());
        return tmpPageChanged;
      }

      final ContentType tmpContentType = ContentTypeUtil.getContentType(tmpPage);
      final WebResponse tmpResponse = tmpPage.getWebResponse();

      if (ContentType.PDF == tmpContentType) {
        try {
          final String tmpNormalizedContent = ContentUtil.getPdfContentAsString(tmpResponse.getContentAsStream(),
              MAX_LENGTH);
          matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
          return tmpPageChanged;
        } catch (final IOException e) {
          Assert.fail("pdfConversionToTextFailed", e.getMessage());
          return tmpPageChanged;
        }
      }

      if (ContentType.XLS == tmpContentType || ContentType.XLSX == tmpContentType) {
        String tmpNormalizedContent = "";
        try {
          final String tmpAcceptLangHeader = tmpPage.getWebResponse().getWebRequest().getAdditionalHeaders()
              .get("Accept-Language");
          final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader(tmpAcceptLangHeader);
          tmpNormalizedContent = ContentUtil.getExcelContentAsString(tmpResponse.getContentAsStream(), tmpLocale,
              MAX_LENGTH);
        } catch (final IOException | InvalidFormatException e) {
          // some server send csv files with xls mime type
          // so lets make another try
          try {
            tmpNormalizedContent = ContentUtil.getTxtContentAsString(tmpResponse.getContentAsStream(),
                tmpResponse.getContentCharset(), MAX_LENGTH);

            if (ContentUtil.isTxt(tmpNormalizedContent)) {
              wetatorEngine.informListenersWarn("xlsConversionToTextFailed", new String[] { e.getMessage() }, e);
              matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
              return tmpPageChanged;
            }
          } catch (final IOException eAsString) {
            Assert.fail("xlsConversionToTextFailed", eAsString.getMessage());
          }
          Assert.fail("xlsConversionToTextFailed", e.getMessage());
        }
        matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
        return tmpPageChanged;
      }

      if (ContentType.DOCX == tmpContentType) {
        try {
          final String tmpNormalizedContent = ContentUtil.getWordContentAsString(tmpResponse.getContentAsStream(),
              MAX_LENGTH);
          matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
          return tmpPageChanged;
        } catch (final IOException | InvalidFormatException e) {
          Assert.fail("docConversionToTextFailed", e.getMessage());
          return tmpPageChanged;
        }
      }

      if (ContentType.RTF == tmpContentType) {
        try {
          final String tmpNormalizedContent = ContentUtil.getRtfContentAsString(tmpResponse.getContentAsStream(),
              MAX_LENGTH);
          matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
          return tmpPageChanged;
        } catch (final IOException | BadLocationException e) {
          Assert.fail("rtfConversionToTextFailed", e.getMessage());
          return tmpPageChanged;
        }
      }

      if (ContentType.TEXT == tmpContentType) {
        try {
          final String tmpNormalizedContent = ContentUtil.getTxtContentAsString(tmpResponse.getContentAsStream(),
              tmpResponse.getContentCharset(), MAX_LENGTH);
          matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
          return tmpPageChanged;
        } catch (final IOException e) {
          Assert.fail("txtConversionToTextFailed", e.getMessage());
          return tmpPageChanged;
        }
      }

      if (ContentType.ZIP == tmpContentType) {
        try {
          final String tmpAcceptLangHeader = tmpPage.getWebResponse().getWebRequest().getAdditionalHeaders()
              .get("Accept-Language");
          final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader(tmpAcceptLangHeader);
          final String tmpNormalizedContent = ContentUtil.getZipContentAsString(tmpResponse.getContentAsStream(),
              tmpResponse.getContentCharset(), tmpLocale, MAX_LENGTH);
          matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
          return tmpPageChanged;
        } catch (final IOException e) {
          Assert.fail("zipConversionToTextFailed", e.getMessage());
          return tmpPageChanged;
        }
      }

      // unsupported content type
      // warn and process the content as plain ascii
      final Charset tmpCharset = tmpResponse.getContentCharset();
      wetatorEngine.informListenersInfo("unsupportedPageType", tmpPage.getWebResponse().getContentType(),
          tmpCharset.name());
      try {
        final String tmpNormalizedContent = ContentUtil.getTxtContentAsString(tmpResponse.getContentAsStream(),
            tmpCharset, MAX_LENGTH);
        matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
        return tmpPageChanged;
      } catch (final IOException e) {
        Assert.fail("txtConversionToTextFailed", e.getMessage());
        return tmpPageChanged;
      }
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("browserBackendError", e.getMessage());
      throw new AssertionException(tmpMessage, e);
    }
  }

  private void matchesWithLog(final ContentPattern aContentToWaitFor, final String aContent) throws AssertionException {
    try {
      aContentToWaitFor.matches(aContent, MAX_LENGTH);
    } catch (final AssertionException e) {
      if (aContent.length() > MAX_LENGTH) {
        final String tmpPageFile = getResponseStore(webClient.getBrowserVersion()).storeTextContent(aContent);
        wetatorEngine.informListenersResponseStored(tmpPageFile);
      }
      throw e;
    }
  }

  @Override
  public void addFailure(final String aMessageKey, final Object[] aParameters, final Throwable aCause) {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameters);
    final AssertionException tmpFailure = new AssertionException(tmpMessage, aCause);
    addFailure(tmpFailure);
  }

  @Override
  public void addFailure(final AssertionException aFailure) {
    synchronized (failures) {
      failures.add(aFailure);
    }
  }

  @Override
  public AssertionException checkAndResetFailures() {
    synchronized (failures) {
      if (failures.isEmpty()) {
        return null;
      }

      final AssertionException tmpResult = failures.get(0);
      for (final AssertionException tmpException : failures) {
        final Throwable tmpCause = tmpException.getCause();
        wetatorEngine.informListenersWarn("pageError", new String[] { tmpException.getMessage() }, tmpCause);
      }
      failures.clear();
      return tmpResult;
    }
  }

  @Override
  public URL getBookmark(final String aBookmarkName) {
    return bookmarks.get(aBookmarkName);
  }

  @Override
  public void saveBookmark(final String aBookmarkName, final URL aBookmarkUrl) {
    bookmarks.put(aBookmarkName, aBookmarkUrl);
  }

  @Override
  public void bookmarkPage(final String aBookmarkName) throws ActionException {
    try {
      final URL tmpUrl = getCurrentPage().getWebResponse().getWebRequest().getUrl();
      saveBookmark(aBookmarkName, tmpUrl);
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("browserBackendError", e.getMessage());
      throw new ActionException(tmpMessage, e);
    }
  }
}
