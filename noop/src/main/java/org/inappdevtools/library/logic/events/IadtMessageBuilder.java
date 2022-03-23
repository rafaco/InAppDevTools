/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.events;

public class IadtMessageBuilder {

    public IadtMessageBuilder(String message) {}

    public IadtMessageBuilder(int messageResource) { }

    public IadtMessageBuilder setMessage(String message) {
        return this;
    }

    public IadtMessageBuilder setMessage(int messageRes) {
        return this;
    }

    public IadtMessageBuilder setType(int type) {
        return this;
    }

    public IadtMessageBuilder isDev() {
        return this;
    }

    public IadtMessageBuilder isInfo() {
        return this;
    }

    public IadtMessageBuilder isWarning() {
        return this;
    }

    public IadtMessageBuilder isError() {
        return this;
    }

    public void fire(){ }
}
