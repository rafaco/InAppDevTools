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

import es.rafaco.inappdevtools.library.view.components.base.FlexData;

public class CollapsibleFlexData extends FlexData {

    private FlexData header;
    private FlexData content;
    private boolean expanded;
    private boolean separator;

    public CollapsibleFlexData(FlexData header, FlexData content) {
        this(header, content, false);
    }

    public CollapsibleFlexData(FlexData header, FlexData content, boolean expanded) {
        super();
        this.header = header;
        this.content = content;
        this.expanded = expanded;
    }

    public FlexData getHeader() {
        return header;
    }

    public void setHeader(FlexData header) {
        this.header = header;
    }

    public FlexData getContent() {
        return content;
    }

    public void setContent(FlexData content) {
        this.content = content;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isSeparator() {
        return separator;
    }

    public void setSeparator(boolean separator) {
        this.separator = separator;
    }
}
