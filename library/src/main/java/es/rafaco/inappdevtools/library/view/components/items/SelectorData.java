/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

import android.widget.AdapterView;

import java.util.List;

public class SelectorData {
    String title;
    private List<String> options;
    private int selected;
    private AdapterView.OnItemSelectedListener listener;

    public SelectorData(String title, List<String> options, int selected, AdapterView.OnItemSelectedListener listener) {
        this.title = title;
        this.options = options;
        this.selected = selected;
        this.listener = listener;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public AdapterView.OnItemSelectedListener getListener() {
        return listener;
    }

    public void setListener(AdapterView.OnItemSelectedListener listener) {
        this.listener = listener;
    }
}
