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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;


/**
 *
 * @author Bruce Schubert
 */
public class ValueMarker {

    private final DoubleProperty value = new SimpleDoubleProperty();
    private final Group group = new Group();
    private final Line line = new Line();
    private final Label label = new Label();
    private Pos textAnchor = Pos.TOP_LEFT;


    /**
     * Constructs a marker line drawn at the given value.
     *
     * @param value
     */
    public ValueMarker(double value) {
        this(value, null);
    }


    /**
     * Constructs a marker line drawn at the given value.
     *
     * @param value
     * @param text
     */
    public ValueMarker(double value, String text) {
        this(value, text, Pos.TOP_LEFT);
    }


    /**
     * Constructs a marker line drawn at the given value.
     *
     * @param value
     * @param text
     * @param textAnchor
     */
    public ValueMarker(double value, String text, Pos textAnchor) {
        this.value.set(value);
        this.label.setText(text);
        this.textAnchor = textAnchor;
        this.line.getStyleClass().add("chart-marker-line");
        this.label.getStyleClass().add("chart-marker-label");
        this.label.applyCss();

        this.label.widthProperty().addListener((observable) -> layoutText());
        this.label.heightProperty().addListener((observable) -> layoutText());

        this.group.getChildren().addAll(line, label);
    }


    public double getValue() {
        return value.get();
    }


    public void setValue(double value) {
        this.value.set(value);
    }


    public DoubleProperty valueProperty() {
        return value;
    }


    public String getLabel() {
        return label.getText();
    }


    public void setLabel(String text) {
        label.setText(text);
    }


    public Group getNode() {
        return group;
    }


    public Pos getTextAnchor() {
        return textAnchor;
    }


    public void setTextAnchor(Pos textAnchor) {
        this.textAnchor = textAnchor;
    }


    void layoutDomainMarker(ValueAxis xAxis, ValueAxis yAxis) {

        // Determine the line height
        double lower = yAxis.getLowerBound();
        Number lowerY = yAxis.toRealValue(lower);
        double upper = yAxis.getUpperBound();
        Number upperY = yAxis.toRealValue(upper);
        // Establish the placement of the line
        line.setStartY(yAxis.getDisplayPosition(lowerY));
        line.setEndY(yAxis.getDisplayPosition(upperY));
        line.setStartX(xAxis.getDisplayPosition(getValue()));
        line.setEndX(line.getStartX());
        // Layout the text base on the line
        layoutText();

    }


    void layoutRangeMarker(ValueAxis xAxis, ValueAxis yAxis) {

        // Determine the line width
        double lower = xAxis.getLowerBound();
        Number lowerX = xAxis.toRealValue(lower);
        double upper = xAxis.getUpperBound();
        Number upperX = xAxis.toRealValue(upper);
        // Establish the line placement
        line.setStartX(xAxis.getDisplayPosition(lowerX));
        line.setEndX(xAxis.getDisplayPosition(upperX));
        line.setStartY(yAxis.getDisplayPosition(getValue()));
        line.setEndY(line.getStartY());
        // Layout the text based on the line placement
        layoutText();
    }


    void layoutText() {
        switch (textAnchor) {
            case TOP_CENTER:
            case CENTER:
            case BOTTOM_CENTER:
                label.setLayoutX(line.getStartX() + ((line.getEndX() - line.getStartX()) / 2) - (label.getWidth() / 2));
                break;
            case TOP_LEFT:
            case CENTER_LEFT:
            case BOTTOM_LEFT:
                label.setLayoutX(line.getStartX());
                break;
            case TOP_RIGHT:
            case CENTER_RIGHT:
            case BOTTOM_RIGHT:
                label.setLayoutX(line.getEndX() - label.getWidth());
                break;
        }
        switch (textAnchor) {
            case CENTER:
            case CENTER_LEFT:
            case CENTER_RIGHT:
                label.setLayoutY(line.getStartY() + ((line.getEndY() - line.getStartY()) / 2) - (label.getHeight() / 2));
                break;
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                label.setLayoutY(line.getEndY() - label.getHeight());
                break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                label.setLayoutY(line.getStartY());
                break;
        }
    }

}
