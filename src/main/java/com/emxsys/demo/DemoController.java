/*
 * Copyright (c) 2015, Bruce Schubert <bruce@emxsys.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     - Neither the name of Bruce Schubert, Emxsys nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.emxsys.demo;

import com.emxsys.chart.EnhancedLineChart;
import com.emxsys.chart.EnhancedScatterChart;
import com.emxsys.chart.LogLineChart;
import com.emxsys.chart.LogScatterChart;
import com.emxsys.chart.extension.*;
import com.emxsys.chart.extension.XYAnnotations.Layer;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * FXML controller class for the JavaFX Chart Extensions DemoApp.
 *
 * @author Bruce Schubert
 */
public class DemoController implements Initializable {

    @FXML
    AnchorPane chartPane;

    @FXML
    private ToggleGroup chartGroup;

    @FXML
    private CheckBox cbSubtitles;

    @FXML
    private CheckBox cbMarkers;

    @FXML
    private CheckBox cbTextAnnotations;

    @FXML
    private CheckBox cbImageAnnotations;

    @FXML
    private CheckBox cbLineAnnotations;

    @FXML
    private CheckBox cbPolygonAnnotations;

    @FXML
    private CheckBox cbFieldAnnotations;

    private XYChart chart;

    /**
     * Initializer called by the FXML loader.
     *
     * @param url Not used.
     * @param rb Not used.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Handle chart type selections
        chartGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            chartPane.getChildren().clear();

            // Create the selected chart
            RadioButton rb1 = (RadioButton) newValue;
            switch (rb1.getId()) {
                case "rbScatterChart":
                    chart = createScatterChart();
                    break;
                case "rbLineChart":
                    chart = createLineChart();
                    break;
                case "rbLogScatterChart":
                    chart = createLogScatterChart();
                    break;
                case "rbLogLineChart":
                    chart = createLogLineChart();
                    break;
                default:
                    chart = null;
                    throw new IllegalStateException("unhandled radiobutton:" + rb1.getId());
            }
            chartPane.getChildren().add(fitToParent(chart));

            // Show the selected extensions
            showSubtitle(cbSubtitles.isSelected());
            initPolygonAnnotations(cbPolygonAnnotations.isSelected());
            initFieldAnnotations(cbFieldAnnotations.isSelected());
            showImageAnnotations(cbImageAnnotations.isSelected());
            showLineAnnotations(cbLineAnnotations.isSelected());
            showTextAnnotations(cbTextAnnotations.isSelected());
            showMarkers(cbMarkers.isSelected());
        });

        // Handle Subtitle checkbox
        cbSubtitles.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            showSubtitle(newValue);
        });

        // Handle Markers checkbox
        cbMarkers.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            showMarkers(newValue);
        });
        // Handle Line Annotation checkbox
        cbTextAnnotations.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            showTextAnnotations(newValue);
        });
        // Handle Line Annotation checkbox
        cbLineAnnotations.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            showLineAnnotations(newValue);
        });
        // Handle Polygon Annotation checkbox
        cbPolygonAnnotations.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            initPolygonAnnotations(newValue);
        });
        // Handle Field Annotation checkbox
        cbFieldAnnotations.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            initFieldAnnotations(newValue);
        });
        // Handle Image Annotation checkbox
        cbImageAnnotations.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            showImageAnnotations(newValue);
        });
    }

    /**
     * Shows a subtitle.
     *
     * @param enabled If true then shows a subtitle.
     */
    private void showSubtitle(Boolean enabled) {
        if (chart instanceof SubtitleExtension) {
            if (enabled) {
                ((SubtitleExtension) chart).addSubtitle("First Subtitle");
                ((SubtitleExtension) chart).addSubtitle("Another Subtitle");
            } else {
                ((SubtitleExtension) chart).clearSubtitles();
            }
        }
    }

    /**
     * Shows a red line annotation on the background layer from the upper left
     * to the lower right.
     *
     * @param enabled Shows a line annotation if true.
     */
    private void showLineAnnotations(Boolean enabled) {
        if (chart instanceof AnnotationExtension) {
            if (enabled) {

                ValueAxis xAxis = (ValueAxis) chart.getXAxis();
                ValueAxis yAxis = (ValueAxis) chart.getYAxis();
                ((AnnotationExtension) chart).getAnnotations().add(new XYLineAnnotation(
                        xAxis.getUpperBound(), yAxis.getLowerBound(),
                        xAxis.getLowerBound(), yAxis.getUpperBound(),
                        2.0,
                        Color.RED),
                        Layer.BACKGROUND);
                chart.requestLayout();
            } else {
                ((AnnotationExtension) chart).getAnnotations().clearLineAnnotations(Layer.BACKGROUND);
            }
        }
    }

