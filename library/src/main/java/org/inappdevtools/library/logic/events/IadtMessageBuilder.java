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

package org.inappdevtools.library.logic.events;

import org.inappdevtools.library.logic.external.CustomToast;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class IadtMessageBuilder {

    private int type = 0;
    private String message;

    public IadtMessageBuilder(String message) {
        setMessage(message);
    }

    public IadtMessageBuilder(int messageResource) {
        setMessage(messageResource);
    }

    public IadtMessageBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public IadtMessageBuilder setMessage(int messageRes) {
        this.message = IadtController.get().getContext().getResources().getString(messageRes);
        return this;
    }

    public IadtMessageBuilder isDev() {
        this.type = CustomToast.TYPE_DEV;
        return this;
    }

    public IadtMessageBuilder isInfo() {
        this.type = CustomToast.TYPE_INFO;
        return this;
    }

    public IadtMessageBuilder isWarning() {
        this.type = CustomToast.TYPE_WARNING;
        return this;
    }

    public IadtMessageBuilder isError() {
        this.type = CustomToast.TYPE_ERROR;
        return this;
    }

    public void fire(){
        if (!IadtController.get().isEnabled()) return;

        String logSeverity;
        switch (type){
            case CustomToast.TYPE_INFO:
                logSeverity = "I";
                break;
            case CustomToast.TYPE_WARNING:
                logSeverity = "W";
                break;
            case CustomToast.TYPE_ERROR:
                logSeverity = "E";
                break;
            case CustomToast.TYPE_DEV:
            default:
                logSeverity = "D";
                break;
        }

        CustomToast.show(IadtController.get().getContext(), message, type);
        FriendlyLog.log(logSeverity, "Message", logSeverity, "Message: " + message);
    }
}
