/*
 * Copyright (c) 2008-2021 wetator.org
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

//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//


package org.wetator.test.jetty;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;

import org.eclipse.jetty.http.MultiPartFormInputStream;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.LazyList;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.StringUtil;

/* ------------------------------------------------------------ */
/**
 * Multipart Form Data Filter.
 * <p>
 * This class decodes the multipart/form-data stream sent by a HTML form that uses a file input item. Any files sent are
 * stored to a temporary file and a File object added to the request as an attribute. All other values are made
 * available via the normal getParameter API and the setCharacterEncoding mechanism is respected when converting bytes
 * to Strings.
 * <p>
 * If the init parameter "delete" is set to "true", any files created will be deleted when the current request returns.
 * <p>
 * The init parameter maxFormKeys sets the maximum number of keys that may be present in a form (default set by system
 * property org.eclipse.jetty.server.Request.maxFormKeys or 1000) to protect against DOS attacks by bad hash keys.
 * <p>
 * The init parameter deleteFiles controls if uploaded files are automatically deleted after the request completes. Use
 * init parameter "maxFileSize" to set the max size file that can be uploaded. Use init parameter "maxRequestSize" to
 * limit the size of the multipart request. <br>
 * Modified for Wetator test: Now also the byte[] created if no filename is given is stored as a request attribute. So
 * we can access it via the attributes as byte[] and not only via the parameters as automatically converted string. (see
 * 'WETATOR modified')
 *
 * @author the jetty team
 * @author frank.danek
 */
public class MultiPartFilter implements Filter {

  public static final String CONTENT_TYPE_SUFFIX = ".org.eclipse.jetty.servlet.contentType";
  private static final String MULTIPART = "org.eclipse.jetty.servlet.MultiPartFile.multiPartInputStream";
  private File tempDir;
  private boolean deleteFiles;
  private ServletContext context;
  private int fileOutputBuffer;
  private long maxFileSize = -1L;
  private long maxRequestSize = -1L;
  private int maxFormKeys = Integer.getInteger("org.eclipse.jetty.server.Request.maxFormKeys", 1000).intValue();

  /* ------------------------------------------------------------------------------- */
  /**
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  @Override
  public void init(final FilterConfig aFilterConfig) throws ServletException {
    tempDir = (File) aFilterConfig.getServletContext().getAttribute("javax.servlet.context.tempdir");
    deleteFiles = "true".equals(aFilterConfig.getInitParameter("deleteFiles"));
    final String tmpFileOutputBuffer = aFilterConfig.getInitParameter("fileOutputBuffer");
    if (tmpFileOutputBuffer != null) {
      fileOutputBuffer = Integer.parseInt(tmpFileOutputBuffer);
    }
    final String tmpMaxFileSize = aFilterConfig.getInitParameter("maxFileSize");
    if (tmpMaxFileSize != null) {
      maxFileSize = Long.parseLong(tmpMaxFileSize.trim());
    }
    final String tmpMaxRequestSize = aFilterConfig.getInitParameter("maxRequestSize");
    if (tmpMaxRequestSize != null) {
      maxRequestSize = Long.parseLong(tmpMaxRequestSize.trim());
    }

    context = aFilterConfig.getServletContext();
    final String tmpMaxFormKeys = aFilterConfig.getInitParameter("maxFormKeys");
    if (tmpMaxFormKeys != null) {
      maxFormKeys = Integer.parseInt(tmpMaxFormKeys);
    }
  }

  /* ------------------------------------------------------------------------------- */
  /**
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
   *      javax.servlet.FilterChain)
   */
  @Override
  public void doFilter(final ServletRequest aRequest, final ServletResponse aResponse, final FilterChain aChain)
      throws IOException, ServletException {
    final HttpServletRequest tmpRequest = (HttpServletRequest) aRequest;
    if (tmpRequest.getContentType() == null || !tmpRequest.getContentType().startsWith("multipart/form-data")) {
      aChain.doFilter(aRequest, aResponse);
      return;
    }

    final InputStream tmpInputStream = new BufferedInputStream(aRequest.getInputStream());
    final String tmpContentType = tmpRequest.getContentType();

    // Get current parameters so we can merge into them
    final MultiMap<String> tmpParameters = new MultiMap<>();
    for (Map.Entry<String, String[]> tmpEntry : aRequest.getParameterMap().entrySet()) {
      final String[] tmpValue = tmpEntry.getValue();
      tmpParameters.addValues(tmpEntry.getKey(), tmpValue);
    }

    final MultipartConfigElement tmpConfig = new MultipartConfigElement(tempDir.getCanonicalPath(), maxFileSize,
        maxRequestSize, fileOutputBuffer);
    final MultiPartFormInputStream tmpMultiPartInputStream = new MultiPartFormInputStream(tmpInputStream,
        tmpContentType, tmpConfig, tempDir);
    aRequest.setAttribute(MULTIPART, tmpMultiPartInputStream);

    try {
      final Collection<Part> tmpParts = tmpMultiPartInputStream.getParts();
      if (tmpParts != null) {
        final Iterator<Part> tmpIterator = tmpParts.iterator();
        while (tmpIterator.hasNext() && tmpParameters.size() < maxFormKeys) {
          final Part tmpPart = tmpIterator.next();
          final MultiPartFormInputStream.MultiPart tmpMultiPart = (MultiPartFormInputStream.MultiPart) tmpPart;
          if (tmpMultiPart.getFile() != null) {
            aRequest.setAttribute(tmpMultiPart.getName(), tmpMultiPart.getFile());
            if (tmpMultiPart.getContentDispositionFilename() != null) {
              tmpParameters.add(tmpMultiPart.getName(), tmpMultiPart.getContentDispositionFilename());
              if (tmpMultiPart.getContentType() != null) {
                tmpParameters.add(tmpMultiPart.getName() + CONTENT_TYPE_SUFFIX, tmpMultiPart.getContentType());
              }
            }
          } else {
            final ByteArrayOutputStream tmpBytes = new ByteArrayOutputStream();
            IO.copy(tmpPart.getInputStream(), tmpBytes);
            tmpParameters.add(tmpPart.getName(), new String(tmpBytes.toByteArray()));
            if (tmpPart.getContentType() != null) {
              tmpParameters.add(tmpPart.getName() + CONTENT_TYPE_SUFFIX, tmpPart.getContentType());
            }

            // WETATOR modified - we add the raw byte[] to the request even if we have no filename
            tmpRequest.setAttribute(tmpPart.getName(), tmpBytes.toByteArray());
          }
        }
      }

      // handle request
      aChain.doFilter(new Wrapper(tmpRequest, tmpParameters), aResponse);
    } finally {
      deleteFiles(aRequest);
    }
  }

