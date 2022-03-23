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

package org.inappdevtools.library.logic.documents.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class DocumentEntryData {

    String label;
    List<String> values;

    public DocumentEntryData(String label, List<String> values) {
        this.label = label;
        this.values = values;
    }

    public DocumentEntryData(String label, String value) {
        this.label = label;
        setValue(value);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void setValue(String value) {
        this.values = new ArrayList<>();
        this.values.add(value);
    }

    public String toString() {
        String result = "";
        String formatFirst = " - %s: %s";
        String formatOthers = "        %s";
        String lineBreak = "\n";

        List<String> values = getValues();
        if (values == null || values.size()<1){
            result += String.format(formatFirst, getLabel(), "Not available");
            result += lineBreak;
        }else{
            for (int i = 0; i<values.size(); i++){
                if(i==0){
                    if (TextUtils.isEmpty(getLabel()))
                        result += values.get(0);
                    else
                        result += String.format(formatFirst, getLabel(), values.get(0));
                    result += lineBreak;
                }else{
                    result += String.format(formatOthers, values.get(i));
                    result += lineBreak;
                }
            }
        }
        return result;
    }
}
