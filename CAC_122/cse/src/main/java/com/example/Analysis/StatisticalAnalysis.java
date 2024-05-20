package com.example.Analysis;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

public class StatisticalAnalysis {

        public static void performAnalysis(Table table) {
            // Columns to be analyzed
            String[] columns = {"heart rate", "Systolic blood pressure", "Diastolic blood pressure", "Respiratory rate", "temperature"};
    
            // Calculate and print statistics
            Table statisticsTable = calculateStatistics(table, columns);
            System.out.println(statisticsTable.print());
            System.out.println("\n====================\n");

            // Calculate and print correlation matrix
            Table correlationTable = calculateCorrelations(table, columns);
            System.out.println(correlationTable.print());

            // Create and display box plots
            createBoxPlot(table, columns);
        }
    
        // Method to calculate the mode of a column
        private static double getMode(DoubleColumn column) {
            double[] values = column.asDoubleArray();
            Map<Double, Integer> frequencyMap = new HashMap<>();
    
            for (double value : values) {
                frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
            }
    
            double mode = Double.NaN;
            int maxCount = 0;
    
            for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mode = entry.getKey();
                }
            }
    
            return mode;
        }
    
        // Method to calculate statistics for specified columns
        public static Table calculateStatistics(Table table, String[] columns) {
            StringColumn columnNames = StringColumn.create("Column Name");
        DoubleColumn means = DoubleColumn.create("Mean");
        DoubleColumn medians = DoubleColumn.create("Median");
        DoubleColumn modes = DoubleColumn.create("Mode");
        DoubleColumn stdDevs = DoubleColumn.create("Standard Deviation");
        DoubleColumn q1s = DoubleColumn.create("Q1");
        DoubleColumn q3s = DoubleColumn.create("Q3");
        DoubleColumn skewnesses = DoubleColumn.create("Skewness");
        DoubleColumn kurtoses = DoubleColumn.create("Kurtosis");

        for (String columnName : columns) {
            if (table.columnNames().contains(columnName)) {
                DoubleColumn column = table.doubleColumn(columnName);

                
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (double value : column) {
                    stats.addValue(value);
                }

                columnNames.append(columnName);
                means.append(stats.getMean());
                System.out.println(stats.getMean());
                medians.append(stats.getPercentile(50));
                modes.append(getMode(column));
                stdDevs.append(stats.getStandardDeviation());
                q1s.append(stats.getPercentile(25));
                q3s.append(stats.getPercentile(75));
                Skewness skewness = new Skewness();
                Kurtosis kurtosis = new Kurtosis();
                skewnesses.append(skewness.evaluate(stats.getValues()));
                kurtoses.append(kurtosis.evaluate(stats.getValues()));
            }
        }

        // Create and return a table with the calculated statistics
        Table statisticsTable = Table.create("Statistics Table", columnNames, means, medians, modes, stdDevs,q1s, q3s,kurtoses,skewnesses);
        return statisticsTable;
    }
    
        // Method to calculate correlation matrix for specified columns
        public static Table calculateCorrelations(Table table, String[] columns) {
            int n = columns.length;
            DoubleColumn[] correlationColumns = new DoubleColumn[n];
            for (int i = 0; i < n; i++) {
                correlationColumns[i] = DoubleColumn.create(columns[i], n);
            }
    
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) {
                        correlationColumns[i].set(j, 1.0);
                    } else {
                        DoubleColumn col1 = table.doubleColumn(columns[i]);
                        DoubleColumn col2 = table.doubleColumn(columns[j]);
                        double correlation = calculatePearsonCorrelation(col1, col2);
                        correlationColumns[i].set(j, correlation);
                    }
                }
            }
    
            // Create and return a table with the correlation matrix
            Table correlationTable = Table.create("Correlation Matrix");
            for (int i = 0; i < n; i++) {
                correlationTable.addColumns(correlationColumns[i]);
            }
    
            return correlationTable;
        }
    
        // Method to calculate Pearson correlation coefficient
        private static double calculatePearsonCorrelation(DoubleColumn col1, DoubleColumn col2) {
            int n = col1.size();
            double sum1 = col1.sum();
            double sum2 = col2.sum();
            double sum1Sq = col1.multiply(col1).sum();
            double sum2Sq = col2.multiply(col2).sum();
            double pSum = col1.multiply(col2).sum();
    
            double num = pSum - (sum1 * sum2 / n);
            double den = Math.sqrt((sum1Sq - (sum1 * sum1 / n)) * (sum2Sq - (sum2 * sum2 / n)));
    
            return (den == 0) ? 0 : num / den;
        }

        // Method to create and display box plots for specified columns
        public static void createBoxPlot(Table table, String[] columns) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        for (String column : columns) {
            DoubleColumn doubleColumn = table.doubleColumn(column);
            dataset.add(doubleColumn.asList(), "Values", column);
        }

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                "Box Plot",
                "Category", 
                "Value",
                dataset,
                false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);

        ApplicationFrame frame = new ApplicationFrame("Box Plot Example");
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        frame.setContentPane(chartPanel);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }
    }


    