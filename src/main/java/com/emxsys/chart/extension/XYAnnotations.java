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

import java.util.ArrayList;
import java.util.Objects;
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

    private final ObservableList<XYAnnotation> fgAnnotations;
    private final ObservableList<XYAnnotation> bgAnnotations;


    public XYAnnotations(XYChart chart, ObservableList<Node> chartChildren,
        ObservableList<Node> plotChildren) {
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

        // Sync the foreground and background layout to the plotContent
        foreground.layoutXProperty().bind(plotContent.layoutXProperty());
        foreground.layoutYProperty().bind(plotContent.layoutYProperty());
        background.layoutXProperty().bind(plotContent.layoutXProperty());
        background.layoutYProperty().bind(plotContent.layoutYProperty());

        // Create lists that notify on changes
        fgAnnotations = FXCollections.observableArrayList();
        bgAnnotations = FXCollections.observableArrayList();

        // Listen to list changes and re-plot
        fgAnnotations.addListener((InvalidationListener) observable -> layoutForeground());
        bgAnnotations.addListener((InvalidationListener) observable -> layoutBackground());
    }


    public void add(XYAnnotation annotation, Layer layer) {
        Objects.requireNonNull(annotation, getClass().getSimpleName() + ": annotation must not be null");
        if (layer == Layer.FOREGROUND) {
            fgAnnotations.add(annotation);
            foreground.getChildren().add(annotation.getNode());
            foreground.requestLayout();
        }
        else {
            bgAnnotations.add(annotation);
            background.getChildren().add(annotation.getNode());
            background.requestLayout();
        }
        plotContent.requestLayout();
    }


    public void remove(XYAnnotation annotation, Layer layer) {
        Objects.requireNonNull(annotation, getClass().getSimpleName() + ": annotation must not be null");

        Group group = (layer == Layer.BACKGROUND) ? background : foreground;
        ObservableList<XYAnnotation> collection = (layer == Layer.BACKGROUND) ? bgAnnotations : fgAnnotations;

        if (annotation.getNode() != null) {
            group.getChildren().remove(annotation.getNode());
        }
        collection.remove(annotation);
    }


    public void clearAnnotations(Layer layer) {
        Group group = layer == Layer.BACKGROUND ? background : foreground;
        ObservableList<XYAnnotation> collection = (layer == Layer.BACKGROUND) ? bgAnnotations : fgAnnotations;
        for (XYAnnotation annotation : collection) {
            group.getChildren().remove(annotation.getNode());
        }
        collection.clear();
    }


    public void clearLineAnnotations(Layer layer) {
        ArrayList<XYAnnotation> copy = new ArrayList<>(layer == Layer.BACKGROUND ? bgAnnotations : fgAnnotations);

        for (XYAnnotation annotation : copy) {
            if (annotation instanceof XYLineAnnotation) {
                remove(annotation, layer);
            }
        }
    }


    public void clearTextAnnotations(Layer layer) {
        ArrayList<XYAnnotation> copy = new ArrayList<>(layer == Layer.BACKGROUND ? bgAnnotations : fgAnnotations);

        for (XYAnnotation annotation : copy) {
            if (annotation instanceof XYTextAnnotation) {
                remove(annotation, layer);
            }
        }
    }


    public void clearPolygonAnnotations(Layer layer) {
        ArrayList<XYAnnotation> copy = new ArrayList<>(layer == Layer.BACKGROUND ? bgAnnotations : fgAnnotations);

        for (XYAnnotation annotation : copy) {
            if (annotation instanceof XYPolygonAnnotation) {
                remove(annotation, layer);
            }
        }
    }


    public void clearImageAnnotations(Layer layer) {
        ArrayList<XYAnnotation> copy = new ArrayList<>(layer == Layer.BACKGROUND ? bgAnnotations : fgAnnotations);

        for (XYAnnotation annotation : copy) {
            if (annotation instanceof XYImageAnnotation) {
                remove(annotation, layer);
            }
        }
    }


    public void layoutAnnotations() {
        layoutBackground();
        layoutForeground();
    }


    private void layoutForeground() {
        ValueAxis xAxis = (ValueAxis) chart.getXAxis();
        ValueAxis yAxis = (ValueAxis) chart.getYAxis();
        for (XYAnnotation annotation : fgAnnotations) {
            annotation.layoutAnnotation(xAxis, yAxis);
        }
    }


    private void layoutBackground() {
        ValueAxis xAxis = (ValueAxis) chart.getXAxis();
        ValueAxis yAxis = (ValueAxis) chart.getYAxis();
        for (XYAnnotation annotation : bgAnnotations) {
            annotation.layoutAnnotation(xAxis, yAxis);
        }
    }

}
