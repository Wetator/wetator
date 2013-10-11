/*
 * Copyright (c) 2008-2013 wetator.org
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


package org.wetator.util.onejar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Groups the one-jar context-related functionality in one place.
 * See http://sourceforge.net/p/one-jar/bugs/72/
 */
public final class OneJarContext {
  private static final String PROTOCOL_HANDLER_PROPERTY = "java.protocol.handler.pkgs";

  private static boolean isOneJarAvailable;

  static {
    OneJarContext.setOneJarAvailable(isOneJarProtocolHandlerAvailable());
    URL.setURLStreamHandlerFactory(new OneJarAwareUrlStreamHandlerFactory());
  }

  private static void setOneJarAvailable(final boolean anAvailableFlag) {
    OneJarContext.isOneJarAvailable = anAvailableFlag;
  }

  private static boolean isOneJarAvailable() {
    return isOneJarAvailable;
  }

  private static boolean isOneJarProtocolHandlerAvailable() {
    final String tmpOriginalValue = System.getProperty(PROTOCOL_HANDLER_PROPERTY, "");
    final Set<String> tmpPackages = new LinkedHashSet<String>(Arrays.asList(tmpOriginalValue.split("\\|")));
    tmpPackages.add("com.simontuffs");
    System.setProperty(PROTOCOL_HANDLER_PROPERTY, StringUtils.join(tmpPackages, "|"));

    try {
      new URL("onejar:dummy");
      return true;
    } catch (final MalformedURLException e) {
      return false;
    }
  }

  /**
   * Initialize the one-jar context.
   */
  public static void init() {
    // no operation - calling this method enforces running the static initializer, which in
    // turn makes sure the URLStreamHandlerFactory is only set once.
  }

  private OneJarContext() {
    // avoid instantiation
  }

  /**
   * URL stream handler factory that knows about one-jar.
   */
  static class OneJarAwareUrlStreamHandlerFactory implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(final String aProtocol) {
      if (!OneJarContext.isOneJarAvailable() || !"jar".equals(aProtocol)) {
        return null;
      }

      return new URLStreamHandler() {
        @Override
        protected URLConnection openConnection(final URL anUrl) throws IOException {
          final URL tmpUrl = new URL("onejar", null, anUrl.getPort(), removeJarPrefixes(anUrl.getFile()));
          return tmpUrl.openConnection();
        }

        private String removeJarPrefixes(final String aPath) {
          return aPath.replaceFirst(".*!", "");
        }
      };
    }
  }
}