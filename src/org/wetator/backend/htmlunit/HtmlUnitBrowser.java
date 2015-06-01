/*
 * Copyright (c) 2008-2015 wetator.org
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
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.text.BadLocationException;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.DialogWindow;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WaitingRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConsole.Logger;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.DebuggerImpl;
import com.gargoylesoftware.htmlunit.javascript.HtmlUnitContextFactory;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJob;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * The HtmlUnit backend.
 *
 * @author rbri
 * @author frank.danek
 */
public final class HtmlUnitBrowser implements IBrowser {
  private static final Log LOG = LogFactory.getLog(HtmlUnitBrowser.class);

  private static final int MAX_LENGTH = 4000;

  /** The maximum history size. */
  protected static final int MAX_HISTORY_SIZE = 15;

  /** Htmlunit WebClient. */
  protected WebClient webClient;
  /** Sometimes we like to ignore some jobs. */
  protected JavaScriptJobFilter jobFilter;
  /** ResponseStore. */
  protected Map<BrowserVersion, ResponseStore> responseStores;
  /** WetatorEngine. */
  protected WetatorEngine wetatorEngine;
  /** The list of failures ({@link AssertionException}s). */
  protected List<AssertionException> failures;
  /** jsTimeout. */
  protected long jsTimeoutInMillis;
  /** The map containing the bookmarks. */
  protected Map<String, URL> bookmarks;
  /** Cache of saved pages. */
  protected WeakHashMap<Page, String> savedPages;

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

    savedPages = new WeakHashMap<Page, String>();

    failures = new LinkedList<AssertionException>();
    wetatorEngine = aWetatorEngine;

    // response store
    final WetatorConfiguration tmpConfiguration = wetatorEngine.getConfiguration();
    jsTimeoutInMillis = tmpConfiguration.getJsTimeoutInSeconds() * 1000L;
    responseStores = new HashMap<BrowserVersion, ResponseStore>();
    for (final BrowserType tmpBrowserType : tmpConfiguration.getBrowserTypes()) {
      final ResponseStore tmpStrore = new ResponseStore(tmpConfiguration.getOutputDir(), tmpBrowserType.getLabel(),
          true);
      responseStores.put(determineBrowserVersionFor(tmpBrowserType), tmpStrore);
    }

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
    // at first take care there is now other session active
    endSession();

    final WetatorConfiguration tmpConfiguration = wetatorEngine.getConfiguration();

    // reset the bookmarks
    bookmarks = new HashMap<String, URL>();

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

        tmpCredentialProvider.addCredentials(tmpUser, tmpPassword, tmpHost, tmpPort, null);
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

      tmpCredentialProvider.addCredentials(tmpUser, tmpPassword);
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

      tmpCredentialProvider.addNTLMCredentials(tmpUser, tmpPassword, null, -1, tmpWorkstation, tmpDomain);

