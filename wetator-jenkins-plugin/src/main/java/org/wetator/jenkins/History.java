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


package org.wetator.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

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
import org.kohsuke.stapler.Stapler;
import org.wetator.jenkins.result.AbstractBaseResult;

/**
 * History of {@link AbstractBaseResult}s over time.
 * 
 * @author frank.danek
 */
public class History {

  private final AbstractBaseResult result;

  /**
   * The constructor.
   * 
   * @param result the result to build the history of
   */
  public History(AbstractBaseResult result) {
    // the method parameters must be raw (without leading a) to make stapler work
    this.result = result;
  }

  /**
   * @return the result
   */
  public AbstractBaseResult getResult() {
    return result;
  }

  /**
   * @return true if a history is available, so if more than one build was executed
   */
  public boolean historyAvailable() {
    return (result.getOwner().getParent().getBuilds().size() > 1);
  }

  /**
   * @param start the index of the first build to get the result of
   * @param end the index of the last build to get the result of
   * @return a list containing the results
   */
  public List<AbstractBaseResult> getList(int start, int end) {
    // the method parameters must be raw (without leading a) to make stapler work
    List<AbstractBaseResult> tmpList = new ArrayList<AbstractBaseResult>();
    int tmpEnd = end;
    tmpEnd = Math.min(tmpEnd, result.getOwner().getParent().getBuilds().size());
    for (AbstractBuild<?, ?> tmpBuild : result.getOwner().getParent().getBuilds().subList(start, tmpEnd)) {
      if (tmpBuild.isBuilding()) {
        continue;
      }
      try {
        AbstractBaseResult tmpResult = result.getResultInBuild(tmpBuild);
        if (tmpResult != null) {
          tmpList.add(tmpResult);
        }
      } catch (Exception e) {
        // could not find the result -> ignore build
      }
    }
    return tmpList;
  }

  /**
   * @return a list containing the results of all builds
   */
  public List<AbstractBaseResult> getList() {
    return getList(0, result.getOwner().getParent().getBuilds().size());
  }

  /**
   * @return a {@link Graph} of the duration of tests over time
   */
  public Graph getDurationGraph() {
    return new GraphImpl(Messages.History_yLabel()) {
      @Override
      protected DataSetBuilder<String, ChartLabel> createDataSet() {
        DataSetBuilder<String, ChartLabel> tmpData = new DataSetBuilder<String, ChartLabel>();

        List<AbstractBaseResult> tmpResults;
        try {
          tmpResults = getList(Integer.parseInt(Stapler.getCurrentRequest().getParameter("start")),
              Integer.parseInt(Stapler.getCurrentRequest().getParameter("end")));
        } catch (NumberFormatException e) {
          tmpResults = getList();
        }

        for (AbstractBaseResult tmpResult : tmpResults) {
          ChartLabel tmpLabel = new ChartLabel(tmpResult) {
            @Override
            public Color getColor() {
              if (baseResult.getFailCount() > 0) {
                return ColorPalette.RED;
              }
              return ColorPalette.BLUE;
            }

            @Override
            public String getToolTip(int aRow) {
              return baseResult.getOwner().getDisplayName() + " : " + baseResult.getDurationString();
            }
          };
          tmpData.add(((double) tmpResult.getDuration()) / (1000), "", tmpLabel);
        }
        return tmpData;
      }
    };
  }

  /**
   * @return a {@link Graph} of the number of tests over time
   */
  public Graph getCountGraph() {
    return new GraphImpl("") {
      @Override
      protected DataSetBuilder<String, ChartLabel> createDataSet() {
        DataSetBuilder<String, ChartLabel> tmpData = new DataSetBuilder<String, ChartLabel>();

        List<AbstractBaseResult> tmpResults;
        try {
          tmpResults = getList(Integer.parseInt(Stapler.getCurrentRequest().getParameter("start")),
              Integer.parseInt(Stapler.getCurrentRequest().getParameter("end")));
        } catch (NumberFormatException e) {
          tmpResults = getList();
        }

        for (AbstractBaseResult tmpResult : tmpResults) {
          ChartLabel tmpLabel = new ChartLabel(tmpResult) {
            @Override
            public String getToolTip(int aRow) {
              if (aRow == 0) {
                return baseResult.getOwner().getDisplayName() + " : "
                    + Messages.History_fail(baseResult.getFailCount());
              }
              return baseResult.getOwner().getDisplayName() + " : "
                  + Messages.History_test(baseResult.getPassCount() + baseResult.getFailCount());
            }
          };
          tmpData.add(tmpResult.getPassCount(), "2Passed", tmpLabel);
          tmpData.add(tmpResult.getFailCount(), "1Failed", tmpLabel);
        }
        return tmpData;
      }
    };
  }

  /**
   * This abstract {@link Graph} builds an area chart of the given data set.
   * 
   * @author frank.danek
   */
  private abstract class GraphImpl extends Graph {

    private final String yLabel;

    /**
     * The constructor.
     * 
     * @param aYLabel the label of the y-axis
     */
    protected GraphImpl(String aYLabel) {
      super(-1, 600, 300); // cannot use timestamp, since ranges may change
      yLabel = aYLabel;
    }

