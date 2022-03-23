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

package org.inappdevtools.library.view.components.items;

import org.inappdevtools.library.view.components.base.FlexData;

public class SeparatorFlexData extends FlexData {

    boolean isHorizontal;
    int thicknessDp;

    public SeparatorFlexData() {
        this(false);
    }

    public SeparatorFlexData(boolean isHorizontal) {
        super();
        this.isHorizontal = isHorizontal;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public int getThicknessDp() {
        return thicknessDp;
    }

    public void setThicknessDp(int thicknessDp) {
        this.thicknessDp = thicknessDp;
    }
}
