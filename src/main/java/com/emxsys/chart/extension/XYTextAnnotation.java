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

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Bruce Schubert
 */
public class XYTextAnnotation {

    private final Label label = new Label();
    private double x;
    private double y;
    private Pos textAnchor = Pos.CENTER;

    public XYTextAnnotation(String text, double x, double y) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("'text' arg cannot be null or empty.");
        }
        this.label.setText(text);
        this.label.getStyleClass().add("chart-annotation-text");

        this.x = x;
        this.y = y;
    }

    public void setText(String text) {
        this.label.setText(text);
    }

    public Label getNode() {
        return label;
    }

    public Pos getTextAnchor() {
        return textAnchor;
    }

    public void setTextAnchor(Pos textAnchor) {
        this.textAnchor = textAnchor;
    }

    void layoutText(ValueAxis xAxis, ValueAxis yAxis) {

//        label.setLayoutY(yAxis.getDisplayPosition(y));
//        label.setLayoutX(xAxis.getDisplayPosition(x));

        switch (textAnchor) {
            case TOP_CENTER:
            case CENTER:
            case BOTTOM_CENTER:
                label.setLayoutX(xAxis.getDisplayPosition(x) - (label.getWidth() / 2));
                break;
            case TOP_LEFT:
            case CENTER_LEFT:
            case BOTTOM_LEFT:
                label.setLayoutX(xAxis.getDisplayPosition(x));
                break;
            case TOP_RIGHT:
            case CENTER_RIGHT:
            case BOTTOM_RIGHT:
                label.setLayoutX(xAxis.getDisplayPosition(x) - label.getWidth());
                break;
        }
        switch (textAnchor) {
            case CENTER:
            case CENTER_LEFT:
            case CENTER_RIGHT:
                label.setLayoutY(yAxis.getDisplayPosition(y) - (label.getHeight() / 2));
                break;
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                label.setLayoutY(yAxis.getDisplayPosition(y));
                break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                label.setLayoutY(yAxis.getDisplayPosition(y) - label.getHeight());
                break;
        }
    }
}
