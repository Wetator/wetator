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


package org.wetator.jenkins;

import hudson.Functions;
import hudson.model.ProminentProjectAction;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.util.Area;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;

import java.awt.Color;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

/**
 * The Wetator Report for the whole project.
 * 
 * @author frank.danek
 */
public class WetatorProjectReport implements ProminentProjectAction {

  private static final String FAILURE_ONLY_COOKIE = "WetatorBuildReport_failureOnly";

  public AbstractProject<?, ?> project;

  /**
   * @param project the project this report belongs to
   */
  public WetatorProjectReport(AbstractProject<?, ?> project) {
    // the method parameters must be raw (without leading a) to make stapler work
    this.project = project;
  }

  /**
   * {@inheritDoc}
   * 
   * @see hudson.model.Action#getIconFileName()
   */
  @Override
  public String getIconFileName() {
    WetatorBuildReport tmpBuildReport = getLastCompletedBuildReport();
    if (tmpBuildReport == null) {
      // no wetator result so far -> hide the summary by returning null
      return null;
    }
    return PluginImpl.ICON_FILE_NAME;
  }

  /**
   * {@inheritDoc}
   * 
   * @see hudson.model.Action#getDisplayName()
   */
  @Override
  public String getDisplayName() {
    return Messages.WetatorProjectReport_DisplayName();
  }

  /**
   * @return the project this report is attached to
   */
  public AbstractProject<?, ?> getProject() {
    return project;
  }

  /**
   * {@inheritDoc}
   * 
   * @see hudson.model.Action#getUrlName()
   */
  @Override
  @Exported(visibility = 2)
  public String getUrlName() {
    return PluginImpl.URL_NAME;
  }

  /**
   * Redirects the index page to the last result.
   * 
   * @param request
   *        Stapler request
   * @param response
   *        Stapler response
   * @throws IOException
   *         in case of an error
   */
  public void doIndex(final StaplerRequest request, final StaplerResponse response) throws IOException {
    // the method parameters must be raw (without leading a) to make stapler work
    AbstractBuild<?, ?> tmpBuild = getLastCompletedBuild();
    if (tmpBuild != null) {
      response.sendRedirect2(String.format("../%d/%s", tmpBuild.getNumber(), PluginImpl.URL_NAME));
    }
  }

  /**
   * @return the last completed build a {@link WetatorBuildReport} is attached to or <code>null</code> if there is no
   *         such build
   */
  public AbstractBuild<?, ?> getLastCompletedBuild() {
    final AbstractBuild<?, ?> tmpLastSuccessfulBuild = project.getLastSuccessfulBuild();

    AbstractBuild<?, ?> tmpLastBuildWithBuildReport = project.getLastBuild();
    while (tmpLastBuildWithBuildReport != null) {
      WetatorBuildReport tmpBuildReport = tmpLastBuildWithBuildReport.getAction(WetatorBuildReport.class);
      if (tmpBuildReport != null) {
        return tmpLastBuildWithBuildReport;
      }
      if (tmpLastBuildWithBuildReport == tmpLastSuccessfulBuild) {
        // if even the last successful build didn't produce a WetatorBuildReport,
        // that means we just don't have any tests configured.
        return null;
      }
      tmpLastBuildWithBuildReport = tmpLastBuildWithBuildReport.getPreviousBuild();
    }

    return null;
  }

  /**
   * @return the {@link WetatorBuildReport} attached to the last completed build having such a report or
   *         <code>null</code> if there is no such build
   */
  public WetatorBuildReport getLastCompletedBuildReport() {
    AbstractBuild<?, ?> tmpBuild = getLastCompletedBuild();
    if (tmpBuild == null) {
      return null;
    }
    return tmpBuild.getAction(WetatorBuildReport.class);
  }

