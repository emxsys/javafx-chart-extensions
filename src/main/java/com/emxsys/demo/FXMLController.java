package com.emxsys.demo;

import com.emxsys.chart.EnhancedLineChart;
import com.emxsys.chart.EnhancedScatterChart;
import com.emxsys.chart.LogLineChart;
import com.emxsys.chart.LogScatterChart;
import com.emxsys.chart.axis.LogarithmicAxis;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class FXMLController implements Initializable {

    @FXML
    AnchorPane chartPane;

    @FXML
    private ToggleGroup chartGroup;

    @FXML
    void selectChart(ActionEvent event) {
        System.out.println(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        chartGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            Chart chart;

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                RadioButton rb = (RadioButton) newValue;
                switch (rb.getId()) {
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
                        throw new IllegalStateException("unhandled radiobutton:" + rb.getId());
                }
                chartPane.getChildren().clear();
                chartPane.getChildren().add(fitToParent(chart));
            }
        });

    }

    private EnhancedScatterChart createScatterChart() {
        NumberAxis xAxis = new NumberAxis("X-Axis", 0d, 8.0d, 1.0d);
        NumberAxis yAxis = new NumberAxis("Y-Axis", 0.0d, 5.0d, 1.0d);
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
        return chart;
    }

    public EnhancedLineChart createLineChart() {
        NumberAxis xAxis = new NumberAxis("Values for X-Axis", 0, 3, 1);
        NumberAxis yAxis = new NumberAxis("Values for Y-Axis", 0, 3, 1);
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
        return chart;
    }

    private LogScatterChart createLogScatterChart() {
        final int NUM_POINTS = 20;
        final double MAX_X = 1000d;
        final double MAX_Y = 100d;
        LogarithmicAxis xAxis = new LogarithmicAxis("X-Axis", 1d, MAX_X, 1.0d);
        LogarithmicAxis yAxis = new LogarithmicAxis("Y-Axis", 1.0d, MAX_Y, 1.0d);
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
        return chart;
    }

    private LogLineChart createLogLineChart() {
        final int NUM_POINTS = 20;
        final double MAX_X = 1000d;
        final double MAX_Y = 100d;
        LogarithmicAxis xAxis = new LogarithmicAxis("X-Axis", 1d, MAX_X, 1.0d);
        LogarithmicAxis yAxis = new LogarithmicAxis("Y-Axis", 1.0d, MAX_Y, 1.0d);
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
