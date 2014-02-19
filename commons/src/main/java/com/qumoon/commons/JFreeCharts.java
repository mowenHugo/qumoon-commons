package com.qumoon.commons;

import com.google.common.collect.ImmutableList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

/**
 * @author kevin
 */
public class JFreeCharts {

    private static final Color C1 = new Color(5, 141, 199);
    private static final Color C2 = new Color(237, 126, 23);
    private static final Color C3 = new Color(80, 180, 50);
    private static final Color C4 = new Color(237, 239, 0);
    private static final Color C5 = new Color(128, 128, 255);
    private static final Color C6 = new Color(160, 164, 36);
    private static final List<Color> COLORS = ImmutableList.of(C1, C2, C3, C4, C5, C6);

    public static Color randomColor() {
        Random rand = new Random();

        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }

    public static JFreeChart createTimeSeriesChart(String title, String timeAxisLabel, String valueAxisLabel,
                                                   List<TimeSeriesCollection> dataSet) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title, timeAxisLabel, valueAxisLabel, null, true, true, false
        );

        XYPlot plot = chart.getXYPlot();

        //set style
        chart.setBackgroundPaint(Color.white);

        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(new Color(204, 204, 204));
        plot.setRangeGridlineStroke(new BasicStroke());
        plot.setOutlineVisible(false);
        plot.setOutlinePaint(Color.white);

        final ValueAxis axis = chart.getXYPlot().getDomainAxis();
        axis.setAutoRange(true);
        ((DateAxis) (axis)).setDateFormatOverride(new SimpleDateFormat(DateUtils.yyyyMMdd));

        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setTickMarksVisible(false);
        plot.setRangeAxis(0, valueAxis);

        for (int index = 0; index < dataSet.size(); index++) {
            StandardXYItemRenderer standardXYItemRenderer = new StandardXYItemRenderer();
            if (index < 6) {
                standardXYItemRenderer.setSeriesPaint(index, COLORS.get(index));
            } else {
                standardXYItemRenderer.setSeriesPaint(index, randomColor());
            }
            standardXYItemRenderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
            plot.setRenderer(index, standardXYItemRenderer);
            plot.setDataset(index, dataSet.get(index));
            plot.mapDatasetToRangeAxis(index, 0);
        }

        return chart;
    }
}
