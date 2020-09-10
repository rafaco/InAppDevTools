/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.view.components.items;


import es.rafaco.inappdevtools.library.view.components.base.ContainerFlexData;
import es.rafaco.inappdevtools.library.view.components.base.FlexData;

public class TimelineFlexData extends ContainerFlexData {

    public enum Position { START, MIDDLE, END;}
    private Position position = Position.MIDDLE;
    private int lineColor;
    private int indicatorColor;
    private float indicatorSize;

    public TimelineFlexData(FlexData child){
        super(child);
    }

    private TimelineFlexData(Builder builder) {
        this(builder.child);
        setPosition(builder.position);
        setLineColor(builder.lineColor);
        setIndicatorColor(builder.indicatorColor);
        setIndicatorSize(builder.indicatorSize);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public float getIndicatorSize() {
        return indicatorSize;
    }

    public void setIndicatorSize(float indicatorSize) {
        this.indicatorSize = indicatorSize;
    }


    public static final class Builder {
        private FlexData child;
        private Position position;
        private int lineColor;
        private int indicatorColor;
        private float indicatorSize;

        private Builder() {
        }

        public Builder child(FlexData val) {
            child = val;
            return this;
        }

        public Builder position(Position val) {
            position = val;
            return this;
        }

        public Builder lineColor(int val) {
            lineColor = val;
            return this;
        }

        public Builder indicatorColor(int val) {
            indicatorColor = val;
            return this;
        }

        public Builder indicatorSize(float val) {
            indicatorSize = val;
            return this;
        }

        public TimelineFlexData build() {
            return new TimelineFlexData(this);
        }
    }
}
