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

import java.util.Iterator;
import java.util.Objects;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Line;

/**
 *
 * @author Bruce Schubert
 * @param <X>
 * @param <Y>
 */
public final class Markers<X, Y> {

    private final XYChart chart;
    private final ObservableList<Node> plotChildren;
    private final ObservableList<ValueMarker> rangeMarkers;
    private final ObservableList<ValueMarker> domainMarkers;

    public Markers(XYChart chart, ObservableList<Node> plotChildren) {
        this.chart = chart;
        this.plotChildren = plotChildren;

        // Create lists that notify on changes
        domainMarkers = FXCollections.observableArrayList();
        rangeMarkers = FXCollections.observableArrayList();

        // Listen to list changes and re-plot
        rangeMarkers.addListener((InvalidationListener) observable -> layoutRangeMarkers());
        domainMarkers.addListener((InvalidationListener) observable -> layoutDomainMarkers());
    }

    /**
     * Adds a marker to the Y Axis.
     *
     * @param marker The marker to be added.
     */
    public void addRangeMarker(ValueMarker marker) {
        Objects.requireNonNull(marker, getClass().getSimpleName() + ": marker must not be null");
        if (rangeMarkers.contains(marker)) {
            return;
        }
        plotChildren.add(marker.getNode());
        rangeMarkers.add(marker);
    }

    public void clearRangeMarkers() {
        
        for (ValueMarker marker : rangeMarkers) {
            plotChildren.remove(marker.getNode());
        }
        rangeMarkers.clear();
    }

    public void removeRangeMarker(ValueMarker marker) {
        Objects.requireNonNull(marker, getClass().getSimpleName() + ": mmarker must not be null");
        if (marker.getNode() != null) {
            plotChildren.remove(marker.getNode());
        }
        rangeMarkers.remove(marker);
    }

    /**
     * Adds a marker to the X axis.
     *
     * @param marker The marker to be added.
     */
    public void addDomainMarker(ValueMarker marker) {
        Objects.requireNonNull(marker, getClass().getSimpleName() + ": marker must not be null");
        if (domainMarkers.contains(marker)) {
            return;
        }
        plotChildren.add(marker.getNode());
        domainMarkers.add(marker);
    }

    public void clearDomainMarkers() {
        for (ValueMarker marker : domainMarkers) {
            plotChildren.remove(marker.getNode());
        }
        domainMarkers.clear();
    }

    public void removeDomainMarker(ValueMarker marker) {
        Objects.requireNonNull(marker, getClass().getSimpleName() + ": mmarker must not be null");
        if (marker.getNode() != null) {
            plotChildren.remove(marker.getNode());
        }
        domainMarkers.remove(marker);
    }

    public void layoutMarkers() {
        layoutDomainMarkers();
        layoutRangeMarkers();
    }

    private void layoutDomainMarkers() {
        //super.layoutPlotChildren();
        ValueAxis xAxis = (ValueAxis) chart.getXAxis();
        ValueAxis yAxis = (ValueAxis) chart.getYAxis();
        for (ValueMarker verticalMarker : domainMarkers) {

            // Determine the line height
            double lower = yAxis.getLowerBound();
            Y lowerY = (Y) yAxis.toRealValue(lower);
            double upper = yAxis.getUpperBound();
            Y upperY = (Y) yAxis.toRealValue(upper);
            // Determine the placement of the line
            Line line = (Line) verticalMarker.getNode();
            line.setStartY(yAxis.getDisplayPosition(lowerY));
            line.setEndY(yAxis.getDisplayPosition(upperY));
            line.setStartX(xAxis.getDisplayPosition(verticalMarker.getValue()));
            line.setEndX(line.getStartX());
        }
    }

    private void layoutRangeMarkers() {
        //super.layoutPlotChildren();
        ValueAxis xAxis = (ValueAxis) chart.getXAxis();
        ValueAxis yAxis = (ValueAxis) chart.getYAxis();
        for (ValueMarker horizontalMarker : rangeMarkers) {
            // Determine the width
            double lower = xAxis.getLowerBound();
            X lowerX = (X) xAxis.toRealValue(lower);
            double upper = xAxis.getUpperBound();
            X upperX = (X) xAxis.toRealValue(upper);
            // Determine the placement
            Line line = (Line) horizontalMarker.getNode();
            line.setStartX(xAxis.getDisplayPosition(lowerX));
            line.setEndX(xAxis.getDisplayPosition(upperX));
            line.setStartY(yAxis.getDisplayPosition(horizontalMarker.getValue()));
            line.setEndY(line.getStartY());
        }
    }

}
