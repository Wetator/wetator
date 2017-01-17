/*
 * Copyright (c) 2008-2017 wetator.org
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author rbri
 */
public class ContentServlet extends HttpServlet {

  private static final long serialVersionUID = 1263421133903634210L;

  @Override
  protected void doGet(final HttpServletRequest aRequest, final HttpServletResponse aResponse) {
    aResponse.setContentType("application/vnd.ms-excel");
    aResponse.setHeader("Expires", "0");
    aResponse.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    aResponse.setHeader("Pragma", "public");
    aResponse.setHeader("Content-Disposition", "attachment; filename=wetator.xls");

    try (ServletOutputStream tmpOut = aResponse.getOutputStream(); XSSFWorkbook tmpWorkbook = new XSSFWorkbook()) {
      final XSSFSheet tmpSheet = tmpWorkbook.createSheet("for testing");
      final XSSFRow tmpRow = tmpSheet.createRow(7);
      final XSSFCell tmpCell = tmpRow.createCell(13);
      tmpCell.setCellValue("POI is fun");
      tmpWorkbook.write(tmpOut);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doPost(final HttpServletRequest aReq, final HttpServletResponse aResp)
      throws ServletException, IOException {
    doGet(aReq, aResp);
  }

}