    /**
     * Shows the Emxsys logo on the background layer at the chart's origin and
     * anchored to the image's bottom left corner.
     *
     * @param enabled Shows the image if true.
     */
    private void showImageAnnotations(Boolean enabled) {
        if (chart instanceof AnnotationExtension) {
            if (enabled) {

                ValueAxis xAxis = (ValueAxis) chart.getXAxis();
                ValueAxis yAxis = (ValueAxis) chart.getYAxis();

                Image logo = new Image(getClass().getResourceAsStream("/images/emxsys_small_rect.jpg"));

                ((AnnotationExtension) chart).getAnnotations().add(new XYImageAnnotation(
                        logo, xAxis.getLowerBound(), yAxis.getLowerBound(), Pos.BOTTOM_LEFT), Layer.BACKGROUND);

                chart.requestLayout();
            } else {
                ((AnnotationExtension) chart).getAnnotations().clearImageAnnotations(Layer.BACKGROUND);
            }
        }
    }

    /**
     * Shows text on the background layer at the upper left, and anchored to the
     * label's top left corner; and text on the foreground layer at the lower
     * right, and anchored to the label's bottom right corner.
     *
     * @param enabled Shows the text labels if true.
     */
    private void showTextAnnotations(Boolean enabled) {
        if (chart instanceof AnnotationExtension) {
            if (enabled) {
                ValueAxis xAxis = (ValueAxis) chart.getXAxis();
                ValueAxis yAxis = (ValueAxis) chart.getYAxis();

                ((AnnotationExtension) chart).getAnnotations().add(new XYTextAnnotation(
                        "Text on Background", xAxis.getLowerBound(), yAxis.getUpperBound(), Pos.TOP_LEFT),
                        Layer.BACKGROUND);

                ((AnnotationExtension) chart).getAnnotations().add(new XYTextAnnotation(
                        "Text on Foreground", xAxis.getUpperBound(), yAxis.getLowerBound(), Pos.BOTTOM_RIGHT),
                        Layer.FOREGROUND);

                chart.requestLayout();
            } else {
                ((AnnotationExtension) chart).getAnnotations().clearTextAnnotations(Layer.BACKGROUND);
                ((AnnotationExtension) chart).getAnnotations().clearTextAnnotations(Layer.FOREGROUND);
            }
        }
    }

    /**
     * Shows a solid orange polygon on the background layer, and a translucent
     * green polygon on the foreground layer; both polygons have a black border.
     *
     * @param enabled Shows the two polygons if true;
     */
    private void initPolygonAnnotations(Boolean enabled) {
        if (chart instanceof AnnotationExtension) {
            if (enabled) {
                ValueAxis xAxis = (ValueAxis) chart.getXAxis();
                ValueAxis yAxis = (ValueAxis) chart.getYAxis();

                ((AnnotationExtension) chart).getAnnotations().add(new XYPolygonAnnotation(new double[]{
                    xAxis.getLowerBound(), yAxis.getLowerBound(),
                    xAxis.getUpperBound(), yAxis.getUpperBound(),
                    xAxis.getLowerBound(), yAxis.getUpperBound()},
                        2.0,
                        Color.BLACK,
                        Color.DARKORANGE),
                        Layer.BACKGROUND);

                ((AnnotationExtension) chart).getAnnotations().add(new XYPolygonAnnotation(new double[]{
                    xAxis.getLowerBound(), yAxis.getLowerBound(),
                    xAxis.getUpperBound(), yAxis.getUpperBound(),
                    xAxis.getUpperBound(), yAxis.getLowerBound()},
                        2.0,
                        Color.BLACK,
                        new Color(0, 1, 0, 0.3)),
                        Layer.FOREGROUND);

                chart.requestLayout();
            } else {
                ((AnnotationExtension) chart).getAnnotations().clearPolygonAnnotations(Layer.BACKGROUND);
                ((AnnotationExtension) chart).getAnnotations().clearPolygonAnnotations(Layer.FOREGROUND);
            }
        }
    }

    /**
     * Shows a solid orange horizontal field background layer, and a translucent
     * green vertical field on the foreground layer.
     *
     * @param enabled Shows the two polygons if true;
     */
    private void initFieldAnnotations(Boolean enabled) {
        if (chart instanceof AnnotationExtension) {
            if (enabled) {
                ((AnnotationExtension) chart).getAnnotations().add(
                        new XYFieldAnnotation(1, 2, Orientation.HORIZONTAL, 0, null, Color.DARKORANGE),
                        Layer.BACKGROUND);

                ((AnnotationExtension) chart).getAnnotations().add(
                        new XYFieldAnnotation(2, 3, Orientation.VERTICAL, 0, null, new Color(0, 1, 0, 0.3)),
                        Layer.FOREGROUND);

                chart.requestLayout();
            } else {
                ((AnnotationExtension) chart).getAnnotations().clearFieldAnnotations(Layer.BACKGROUND);
                ((AnnotationExtension) chart).getAnnotations().clearFieldAnnotations(Layer.FOREGROUND);
            }
        }
    }

