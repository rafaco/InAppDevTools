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

public class IadtEvent {

    public IadtEvent(long date) { }

    public IadtEvent() { }

    public IadtEvent setDateNow() {
        return this;
    }

    public IadtEvent setDate(long date) {
        return this;
    }

    public IadtEvent setMessage(String message) {
        return this;
    }

    public IadtEvent setSeverity(String severity) {
        return this;
    }

    public IadtEvent setCategory(String category) {
        return this;
    }

    public IadtEvent setSubcategory(String subcategory) {
        return this;
    }

    public IadtEvent setExtra(String extra) {
        return this;
    }

    public IadtEvent setLinkedId(long linkedId) {
        return this;
    }

    public void fire(){
    }
}
