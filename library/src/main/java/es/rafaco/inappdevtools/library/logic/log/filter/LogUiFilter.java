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

public class LogUiFilter {

    private String text;
    private int sessionInt;
    private int severityInt;
    private int typeInt;
    private int categoryInt;
    private String categoryName;
    private int tagInt;
    private String tagName;

    private boolean wrapLines; //Only affect ui

    public LogUiFilter() {
    }

    public LogUiFilter(LogUiFilter filter) {
        this.setSessionInt(filter.getSessionInt());
        this.setSeverityInt(filter.getSeverityInt());
        this.setTypeInt(filter.getTypeInt());
        this.setCategoryInt(filter.getCategoryInt());
        this.setCategoryName(filter.getCategoryName());
        this.setTagInt(filter.getTagInt());
        this.setTagName(filter.getTagName());
        this.setText(filter.getText());
        this.setWrapLines(filter.isWrapLines());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSessionInt() {
        return sessionInt;
    }

    public void setSessionInt(int position) {
        this.sessionInt = position;
    }

    public int getSeverityInt() {
        return severityInt;
    }

    public void setSeverityInt(int selectedSeverity)   {
        this.severityInt = selectedSeverity;
    }

    public int getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(int typeInt) {
        this.typeInt = typeInt;
    }

    public int getCategoryInt() {
        return categoryInt;
    }

    public void setCategoryInt(int categoryInt) {
        this.categoryInt = categoryInt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTagInt() {
        return tagInt;
    }

    public void setTagInt(int tagInt) {
        this.tagInt = tagInt;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isWrapLines() {
        return wrapLines;
    }

    public void setWrapLines(boolean wrapLines) {
        this.wrapLines = wrapLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogUiFilter)) return false;

        LogUiFilter that = (LogUiFilter) o;

        if (getSessionInt() != that.getSessionInt()) return false;
        if (getSeverityInt() != that.getSeverityInt()) return false;
        if (getTypeInt() != that.getTypeInt()) return false;
        if (getCategoryInt() != that.getCategoryInt()) return false;
        if (getTagInt() != that.getTagInt()) return false;
        if (getText() != null ? !getText().equals(that.getText()) : that.getText() != null)
            return false;
        if (getCategoryName() != null ? !getCategoryName().equals(that.getCategoryName()) : that.getCategoryName() != null)
            return false;
        if (isWrapLines() != that.isWrapLines())
            return false;
        return getTagName() != null ? getTagName().equals(that.getTagName()) : that.getTagName() == null;
    }
}