    /**
     * Shows the min, max and average value markers for series #1.
     *
     * @param enabled Shows the value markers if true.
     */
    private void showMarkers(Boolean enabled) {
        // Test the chart for  the MarkerExtension
        if (chart instanceof MarkerExtension) {
            if (enabled) {
                XYChart.Series series1 = (XYChart.Series) chart.getData().get(0);
                ObservableList data = series1.getData();
                double minX = Double.MAX_VALUE;
                double minY = Double.MAX_VALUE;
                double maxX = Double.MIN_VALUE;
                double maxY = Double.MIN_VALUE;
                double avgX = 0;
                double avgY = 0;
                double totalX = 0;
                double totalY = 0;
                int numItems = 0;

                for (Iterator it = data.iterator(); it.hasNext();) {
                    XYChart.Data xy = (XYChart.Data) it.next();
                    double x = (double) xy.getXValue();
                    double y = (double) xy.getYValue();
                    minX = Math.min(x, minX);
                    minY = Math.min(y, minY);
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                    totalX += x;
                    totalY += y;
                    numItems++;
                }
                avgX = totalX / numItems;
                avgY = totalY / numItems;

                ((MarkerExtension) chart).getMarkers().addRangeMarker(new ValueMarker(maxY, String.format("Series 1 Min: %1$.1f", maxY), Pos.TOP_RIGHT));
                ((MarkerExtension) chart).getMarkers().addRangeMarker(new ValueMarker(avgY, String.format("Series 1 Avg: %1$.1f", avgY), Pos.TOP_CENTER));
                ((MarkerExtension) chart).getMarkers().addRangeMarker(new ValueMarker(minY, String.format("Series 1 Max: %1$.1f", minY), Pos.BOTTOM_LEFT));
                ((MarkerExtension) chart).getMarkers().addDomainMarker(new ValueMarker(3, "Fixed", Pos.BOTTOM_RIGHT));
            } else {
                ((MarkerExtension) chart).getMarkers().clearDomainMarkers();
                ((MarkerExtension) chart).getMarkers().clearRangeMarkers();
            }
        }

    }

    /**
     * Creates a scatter chart that includes the JavaFX Chart Extensions.
     *
     * @return An enhanced scatter chart.
     */
    public static EnhancedScatterChart createScatterChart() {
        ObservableList<XYChart.Series> data = FXCollections.observableArrayList(
                new EnhancedScatterChart.Series("Series 1",
                        FXCollections.<EnhancedScatterChart.Data>observableArrayList(
                                new XYChart.Data(0.2, 3.5),
                                new XYChart.Data(0.7, 4.6),
                                new XYChart.Data(1.8, 1.7),
                                new XYChart.Data(2.1, 2.8),
                                new XYChart.Data(4.0, 2.2),
                                new XYChart.Data(4.1, 2.6),
                                new XYChart.Data(4.5, 2.0),
                                new XYChart.Data(6.0, 3.0),
                                new XYChart.Data(7.0, 2.0),
                                new XYChart.Data(7.8, 4.0)
                        ))
        );
        NumberAxis xAxis = new NumberAxis("X-Axis (Domain)", 0d, 8.0d, 1.0d);
        NumberAxis yAxis = new NumberAxis("Y-Axis (Range)", 0.0d, 5.0d, 1.0d);
        EnhancedScatterChart chart = new EnhancedScatterChart(xAxis, yAxis, data);
        chart.setTitle("Scatter Chart");
        return chart;
    }

    /**
     * Creates a line chart that includes the JavaFX Chart Extensions.
     *
     * @return An enhanced line chart.
     */
    public static EnhancedLineChart createLineChart() {
        ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections.observableArrayList(
                new EnhancedLineChart.Series<>("Series 1",
                        FXCollections.observableArrayList(
                                new XYChart.Data<>(0.0, 1.0),
                                new XYChart.Data<>(1.2, 1.4),
                                new XYChart.Data<>(2.2, 1.9),
                                new XYChart.Data<>(2.7, 2.3),
                                new XYChart.Data<>(2.9, 0.5)
                        )),
                new EnhancedLineChart.Series<>("Series 2",
                        FXCollections.observableArrayList(
                                new XYChart.Data<>(0.0, 1.6),
                                new XYChart.Data<>(0.8, 0.4),
                                new XYChart.Data<>(1.4, 2.9),
                                new XYChart.Data<>(2.1, 1.3),
                                new XYChart.Data<>(2.6, 0.9)
                        ))
        );
        NumberAxis xAxis = new NumberAxis("Values for X-Axis (Domain)", 0, 3, 1);
        NumberAxis yAxis = new NumberAxis("Values for Y-Axis (Range)", 0, 3, 1);
        EnhancedLineChart chart = new EnhancedLineChart(xAxis, yAxis, lineChartData);
        chart.setTitle("Line Chart");
        return chart;
    }

