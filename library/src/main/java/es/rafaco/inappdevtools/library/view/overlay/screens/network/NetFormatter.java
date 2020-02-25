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

package es.rafaco.inappdevtools.library.view.overlay.screens.network;

import java.util.Locale;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class NetFormatter {

    NetSummary data;

    public NetFormatter(NetSummary data) {
        this.data = data;
    }

    public boolean isDone(){
        return data.status != 0;
    }

    public int getColor() {
        if (!isDone())
            return R.color.rally_gray;
        else if (isDone() && data.code > 0 && data.code == 200)
            return R.color.rally_green;
        else
            return R.color.rally_orange;
    }

    public String getStatusString(){
        switch (data.status){
            case NetSummary.Status.COMPLETE:
                return "Complete";
            case NetSummary.Status.ERROR:
                return "Error";
            case NetSummary.Status.REQUESTING:
            default:
                return "Requesting";
        }
        //TODO: Old way from pandora
        /*String status = !done ? "Pending"
                : (summary.status == 1 ? "Error"
                : "Received");*/
    }


    public String getStartDate(){
        return DateUtils.formatTimeWithMillis(data.start_time);
    }

    public String getRequestSize() {
        return Humanizer.humanReadableByteCount(data.request_size, false);
    }

    public String getResponseSize() {
        return Humanizer.humanReadableByteCount(data.response_size, false);
    }

    public String getDurationString() {
        if (data.end_time <= 0)
            return "N/A";
        return Humanizer.getDuration(data.end_time - data.start_time);
    }


    public String getComposedLine() {
        return String.format(Locale.getDefault(), "%s    %s    %s%s%s",
                getStartDate(),
                data.method,
                getComposedLineCode(),
                getComposedLineSize(),
                getComposedLineDuration());
    }

    private String getComposedLineDuration() {
        return isDone() && data.end_time > 0 && data.start_time > 0
                ? data.end_time - data.start_time + "ms" : "";
    }

    private String getComposedLineSize(){
        return (isDone() && data.response_size > 0)
                ? data.response_size + "    " : "";
    }

    private String getComposedLineCode(){
        return isDone() && data.code > 0 ? data.code + "    " : "";
    }
}
