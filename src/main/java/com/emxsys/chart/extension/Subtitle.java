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

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 *
 * @author Bruce Schubert
 */
public class Subtitle {

    private final Chart chart;
    private final ObservableList<Node> children;

    private String subtitle;
    private final Label subtitleLabel = new Label();
    private final Label titleLabel;
    private final Pane chartContent;
    private final Node legend;

    /**
     * Constructs a Subtitle for a JavaFX chart.
     *
     * @param chart The chart to have a subtitle.
     * @param children A modifiable list of the chart's children.
     * @param legend The chart's legend object.
     */
    public Subtitle(Chart chart, ObservableList<Node> children, Node legend) {
        this.chart = chart;
        this.children = children;
        this.children.add(subtitleLabel);
        subtitleLabel.getStyleClass().add("chart-subtitle");
        subtitleLabel.setAlignment(Pos.CENTER);

        // TODO: could possibly discover or validate title and content based on styles...
        // Observe this excerpt from the Chart constructor:
        //        titleLabel.getStyleClass().add("chart-title");
        //        chartContent.getStyleClass().add("chart-content");
        this.titleLabel = (Label) children.get(0);
        this.chartContent = (Pane) children.get(1);
        this.legend = legend;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    /**
     * 
     * @param subtitle 
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        this.subtitleLabel.setText(subtitle);
        this.chart.requestLayout();
    }

    /**
     * 
     */
    public void layoutSubtitles() {

        if (subtitle == null || subtitle.isEmpty()) {
            subtitleLabel.setVisible(false);
            return;
        }
        subtitleLabel.setVisible(true);

        double top = titleLabel.getLayoutY() + titleLabel.getHeight();
        double left = chart.snappedLeftInset();
        double bottom = chart.snappedBottomInset();
        double right = chart.snappedRightInset();
        final double width = chart.getWidth();
        final double height = chart.getHeight();

        switch (chart.getTitleSide()) {
            case TOP: {
                final double subtitleHeight = (subtitleLabel.prefHeight(width - left - right));
                subtitleLabel.resizeRelocate(left, top, width - left - right, subtitleHeight);
                // Resize the chart content to accomdate the subtitle
                top += subtitleHeight;
                chartContent.resizeRelocate(
                        chartContent.getLayoutX(),
                        chartContent.getLayoutY() + subtitleHeight,
                        chartContent.getWidth(),
                        chartContent.getHeight() - subtitleHeight);
                break;
            }
            case BOTTOM: {
                throw new UnsupportedOperationException("BOTTOM side Subtitle not implemented yet.");
//                final double subtitleHeight = (subtitleLabel.prefHeight(width - left - right));
//                subtitleLabel.resizeRelocate(left, height - bottom - subtitleHeight, width - left - right, subtitleHeight);
//                bottom += subtitleHeight;
//                top += subtitleHeight;
//                break;
            }
            case LEFT: {
                throw new UnsupportedOperationException("LEFT side Subtitle not implemented yet.");
//                final double titleWidth = (subtitleLabel.prefWidth(height - top - bottom));
//                subtitleLabel.resizeRelocate(left, top, titleWidth, height - top - bottom);
//                left += titleWidth;
//                break;
            }
            case RIGHT: {
                throw new UnsupportedOperationException("RIGHT side Subtitle not implemented yet.");
//                final double titleWidth = (subtitleLabel.prefWidth(height - top - bottom));
//                subtitleLabel.resizeRelocate(width - right - titleWidth, top, titleWidth, height - top - bottom);
//                right += titleWidth;
//                break;
            }
            default:
                break;
        }

    }

}
