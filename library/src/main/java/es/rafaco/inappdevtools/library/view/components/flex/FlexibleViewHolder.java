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

import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
//#endif

public abstract class FlexibleViewHolder extends RecyclerView.ViewHolder {

    //TODO: remove usages and param from constructor (used at traces)
    FlexibleAdapter adapter;

    //Constructor used when creating from other viewHolders, like RunButtons and RunButtonGroup
    // at ComplexCard
    //It should be the only one and we should stop passing adapter to the view holders
    public FlexibleViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    //Constructor used when creating from the adapter
    //We should stop passing adapter to the view holders, needed at current traces
    public FlexibleViewHolder(@NonNull View itemView,@NonNull FlexibleAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
    }
    public void onCreate(ViewGroup viewGroup, int viewType){}
    public abstract void bindTo(Object abstractData, int position);
}
