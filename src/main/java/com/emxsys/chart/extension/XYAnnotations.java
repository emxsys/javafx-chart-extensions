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
 *     - Neither the name of Bruce Schubert,  nor the names of its 
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
package com.emxsys.chart.extension;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Line;

/**
 *
 * @author Bruce Schubert
 */
public class XYAnnotations {

    public enum Layer {

        BACKGROUND, FOREGROUND
    };

    private final XYChart chart;
    private final ObservableList<Node> chartChildren;
    private final ObservableList<Node> plotChildren;
    private final Group plotContent;

    private final Group background = new Group();
    private final Group foreground = new Group();

    private final ObservableList<XYTextAnnotation> texts;
    private final ObservableList<XYLineAnnotation> lines;
    private final ObservableList<XYImageAnnotation> images;
    private final ObservableList<XYPolygonAnnotation> polygons;

    public XYAnnotations(XYChart chart, ObservableList<Node> chartChildren, ObservableList<Node> plotChildren) {
        this.chart = chart;
        this.chartChildren = chartChildren;
        this.plotChildren = plotChildren;

        // The chartChildren contains a plotBackground, plotArea, XAxis and YAxis.
        // Find the plotArea and add our background and foreground on either side 
        // of the plotContent, which is the last item in the plotArea.
        Group plotArea = (Group) chartChildren.get(1);
        int plotContentIndex = plotArea.getChildren().size() - 1;
        if (plotContentIndex < 0) {
            throw new IllegalStateException("plotArea is empty!");
        }
        plotContent = (Group) plotArea.getChildren().get(plotContentIndex);
        plotArea.getChildren().add(plotContentIndex + 1, foreground);
        plotArea.getChildren().add(0, background);
//        plotArea.getChildren().add(plotContentIndex, background);

        // Create lists that notify on changes
        texts = FXCollections.observableArrayList();
        lines = FXCollections.observableArrayList();
        images = FXCollections.observableArrayList();
        polygons = FXCollections.observableArrayList();

        // Listen to list changes and re-plot
        texts.addListener((InvalidationListener) observable -> layoutTexts());
        lines.addListener((InvalidationListener) observable -> layoutLines());
        images.addListener((InvalidationListener) observable -> layoutImages());
        polygons.addListener((InvalidationListener) observable -> layoutPolygons());
    }

    public void add(XYTextAnnotation annotation, Layer backgroundOrForeground) {
        Group layer = backgroundOrForeground == Layer.BACKGROUND ? background : foreground;
        layer.getChildren().add(annotation.getNode());
        this.texts.add(annotation);
    }

    public void add(XYLineAnnotation annotation, Layer backgroundOrForeground) {
        Group layer = backgroundOrForeground == Layer.BACKGROUND ? background : foreground;
        layer.getChildren().add(annotation.getNode());
        this.lines.add(annotation);
    }

    public void add(XYImageAnnotation annotation, Layer backgroundOrForeground) {
        Group layer = backgroundOrForeground == Layer.BACKGROUND ? background : foreground;
        layer.getChildren().add(annotation.getNode());
        this.images.add(annotation);
    }

    public void add(XYPolygonAnnotation annotation, Layer backgroundOrForeground) {
        Group layer = backgroundOrForeground == Layer.BACKGROUND ? background : foreground;
        layer.getChildren().add(annotation.getNode());
        this.polygons.add(annotation);

    }

    public void layoutAnnotations() {
        background.setLayoutX(plotContent.getLayoutX());
        background.setLayoutY(plotContent.getLayoutY());

        foreground.setLayoutX(plotContent.getLayoutX());
        foreground.setLayoutY(plotContent.getLayoutY());

        layoutPolygons();
        layoutLines();
        layoutImages();
        layoutTexts();
    }

    private void layoutTexts() {

        ValueAxis xAxis = (ValueAxis) chart.getXAxis();
        ValueAxis yAxis = (ValueAxis) chart.getYAxis();
        for (XYTextAnnotation annotation : texts) {
            annotation.layoutText(xAxis, yAxis);
        }
    }

    private void layoutLines() {

        ValueAxis xAxis = (ValueAxis) chart.getXAxis();
        ValueAxis yAxis = (ValueAxis) chart.getYAxis();
        for (XYLineAnnotation annotation : lines) {
            annotation.layoutLine(xAxis, yAxis);
        }
    }

    private void layoutImages() {

        ValueAxis xAxis = (ValueAxis) chart.getXAxis();
        ValueAxis yAxis = (ValueAxis) chart.getYAxis();
        for (XYImageAnnotation annotation : images) {
            annotation.layoutImage(xAxis, yAxis);
        }
    }

    private void layoutPolygons() {

        ValueAxis xAxis = (ValueAxis) chart.getXAxis();
        ValueAxis yAxis = (ValueAxis) chart.getYAxis();
        for (XYPolygonAnnotation annotation : polygons) {
            annotation.layoutPolygon(xAxis, yAxis);
        }
    }

}