  /**
   * Display the test result trend.
   * 
   * @param request the request
   * @param response the response
   * @throws IOException in case of problems generating the trend graph
   */
  @SuppressWarnings("deprecation")
  public void doTrend(StaplerRequest request, StaplerResponse response) throws IOException {
    // the method parameters must be raw (without leading a) to make stapler work
    WetatorBuildReport tmpBuildReport = getLastCompletedBuildReport();
    if (tmpBuildReport == null) {
      // no wetator result so far
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    if (ChartUtil.awtProblemCause != null) {
      // not available. send out error message
      response.sendRedirect2(request.getContextPath() + "/images/headless.png");
      return;
    }

    Calendar tmpTimestamp = project.getLastCompletedBuild().getTimestamp();
    if (request.checkIfModified(tmpTimestamp, response)) {
      return; // up to date
    }

    ChartUtil.generateGraph(request, response, createChart(request, buildDataSet(request)), calculateDefaultSize());
  }

  /**
   * Generates the clickable map HTML fragment for {@link #doTrend(StaplerRequest, StaplerResponse)}.
   * 
   * @param request the request
   * @param response the response
   * @throws IOException in case of problems generating the trend map
   */
  @SuppressWarnings("deprecation")
  public void doTrendMap(StaplerRequest request, StaplerResponse response) throws IOException {
    // the method parameters must be raw (without leading a) to make stapler work
    WetatorBuildReport tmpBuildReport = getLastCompletedBuildReport();
    if (tmpBuildReport == null) {
      // no wetator result so far
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    Calendar tmpTimestamp = project.getLastCompletedBuild().getTimestamp();
    if (request.checkIfModified(tmpTimestamp, response)) {
      return; // up to date
    }

    ChartUtil.generateClickableMap(request, response, createChart(request, buildDataSet(request)),
        calculateDefaultSize());
  }

  /**
   * Changes the test result report display mode.
   * 
   * @param request the request
   * @param response the response
   * @throws IOException in case of problems during redirect
   */
  public void doFlipTrend(StaplerRequest request, StaplerResponse response) throws IOException {
    // the method parameters must be raw (without leading a) to make stapler work
    boolean tmpFailureOnly = false;

    // check the current preference value
    Cookie[] tmpCookies = request.getCookies();
    if (tmpCookies != null) {
      for (Cookie tmpCookie : tmpCookies) {
        if (tmpCookie.getName().equals(FAILURE_ONLY_COOKIE))
          tmpFailureOnly = Boolean.parseBoolean(tmpCookie.getValue());
      }
    }

    // flip!
    tmpFailureOnly = !tmpFailureOnly;

    // set the updated value
    Cookie tmpCookie = new Cookie(FAILURE_ONLY_COOKIE, String.valueOf(tmpFailureOnly));
    List<Ancestor> tmpAncestors = request.getAncestors();
    Ancestor tmpAnchestor = tmpAncestors.get(tmpAncestors.size() - 2);
    tmpCookie.setPath(tmpAnchestor.getUrl()); // just for this project
    tmpCookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
    response.addCookie(tmpCookie);

    // back to the project page
    response.sendRedirect("..");
  }

  /**
   * Determines the default size of the trend graph.
   * This is default because the query parameter can choose arbitrary size.
   * If the screen resolution is too low, use a smaller size.
   */
  private Area calculateDefaultSize() {
    Area tmpResolution = Functions.getScreenResolution();
    if (tmpResolution != null && tmpResolution.width <= 800) {
      return new Area(250, 100);
    }
    return new Area(500, 200);
  }

  private CategoryDataset buildDataSet(StaplerRequest aRequest) {
    boolean tmpFailureOnly = Boolean.valueOf(aRequest.getParameter("failureOnly"));

    DataSetBuilder<String, NumberOnlyBuildLabel> tmpDataSetBuilder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

    for (AbstractBuild<?, ?> tmpBuild = getLastCompletedBuild(); tmpBuild != null; tmpBuild = tmpBuild
        .getPreviousBuild()) {
      WetatorBuildReport tmpReport = tmpBuild.getAction(WetatorBuildReport.class);
      if (tmpReport == null || tmpReport.build == null) {
        // no report so just ignore the build
        continue;
      }
      tmpDataSetBuilder.add(tmpReport.getFailCount(), "failed", new NumberOnlyBuildLabel(tmpReport.build));
      if (!tmpFailureOnly) {
        tmpDataSetBuilder.add(tmpReport.getTotalCount() - tmpReport.getFailCount(), "passed", new NumberOnlyBuildLabel(
            tmpReport.build));
      }
    }
    return tmpDataSetBuilder.build();
  }

  private JFreeChart createChart(StaplerRequest aRequest, CategoryDataset aDataset) {
    final String tmpRelPath = getRelPath(aRequest);

    final JFreeChart tmpChart = ChartFactory.createStackedAreaChart( //
        null, // chart title
        null, // unused
        Messages.WetatorProjectReport_yLabel(), // range axis label
        aDataset, // data
        PlotOrientation.VERTICAL, // orientation
        false, // include legend
        true, // tooltips
        false // urls
        );

    // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

    // set the background color for the chart...

    // final StandardLegend legend = (StandardLegend) chart.getLegend();
    // legend.setAnchor(StandardLegend.SOUTH);

    tmpChart.setBackgroundPaint(Color.white);

    final CategoryPlot tmpPlot = tmpChart.getCategoryPlot();

    // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
    tmpPlot.setBackgroundPaint(Color.WHITE);
    tmpPlot.setOutlinePaint(null);
    tmpPlot.setForegroundAlpha(0.8f);
    // plot.setDomainGridlinesVisible(true);
    // plot.setDomainGridlinePaint(Color.white);
    tmpPlot.setRangeGridlinesVisible(true);
    tmpPlot.setRangeGridlinePaint(Color.black);

    CategoryAxis tmpDomainAxis = new ShiftedCategoryAxis(null);
    tmpPlot.setDomainAxis(tmpDomainAxis);
    tmpDomainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
    tmpDomainAxis.setLowerMargin(0.0);
    tmpDomainAxis.setUpperMargin(0.0);
    tmpDomainAxis.setCategoryMargin(0.0);

    final NumberAxis tmpRangeAxis = (NumberAxis) tmpPlot.getRangeAxis();
    tmpRangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    StackedAreaRenderer tmpAreaRenderer = new StackedAreaRenderer2() {

      private static final long serialVersionUID = 2111499896183437611L;

      @Override
      public String generateURL(CategoryDataset aCategoryDataset, int aRow, int aColumn) {
        NumberOnlyBuildLabel tmpLabel = (NumberOnlyBuildLabel) aCategoryDataset.getColumnKey(aColumn);
        return tmpRelPath + tmpLabel.build.getNumber() + "/" + getUrlName() + "/";
      }

      @Override
      public String generateToolTip(CategoryDataset aCategoryDataset, int aRow, int aColumn) {
        NumberOnlyBuildLabel tmpLabel = (NumberOnlyBuildLabel) aCategoryDataset.getColumnKey(aColumn);
        WetatorBuildReport tmpBuildReport = tmpLabel.build.getAction(WetatorBuildReport.class);
        if (aRow == 0) {
          return String.valueOf(Messages.WetatorBuildReport_fail(tmpLabel.build.getNumber(),
              tmpBuildReport.getFailCount()));
        }
        return String.valueOf(Messages.WetatorBuildReport_test(tmpLabel.build.getNumber(),
            tmpBuildReport.getTotalCount()));
      }
    };
    tmpPlot.setRenderer(tmpAreaRenderer);
    tmpAreaRenderer.setSeriesPaint(0, ColorPalette.RED); // Failures.
    tmpAreaRenderer.setSeriesPaint(1, ColorPalette.BLUE); // Passes.

    // crop extra space around the graph
    tmpPlot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

    return tmpChart;
  }

  private String getRelPath(StaplerRequest aRequest) {
    String tmpRelPath = aRequest.getParameter("rel");
    if (tmpRelPath == null) {
      return "";
    }
    return tmpRelPath;
  }
}
