// ========================================================================
// Copyright (c) 1996-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses.
// ========================================================================


package org.rbri.wet.test.jetty;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.eclipse.jetty.util.LazyList;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;

/* ------------------------------------------------------------ */
/**
 * Multipart Form Data Filter.
 * <p>
 * This class decodes the multipart/form-data stream sent by a HTML form that uses a file input item. Any files sent are
 * stored to a tempary file and a File object added to the request as an attribute. All other values are made available
 * via the normal getParameter API and the setCharacterEncoding mechanism is respected when converting bytes to Strings.
 * If the init paramter "delete" is set to "true", any files created will be deleted when the current request returns. <br/>
 * <br/>
 * Modified for Wetator test: Now also the byte[] created if no filename is given is stored as a request attribute. So
 * we can access it via the attributes as byte[] and not only via the parameters as automatically converted string.
 * 
 * @author the jetty team
 * @author frank.danek
 */
public class MultiPartFilter implements Filter {

  private static final String FILES = "org.eclipse.jetty.servlet.MultiPartFilter.files";
  private File tempDir;
  private boolean deleteFiles;
  private ServletContext context;
  private int fileOutputBuffer;

  /* ------------------------------------------------------------------------------- */
  /**
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  @Override
  public void init(FilterConfig aFilterConfig) throws ServletException {
    tempDir = (File) aFilterConfig.getServletContext().getAttribute("javax.servlet.context.tempdir");
    deleteFiles = "true".equals(aFilterConfig.getInitParameter("deleteFiles"));
    String tmpFileOutputBuffer = aFilterConfig.getInitParameter("fileOutputBuffer");
    if (tmpFileOutputBuffer != null) {
      fileOutputBuffer = Integer.parseInt(tmpFileOutputBuffer);
    }
    context = aFilterConfig.getServletContext();
  }

  /* ------------------------------------------------------------------------------- */
  /**
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
   *      javax.servlet.FilterChain)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void doFilter(ServletRequest aRequest, ServletResponse aResponse, FilterChain aChain) throws IOException,
      ServletException {
    HttpServletRequest tmpRequest = (HttpServletRequest) aRequest;
    if (tmpRequest.getContentType() == null || !tmpRequest.getContentType().startsWith("multipart/form-data")) {
      aChain.doFilter(aRequest, aResponse);
      return;
    }

    BufferedInputStream tmpInputStream = new BufferedInputStream(aRequest.getInputStream());
    String tmpContentType = tmpRequest.getContentType();

    // ToDo - handle encodings

    String tmpBoundary = "--" + value(tmpContentType.substring(tmpContentType.indexOf("boundary=")));
    byte[] tmpByteBoundary = (tmpBoundary + "--").getBytes(StringUtil.__ISO_8859_1);

    MultiMap<String> tmpParameters = new MultiMap<String>();
    for (Iterator<Map.Entry<String, Object>> tmpIterator = aRequest.getParameterMap().entrySet().iterator(); tmpIterator
        .hasNext();) {
      Map.Entry<String, Object> tmpEntry = tmpIterator.next();
      Object tmpValue = tmpEntry.getValue();
      if (tmpValue instanceof String[]) {
        tmpParameters.addValues(tmpEntry.getKey(), (String[]) tmpValue);
      } else {
        tmpParameters.add(tmpEntry.getKey(), tmpValue);
      }
    }

    try {
      // Get first boundary
      byte[] tmpBytes = TypeUtil.readLine(tmpInputStream);
      String tmpLine;
      if (tmpBytes == null) {
        tmpLine = null;
      } else {
        tmpLine = new String(tmpBytes, "UTF-8");
      }
      if (tmpLine == null || !tmpLine.equals(tmpBoundary)) {
        throw new IOException("Missing initial multi part boundary");
      }

      // Read each part
      boolean tmpLastPart = false;
      String tmpContentDisposition = null;

      outer: while (!tmpLastPart) {
        while (true) {
          // read a line
          tmpBytes = TypeUtil.readLine(tmpInputStream);
          if (tmpBytes == null) {
            break outer;
          }

          // If blank line, end of part headers
          if (tmpBytes.length == 0) {
            break;
          }
          tmpLine = new String(tmpBytes, "UTF-8");

          // place part header key and value in map
          int tmpColonPosition = tmpLine.indexOf(':', 0);
          if (tmpColonPosition > 0) {
            String tmpKey = tmpLine.substring(0, tmpColonPosition).trim().toLowerCase();
            String tmpValue = tmpLine.substring(tmpColonPosition + 1, tmpLine.length()).trim();
            if ("content-disposition".equals(tmpKey)) {
              tmpContentDisposition = tmpValue;
            }
          }
        }
        // Extract content-disposition
        boolean tmpFormData = false;
        if (tmpContentDisposition == null) {
          throw new IOException("Missing content-disposition");
        }

        StringTokenizer tmpTokenizer = new StringTokenizer(tmpContentDisposition, ";");
        String tmpName = null;
        String tmpFileName = null;
        while (tmpTokenizer.hasMoreTokens()) {
          String tmpToken = tmpTokenizer.nextToken().trim();
          String tmpLowerToken = tmpToken.toLowerCase();
          if (tmpToken.startsWith("form-data")) {
            tmpFormData = true;
          } else if (tmpLowerToken.startsWith("name=")) {
            tmpName = value(tmpToken);
          } else if (tmpLowerToken.startsWith("filename=")) {
            tmpFileName = value(tmpToken);
          }
        }

        // Check disposition
        if (!tmpFormData) {
          continue;
        }
        // It is valid for reset and submit buttons to have an empty name.
        // If no name is supplied, the browser skips sending the info for that field.
        // However, if you supply the empty string as the name, the browser sends the
        // field, with name as the empty string. So, only continue this loop if we
        // have not yet seen a name field.
        if (tmpName == null) {
          continue;
        }

        OutputStream tmpOutputStream = null;
        File tmpFile = null;
        try {
          if (tmpFileName != null && tmpFileName.length() > 0) {
            tmpFile = File.createTempFile("MultiPart", "", tempDir);
            tmpOutputStream = new FileOutputStream(tmpFile);
            if (fileOutputBuffer > 0) {
              tmpOutputStream = new BufferedOutputStream(tmpOutputStream, fileOutputBuffer);
            }
            aRequest.setAttribute(tmpName, tmpFile);
            tmpParameters.add(tmpName, tmpFileName);

            if (deleteFiles) {
              tmpFile.deleteOnExit();
              List<File> tmpFiles = (ArrayList<File>) aRequest.getAttribute(FILES);
              if (tmpFiles == null) {
                tmpFiles = new ArrayList<File>();
                aRequest.setAttribute(FILES, tmpFiles);
              }
              tmpFiles.add(tmpFile);
            }

          } else {
            tmpOutputStream = new ByteArrayOutputStream();
          }

          int tmpState = -2;
          int tmpChar;
          boolean tmpCR = false;
          boolean tmpLF = false;

          // loop for all lines`
          while (true) {
            int tmpByteLength = 0;
            if (tmpState != -2) {
              tmpChar = tmpState;
            } else {
              tmpChar = tmpInputStream.read();
            }
            while (tmpChar != -1) {
              tmpState = -2;
              // look for CR and/or LF
              if (tmpChar == 13 || tmpChar == 10) {
                if (tmpChar == 13) {
                  tmpState = tmpInputStream.read();
                }
                break;
              }
              // look for boundary
              if (tmpByteLength >= 0 && tmpByteLength < tmpByteBoundary.length
                  && tmpChar == tmpByteBoundary[tmpByteLength]) {
                tmpByteLength++;
              } else {
                // this is not a boundary
                if (tmpCR) {
                  tmpOutputStream.write(13);
                }
                if (tmpLF) {
                  tmpOutputStream.write(10);
                }
                tmpCR = false;
                tmpLF = false;
                if (tmpByteLength > 0) {
                  tmpOutputStream.write(tmpByteBoundary, 0, tmpByteLength);
                }
                tmpByteLength = -1;
                tmpOutputStream.write(tmpChar);
              }
              if (tmpState != -2) {
                tmpChar = tmpState;
              } else {
                tmpChar = tmpInputStream.read();
              }
            }
            // check partial boundary
            if ((tmpByteLength > 0 && tmpByteLength < tmpByteBoundary.length - 2)
                || (tmpByteLength == tmpByteBoundary.length - 1)) {
              if (tmpCR) {
                tmpOutputStream.write(13);
              }
              if (tmpLF) {
                tmpOutputStream.write(10);
              }
              tmpCR = false;
              tmpLF = false;
              tmpOutputStream.write(tmpByteBoundary, 0, tmpByteLength);
              tmpByteLength = -1;
            }
            // boundary match
            if (tmpByteLength > 0 || tmpChar == -1) {
              if (tmpByteLength == tmpByteBoundary.length) {
                tmpLastPart = true;
              }
              if (tmpState == 10) {
                tmpState = -2;
              }
              break;
            }
            // handle CR LF
            if (tmpCR) {
              tmpOutputStream.write(13);
            }
            if (tmpLF) {
              tmpOutputStream.write(10);
            }
            tmpCR = tmpChar == 13;
            tmpLF = tmpChar == 10 || tmpState == 10;
            if (tmpState == 10) {
              tmpState = -2;
            }
          }
        } finally {
          tmpOutputStream.close();
        }

        if (tmpFile == null) {
          tmpBytes = ((ByteArrayOutputStream) tmpOutputStream).toByteArray();
          tmpParameters.add(tmpName, tmpBytes);
          aRequest.setAttribute(tmpName, tmpBytes);
        }
      }

      // handle request
      aChain.doFilter(new Wrapper(tmpRequest, tmpParameters), aResponse);
    } finally {
      deleteFiles(aRequest);
    }
  }

  @SuppressWarnings("unchecked")
  private void deleteFiles(ServletRequest aRequest) {
    List<File> tmpFiles = (ArrayList<File>) aRequest.getAttribute(FILES);
    if (tmpFiles != null) {
      Iterator<File> tmpIterator = tmpFiles.iterator();
      while (tmpIterator.hasNext()) {
        File tmpFile = tmpIterator.next();
        try {
          tmpFile.delete();
        } catch (Exception e) {
          context.log("failed to delete " + tmpFile, e);
        }
      }
    }
  }

  /* ------------------------------------------------------------ */
  private String value(String aNameEqualsValue) {
    String tmpValue = aNameEqualsValue.substring(aNameEqualsValue.indexOf('=') + 1).trim();
    int i = tmpValue.indexOf(';');
    if (i > 0) {
      tmpValue = tmpValue.substring(0, i);
    }
    if (tmpValue.startsWith("\"")) {
      tmpValue = tmpValue.substring(1, tmpValue.indexOf('"', 1));
    } else {
      i = tmpValue.indexOf(' ');
      if (i > 0) {
        tmpValue = tmpValue.substring(0, i);
      }
    }
    return tmpValue;
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
    public Wrapper(HttpServletRequest aRequest, MultiMap<String> aMap) {
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
    public String getParameter(String aName) {
      Object tmpObject = parameters.get(aName);
      if (!(tmpObject instanceof byte[]) && LazyList.size(tmpObject) > 0) {
        tmpObject = LazyList.get(tmpObject, 0);
      }

      if (tmpObject instanceof byte[]) {
        try {
          String tmpString = new String((byte[]) tmpObject, encoding);
          return tmpString;
        } catch (Exception e) {
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
      return Collections.unmodifiableMap(parameters.toStringArrayMap());
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getParameterNames() {
      return Collections.enumeration(parameters.keySet());
    }

    /* ------------------------------------------------------------------------------- */
    /**
     * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(String aName) {
      List<?> tmpValues = parameters.getValues(aName);
      if (tmpValues == null || tmpValues.size() == 0) {
        return new String[0];
      }
      String[] tmpValueArray = new String[tmpValues.size()];
      for (int i = 0; i < tmpValues.size(); i++) {
        Object tmpObject = tmpValues.get(i);
        if (tmpObject instanceof byte[]) {
          try {
            tmpValueArray[i] = new String((byte[]) tmpObject, encoding);
          } catch (Exception e) {
            e.printStackTrace();
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
    public void setCharacterEncoding(String anEncoding) throws UnsupportedEncodingException {
      encoding = anEncoding;
    }
  }
}
