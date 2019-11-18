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

package es.rafaco.inappdevtools.library.view.components.deco;

import android.content.Context;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.List;

import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

public class DecoratedToolInfoAdapter
        extends RecyclerView.Adapter<DecoratedViewHolder> {

    private Context context;
    private List<DecoratedToolInfo> originalData;
    private boolean switchMode = false;

    public DecoratedToolInfoAdapter(Context context, List<DecoratedToolInfo> data) {
        this.context = context;
        this.originalData = data;
    }

    @Override
    public int getItemCount() {
        return originalData.size();
    }

    @NonNull
    @Override
    public DecoratedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DecoratedViewHolder(parent, switchMode);
    }

    @Override
    public void onBindViewHolder(@NonNull DecoratedViewHolder holder, int position) {
        final DecoratedToolInfo data = originalData.get(position);
        holder.bindTo(data);
    }


    public void enableSwitchMode(){
        switchMode = true;
    }

    public void add(DecoratedToolInfo data){
        originalData.add(data);
        notifyDataSetChanged();
    }

    public void replaceAll(final List<DecoratedToolInfo> data){
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                originalData.clear();
                //notifyDataSetInvalidated();
                originalData.addAll(data);
                notifyDataSetChanged();
            }
        });
    }
}
