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
import es.rafaco.inappdevtools.library.view.components.base.ContainerFlexData;
import es.rafaco.inappdevtools.library.view.components.base.ContainerFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisData;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderFlexData;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.AnalysisViewHolder;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.components.cards.CardViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.CheckboxData;
import es.rafaco.inappdevtools.library.view.components.items.CheckboxViewHolder;
import es.rafaco.inappdevtools.library.view.components.cards.ComplexCardViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.CollapsibleFlexData;
import es.rafaco.inappdevtools.library.view.components.items.CollapsibleFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.ConfigData;
import es.rafaco.inappdevtools.library.view.components.items.ConfigViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.EditTextData;
import es.rafaco.inappdevtools.library.view.components.items.EditTextViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.SeparatorFlexData;
import es.rafaco.inappdevtools.library.view.components.items.SeparatorFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.ImageData;
import es.rafaco.inappdevtools.library.view.components.items.ImageViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.LinkItemData;
import es.rafaco.inappdevtools.library.view.components.items.LinkViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.components.items.OverviewViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.RecyclerGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.RecyclerGroupFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexViewHolder;
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
public class FlexLoader {

    public static List<FlexDescriptor> getAllDescriptors() {
        List<FlexDescriptor> descriptors = new ArrayList<>();

        /* OLD COMPONENTS
         * Refactor pending, they don't extends FlexData/FlexItemViewHolder
         */
        descriptors.add(new FlexDescriptor(ImageData.class, ImageViewHolder.class, R.layout.flexible_item_image));
        descriptors.add(new FlexDescriptor(SelectorData.class, SelectorViewHolder.class, R.layout.flexible_item_selector));
        descriptors.add(new FlexDescriptor(EditTextData.class, EditTextViewHolder.class, R.layout.flexible_item_edit_text));
        descriptors.add(new FlexDescriptor(OverviewData.class, OverviewViewHolder.class, R.layout.flexible_item_overview));
        descriptors.add(new FlexDescriptor(WidgetData.class, WidgetViewHolder.class, R.layout.flexible_item_widget));
        descriptors.add(new FlexDescriptor(WideWidgetData.class, WidgetViewHolder.class, R.layout.flexible_item_widget));
        descriptors.add(new FlexDescriptor(LinkItemData.class, LinkViewHolder.class, R.layout.flexible_item_link));
        descriptors.add(new FlexDescriptor(TraceItemData.class, TraceViewHolder.class, R.layout.flexible_item_trace));
        descriptors.add(new FlexDescriptor(TraceGroupItem.class, TraceGroupViewHolder.class, R.layout.flexible_item_trace_group));
        descriptors.add(new FlexDescriptor(AnalysisData.class, AnalysisViewHolder.class, R.layout.flexible_item_analysis));
        descriptors.add(new FlexDescriptor(ConfigData.class, ConfigViewHolder.class, R.layout.flexible_item_config));
        descriptors.add(new FlexDescriptor(CheckboxData.class, CheckboxViewHolder.class, R.layout.flexible_item_checkbox));

        descriptors.add(new FlexDescriptor(CardData.class, CardViewHolder.class, R.layout.flexible_item_card));
        descriptors.add(new FlexDescriptor(DocumentSectionData.class, ComplexCardViewHolder.class, R.layout.flexible_item_complex_card));

        //GROUPS
        descriptors.add(new FlexDescriptor(LinearGroupFlexData.class, LinearGroupFlexViewHolder.class, R.layout.flex_group_linear));
        descriptors.add(new FlexDescriptor(RecyclerGroupFlexData.class, RecyclerGroupFlexViewHolder.class, R.layout.flex_group_recycler));
        descriptors.add(new FlexDescriptor(CardGroupFlexData.class, CardGroupFlexViewHolder.class, R.layout.flex_group_card));

        //ITEMS
        descriptors.add(new FlexDescriptor(String.class, TextFlexViewHolder.class, R.layout.flex_text));
        descriptors.add(new FlexDescriptor(TextFlexData.class, TextFlexViewHolder.class, R.layout.flex_text));
        descriptors.add(new FlexDescriptor(ButtonFlexData.class, ButtonFlexViewHolder.class, R.layout.flex_item_button));
        descriptors.add(new FlexDescriptor(ButtonBorderlessFlexData.class, ButtonFlexViewHolder.class, R.layout.flex_button_borderless));
        descriptors.add(new FlexDescriptor(CardHeaderFlexData.class, CardHeaderFlexViewHolder.class, R.layout.flex_card_header));
        descriptors.add(new FlexDescriptor(SeparatorFlexData.class, SeparatorFlexViewHolder.class, R.layout.flex_separator));
        descriptors.add(new FlexDescriptor(CollapsibleFlexData.class, CollapsibleFlexViewHolder.class, R.layout.flex_collapsible));

        //CONTAINERS
        descriptors.add(new FlexDescriptor(ContainerFlexData.class, ContainerFlexViewHolder.class, R.layout.flex_container));

        return descriptors;
    }

    public static FlexDescriptor getDescriptor(Class<?> dataClass) {
        for (FlexDescriptor descriptor : getAllDescriptors()) {
            if (descriptor.dataClass.equals(dataClass)){
                return descriptor;
            }
        }
        return null;
    }

    public static boolean isFullSpan(Class<?> itemDataClass) {
        if (itemDataClass.equals(String.class)
                || itemDataClass.equals(LinearGroupFlexData.class)
                || itemDataClass.equals(OverviewData.class)
                || itemDataClass.equals(DocumentSectionData.class)
                || itemDataClass.equals(WideWidgetData.class)
                || itemDataClass.equals(CardData.class)){
            return true;
        }
        return false;
    }
}
