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


package org.wetator.test.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus.Code;
import org.wetator.test.AbstractWebServerTest;

/**
 * @author frank.danek
 */
public class RedirectServlet extends HttpServlet {

  private static final long serialVersionUID = -2150482777498443709L;

  @Override
  protected void doGet(final HttpServletRequest aRequest, final HttpServletResponse aResponse) throws ServletException,
      IOException {
    final String tmpPath = aRequest.getServletPath();

    if (tmpPath.endsWith("redirect_header.php")) {
      aResponse.setStatus(Code.MOVED_TEMPORARILY.getCode());
      final String tmpTarget = determineTarget(aRequest);
      aResponse.setHeader("Location", tmpTarget);
    } else if (tmpPath.endsWith("redirect_js.php")) {
      final String tmpTarget = determineTarget(aRequest);
      String tmpWait = aRequest.getParameter("wait");
      if (StringUtils.isEmpty(tmpWait)) {
        tmpWait = "444";
      }

      aResponse.getWriter().println(
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">");
      aResponse.getWriter().println("<html>");
      aResponse.getWriter().println("<head>");
      aResponse.getWriter().println("<title>Wetator / Redirect via JavaScript</title>");
      aResponse.getWriter().println("<script type=\"text/javascript\">");
      aResponse.getWriter().println("function redirect(){");
      aResponse.getWriter().println("  window.location = '" + tmpTarget + "'");
      aResponse.getWriter().println("}");
      aResponse.getWriter().println("function startRedirect() {");
      aResponse.getWriter().println("  setTimeout('redirect()', " + tmpWait + ");");
      aResponse.getWriter().println("}");
      aResponse.getWriter().println("</script>");
      aResponse.getWriter().println("</head>");
      aResponse.getWriter().println("<body onLoad=\"startRedirect();\">");
      aResponse.getWriter().println("<h1 class=\"command_test\">Wetator / Redirect via JavaScript</h1>");
      aResponse.getWriter().println("  <p>Redirection to '" + tmpTarget + "' in " + tmpWait + "ms</p>");
      aResponse.getWriter().println("</body>");
      aResponse.getWriter().println("</html>");
    } else if (tmpPath.endsWith("redirect_meta.php")) {
      final String tmpTarget = determineTarget(aRequest);
      String tmpWait = aRequest.getParameter("wait");
      if (StringUtils.isEmpty(tmpWait)) {
        tmpWait = "4";
      }

      aResponse.getWriter().println(
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">");
      aResponse.getWriter().println("<html>");
      aResponse.getWriter().println("<head>");
      aResponse.getWriter().println("<meta http-equiv='refresh' content='" + tmpWait + "; URL=" + tmpTarget + "'/>");
      aResponse.getWriter().println("<title>Wetator / Redirect via MetaTag</title>");
      aResponse.getWriter().println("</head>");
      aResponse.getWriter().println("<body>");
      aResponse.getWriter().println("<h1 class=\"command_test\">Wetator / Redirect via MetaTag</h1>");
      aResponse.getWriter().println("  <p>Redirection to '" + tmpTarget + "' in " + tmpWait + "ms</p>");
      aResponse.getWriter().println("</body>");
      aResponse.getWriter().println("</body>");
      aResponse.getWriter().println("</html>");
    }
    aResponse.getWriter().flush();
  }

  @Override
  protected void doPost(final HttpServletRequest aRequest, final HttpServletResponse aResponse)
      throws ServletException, IOException {
    doGet(aRequest, aResponse);
  }

  protected String determineTarget(final HttpServletRequest aRequest) {
    String tmpTarget = aRequest.getParameter("target");
    if (StringUtils.isEmpty(tmpTarget)) {
      return "http://localhost:" + AbstractWebServerTest.DEFAULT_PORT;
    }

    if (!tmpTarget.startsWith("http://")) {
      if (!tmpTarget.startsWith("/")) {
        tmpTarget = "/" + tmpTarget;
      }
      return "http://localhost:" + AbstractWebServerTest.DEFAULT_PORT + tmpTarget;
    }
    return tmpTarget;
  }
}
