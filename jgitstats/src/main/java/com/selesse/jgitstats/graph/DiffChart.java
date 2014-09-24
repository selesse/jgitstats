package com.selesse.jgitstats.graph;

import com.selesse.gitwrapper.CommitDiff;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultKeyedValues2DDataset;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DiffChart {
    private List<CommitDiff> commitDiffList;
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;

    public DiffChart(List<CommitDiff> commitDiffList) {
        this.commitDiffList = commitDiffList;
    }

    public void writeChart(OutputStream out) throws IOException {
        CategoryDataset dataset = getDataSet();

        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Diffs",
                "File",
                "Line Change",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                false,
                false
        );
        final CategoryPlot plot = chart.getCategoryPlot();
        ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());
        plot.getRenderer().setSeriesPaint(0, Color.RED);
        plot.getRenderer().setSeriesPaint(1, Color.GREEN);

        ChartUtilities.writeChartAsPNG(out, chart, WIDTH, HEIGHT);
    }

    private CategoryDataset getDataSet() {
        DefaultKeyedValues2DDataset data = new DefaultKeyedValues2DDataset();
        for (CommitDiff commitDiff : commitDiffList) {
            data.addValue(-1 * commitDiff.getLinesRemoved(), "Lines removed", commitDiff.getNewPath());
            data.addValue(commitDiff.getLinesAdded(), "Lines added", commitDiff.getNewPath());
        }
        return data;
    }
}
