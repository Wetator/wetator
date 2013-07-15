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


package org.rbri.wet.test.jetty;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author frank.danek
 */
public class SnoopyServlet extends HttpServlet {

  private static final long serialVersionUID = -2387076015181680367L;

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
    aResponse.getWriter().println(
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">");
    aResponse.getWriter().println("<html>");
    aResponse.getWriter().println("<head>");
    aResponse.getWriter().println("<title>Wetator / Request Snoopy / Jetty</title>");
    aResponse.getWriter().println("</head>");
    aResponse.getWriter().println("<body>");

    aResponse.getWriter().println("<h1>Wetator / Request Snoopy Jetty</h1>");
    aResponse.getWriter().println("<h1>GET Parameters</h1>");
    aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
    aResponse.getWriter().println("<tr>");
    aResponse.getWriter().println("<th>Key</th>");
    aResponse.getWriter().println("<th>Value</th>");
    aResponse.getWriter().println("</tr>");

    // a small hack to distinguish between get and post parameters
    Set<String> tmpGetParameterNames = determineGetParameterNames(aRequest);

    Set<String> tmpFileParameterNames = determineFileParameterNames(aRequest);

    List<String> tmpParameterNames = Collections.list((Enumeration<String>) aRequest.getParameterNames());
    Collections.sort(tmpParameterNames);
    for (String tmpName : tmpParameterNames) {
      if (tmpGetParameterNames.contains(tmpName)) {
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpName);
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("<td>");
        String[] tmpValues = aRequest.getParameterValues(tmpName);
        boolean tmpIsNotFirst = false;
        if (tmpValues.length != 0) {
          for (String tmpValue : tmpValues) {
            if (tmpIsNotFirst) {
              aResponse.getWriter().print(", ");
            }
            tmpIsNotFirst = true;
            aResponse.getWriter().print(tmpValue);
          }
          aResponse.getWriter().println("");
        }
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("</tr>");
      }
    }
    aResponse.getWriter().println("</table>");

    aResponse.getWriter().println("<h1>POST Parameters</h1>");
    aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
    aResponse.getWriter().println("<tr>");
    aResponse.getWriter().println("<th>Key</th>");
    aResponse.getWriter().println("<th>Value</th>");
    aResponse.getWriter().println("</tr>");

    for (String tmpName : tmpParameterNames) {
      if (!tmpGetParameterNames.contains(tmpName) && !tmpFileParameterNames.contains(tmpName)) {
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpName);
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("<td>");
        String[] tmpValues = aRequest.getParameterValues(tmpName);
        boolean tmpIsNotFirst = false;
        if (tmpValues.length != 0) {
          for (String tmpValue : tmpValues) {
            if (tmpIsNotFirst) {
              aResponse.getWriter().print(", ");
            }
            tmpIsNotFirst = true;
            aResponse.getWriter().print(tmpValue);
          }
        }
        aResponse.getWriter().println("");
        aResponse.getWriter().println("</td>");

        if (aRequest.getAttribute(tmpName) != null) {
          aResponse.getWriter().println("<td>");
          aResponse.getWriter().println(aRequest.getAttribute(tmpName).getClass().getName());
          aResponse.getWriter().println("</td>");
        }

        aResponse.getWriter().println("</tr>");
      }
    }
    aResponse.getWriter().println("</table>");

    if (!tmpFileParameterNames.isEmpty()) {
      aResponse.getWriter().println("<h1>File Upload</h1>");
      aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
      aResponse.getWriter().println("<tr>");
      aResponse.getWriter().println("<th>Upload Control</th>");
      aResponse.getWriter().println("<th>Values</th>");
      aResponse.getWriter().println("</tr>");

      for (String tmpFileParameterName : tmpFileParameterNames) {
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpFileParameterName);
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println("<table border='0' cellpadding='3' cellspacing='0'>");
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>name</td>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(aRequest.getParameter(tmpFileParameterName));
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("</tr>");
        Object tmpAttribute = aRequest.getAttribute(tmpFileParameterName);
        if (tmpAttribute instanceof File) {
          File tmpFile = (File) tmpAttribute;
          aResponse.getWriter().println("<tr>");
          aResponse.getWriter().println("<td>tmp_name</td>");
          aResponse.getWriter().println("<td>");
          aResponse.getWriter().println(tmpFile.getAbsolutePath());
          aResponse.getWriter().println("</td>");
          aResponse.getWriter().println("</tr>");
          aResponse.getWriter().println("<tr>");
          aResponse.getWriter().println("<td>size</td>");
          aResponse.getWriter().println("<td>");
          aResponse.getWriter().println(tmpFile.length());
          aResponse.getWriter().println("</td>");
          aResponse.getWriter().println("</tr>");
          char[] tmpBuffer = new char[13];
          FileReader tmpReader = new FileReader(tmpFile);
          tmpReader.read(tmpBuffer);
          String tmpValue = new String(tmpBuffer);
          if (!tmpValue.isEmpty()) {
            aResponse.getWriter().println("<tr>");
            aResponse.getWriter().println("<td>SampleData</td>");
            aResponse.getWriter().println("<td>");
            aResponse.getWriter().println(tmpValue + "....");
            aResponse.getWriter().println("</td>");
            aResponse.getWriter().println("</tr>");
          }
        } else if (tmpAttribute instanceof byte[]) {
          byte[] tmpBytes = (byte[]) tmpAttribute;
          aResponse.getWriter().println("<tr>");
          aResponse.getWriter().println("<td>size</td>");
          aResponse.getWriter().println("<td>");
          aResponse.getWriter().println(tmpBytes.length);
          aResponse.getWriter().println("</td>");
          aResponse.getWriter().println("</tr>");
          String tmpValue = new String(tmpBytes);
          tmpValue = tmpValue.substring(0, Math.min(13, tmpValue.length()));
          if (!tmpValue.isEmpty()) {
            aResponse.getWriter().println("<tr>");
            aResponse.getWriter().println("<td>SampleData</td>");
            aResponse.getWriter().println("<td>");
            aResponse.getWriter().println(tmpValue + "....");
            aResponse.getWriter().println("</td>");
            aResponse.getWriter().println("</tr>");
          }
        }
        aResponse.getWriter().println("</table>");
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("</tr>");
      }

      aResponse.getWriter().println("</table>");
    }

    aResponse.getWriter().println("<h1>Headers</h1>");
    aResponse.getWriter().println("<table border='0' cellpadding='4' cellspacing='4'>");
    aResponse.getWriter().println("<tr>");
    aResponse.getWriter().println("<th>Key</th>");
    aResponse.getWriter().println("<th>Value</th>");
    aResponse.getWriter().println("</tr>");
    List<String> tmpHeaderNames = Collections.list((Enumeration<String>) aRequest.getHeaderNames());
    Collections.sort(tmpHeaderNames);
    for (String tmpEntry : tmpHeaderNames) {
      aResponse.getWriter().println("<tr>");
      aResponse.getWriter().println("<td>");
      aResponse.getWriter().println(tmpEntry);
      aResponse.getWriter().println("</td>");
      aResponse.getWriter().println("<td>");
      aResponse.getWriter().println(aRequest.getHeader(tmpEntry));
      aResponse.getWriter().println("</td>");
      aResponse.getWriter().println("</tr>");
    }
    aResponse.getWriter().println("</table>");

    aResponse.getWriter().println("</body>");
    aResponse.getWriter().println("</html>");

    aResponse.getWriter().flush();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest aReq, HttpServletResponse aResp) throws ServletException, IOException {
    doGet(aReq, aResp);
  }

  /**
   * HttpUtils.parseQueryString is deprecated.
   * So we build our own based on tomcats servlet api impl.
   * 
   * @param aRequest the request to read from
   * @return a set with all get parameter names
   */
  private Set<String> determineGetParameterNames(HttpServletRequest aRequest) {
    String tmpQueryString = aRequest.getQueryString();

    Set<String> tmpParamNames = new HashSet<String>();
    if (tmpQueryString == null) {
      return tmpParamNames;
    }

    StringTokenizer tmpTokenizer = new StringTokenizer(tmpQueryString, "&");
    while (tmpTokenizer.hasMoreTokens()) {
      String tmpPair = tmpTokenizer.nextToken();
      int tmpPos = tmpPair.indexOf('=');
      if (tmpPos == -1) {
        throw new IllegalArgumentException();
      }
      String tmpKey = parseName(tmpPair.substring(0, tmpPos));
      tmpParamNames.add(tmpKey);
    }

    return tmpParamNames;
  }

  @SuppressWarnings("unchecked")
  private Set<String> determineFileParameterNames(HttpServletRequest aRequest) {
    Set<String> tmpFileParameterNames = new HashSet<String>();

    List<String> tmpParameterNames = Collections.list((Enumeration<String>) aRequest.getParameterNames());
    for (String tmpName : tmpParameterNames) {
      Object tmpAttribute = aRequest.getAttribute(tmpName);
      if (tmpAttribute != null) {
        if (tmpAttribute instanceof File || tmpAttribute instanceof byte[]) {
          tmpFileParameterNames.add(tmpName);
        }
      }
    }

    return tmpFileParameterNames;
  }

  /**
   * Parse a name in the query string.
   */
  private static String parseName(String aString) {
    StringBuffer tmpResult = new StringBuffer();
    for (int i = 0; i < aString.length(); i++) {
      char tmpChar = aString.charAt(i);
      switch (tmpChar) {
        case '+':
          tmpResult.append(' ');
          break;
        case '%':
          try {
            tmpResult.append((char) Integer.parseInt(aString.substring(i + 1, i + 3), 16));
            i += 2;
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
          } catch (StringIndexOutOfBoundsException e) {
            String tmpRest = aString.substring(i);
            tmpResult.append(tmpRest);
            if (tmpRest.length() == 2) {
              i++;
            }
          }

          break;
        default:
          tmpResult.append(tmpChar);
          break;
      }
    }
    return tmpResult.toString();
  }
}
