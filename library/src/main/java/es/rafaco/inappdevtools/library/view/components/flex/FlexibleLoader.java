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

package es.rafaco.inappdevtools.library.view.components.flex;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisData;


//TODO: remove this class
public class FlexibleLoader {

    public static List<FlexibleItemDescriptor> getAllDescriptors() {
        List<FlexibleItemDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FlexibleItemDescriptor(String.class, HeaderViewHolder.class, R.layout.flexible_item_header));
        descriptors.add(new FlexibleItemDescriptor(RunButton.class, RunButtonViewHolder.class, R.layout.flexible_item_run_button));
        descriptors.add(new FlexibleItemDescriptor(ButtonGroupData.class, ButtonGroupViewHolder.class, R.layout.flexible_item_button_group));
        descriptors.add(new FlexibleItemDescriptor(CardData.class, CardViewHolder.class, R.layout.flexible_item_card));
        descriptors.add(new FlexibleItemDescriptor(SelectorData.class, SelectorViewHolder.class, R.layout.flexible_item_selector));
        descriptors.add(new FlexibleItemDescriptor(EditTextData.class, EditTextViewHolder.class, R.layout.flexible_item_edit_text));
        descriptors.add(new FlexibleItemDescriptor(OverviewData.class, OverviewViewHolder.class, R.layout.flexible_item_overview));
        descriptors.add(new FlexibleItemDescriptor(DocumentSectionData.class, ComplexCardViewHolder.class, R.layout.flexible_item_complex_card));
        descriptors.add(new FlexibleItemDescriptor(WidgetData.class, WidgetViewHolder.class, R.layout.flexible_item_widget));
        descriptors.add(new FlexibleItemDescriptor(WideWidgetData.class, WidgetViewHolder.class, R.layout.flexible_item_widget));
        descriptors.add(new FlexibleItemDescriptor(LinkItem.class, LinkViewHolder.class, R.layout.flexible_item_link));
        descriptors.add(new FlexibleItemDescriptor(TraceItemData.class, TraceViewHolder.class, R.layout.flexible_item_trace));
        descriptors.add(new FlexibleItemDescriptor(TraceGroupItem.class, TraceGroupViewHolder.class, R.layout.flexible_item_trace_group));
        descriptors.add(new FlexibleItemDescriptor(AnalysisData.class, AnalysisViewHolder.class, R.layout.flexible_item_analysis));
        descriptors.add(new FlexibleItemDescriptor(ConfigData.class, ConfigViewHolder.class, R.layout.flexible_item_config));
        descriptors.add(new FlexibleItemDescriptor(CheckboxData.class, CheckboxViewHolder.class, R.layout.flexible_item_checkbox));
        return descriptors;
    }

    public static FlexibleItemDescriptor getDescriptor(Class<?> dataClass) {
        for (FlexibleItemDescriptor descriptor : getAllDescriptors()) {
            if (descriptor.dataClass.equals(dataClass)){
                return descriptor;
            }
        }
        return null;
    }

    public static boolean isFullSpan(Class<?> itemDataClass) {
        if (itemDataClass.equals(String.class)
                || itemDataClass.equals(ButtonGroupData.class)
                || itemDataClass.equals(OverviewData.class)
                || itemDataClass.equals(DocumentSectionData.class)
                || itemDataClass.equals(WideWidgetData.class)
                || itemDataClass.equals(CardData.class)){
            return true;
        }
        return false;
    }
}
