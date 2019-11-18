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

package es.rafaco.inappdevtools.library.logic.log.filter;

import java.util.ArrayList;
import java.util.List;

public class LogBackFilter {

    String text = "";
    List<String> severities;
    List<String> categories;
    List<String> notCategories;
    List<String> subcategories;
    long fromDate;
    long toDate;

    public LogBackFilter() {
        this.severities = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.notCategories = new ArrayList<>();
        this.subcategories = new ArrayList<>();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSeverities(List<String> severities) {
        this.severities = severities;
    }

    public List<String> getSeverities(){
        return severities;
    }


    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setNotCategories(List<String> notCategories) {
        this.notCategories = notCategories;
    }

    public void setSubcategories(List<String> subcategories) {
        this.subcategories = subcategories;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getNotCategories() {
        return notCategories;
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public long getFromDate() {
        return fromDate;
    }

    public long getToDate() {
        return toDate;
    }
}
