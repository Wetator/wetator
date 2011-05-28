/*
 * Copyright (c) wetator.org
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
 * History of {@link AbstractBaseResult} over time.
 * 
 * @author frank.danek
 */
public class History {

  private final AbstractBaseResult result;

  public History(AbstractBaseResult result) {
    this.result = result;
  }

  public AbstractBaseResult getResult() {
    return result;
  }

  public boolean historyAvailable() {
    return (result.getOwner().getParent().getBuilds().size() > 1);
  }

  public List<AbstractBaseResult> getList(int start, int end) {
    List<AbstractBaseResult> tmpList = new ArrayList<AbstractBaseResult>();
    end = Math.min(end, result.getOwner().getParent().getBuilds().size());
    for (AbstractBuild<?, ?> tmpBuild : result.getOwner().getParent().getBuilds().subList(start, end)) {
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

  public List<AbstractBaseResult> getList() {
    return getList(0, result.getOwner().getParent().getBuilds().size());
  }

  /**
   * Graph of duration of tests over time.
   */
  public Graph getDurationGraph() {
    return new GraphImpl("seconds") {
      @Override
      protected DataSetBuilder<String, ChartLabel> createDataSet() {
        DataSetBuilder<String, ChartLabel> data = new DataSetBuilder<String, ChartLabel>();

        List<AbstractBaseResult> list;
        try {
          list = getList(Integer.parseInt(Stapler.getCurrentRequest().getParameter("start")),
              Integer.parseInt(Stapler.getCurrentRequest().getParameter("end")));
        } catch (NumberFormatException e) {
          list = getList();
        }

        for (AbstractBaseResult o : list) {
          data.add(((double) o.getDuration()) / (1000), "", new ChartLabel(o) {
            @Override
            public Color getColor() {
              if (o.getFailCount() > 0) {
                return ColorPalette.RED;
              }
              return ColorPalette.BLUE;
            }

            @Override
            public String getToolTip(int row) {
              return o.getOwner().getDisplayName() + " : " + o.getDurationString();
            }
          });
        }
        return data;
      }

    };
  }

  /**
   * Graph of # of tests over time.
   */
  public Graph getCountGraph() {
    return new GraphImpl("") {
      @Override
      protected DataSetBuilder<String, ChartLabel> createDataSet() {
        DataSetBuilder<String, ChartLabel> data = new DataSetBuilder<String, ChartLabel>();

        List<AbstractBaseResult> list;
        try {
          list = getList(Integer.parseInt(Stapler.getCurrentRequest().getParameter("start")),
              Integer.parseInt(Stapler.getCurrentRequest().getParameter("end")));
        } catch (NumberFormatException e) {
          list = getList();
        }

        for (AbstractBaseResult o : list) {
          ChartLabel tmpLabel = new ChartLabel(o) {
            @Override
            public String getToolTip(int row) {
              if (row == 0) {
                return o.getOwner().getDisplayName() + " : " + Messages.History_fail(o.getFailCount());
              }
              return o.getOwner().getDisplayName() + " : " + Messages.History_test(o.getPassCount() + o.getFailCount());
            }
          };
          data.add(o.getPassCount(), "2Passed", tmpLabel);
          data.add(o.getFailCount(), "1Failed", tmpLabel);
        }
        return data;
      }
    };
  }

  private abstract class GraphImpl extends Graph {
    private final String yLabel;

    protected GraphImpl(String yLabel) {
      super(-1, 600, 300); // cannot use timestamp, since ranges may change
      this.yLabel = yLabel;
    }

    protected abstract DataSetBuilder<String, ChartLabel> createDataSet();

    @Override
    protected JFreeChart createGraph() {
      final CategoryDataset dataset = createDataSet().build();

      final JFreeChart chart = ChartFactory.createStackedAreaChart(null, // chart
                                                                         // title
          null, // unused
          yLabel, // range axis label
          dataset, // data
          PlotOrientation.VERTICAL, // orientation
          false, // include legend
          true, // tooltips
          false // urls
          );

      chart.setBackgroundPaint(Color.white);

      final CategoryPlot plot = chart.getCategoryPlot();

      // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
      plot.setBackgroundPaint(Color.WHITE);
      plot.setOutlinePaint(null);
      plot.setForegroundAlpha(0.8f);
      // plot.setDomainGridlinesVisible(true);
      // plot.setDomainGridlinePaint(Color.white);
      plot.setRangeGridlinesVisible(true);
      plot.setRangeGridlinePaint(Color.black);

      CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
      plot.setDomainAxis(domainAxis);
      domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
      domainAxis.setLowerMargin(0.0);
      domainAxis.setUpperMargin(0.0);
      domainAxis.setCategoryMargin(0.0);

      final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
      ChartUtil.adjustChebyshev(dataset, rangeAxis);
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      rangeAxis.setAutoRange(true);

      StackedAreaRenderer ar = new StackedAreaRenderer2() {
        private static final long serialVersionUID = 169518184315479166L;

        @Override
        public Paint getItemPaint(int row, int column) {
          ChartLabel key = (ChartLabel) dataset.getColumnKey(column);
          if (key.getColor() != null)
            return key.getColor();
          return super.getItemPaint(row, column);
        }

        @Override
        public String generateURL(CategoryDataset categoryDataset, int row, int column) {
          ChartLabel label = (ChartLabel) categoryDataset.getColumnKey(column);
          return label.getUrl();
        }

        @Override
        public String generateToolTip(CategoryDataset categoryDataset, int row, int column) {
          ChartLabel label = (ChartLabel) categoryDataset.getColumnKey(column);
          return label.getToolTip(row);
        }
      };
      plot.setRenderer(ar);
      ar.setSeriesPaint(0, ColorPalette.RED); // Failures.
      ar.setSeriesPaint(1, ColorPalette.BLUE); // Total.

      // crop extra space around the graph
      plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

      return chart;
    }
  }

  abstract class ChartLabel implements Comparable<ChartLabel> {
    AbstractBaseResult o;
    String url;

    public ChartLabel(AbstractBaseResult o) {
      this.o = o;
      this.url = null;
    }

    public String getUrl() {
      if (this.url == null) {
        generateUrl();
      }
      return url;
    }

    private void generateUrl() {
      AbstractBuild<?, ?> build = o.getOwner();
      String buildLink = build.getUrl();
      String actionUrl = o.getOwner().getAction(WetatorBuildReport.class).getUrlName();
      this.url = Hudson.getInstance().getRootUrl() + buildLink + actionUrl + o.getUrl();
    }

    public String getToolTip(@SuppressWarnings("unused") int row) {
      return null;
    }

    public Color getColor() {
      return null;
    }

    @Override
    public int compareTo(ChartLabel that) {
      return this.o.getOwner().number - that.o.getOwner().number;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof ChartLabel)) {
        return false;
      }
      ChartLabel that = (ChartLabel) other;
      return this.o == that.o;
    }

    @Override
    public int hashCode() {
      return o.hashCode();
    }

    @Override
    public String toString() {
      String l = o.getOwner().getDisplayName();
      String s = o.getOwner().getBuiltOnStr();
      if (s != null)
        l += ' ' + s;
      return l;
    }
  }
}
