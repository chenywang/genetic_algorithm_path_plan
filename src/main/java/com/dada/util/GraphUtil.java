package com.dada.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.dada.util.CommonUtil.println;

public class GraphUtil {

    public static void drawLineChart(List<List<double[]>> data, List<String> lineName, String title, String xTitle, String yTitle){
        ChartPanel panel;
        XYDataset xydataset = createDataSet(data, lineName);
        JFreeChart jfreechart = ChartFactory.createXYLineChart(title, xTitle, yTitle, xydataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();

        panel = new ChartPanel(jfreechart, true);
        ValueAxis rangeAxis = xyplot.getRangeAxis();//获取柱状
        rangeAxis.setLabelFont(new Font("黑体", Font.BOLD, 15));
        jfreechart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));
        jfreechart.getTitle().setFont(new Font("宋体", Font.BOLD, 20));//设置标题字体

        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setBackgroundPaint(Color.lightGray);
        xyPlot.setRangeGridlinesVisible(false);
        // 获显示线条对象
        XYLineAndShapeRenderer xyLineAndShapeRenderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
        xyLineAndShapeRenderer.setBaseShapesVisible(true);
        xyLineAndShapeRenderer.setDrawOutlines(true);
        xyLineAndShapeRenderer.setUseFillPaint(true);
        xyLineAndShapeRenderer.setBaseFillPaint(Color.white);
        // 设置折线加粗
        xyLineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(5F));
        xyLineAndShapeRenderer.setSeriesOutlineStroke(0, new BasicStroke(5.0F));
        // 设置折线拐点
        xyLineAndShapeRenderer.setSeriesShape(0,
                new java.awt.geom.Ellipse2D.Double(-5D, -5D, 10D, 10D));


        JFrame frame = new JFrame(title);
        frame.setLayout(new GridLayout(1, 1, 10, 10));
        frame.add(panel);    //添加折线图
        frame.setBounds(200, 200, 800, 600);
        frame.setVisible(true);
    }

    private static XYDataset createDataSet(List<List<double[]>> data, List<String> lineName){
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        for (int i = 0;i < data.size();i++){
            XYSeries timeseries = new XYSeries(lineName.get(i));
            List<double[]> line = data.get(i);
            for (int j = 0;j < line.size();j++){
                timeseries.add(line.get(j)[0], line.get(j)[1]);
            }
            xyseriescollection.addSeries(timeseries);
        }
        return xyseriescollection;
    }

    public static void main(String args[]) {
        List<List<double[]>> data = new ArrayList<>();
        List<String> lineName = new ArrayList<>();
        String title = "测试画图", xTitle = "x的标题", yTitle = "y的标题";
        Random rand = new Random();
        int lineCount = 5, lineSize = 10;
        for (int i = 0;i < lineCount;i++){
            lineName.add("line" + i);
            List<double[]> line = new ArrayList<>();
            for (int j = 0;j < lineSize;j++){
                line.add(new double[]{j, rand.nextDouble()});
            }
            data.add(line);
        }
        drawLineChart(data, lineName, title, xTitle, yTitle);

    }
}
