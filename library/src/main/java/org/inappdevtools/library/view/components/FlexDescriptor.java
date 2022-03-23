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

package org.inappdevtools.library.view.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.inappdevtools.library.logic.log.FriendlyLog;

import java.lang.reflect.Constructor;

public class FlexDescriptor {

    public final Class<?> dataClass;
    public final Class<? extends FlexViewHolder> viewHolderClass;
    public final int layoutResourceId;

    public FlexDescriptor(Class<?> dataClass, Class<? extends FlexViewHolder> viewHolderClass, int layoutResourceId) {
        this.dataClass = dataClass;
        this.viewHolderClass = viewHolderClass;
        this.layoutResourceId = layoutResourceId;
    }

    public FlexViewHolder addToView(Object data, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view =  inflater.inflate(layoutResourceId, container, false);
        FlexViewHolder holder = constructViewHolder(view);
        if (holder!=null){
            holder.onCreate(container, -1);
            holder.bindTo(data, -1);
            container.addView(view);
        }
        return holder;
    }

    //TODO: use this for all usages (without adapter)
    private FlexViewHolder constructViewHolder(View view) {
        FlexViewHolder holder = null;
        try {
            Constructor<? extends FlexViewHolder> constructor = viewHolderClass
                    .getConstructor(View.class, FlexAdapter.class);
            holder = constructor.newInstance(new Object[] { view, null });
        } catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }
        return holder;
    }
}
