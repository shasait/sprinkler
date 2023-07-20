/*
 * Copyright (C) 2021 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.service.sensor.provider.hww;

/**
 *
 */
public class HwwConfiguration {

    private Integer sriLayer;

    private Integer spatialReference;

    private Float positionX;
    private Float positionY;

    public Integer getSriLayer() {
        return sriLayer;
    }

    public void setSriLayer(Integer sriLayer) {
        this.sriLayer = sriLayer;
    }

    public Integer getSpatialReference() {
        return spatialReference;
    }

    public void setSpatialReference(Integer spatialReference) {
        this.spatialReference = spatialReference;
    }

    public Float getPositionX() {
        return positionX;
    }

    public void setPositionX(Float positionX) {
        this.positionX = positionX;
    }

    public Float getPositionY() {
        return positionY;
    }

    public void setPositionY(Float positionY) {
        this.positionY = positionY;
    }

}
