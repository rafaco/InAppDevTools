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

package es.rafaco.inappdevtools.library.view.components.base;

import java.util.ArrayList;
import java.util.List;

public class FlexGroupData extends FlexItemData {

    //TODO: replace by List<FlexItemData> after all items refactored
    protected List<Object> children;

    public FlexGroupData(){
        this(new ArrayList<Object>());
    }

    public FlexGroupData(List<Object> children) {
        super();
        this.children = children;
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }

    public void add(Object abstractData) {
        if (children==null)
            children = new ArrayList<Object>();
        children.add(abstractData);
    }
}