  /* ------------------------------------------------------------ */
  private void deleteFiles(final ServletRequest aRequest) {
    if (!deleteFiles) {
      return;
    }

    final MultiPartFormInputStream tmpMultiPartInputStream = (MultiPartFormInputStream) aRequest
        .getAttribute(MULTIPART);
    if (tmpMultiPartInputStream != null) {
      try {
        tmpMultiPartInputStream.deleteParts();
      } catch (final Exception e) {
        context.log("Error deleting multipart tmp files", e);
      }
    }
    aRequest.removeAttribute(MULTIPART);
  }

  /* ------------------------------------------------------------------------------- */
  /**
   * @see javax.servlet.Filter#destroy()
   */
  @Override
  public void destroy() {
  }

  /* ------------------------------------------------------------------------------- */
  /* ------------------------------------------------------------------------------- */
  /**
   * @author the jetty team
   * @author frank.danek
   */
  private static class Wrapper extends HttpServletRequestWrapper {
    String encoding = StringUtil.__UTF8;
    MultiMap<String> parameters;

    /* ------------------------------------------------------------------------------- */
    /**
     * Constructor.
     *
     * @param aRequest the request to wrap
     * @param aMap the parameter map
     */
    Wrapper(final HttpServletRequest aRequest, final MultiMap<String> aMap) {
      super(aRequest);
      parameters = aMap;
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    @Override
    public int getContentLength() {
      return 0;
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(final String aName) {
      Object tmpObject = parameters.get(aName);
      if (!(tmpObject instanceof byte[]) && LazyList.size(tmpObject) > 0) {
        tmpObject = LazyList.get(tmpObject, 0);
      }

      if (tmpObject instanceof byte[]) {
        try {
          final String tmpString = new String((byte[]) tmpObject, encoding);
          return tmpString;
        } catch (final Exception e) {
          e.printStackTrace();
        }
      } else if (tmpObject != null) {
        return String.valueOf(tmpObject);
      }
      return null;
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Map getParameterMap() {
      final Map<String, String[]> tmpMap = new HashMap<>();

      for (Object tmpKey : parameters.keySet()) {
        final String[] tmpArray = LazyList.toStringArray(getParameter((String) tmpKey));
        tmpMap.put((String) tmpKey, tmpArray);
      }

      return Collections.unmodifiableMap(tmpMap);
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    @Override
    public Enumeration<String> getParameterNames() {
      return Collections.enumeration(parameters.keySet());
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(final String aName) {
      final List<?> tmpValues = parameters.getValues(aName);
      if (tmpValues == null || tmpValues.size() == 0) {
        return new String[0];
      }
      final String[] tmpValueArray = new String[tmpValues.size()];
      for (int i = 0; i < tmpValues.size(); i++) {
        final Object tmpObject = tmpValues.get(i);
        if (tmpObject instanceof byte[]) {
          try {
            tmpValueArray[i] = new String((byte[]) tmpObject, encoding);
          } catch (final Exception e) {
            throw new RuntimeException(e);
          }
        } else if (tmpObject instanceof String) {
          tmpValueArray[i] = (String) tmpObject;
        }
      }
      return tmpValueArray;
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(final String anEncoding) throws UnsupportedEncodingException {
      encoding = anEncoding;
    }
  }
}
