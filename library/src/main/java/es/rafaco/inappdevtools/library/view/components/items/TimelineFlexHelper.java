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

package es.rafaco.inappdevtools.library.view.components.items;

import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.cards.HeaderIconFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;

public class TimelineFlexHelper {

    public static TimelineFlexData buildRepoItem(TimelineFlexData.Position position,
                                                 int color, int icon, String title,
                                                 String subtitle,
                                                 String count, String message, List<Object> buttons){

        CardGroupFlexData cardGroup = new CardGroupFlexData();
        cardGroup.setBgColorResource(R.color.iadt_surface_top);

        HeaderDoubleFlexData headerData = new HeaderDoubleFlexData(title, null, icon,null);
        if (count == null){
            headerData.setNavIcon(R.string.gmd_remove);
        }
        else if (count.equals("")){
            headerData.setNavIcon(R.string.gmd_done);
        }
        else{
            headerData.setNavCount(count);
        }
        headerData.setAccentColor(color);
        headerData.setTitleColor(color);
        headerData.setContent(subtitle);
        //cardGroup.add(headerData);

        LinearGroupFlexData collapsedContent = new LinearGroupFlexData();
        HeaderIconFlexData collapsibleHeader = new HeaderIconFlexData.Builder("")
                .setExpandable(true)
                .setExpanded(false)
                .setHiddenTitleWhenExpanded(false)
                .setOverview("")
                .build();

        TextFlexData messageData = new TextFlexData(message);
        messageData.setSize(TextFlexData.Size.LARGE);
        messageData.setHorizontalMargin(true);
        collapsedContent.add(messageData);

        LinearGroupFlexData buttonsGroup = new LinearGroupFlexData(buttons);
        buttonsGroup.setHorizontal(true);
        collapsedContent.add(buttonsGroup);

        CollapsibleFlexData collapsibleGroup = new CollapsibleFlexData(headerData,
                collapsedContent, false);
        cardGroup.add(collapsibleGroup);

        TimelineFlexData timeline = new TimelineFlexData(cardGroup).newBuilder()
                .position(position)
                .indicatorColor(color)
                .child(cardGroup)
                .build();
        timeline.setFullSpan(true);

        return timeline;
    }
}
