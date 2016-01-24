/*
 * Copyright (c) 2008-2016 wetator.org
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

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpStatus.Code;

/**
 * @author frank.danek
 */
public class HttpHeaderServlet extends HttpServlet {

  private static final long serialVersionUID = 2466057799555730590L;

  @Override
  protected void doGet(final HttpServletRequest aRequest, final HttpServletResponse aResponse) throws ServletException,
      IOException {
    final String tmpCode = aRequest.getParameter("code");
    final Code tmpStatusCode = HttpStatus.getCode(Integer.valueOf(tmpCode));
    aResponse.sendError(tmpStatusCode.getCode(), tmpStatusCode.getMessage());
  }

  @Override
  protected void doPost(final HttpServletRequest aReq, final HttpServletResponse aResp) throws ServletException,
      IOException {
    doGet(aReq, aResp);
  }

}
