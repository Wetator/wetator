/*
 * Copyright (c) 2008-2020 wetator.org
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


package org.wetator.test.jetty;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * @author frank.danek
 */
public class SnoopyServlet extends HttpServlet {

  private static final long serialVersionUID = -2387076015181680367L;

  @Override
  protected void doGet(final HttpServletRequest aRequest, final HttpServletResponse aResponse)
      throws ServletException, IOException {
    aResponse.getWriter()
        .println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">");
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
    final Set<String> tmpGetParameterNames = determineGetParameterNames(aRequest);

    final Set<String> tmpFileParameterNames = determineFileParameterNames(aRequest);

    final List<String> tmpParameterNames = Collections.list(aRequest.getParameterNames());
    Collections.sort(tmpParameterNames);
    for (String tmpName : tmpParameterNames) {
      if (tmpGetParameterNames.contains(tmpName)) {
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpName);
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("<td>");
        final String[] tmpValues = aRequest.getParameterValues(tmpName);
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
        final String[] tmpValues = aRequest.getParameterValues(tmpName);
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

      final Collection<Part> tmpParts = aRequest.getParts().stream()
          .filter(p -> tmpFileParameterNames.contains(p.getName()))
          .sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toList());
      for (Part tmpPart : tmpParts) {
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpPart.getName());
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println("<table border='0' cellpadding='3' cellspacing='0'>");
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>name</td>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpPart.getSubmittedFileName());
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("</tr>");
        aResponse.getWriter().println("<tr>");
        aResponse.getWriter().println("<td>size</td>");
        aResponse.getWriter().println("<td>");
        aResponse.getWriter().println(tmpPart.getSize());
        aResponse.getWriter().println("</td>");
        aResponse.getWriter().println("</tr>");

        final char[] tmpBuffer = new char[13];
        try (Reader tmpReader = new InputStreamReader(tmpPart.getInputStream(), StandardCharsets.UTF_8)) {
          tmpReader.read(tmpBuffer);
        }
        final String tmpValue = new String(tmpBuffer);
        if (!tmpValue.isEmpty()) {
          aResponse.getWriter().println("<tr>");
          aResponse.getWriter().println("<td>SampleData</td>");
          aResponse.getWriter().println("<td>");
          aResponse.getWriter().println(tmpValue + (tmpPart.getSize() > 13 ? "...." : ""));
          aResponse.getWriter().println("</td>");
          aResponse.getWriter().println("</tr>");
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
    final List<String> tmpHeaderNames = Collections.list(aRequest.getHeaderNames());
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

  @Override
  protected void doPost(final HttpServletRequest aReq, final HttpServletResponse aResp)
      throws ServletException, IOException {
    doGet(aReq, aResp);
  }

  /**
   * HttpUtils.parseQueryString is deprecated.
   * So we build our own based on tomcats servlet api impl.
   *
   * @param aRequest the request to read from
   * @return a set with all get parameter names
   */
  private Set<String> determineGetParameterNames(final HttpServletRequest aRequest) {
    final String tmpQueryString = aRequest.getQueryString();

    final Set<String> tmpParamNames = new HashSet<>();
    if (tmpQueryString == null) {
      return tmpParamNames;
    }

    final StringTokenizer tmpTokenizer = new StringTokenizer(tmpQueryString, "&");
    while (tmpTokenizer.hasMoreTokens()) {
      final String tmpPair = tmpTokenizer.nextToken();
      final int tmpPos = tmpPair.indexOf('=');
      if (tmpPos == -1) {
        throw new IllegalArgumentException();
      }
      final String tmpKey = parseName(tmpPair.substring(0, tmpPos));
      tmpParamNames.add(tmpKey);
    }

    return tmpParamNames;
  }

  private Set<String> determineFileParameterNames(final HttpServletRequest aRequest)
      throws IOException, ServletException {
    final Set<String> tmpFileParameterNames = new HashSet<>();

    if (aRequest.getContentType() != null && aRequest.getContentType().startsWith("multipart/form-data")) {
      final List<String> tmpParameterNames = Collections.list(aRequest.getParameterNames());

      final Collection<Part> tmpParts = aRequest.getParts();
      if (tmpParts != null) {
        tmpParts.stream().filter(p -> !tmpParameterNames.contains(p.getName())).map(p -> p.getName())
            .forEach(tmpFileParameterNames::add);
      }
    }

    return tmpFileParameterNames;
  }

  /**
   * Parse a name in the query string.
   */
  private static String parseName(final String aString) {
    final StringBuilder tmpResult = new StringBuilder();
    for (int i = 0; i < aString.length(); i++) {
      final char tmpChar = aString.charAt(i);
      switch (tmpChar) {
        case '+':
          tmpResult.append(' ');
          break;
        case '%':
          try {
            tmpResult.append((char) Integer.parseInt(aString.substring(i + 1, i + 3), 16));
            i += 2;
          } catch (final NumberFormatException e) {
            throw new IllegalArgumentException();
          } catch (final StringIndexOutOfBoundsException e) {
            final String tmpRest = aString.substring(i);
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
