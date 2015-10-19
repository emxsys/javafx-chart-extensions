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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


/**
 *
 * @author Bruce Schubert
 */
public class XYImageAnnotation implements XYAnnotation {

    private final double x;
    private final double y;
    private double displayX;
    private double displayY;
    private final ImageView imageView = new ImageView();
    private final AnchorPane pane = new AnchorPane();
    private Pos imageAnchor;


    public XYImageAnnotation(double x, double y, Image image) {
        this(x, y, image, Pos.TOP_LEFT);
    }


    public XYImageAnnotation(double x, double y, Image image, Pos anchor) {
        this.x = x;
        this.y = y;
        this.displayX = x;
        this.displayY = y;
        this.imageAnchor = anchor;
        this.imageView.setImage(image);
        this.imageView.setSmooth(false);
        this.pane.getChildren().add(imageView);

        this.pane.widthProperty().addListener((observable) -> layoutImage());
        this.pane.heightProperty().addListener((observable) -> layoutImage());

    }


    @Override
    public Node getNode() {
        return pane;
    }


    @Override
    public void layoutAnnotation(ValueAxis xAxis, ValueAxis yAxis) {
        displayX = xAxis.getDisplayPosition(x);
        displayY = yAxis.getDisplayPosition(y);
        layoutImage();
    }


    protected void layoutImage() {

        switch (imageAnchor) {
            case TOP_CENTER:
            case CENTER:
            case BOTTOM_CENTER:
                pane.setLayoutX(displayX - (pane.getWidth() / 2));
                break;
            case TOP_LEFT:
            case CENTER_LEFT:
            case BOTTOM_LEFT:
                pane.setLayoutX(displayX);
                break;
            case TOP_RIGHT:
            case CENTER_RIGHT:
            case BOTTOM_RIGHT:
                pane.setLayoutX(displayX - pane.getWidth());
                break;
            default:
                throw new IllegalStateException(getClass().getSimpleName() + ": " + imageAnchor.name() + " is not supported.");
        }
        switch (imageAnchor) {
            case CENTER:
            case CENTER_LEFT:
            case CENTER_RIGHT:
                pane.setLayoutY(displayY - (pane.getHeight() / 2));
                break;
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                pane.setLayoutY(displayY);
                break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                pane.setLayoutY(displayY - pane.getHeight());
                break;
            default:
                throw new IllegalStateException(getClass().getSimpleName() + ": " + imageAnchor.name() + " is not supported.");
        }
    }

}
