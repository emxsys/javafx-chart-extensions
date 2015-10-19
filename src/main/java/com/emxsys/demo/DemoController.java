package com.emxsys.demo;

import com.emxsys.chart.EnhancedLineChart;
import com.emxsys.chart.EnhancedScatterChart;
import com.emxsys.chart.LogLineChart;
import com.emxsys.chart.LogScatterChart;
import com.emxsys.chart.extension.AnnotationExtension;
import com.emxsys.chart.extension.LogarithmicAxis;
import com.emxsys.chart.extension.MarkerExtension;
import com.emxsys.chart.extension.SubtitleExtension;
import com.emxsys.chart.extension.ValueMarker;
import com.emxsys.chart.extension.XYAnnotations;
import com.emxsys.chart.extension.XYAnnotations.Layer;
import com.emxsys.chart.extension.XYLineAnnotation;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


/**
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

    private XYChart chart;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Handle chart type selections
        chartGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            chartPane.getChildren().clear();

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
            initSubtitle(cbSubtitles.isSelected());
            initMarkers(cbMarkers.isSelected());
            initLineAnnotation(cbLineAnnotations.isSelected());
        });

        // Handle Subtitle checkbox
        cbSubtitles.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            initSubtitle(newValue);
        });

        // Handle Markers checkbox
        cbMarkers.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            initMarkers(newValue);
        });
        // Handle Line Annotation checkbox
        cbLineAnnotations.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            initLineAnnotation(newValue);
        });
    }


    private void initSubtitle(Boolean enabled) {
        if (chart instanceof SubtitleExtension) {
            ((SubtitleExtension) chart).setSubtitle(enabled ? "Subtitle" : null);
        }
    }


    private void initLineAnnotation(Boolean enabled) {
        if (chart instanceof AnnotationExtension) {
            if (enabled) {
                ValueAxis xAxis = (ValueAxis) chart.getXAxis();
                ValueAxis yAxis = (ValueAxis) chart.getYAxis();
                ((AnnotationExtension) chart).getAnnotations().add(
                    new XYLineAnnotation(
                        xAxis.getLowerBound(), yAxis.getLowerBound(),
                        xAxis.getUpperBound(), yAxis.getUpperBound(),
                        2.0,
                        Color.RED),
                    Layer.BACKGROUND);
                chart.requestLayout();
            }
            else {
                ((AnnotationExtension) chart).getAnnotations().clearLineAnnotations(Layer.BACKGROUND);
            }
        }
    }


    private void initMarkers(Boolean enabled) {
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

                ((MarkerExtension) chart).getMarkers().addRangeMarker(new ValueMarker(minY, String.format("Series 1 Min: %1$.1f", minY), Pos.TOP_LEFT));
                ((MarkerExtension) chart).getMarkers().addRangeMarker(new ValueMarker(avgY, String.format("Series 1 Avg: %1$.1f", avgY), Pos.TOP_CENTER));
                ((MarkerExtension) chart).getMarkers().addRangeMarker(new ValueMarker(maxY, String.format("Series 1 Max: %1$.1f", maxY), Pos.BOTTOM_LEFT));
            }
            else {
                ((MarkerExtension) chart).getMarkers().clearDomainMarkers();
                ((MarkerExtension) chart).getMarkers().clearRangeMarkers();
            }
        }

    }


    private EnhancedScatterChart createScatterChart() {
        NumberAxis xAxis = new NumberAxis("X-Axis (Domain)", 0d, 8.0d, 1.0d);
        NumberAxis yAxis = new NumberAxis("Y-Axis (Range)", 0.0d, 5.0d, 1.0d);
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
        EnhancedScatterChart chart = new EnhancedScatterChart(xAxis, yAxis, data);
        chart.setTitle("EnhancedScatterChart");
        return chart;
    }


    public EnhancedLineChart createLineChart() {
        NumberAxis xAxis = new NumberAxis("Values for X-Axis (Domain)", 0, 3, 1);
        NumberAxis yAxis = new NumberAxis("Values for Y-Axis (Range)", 0, 3, 1);
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
        EnhancedLineChart chart = new EnhancedLineChart(xAxis, yAxis, lineChartData);
        chart.setTitle("EnhancedLineChart");
        return chart;
    }


    private LogScatterChart createLogScatterChart() {
        final int NUM_POINTS = 20;
        final double MAX_X = 1000d;
        final double MAX_Y = 100d;
        LogarithmicAxis xAxis = new LogarithmicAxis("X-Axis (Domain)", 1d, MAX_X, 1.0d);
        LogarithmicAxis yAxis = new LogarithmicAxis("Y-Axis (Range)", 1.0d, MAX_Y, 1.0d);
        ObservableList<XYChart.Series> dataset = FXCollections.observableArrayList();
        ScatterChart.Series series1 = new EnhancedScatterChart.Series();
        series1.setName("Log Series 1");
        double xInterval = Math.log10(MAX_X) / NUM_POINTS;
        double yInterval = MAX_Y / NUM_POINTS;
        for (int i = 1; i <= NUM_POINTS; i++) {
            series1.getData().add(new XYChart.Data(
                Math.pow(10, i * xInterval),
                i * yInterval)
            );
        }
        dataset.add(series1);

        LogScatterChart chart = new LogScatterChart(xAxis, yAxis, dataset);
        Platform.runLater(() -> {
            chart.setTitle("LogScatterChart");
        });

        return chart;
    }


    private LogLineChart createLogLineChart() {
        final int NUM_POINTS = 20;
        final double MAX_X = 1000d;
        final double MAX_Y = 100d;
        LogarithmicAxis xAxis = new LogarithmicAxis("X-Axis (Domain)", 1d, MAX_X, 1.0d);
        LogarithmicAxis yAxis = new LogarithmicAxis("Y-Axis (Range)", 1.0d, MAX_Y, 1.0d);
        ObservableList<XYChart.Series> dataset = FXCollections.observableArrayList();
        ScatterChart.Series series1 = new EnhancedScatterChart.Series();
        series1.setName("Log Series 1");
        double xInterval = Math.log10(MAX_X) / NUM_POINTS;
        double yInterval = MAX_Y / NUM_POINTS;
        for (int i = 1; i <= NUM_POINTS; i++) {
            series1.getData().add(new XYChart.Data(
                Math.pow(10, i * xInterval),
                i * yInterval)
            );
        }
        dataset.add(series1);
        LogLineChart chart = new LogLineChart(xAxis, yAxis, dataset);
        chart.setTitle("LogLineChart");

        return chart;
    }


    public static Node fitToParent(Node child) {
        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setBottomAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);
        return child;
    }
}
