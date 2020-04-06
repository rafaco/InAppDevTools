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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class FlexibleItemDescriptor {

    public final Class<?> dataClass;
    public final Class<? extends FlexibleViewHolder> viewHolderClass;
    public final int layoutResourceId;

    public FlexibleItemDescriptor(Class<?> dataClass, Class<? extends FlexibleViewHolder> viewHolderClass, int layoutResourceId) {
        this.dataClass = dataClass;
        this.viewHolderClass = viewHolderClass;
        this.layoutResourceId = layoutResourceId;
    }

    public View addToView(Object data, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view =  inflater.inflate(layoutResourceId, container, false);
        FlexibleViewHolder holder = constructViewHolder(view);
        holder.onCreate(container, -1);
        holder.bindTo(data, -1);
        container.addView(view);
        return view;
    }

    //TODO: use this for all usages
    private FlexibleViewHolder constructViewHolder(View view) {
        FlexibleViewHolder holder = null;
        try {
            Constructor<? extends FlexibleViewHolder> constructor = viewHolderClass
                    .getConstructor(View.class);
            holder = constructor.newInstance(new Object[] { view });
        } catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }
        return holder;
    }
}