      LOG.info("NTLM enabled  user '" + tmpUser + "' workstation '" + tmpWorkstation + "' domain '" + tmpDomain + "'.");
    }

    // setup our listener
    webClient.addWebWindowListener(new WebWindowListener(this));
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

    // set the timeout
    webClient.getOptions().setTimeout(tmpConfiguration.getHttpTimeoutInSeconds() * 1000);

    // debug stuff
    if (tmpConfiguration.isLogEnabled()) {
      final HtmlUnitContextFactory tmpContextFactory = webClient.getJavaScriptEngine().getContextFactory();
      tmpContextFactory.setDebugger(new DebuggerImpl());
    }
    // webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    // WebClientUtils.attachVisualDebugger(webClient);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#endSession()
   */
  @Override
  public void endSession() {
    if (null != webClient) {
      // TODO maybe we have to do more here
      try {
        // unset the onbeforeunload handler to avoid it interfering
        webClient.setOnbeforeunloadHandler(null);

        webClient.close();
      } catch (final ScriptException e) {
        LOG.warn("Could not close previous window.", e);
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#openUrl(java.net.URL)
   */
  @Override
  public void openUrl(final URL aUrl) throws ActionException {
    try {
      webClient.getPage(aUrl);
      waitForImmediateJobs();
    } catch (final ScriptException e) {
      addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      addFailure("javascriptError", new String[] { tmpScriptException.getMessage() }, tmpScriptException);
    } catch (final FailingHttpStatusCodeException e) {
      final String tmpMessage = Messages
          .getMessage("openServerError", new String[] { aUrl.toString(), e.getMessage() });
      throw new ActionException(tmpMessage, e);
    } catch (final UnknownHostException e) {
      final String tmpMessage = Messages.getMessage("unknownHostError",
          new String[] { aUrl.toString(), e.getMessage() });
      throw new ActionException(tmpMessage, e);
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("openBackendError", new String[] { e.getMessage() });
      throw new ActionException(tmpMessage, e);
    } catch (final Throwable e) {
      LOG.error("OpenUrl '" + aUrl.toExternalForm() + "' fails. " + e.getMessage());
      final String tmpMessage = Messages
          .getMessage("openServerError", new String[] { aUrl.toString(), e.getMessage() });
      throw new ActionException(tmpMessage, e);
    }

    try {
      final String tmpRef = aUrl.getRef();
      if (StringUtils.isNotEmpty(tmpRef)) {
        checkAnchor(tmpRef);
      }
    } catch (final AssertionException e) {
      // we are in an action so build the correct exception
      throw new ActionException(e.getMessage(), e.getCause());
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("openBackendError", new String[] { e.getMessage() });
      throw new ActionException(tmpMessage, e);
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
   * Our own confirm handler.
   */
  public static final class ConfirmHandler implements com.gargoylesoftware.htmlunit.ConfirmHandler {
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
      LOG.debug("handleConfirm " + aConfirmationMessage);
      String tmpConfirmationMessage = "";
      if (StringUtils.isNotEmpty(aConfirmationMessage)) {
        tmpConfirmationMessage = aConfirmationMessage;
      }
      String tmpUrl = "";
      try {
        tmpUrl = aPage.getWebResponse().getWebRequest().getUrl().toExternalForm();
      } catch (final NullPointerException e) {
        // ignore
      }
      wetatorEngine.informListenersInfo("javascriptConfirm", new String[] { tmpConfirmationMessage, tmpUrl });

      try {
        if (null != message) {
          message.matches(aConfirmationMessage, MAX_LENGTH);
        }

        if (result) {
          wetatorEngine.informListenersInfo("javascriptConfirmOk", new String[] { tmpConfirmationMessage });
        } else {
          wetatorEngine.informListenersInfo("javascriptConfirmCancel", new String[] { tmpConfirmationMessage });
        }
        return result;
      } catch (final AssertionException e) {
        final String tmpMessage = Messages.getMessage("confirmationMessageDoesNotMatch",
            new String[] { e.getMessage() });
        wetatorEngine.getBrowser().addFailure(new AssertionException(tmpMessage, e));

        wetatorEngine.informListenersInfo("javascriptConfirmOk", new String[] { tmpConfirmationMessage });
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
    protected void chooseOkOnNextConfirmFor(final ContentPattern aMessagePattern) {
      message = aMessagePattern;
      result = true;
    }

    /**
     * Prepare for the next confirm handler.
     *
     * @param aMessagePattern the expected text
     */
    protected void chooseCancelOnNextConfirmFor(final ContentPattern aMessagePattern) {
      message = aMessagePattern;
      result = false;
    }
  }

  /**
   * Our own WebConsole logger.
   */
  public static class WebConsoleLogger implements Logger {
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
      LOG.debug("Console [trace]: " + aMessage);

      wetatorEngine.informListenersInfo("ConsoleTrace", new String[] { aMessage.toString() });
    }

    @Override
    public void debug(final Object aMessage) {
      LOG.debug("Console [debug]: " + aMessage);

      wetatorEngine.informListenersInfo("ConsoleDebug", new String[] { aMessage.toString() });
    }

    @Override
    public void info(final Object aMessage) {
      LOG.debug("Console [info]: " + aMessage);

      wetatorEngine.informListenersInfo("ConsoleInfo", new String[] { aMessage.toString() });
    }

    @Override
    public void warn(final Object aMessage) {
      LOG.debug("Console [warn]: " + aMessage);

      wetatorEngine.informListenersInfo("ConsoleWarn", new String[] { aMessage.toString() });
    }

    @Override
    public void error(final Object aMessage) {
      LOG.debug("Console [error]: " + aMessage);

      wetatorEngine.informListenersInfo("ConsoleError", new String[] { aMessage.toString() });
    }
  }

  /**
   * Our own IncorrectnessListener.
   */
  public static class IncorrectnessListener implements com.gargoylesoftware.htmlunit.IncorrectnessListener {
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

      wetatorEngine.informListenersWarn("Incorrectness", new String[] { aMessage, anOrigin.toString() }, (String) null);
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
  public void closeWindow(final SecretString aWindowName) throws ActionException {
    final List<WebWindow> tmpWebWindows = webClient.getWebWindows();
    if (tmpWebWindows.isEmpty()) {
      final String tmpMessage = Messages.getMessage("noWindowToClose", null);
      throw new ActionException(tmpMessage);
    }

    if (null == aWindowName || aWindowName.isEmpty()) {
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
      final String tmpMessage = Messages.getMessage("noWindowToClose", null);
      throw new ActionException(tmpMessage);
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
    final String tmpMessage = Messages.getMessage("noWindowByNameToClose", new String[] { aWindowName.toString() });
    throw new ActionException(tmpMessage);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#goBackInCurrentWindow(int)
   */
  @Override
  public void goBackInCurrentWindow(final int aSteps) throws ActionException {
    WebWindow tmpCurrentWindow = webClient.getCurrentWindow();

    if (null == tmpCurrentWindow) {
      final String tmpMessage = Messages.getMessage("noWebWindow", null);
      throw new ActionException(tmpMessage);
    }

    // sometimes the current window in HtmlUnit is an
    // iFrame; but we need the topmost one
    tmpCurrentWindow = tmpCurrentWindow.getTopWindow();
    final History tmpHistory = tmpCurrentWindow.getHistory();

    final int tmpIndexPos = tmpHistory.getIndex() - aSteps;
    if (tmpIndexPos >= tmpHistory.getLength() || tmpIndexPos < 0) {
      final String tmpMessage = Messages.getMessage(
          "outsideHistory",
          new String[] { Integer.toString(aSteps), Integer.toString(tmpIndexPos),
              Integer.toString(tmpHistory.getLength()) });
      throw new ActionException(tmpMessage);
    }

    try {
      tmpHistory.go(-1 * aSteps);
    } catch (final IOException e) {
      final String tmpMessage = Messages.getMessage("historyFailed", new String[] { e.getMessage() });
      throw new ActionException(tmpMessage);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#saveCurrentWindowToLog(IControl...)
   */
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
          String tmpPageFile = getResponseStore(webClient.getBrowserVersion()).storePage(webClient, tmpPage);
          savedPages.put(tmpPage, tmpPageFile);

          // highlight changed control if possible
          final StringBuilder tmpParam = new StringBuilder();
          if (null != aControls) {
            String tmpDelim = "";
            for (final IControl tmpControl : aControls) {
              @SuppressWarnings("unchecked")
              final HtmlUnitBaseControl<HtmlElement> tmpHtmlUnitControl = (HtmlUnitBaseControl<HtmlElement>) tmpControl;
              if (tmpHtmlUnitControl.isPartOf(tmpPage)) {
                tmpParam.append("highlight=");
                tmpParam.append(URLEncoder.encode(tmpControl.getUniqueSelector(), "ASCII"));
                tmpParam.append(tmpDelim);
                tmpDelim = "&";
              }
            }
          }

          if (tmpParam.length() > 0) {
            tmpPageFile = tmpPageFile + "?" + tmpParam.toString();
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

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#markControls(org.wetator.backend.control.IControl[])
   */
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
                tmpParam.append("highlight=");
                tmpParam.append(URLEncoder.encode(tmpControl.getUniqueSelector(), "ASCII"));
                tmpParam.append(tmpDelim);
                tmpDelim = "&";
              }
            }

            if (tmpParam.length() > 0) {
              tmpPageFile = tmpPageFile + "?" + tmpParam.toString();
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
   * Checks if the url contains a hash, that the matching anchor is on the page.
   *
   * @param aRef the hash from the url
   * @throws AssertionException if no matching anchor found
   * @throws BackendException if there is no current page
   */
  protected void checkAnchor(final String aRef) throws AssertionException, BackendException {
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
        htmlUnitBrowser.savedPages.remove(tmpPage);
        LOG.debug("webWindowClosed: (url '"
            + anEvent.getWebWindow().getEnclosedPage().getWebResponse().getWebRequest().getUrl() + "')");
      }
    }

    @Override
    public void webWindowContentChanged(final WebWindowEvent anEvent) {
      LOG.debug("webWindowContentChanged");
      if (null != anEvent.getOldPage()) {
        htmlUnitBrowser.savedPages.remove(anEvent.getOldPage());
      }

      final Page tmpNewPage = anEvent.getNewPage();
      // first load into a new window
      if (null != tmpNewPage && null == anEvent.getOldPage()) {
        final URL tmpUrl = tmpNewPage.getWebResponse().getWebRequest().getUrl();
        final String tmpRef = tmpUrl.getRef();
        if (StringUtils.isNotEmpty(tmpRef)) {
          try {
            PageUtil.checkAnchor(tmpRef, tmpNewPage);
          } catch (final AssertionException e) {
            // TODO this is now inconsistent because open-url and click-on for an anchor throw an ActionException (as
            // they are actions)
            htmlUnitBrowser.addFailure(e);
          }
        }
      }
    }
  }

  /**
   * Ignore some jobs (like heartbeat).
   */
  public static final class JavaScriptJobFilter implements
      com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager.JavaScriptJobFilter {

    @SuppressWarnings("hiding")
    private static final Log LOG = LogFactory.getLog(JavaScriptJobFilter.class);

    private List<SearchPattern> patterns;
    private boolean isDebugEnabled;

    /**
     * The constructor.
     */
    public JavaScriptJobFilter() {
      super();
      patterns = new ArrayList<SearchPattern>();

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
          if (isDebugEnabled) {
            LOG.debug("JsJob filtered out: '" + tmpJob + "'");
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
      final String tmpMessage = Messages.getMessage("noWebWindow", null);
      throw new BackendException(tmpMessage);
    }

    // sometimes the current window in HtmlUnit is an
    // iFrame; but we need the topmost one
    tmpWebWindow = tmpWebWindow.getTopWindow();
    final Page tmpPage = tmpWebWindow.getEnclosedPage();
    if (null == tmpPage) {
      final String tmpMessage = Messages.getMessage("noPageInWebWindow", null);
      throw new BackendException(tmpMessage);
    }

    return tmpPage;
  }

  private BrowserVersion determineBrowserVersionFor(final IBrowser.BrowserType aBrowserType) {
    if (IBrowser.BrowserType.FIREFOX_31 == aBrowserType) {
      return BrowserVersion.FIREFOX_31;
    }
    if (IBrowser.BrowserType.FIREFOX_31 == aBrowserType) {
      return BrowserVersion.FIREFOX_38;
    }
    if (IBrowser.BrowserType.INTERNET_EXPLORER_8 == aBrowserType) {
      return BrowserVersion.INTERNET_EXPLORER_8;
    }
    if (IBrowser.BrowserType.INTERNET_EXPLORER_11 == aBrowserType) {
      return BrowserVersion.INTERNET_EXPLORER_11;
    }
    if (IBrowser.BrowserType.CHROME == aBrowserType) {
      return BrowserVersion.CHROME;
    }
    return BrowserVersion.FIREFOX_38;
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

    final String tmpMessage = Messages.getMessage("noHtmlPage", new String[] { tmpPage.getClass().toString() });
    throw new BackendException(tmpMessage);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#getControlFinder()
   */
  @Override
  public IControlFinder getControlFinder() throws BackendException {
    final HtmlPage tmpHtmlPage = getCurrentHtmlPage();

    return new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#getFocusedControl()
   */
  @Override
  public IControl getFocusedControl() throws BackendException {
    final HtmlElement tmpHtmlElement = getCurrentHtmlPage().getFocusedElement();
    if (tmpHtmlElement != null) {
      final Class<? extends HtmlUnitBaseControl<?>> tmpControlClass = controlRepository
          .getForHtmlElement(tmpHtmlElement);
      if (tmpControlClass != null) {
        try {
          return tmpControlClass.getConstructor(tmpHtmlElement.getClass()).newInstance(tmpHtmlElement);
        } catch (final Exception e) {
          wetatorEngine.informListenersWarn("createFocusedControlError", new String[] { tmpControlClass.getName(),
              tmpHtmlElement.getClass().getName() }, e);
        }
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#waitForImmediateJobs()
   */
  @Override
  public boolean waitForImmediateJobs() throws BackendException {
    return waitForImmediateJobs(jsTimeoutInMillis);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#waitForImmediateJobs(long)
   */
  @Override
  public boolean waitForImmediateJobs(final long aTimeoutInMillis) throws BackendException {
    boolean tmpPendingJobs = false;

    Page tmpPage = getCurrentPage();
    if (tmpPage.isHtmlPage()) {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + aTimeoutInMillis;
      while (System.currentTimeMillis() < tmpEndTime) {
        final HtmlPage tmpHtmlPage = (HtmlPage) tmpPage;

        tmpPendingJobs = areJobsPendig(tmpHtmlPage, System.currentTimeMillis() - tmpEndTime);
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

    if (tmpPendingJobs && tmpPage.isHtmlPage()) {
      wetatorEngine.informListenersWarn("stillJobsPending", new String[] { Long.toString(aTimeoutInMillis / 1000) },
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

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#assertTitleInTimeFrame(org.wetator.core.searchpattern.ContentPattern, long)
   */
  @Override
  public boolean assertTitleInTimeFrame(final ContentPattern aTitleToWaitFor, final long aTimeoutInSeconds)
      throws AssertionException {
    final long tmpWaitTime = Math.max(jsTimeoutInMillis, aTimeoutInSeconds * 1000L);

    boolean tmpPageChanged = false;

    try {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + tmpWaitTime;

      Page tmpPage = null;

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
                new String[] { Long.toString(jsTimeoutInMillis / 1000) }, ((HtmlPage) tmpPage).getEnclosingWindow()
                    .getJobManager().jobStatusDump(jobFilter));
          }
          return tmpPageChanged;
        } catch (final AssertionException e) {
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
          wetatorEngine.informListenersWarn("stillJobsActive",
              new String[] { Long.toString(jsTimeoutInMillis / 1000) }, ((HtmlPage) tmpPage).getEnclosingWindow()
                  .getJobManager().jobStatusDump(jobFilter));
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
          Assert.fail("pdfConversionToTextFailed", new String[] { e.getMessage() });
          return tmpPageChanged;
        }
      }

      // content type without title
      Assert.fail("assertTitleUnsupportedContent", new String[] { tmpContentType.toString() });

    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("browserBackendError", new String[] { e.getMessage() });
      throw new AssertionException(tmpMessage, e);
    }

    return tmpPageChanged;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#assertContentInTimeFrame(org.wetator.core.searchpattern.ContentPattern, long)
   */
  @Override
  public boolean assertContentInTimeFrame(final ContentPattern aContentToWaitFor, final long aTimeoutInSeconds)
      throws AssertionException {
    final long tmpWaitTime = Math.max(jsTimeoutInMillis, aTimeoutInSeconds * 1000L);

    boolean tmpPageChanged = false;

    try {
      // try with wait
      long tmpEndTime = System.currentTimeMillis() + tmpWaitTime;

      Page tmpPage = null;

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
                  new String[] { Long.toString(jsTimeoutInMillis / 1000) }, ((HtmlPage) tmpPage).getEnclosingWindow()
                      .getJobManager().jobStatusDump(jobFilter));
            }
            return tmpPageChanged;
          } catch (final AssertionException e) {
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
          wetatorEngine.informListenersWarn("stillJobsActive",
              new String[] { Long.toString(jsTimeoutInMillis / 1000) }, ((HtmlPage) tmpPage).getEnclosingWindow()
                  .getJobManager().jobStatusDump(jobFilter));
        }

        final String tmpNormalizedContent = new HtmlPageIndex(tmpHtmlPage).getText();
        matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
        return tmpPageChanged;
      }

      if (tmpPage instanceof XmlPage) {
        final XmlPage tmpXmlPage = (XmlPage) tmpPage;
        final String tmpNormalizedContent = new NormalizedString(tmpXmlPage.getContent()).toString();
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
          Assert.fail("pdfConversionToTextFailed", new String[] { e.getMessage() });
          return tmpPageChanged;
        }
      }

      if (ContentType.XLS == tmpContentType) {
        String tmpNormalizedContent = "";
        try {
          final String tmpAcceptLangHeader = tmpPage.getWebResponse().getWebRequest().getAdditionalHeaders()
              .get("Accept-Language");
          final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader(tmpAcceptLangHeader);
          tmpNormalizedContent = ContentUtil.getXlsContentAsString(tmpResponse.getContentAsStream(), tmpLocale,
              MAX_LENGTH);
        } catch (final IOException e) {
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
            Assert.fail("xlsConversionToTextFailed", new String[] { eAsString.getMessage() });
          }
          Assert.fail("xlsConversionToTextFailed", new String[] { e.getMessage() });
        }
        matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
        return tmpPageChanged;
      }

      if (ContentType.RTF == tmpContentType) {
        try {
          final String tmpNormalizedContent = ContentUtil.getRtfContentAsString(tmpResponse.getContentAsStream(),
              MAX_LENGTH);
          matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
          return tmpPageChanged;
        } catch (final IOException e) {
          Assert.fail("rtfConversionToTextFailed", new String[] { e.getMessage() });
          return tmpPageChanged;
        } catch (final BadLocationException e) {
          Assert.fail("rtfConversionToTextFailed", new String[] { e.getMessage() });
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
          Assert.fail("txtConversionToTextFailed", new String[] { e.getMessage() });
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
          Assert.fail("zipConversionToTextFailed", new String[] { e.getMessage() });
          return tmpPageChanged;
        }
      }

      // unsupported content type
      // warn and process the content as plain ascii
      final String tmpCharset = tmpResponse.getContentCharset();
      wetatorEngine.informListenersInfo("unsupportedPageType", new String[] {
          tmpPage.getWebResponse().getContentType(), tmpCharset });
      try {
        final String tmpNormalizedContent = ContentUtil.getTxtContentAsString(tmpResponse.getContentAsStream(),
            tmpCharset, MAX_LENGTH);
        matchesWithLog(aContentToWaitFor, tmpNormalizedContent);
        return tmpPageChanged;
      } catch (final IOException e) {
        Assert.fail("txtConversionToTextFailed", new String[] { e.getMessage() });
        return tmpPageChanged;
      }
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("browserBackendError", new String[] { e.getMessage() });
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

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#addFailure(java.lang.String, java.lang.Object[], java.lang.Throwable)
   */
  @Override
  public void addFailure(final String aMessageKey, final Object[] aParameterArray, final Throwable aCause) {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    final AssertionException tmpFailure = new AssertionException(tmpMessage, aCause);
    addFailure(tmpFailure);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#addFailure(java.lang.String, java.lang.Object[], java.lang.Throwable)
   */
  @Override
  public void addFailure(final AssertionException aFailure) {
    failures.add(aFailure);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.IBrowser#checkAndResetFailures()
   */
  @Override
  public AssertionException checkAndResetFailures() {
    if (failures.isEmpty()) {
      return null;
    }

    final AssertionException tmpResult = failures.get(0);
    for (final AssertionException tmpException : failures) {
      final Throwable tmpCause = tmpException.getCause();
      wetatorEngine.informListenersWarn("pageError", new String[] { tmpException.getMessage() }, tmpCause);
    }
    failures = new LinkedList<AssertionException>();
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
   * @see org.wetator.backend.IBrowser#bookmarkPage(String)
   */
  @Override
  public void bookmarkPage(final String aBookmarkName) throws ActionException {
    try {
      final URL tmpUrl = getCurrentPage().getWebResponse().getWebRequest().getUrl();
      saveBookmark(aBookmarkName, tmpUrl);
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("browserBackendError", new String[] { e.getMessage() });
      throw new ActionException(tmpMessage, e);
    }
  }
}