    /**
     * Creates a logarithmic scatter chart that includes the JavaFX Chart
     * Extensions.
     *
     * @return An enhanced logarithmic scatter chart.
     */
    public static LogScatterChart createLogScatterChart() {
        final int NUM_POINTS = 20;
        final double MIN_X = 1d;
        final double MAX_X = 1000d;
        final double MIN_Y = 1d;
        final double MAX_Y = 100d;
        final double X_TICK_UNIT = 1d;  // only working value is 1 at this time
        final double Y_TICK_UNIT = 1d;

        // Create the dataset
        ObservableList<XYChart.Series> dataset = FXCollections.observableArrayList();
        ScatterChart.Series series1 = new ScatterChart.Series();
        series1.setName("Series 1");
        double xInterval = Math.log10(MAX_X) / NUM_POINTS;
        double yInterval = MAX_Y / NUM_POINTS;
        for (int i = 1; i <= NUM_POINTS; i++) {
            series1.getData().add(new XYChart.Data(
                    Math.pow(10, i * xInterval),
                    i * yInterval)
            );
        }
        dataset.add(series1);

        // Create the chart
        LogarithmicAxis xAxis = new LogarithmicAxis("X-Axis (Domain)", MIN_X, MAX_X, X_TICK_UNIT);
        LogarithmicAxis yAxis = new LogarithmicAxis("Y-Axis (Range)", MIN_Y, MAX_Y, Y_TICK_UNIT);
        LogScatterChart chart = new LogScatterChart(xAxis, yAxis, dataset);
        chart.setTitle("Logarithmic Scatter Chart");

        return chart;
    }

    /**
     * Creates a logarithmic line chart that includes the JavaFX Chart
     * Extensions.
     *
     * @return An enhanced logarithmic line chart.
     */
    public static LogLineChart createLogLineChart() {
        final int NUM_POINTS = 20;
        final double MIN_X = 10d;
        final double MAX_X = 1000d;
        final double MIN_Y = 1d;
        final double MAX_Y = 100d;
        final double X_TICK_UNIT = 1d;  // only acceptable value is 1 at this time
        final double Y_TICK_UNIT = 1d;
        // Create the dataset
        ObservableList<XYChart.Series> dataset = FXCollections.observableArrayList();
        LineChart.Series series1 = new LineChart.Series();
        series1.setName("Series 1");
        double yInterval = Math.log10(MAX_Y) / NUM_POINTS;
        double xInterval = MAX_X / NUM_POINTS;
        for (int i = 1; i < NUM_POINTS; i++) {
            series1.getData().add(new XYChart.Data(
                    i * xInterval,
                    Math.pow(10, i * yInterval))
            );
        }
        dataset.add(series1);

        LineChart.Series series2 = new LineChart.Series();
        series2.setName("Series 2");
        xInterval = Math.log10(MAX_X) / NUM_POINTS;
        yInterval = Math.log10(MAX_Y) / NUM_POINTS;
        for (int i = 0; i < NUM_POINTS; i++) {
            series2.getData().add(new XYChart.Data(
                    Math.pow(10, i * xInterval),
                    Math.pow(10, i * yInterval))
            );
        }
        dataset.add(series2);

        // Create the chart
        LogarithmicAxis xAxis = new LogarithmicAxis("X-Axis (Domain)", MIN_X, MAX_X, X_TICK_UNIT);
        LogarithmicAxis yAxis = new LogarithmicAxis("Y-Axis (Range)", MIN_Y, MAX_Y, Y_TICK_UNIT);
        LogLineChart chart = new LogLineChart(xAxis, yAxis, dataset);
        chart.setTitle("Logarithmic Line Chart");

        return chart;
    }

    /**
     * Sets the anchors to 0 for all fours sides of the child node so that it
     * conforms to the parent's size.
     *
     * @param child Node to have the anchors set.
     * @return The modified child node.
     */
    public static Node fitToParent(Node child) {
        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setBottomAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);
        return child;
    }
}