    /**
     * @return the {@link DataSetBuilder} containing the data of the graph
     */
    protected abstract DataSetBuilder<String, ChartLabel> createDataSet();

    /**
     * {@inheritDoc}
     * 
     * @see hudson.util.Graph#createGraph()
     */
    @Override
    protected JFreeChart createGraph() {
      final CategoryDataset tmpDataSet = createDataSet().build();

      final JFreeChart tmpChart = ChartFactory.createStackedAreaChart( //
          null, // chart title
          null, // x-axis label
          yLabel, // y-axis label
          tmpDataSet, // data
          PlotOrientation.VERTICAL, // orientation
          false, // include legend
          true, // tooltips
          false // urls
          );

      tmpChart.setBackgroundPaint(Color.white);

      final CategoryPlot tmpPlot = tmpChart.getCategoryPlot();

      // tmpPlot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
      tmpPlot.setBackgroundPaint(Color.WHITE);
      tmpPlot.setOutlinePaint(null);
      tmpPlot.setForegroundAlpha(0.8f);
      // tmpPlot.setDomainGridlinesVisible(true);
      // tmpPlot.setDomainGridlinePaint(Color.white);
      tmpPlot.setRangeGridlinesVisible(true);
      tmpPlot.setRangeGridlinePaint(Color.black);

      CategoryAxis tmpDomainAxis = new ShiftedCategoryAxis(null);
      tmpPlot.setDomainAxis(tmpDomainAxis);
      tmpDomainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
      tmpDomainAxis.setLowerMargin(0.0);
      tmpDomainAxis.setUpperMargin(0.0);
      tmpDomainAxis.setCategoryMargin(0.0);

      final NumberAxis tmpRangeAxis = (NumberAxis) tmpPlot.getRangeAxis();
      ChartUtil.adjustChebyshev(tmpDataSet, tmpRangeAxis);
      tmpRangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      tmpRangeAxis.setAutoRange(true);

      StackedAreaRenderer tmpAreaRenderer = new StackedAreaRenderer2() {

        private static final long serialVersionUID = 169518184315479166L;

        @Override
        public Paint getItemPaint(int aRow, int aColumn) {
          ChartLabel tmpKey = (ChartLabel) tmpDataSet.getColumnKey(aColumn);
          if (tmpKey.getColor() != null) {
            return tmpKey.getColor();
          }
          return super.getItemPaint(aRow, aColumn);
        }

        @Override
        public String generateURL(CategoryDataset aCategoryDataset, int aRow, int aColumn) {
          ChartLabel tmpLabel = (ChartLabel) aCategoryDataset.getColumnKey(aColumn);
          return tmpLabel.getUrl();
        }

        @Override
        public String generateToolTip(CategoryDataset aCategoryDataset, int aRow, int aColumn) {
          ChartLabel tmpLabel = (ChartLabel) aCategoryDataset.getColumnKey(aColumn);
          return tmpLabel.getToolTip(aRow);
        }
      };
      tmpPlot.setRenderer(tmpAreaRenderer);
      tmpAreaRenderer.setSeriesPaint(0, ColorPalette.RED); // Failures.
      tmpAreaRenderer.setSeriesPaint(1, ColorPalette.BLUE); // Total.

      // crop extra space around the graph
      tmpPlot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

      return tmpChart;
    }
  }

  /**
   * This class represents the label of one point of the graph.
   * 
   * @author frank.danek
   */
  private abstract class ChartLabel implements Comparable<ChartLabel> {

    protected AbstractBaseResult baseResult;
    protected String url;

    /**
     * The constructor.
     * 
     * @param aResult the result to build the label for
     */
    public ChartLabel(AbstractBaseResult aResult) {
      baseResult = aResult;
      url = null;
    }

    /**
     * @return the URL pointing to the result
     */
    public String getUrl() {
      if (url == null) {
        generateUrl();
      }
      return url;
    }

    private void generateUrl() {
      AbstractBuild<?, ?> tmpBuild = baseResult.getOwner();
      String tmpBuildLink = tmpBuild.getUrl();
      String tmpActionUrl = baseResult.getOwner().getAction(WetatorBuildReport.class).getUrlName();
      url = Hudson.getInstance().getRootUrl() + tmpBuildLink + tmpActionUrl + baseResult.getUrl();
    }

    /**
     * @param aRow the row to get the tool tip for
     * @return the tool tip
     */
    public String getToolTip(int aRow) {
      return null;
    }

    /**
     * @return the {@link Color} of the point
     */
    public Color getColor() {
      return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ChartLabel anOther) {
      return baseResult.getOwner().number - anOther.baseResult.getOwner().number;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object anOther) {
      if (!(anOther instanceof ChartLabel)) {
        return false;
      }
      ChartLabel tmpOther = (ChartLabel) anOther;
      return baseResult == tmpOther.baseResult;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return baseResult.hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      String tmpBuildName = baseResult.getOwner().getDisplayName();
      String tmpSlaveName = baseResult.getOwner().getBuiltOnStr();
      if (tmpSlaveName != null) {
        tmpBuildName += ' ' + tmpSlaveName;
      }
      return tmpBuildName;
    }
  }
}
