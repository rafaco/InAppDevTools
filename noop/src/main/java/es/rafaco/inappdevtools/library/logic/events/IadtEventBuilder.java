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

package es.rafaco.inappdevtools.library.logic.events;

public class IadtEventBuilder {

    public IadtEventBuilder(long date) { }

    public IadtEventBuilder() { }

    public IadtEventBuilder(String message) { }

    public IadtEventBuilder setDateNow() {
        return this;
    }

    public IadtEventBuilder setDate(long date) {
        return this;
    }

    public IadtEventBuilder setMessage(String message) {
        return this;
    }


    /**
     * @param severity (V, D, I, W, E, F, WTF)
     * @return
     */
    public IadtEventBuilder setSeverity(String severity) {
        return this;
    }

    public IadtEventBuilder setCategory(String category) {
        return this;
    }

    public IadtEventBuilder setSubcategory(String subcategory) {
        return this;
    }

    public IadtEventBuilder setExtra(String extra) {
        return this;
    }

    public IadtEventBuilder setLinkedId(long linkedId) {
        return this;
    }

    public IadtEventBuilder isInfo() { return this; }

    public void fire(){ }
}
