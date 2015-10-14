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
package com.emxsys.chart.extension;

import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

/**
 * Major grid line support for logarithmic charts.
 *
 * @author Bruce Schubert
 * @param <X>
 * @param <Y>
 */
public class MajorLogGridLines<X,Y> {

    private Group plotArea = null;
    // References to XYChart grid lines
    private Path horzGridLines = null;
    private Path vertGridLines = null;
    // Major log grid lines added to XYChart
    private Path majorHorzGridLines = new Path();
    private Path majorVertGridLines = new Path();
    private final XYChart<X,Y> chart;

    public MajorLogGridLines(XYChart<X,Y> chart, ObservableList<Node> chartChildren) {
        this.chart = chart;
        
        majorHorzGridLines.getStyleClass().setAll("chart-major-horizontal-grid-lines");
        majorVertGridLines.getStyleClass().setAll("chart-major-vertical-grid-lines");

        // Find the gridlines contained in the plotArea of the XYChart.
        // The chart children include in the following order:
        //  plotBackground
        //  plotArea
        //  xAxis
        //  yAxis
        Iterator<Node> chartIt = chartChildren.iterator();
        while (chartIt.hasNext() && plotArea == null) {

            Node chartChild = chartIt.next();

            // Find the "plotArea" which is a Group instance
            if (chartChild instanceof Group) {

                plotArea = (Group) chartChild;
                // Insert our custom background annotations after the zero lines (indices 0 and 1). 
                ObservableList<Node> plotAreaChildren = plotArea.getChildren();
                plotAreaChildren.add(2, majorHorzGridLines);
                plotAreaChildren.add(2, majorVertGridLines);

                // Obtain references to the factory "grid lines" which are Path instances
                for (Node plotAreaChild : plotAreaChildren) {
                    if (plotAreaChild instanceof Path) {

                        // We can identify the horizontal and vertical grid lines by the CSS.
                        ObservableList<String> styles = plotAreaChild.getStyleClass();

                        if (horzGridLines == null && styles.contains("chart-horizontal-grid-lines")) {
                            horzGridLines = (Path) plotAreaChild;
                        } else if (vertGridLines == null && styles.contains("chart-vertical-grid-lines")) {
                            vertGridLines = (Path) plotAreaChild;
                        }
                        if (horzGridLines != null && vertGridLines != null) {
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public void layoutGridlines() {
        
        final Rectangle clip = (Rectangle) plotArea.getClip();
        final Axis<X> xa = chart.getXAxis();
        final Axis<Y> ya = chart.getYAxis();
        double top = clip.getY();
        double left = clip.getX();
        double xAxisWidth = xa.getWidth();
        double yAxisHeight = ya.getHeight();
        double epsilon = 0.0001;
        majorVertGridLines.getElements().clear();
        if (vertGridLines != null && chart.getVerticalGridLinesVisible()) {
            if (xa instanceof LogarithmicAxis) {
                // Move the major grid lines from the default Path to our custom Path
                final ObservableList<Axis.TickMark<X>> tickMarks = xa.getTickMarks();
                for (int i = 0; i < tickMarks.size(); i++) {
                    Axis.TickMark<X> tick = tickMarks.get(i);
                    Double value = (Double) tick.getValue();
                    double log = ((LogarithmicAxis) xa).calculateLog(value);
                    double delta = Math.abs(log - Math.round(log));
                    if (delta < epsilon) {
                        double pixelOffset = (i == (tickMarks.size() - 1)) ? -0.5 : 0.5;
                        double x = xa.getDisplayPosition(tick.getValue());
                        //if ((x != xAxisZero || !isVerticalZeroLineVisible()) && x > 0 && x <= xAxisWidth) {
                        if (x > 0 && x <= xAxisWidth) {
                            majorVertGridLines.getElements().add(new MoveTo(left + x + pixelOffset, top));
                            majorVertGridLines.getElements().add(new LineTo(left + x + pixelOffset, top + yAxisHeight));
                        }
                    }
                }
            }
        }
        majorHorzGridLines.getElements().clear();
        if (horzGridLines != null && chart.isHorizontalGridLinesVisible()) {
            if (ya instanceof LogarithmicAxis) {
                // Move the major grid lines from the default Path to our custom Path
                final ObservableList<Axis.TickMark<Y>> tickMarks = ya.getTickMarks();
                for (int i = 0; i < tickMarks.size(); i++) {
                    Axis.TickMark<Y> tick = tickMarks.get(i);
                    Double value = (Double) tick.getValue();
                    double log = ((LogarithmicAxis) xa).calculateLog(value);
                    if ((log % 1) < epsilon) {
                        double pixelOffset = (i == (tickMarks.size() - 1)) ? -0.5 : 0.5;
                        double y = ya.getDisplayPosition(tick.getValue());
                        if (y > 0 && y <= yAxisHeight) {
                            majorHorzGridLines.getElements().add(new MoveTo(left, top + y + pixelOffset));
                            majorHorzGridLines.getElements().add(new LineTo(left + xAxisWidth, top + y + pixelOffset));
                        }
                    }
                }
            }
        }
    }

}
