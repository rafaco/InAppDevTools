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

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.external.CustomToast;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;

public class IadtEventBuilder {

    private long date;
    private String severity = "V";
    private String category = "Custom";
    private String subcategory = "Default";
    private String message;
    private String extra;
    private long linkedId;

    public IadtEventBuilder(long date) {
        this.date = date;
    }

    public IadtEventBuilder() {
        setDateNow();
    }

    public IadtEventBuilder(String message) {
        setDateNow();
        setMessage(message);
    }

    public IadtEventBuilder setDateNow() {
        date = DateUtils.getLong();
        return this;
    }

    public IadtEventBuilder setDate(long date) {
        this.date = date;
        return this;
    }

    public IadtEventBuilder setMessage(String message) {
        this.message = message;
        return this;
    }


    /**
     * @param severity (V, D, I, W, E, F, WTF)
     * @return
     */
    public IadtEventBuilder setSeverity(String severity) {
        this.severity = severity;
        return this;
    }

    public IadtEventBuilder isDev() {
        this.severity = FriendlyLog.LEVEL.D.name();
        return this;
    }

    public IadtEventBuilder isInfo() {
        this.severity = FriendlyLog.LEVEL.I.name();
        return this;
    }

    public IadtEventBuilder isWarning() {
        this.severity = FriendlyLog.LEVEL.W.name();
        return this;
    }

    public IadtEventBuilder isError() {
        this.severity = FriendlyLog.LEVEL.E.name();
        return this;
    }

    public IadtEventBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    public IadtEventBuilder setSubcategory(String subcategory) {
        this.subcategory = subcategory;
        return this;
    }

    public IadtEventBuilder setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    public IadtEventBuilder setLinkedId(long linkedId) {
        this.linkedId = linkedId;
        return this;
    }

    public void fire(){
        if (!IadtController.isEnabled())
            return;

        final Friendly log = new Friendly();
        log.setDate(date);
        log.setSeverity(severity);
        log.setCategory(category);
        log.setSubcategory(subcategory);
        log.setMessage(message);
        log.setExtra(extra);
        log.setLinkedId(linkedId);

        FriendlyLog.log(log);
    }
}
