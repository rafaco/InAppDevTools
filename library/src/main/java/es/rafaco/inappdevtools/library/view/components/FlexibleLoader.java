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

package es.rafaco.inappdevtools.library.view.components;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisData;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderData;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupData;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.AnalysisViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.ButtonGroupData;
import es.rafaco.inappdevtools.library.view.components.groups.ButtonGroupViewHolder;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.components.cards.CardViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.CheckboxData;
import es.rafaco.inappdevtools.library.view.components.items.CheckboxViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.CollapsibleLinearGroupData;
import es.rafaco.inappdevtools.library.view.components.groups.CollapsibleLinearGroupViewHolder;
import es.rafaco.inappdevtools.library.view.components.cards.ComplexCardViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.ConfigData;
import es.rafaco.inappdevtools.library.view.components.items.ConfigViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.EditTextData;
import es.rafaco.inappdevtools.library.view.components.items.EditTextViewHolder;
import es.rafaco.inappdevtools.library.view.components.cards.FlatCardData;
import es.rafaco.inappdevtools.library.view.components.cards.FlatCardViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.TextItemData;
import es.rafaco.inappdevtools.library.view.components.items.TextViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.ImageData;
import es.rafaco.inappdevtools.library.view.components.items.ImageViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.LinkItemData;
import es.rafaco.inappdevtools.library.view.components.items.LinkViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.components.items.OverviewViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.RecyclerGroupData;
import es.rafaco.inappdevtools.library.view.components.groups.RecyclerGroupViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.RunButtonViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.SelectorData;
import es.rafaco.inappdevtools.library.view.components.items.SelectorViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.TraceGroupItem;
import es.rafaco.inappdevtools.library.view.components.items.TraceGroupViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.TraceItemData;
import es.rafaco.inappdevtools.library.view.components.items.TraceViewHolder;
import es.rafaco.inappdevtools.library.view.components.cards.WideWidgetData;
import es.rafaco.inappdevtools.library.view.components.cards.WidgetData;
import es.rafaco.inappdevtools.library.view.components.cards.WidgetViewHolder;

//TODO: remove this class
public class FlexibleLoader {

    public static List<FlexibleItemDescriptor> getAllDescriptors() {
        List<FlexibleItemDescriptor> descriptors = new ArrayList<>();

        descriptors.add(new FlexibleItemDescriptor(ImageData.class, ImageViewHolder.class, R.layout.flexible_item_image));
        descriptors.add(new FlexibleItemDescriptor(RunButton.class, RunButtonViewHolder.class, R.layout.flexible_item_run_button));
        descriptors.add(new FlexibleItemDescriptor(SelectorData.class, SelectorViewHolder.class, R.layout.flexible_item_selector));
        descriptors.add(new FlexibleItemDescriptor(EditTextData.class, EditTextViewHolder.class, R.layout.flexible_item_edit_text));
        descriptors.add(new FlexibleItemDescriptor(OverviewData.class, OverviewViewHolder.class, R.layout.flexible_item_overview));
        descriptors.add(new FlexibleItemDescriptor(WidgetData.class, WidgetViewHolder.class, R.layout.flexible_item_widget));
        descriptors.add(new FlexibleItemDescriptor(WideWidgetData.class, WidgetViewHolder.class, R.layout.flexible_item_widget));
        descriptors.add(new FlexibleItemDescriptor(LinkItemData.class, LinkViewHolder.class, R.layout.flexible_item_link));
        descriptors.add(new FlexibleItemDescriptor(TraceItemData.class, TraceViewHolder.class, R.layout.flexible_item_trace));
        descriptors.add(new FlexibleItemDescriptor(AnalysisData.class, AnalysisViewHolder.class, R.layout.flexible_item_analysis));
        descriptors.add(new FlexibleItemDescriptor(ConfigData.class, ConfigViewHolder.class, R.layout.flexible_item_config));
        descriptors.add(new FlexibleItemDescriptor(CheckboxData.class, CheckboxViewHolder.class, R.layout.flexible_item_checkbox));

        descriptors.add(new FlexibleItemDescriptor(CardData.class, CardViewHolder.class, R.layout.flexible_item_card));
        descriptors.add(new FlexibleItemDescriptor(DocumentSectionData.class, ComplexCardViewHolder.class, R.layout.flexible_item_complex_card));
        descriptors.add(new FlexibleItemDescriptor(ButtonGroupData.class, ButtonGroupViewHolder.class, R.layout.flexible_item_button_group));
        descriptors.add(new FlexibleItemDescriptor(TraceGroupItem.class, TraceGroupViewHolder.class, R.layout.flexible_item_trace_group));

        descriptors.add(new FlexibleItemDescriptor(CollapsibleLinearGroupData.class, CollapsibleLinearGroupViewHolder.class, R.layout.flexible_item_collapsible_list));
        descriptors.add(new FlexibleItemDescriptor(FlatCardData.class, FlatCardViewHolder.class, R.layout.flexible_item_flat_card));


        descriptors.add(new FlexibleItemDescriptor(LinearGroupData.class, LinearGroupViewHolder.class, R.layout.flexible_group_linear));
        descriptors.add(new FlexibleItemDescriptor(RecyclerGroupData.class, RecyclerGroupViewHolder.class, R.layout.flexible_group_recycler));
        descriptors.add(new FlexibleItemDescriptor(CardGroupData.class, CardGroupViewHolder.class, R.layout.flexible_group_card));
        descriptors.add(new FlexibleItemDescriptor(CardHeaderData.class, CardHeaderViewHolder.class, R.layout.flexible_item_card_header));
        descriptors.add(new FlexibleItemDescriptor(TextItemData.class, TextViewHolder.class, R.layout.flexible_item_text));
        descriptors.add(new FlexibleItemDescriptor(String.class, TextViewHolder.class, R.layout.flexible_item_text));
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
