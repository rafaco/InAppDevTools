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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.annotation.Nullable;
//@import androidx.core.view.ViewCompat;
//@import androidx.recyclerview.widget.GridLayoutManager;
//@import androidx.recyclerview.widget.StaggeredGridLayoutManager;
//@import androidx.recyclerview.widget.RecyclerView;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//#else
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
//#endif

import java.lang.reflect.Constructor;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.components.base.FlexData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexViewHolder;

public class FlexAdapter extends RecyclerView.Adapter<FlexViewHolder> {

    public enum Layout { LINEAR, GRID, STAGGERED }

    private Layout layout;
    private final int spanCount;
    private FullWidthSolver fullWidthSolver;
    private List<FlexDescriptor> descriptors;
    private List<Object> items;
    private OnItemActionListener onItemActionListener;

    public FlexAdapter(List<Object> data) {
        this(Layout.LINEAR, 1, data);
    }

    public FlexAdapter(Layout layout, int spanCount, List<Object> data) {
        this.layout = layout;
        this.spanCount = spanCount;
        this.items = data;
        this.descriptors = FlexLoader.getAllDescriptors();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        recyclerView.setLayoutManager(getLayoutManager(recyclerView));
        super.onAttachedToRecyclerView(recyclerView);
    }

    private RecyclerView.LayoutManager getLayoutManager(@NonNull RecyclerView recyclerView) {
        if (layout.equals(Layout.LINEAR)){
            return new LinearLayoutManager(recyclerView.getContext());
        }
        else if (layout.equals(Layout.GRID)){
            final GridLayoutManager manager = new GridLayoutManager(recyclerView.getContext(),
                    spanCount, RecyclerView.VERTICAL, false);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isFullWidthItem(position) ? manager.getSpanCount() : 1);
                }
            });
            return manager;
        }
        else if (layout.equals(Layout.STAGGERED)){
            final StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
            manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            return manager;
        }
        return null;
    }

    public Class<?> getItemDataClass(int position) {
        Object item = items.get(position);
        return item.getClass();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        for (int i=0; i<descriptors.size(); i++){
            FlexDescriptor descriptor = descriptors.get(i);
            if (item.getClass().equals(descriptor.dataClass)){
                return i;
            }
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public FlexViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        FlexDescriptor desc = descriptors.get(viewType);
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(desc.layoutResourceId, viewGroup, false);
        FlexViewHolder holder = constructViewHolder(desc, view);
        if (holder==null){
            return new TextFlexViewHolder(viewGroup, this);
        }
        holder.onCreate(viewGroup, viewType);
        return holder;
    }

    private FlexViewHolder constructViewHolder(FlexDescriptor desc, View view) {
        FlexViewHolder holder = null;
        try {
            Constructor<? extends FlexViewHolder> ctor = desc.viewHolderClass
                    .getConstructor(View.class, FlexAdapter.class);
            holder = ctor.newInstance(view, this);
        } catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FlexViewHolder holder, int position) {
        Object viewData = items.get(position);
        FlexDescriptor desc = descriptors.get(getItemViewType(position));

        if (layout.equals(Layout.STAGGERED) && isFullWidthItem(position)) {
            StaggeredGridLayoutManager.LayoutParams layoutParams;
            layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }

        FlexViewHolder castedHolder = desc.viewHolderClass.cast(holder);
        Object castedData = desc.dataClass.cast(viewData);
        castedHolder.bindTo(castedData, position);
    }

    public List<Object> getItems(){
        return items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void replaceItems(final List<?> data){
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }


    //region [ ITEM ACTION ]

    public interface OnItemActionListener {
        Object onItemAction(FlexViewHolder viewHolder, View view, int position, long id);
    }

    public void setOnItemActionListener(@Nullable OnItemActionListener listener) {
        onItemActionListener = listener;
    }

    public OnItemActionListener getOnItemClickListener() {
        return onItemActionListener;
    }

    public Object performItemAction(FlexViewHolder viewHolder, View view, int position, long id) {
        if (onItemActionListener != null) {
            return onItemActionListener.onItemAction(viewHolder, view, position, id);
        }
        return null;
    }

    //endregion

    //region [ FULL WIDTH SOLVER ]

    public interface FullWidthSolver {
        Boolean isFullWidth(int position);
    }

    public void setFullWidthSolver(FullWidthSolver solver) {
        this.fullWidthSolver = solver;
    }

    protected boolean isFullWidthItem(int position) {
        //Solve first from this specific item data
        Object itemData = getItems().get(position);
        if (itemData instanceof FlexData){
            Boolean fullSpanOnData = ((FlexData)itemData).isFullSpan();
            if (fullSpanOnData != null){
                return fullSpanOnData;
            }
        }

        //Fallback to custom solver if it has been provided
        if (fullWidthSolver!=null){
            Boolean fullSpanFromSolver = fullWidthSolver.isFullWidth(position);
            if (fullSpanFromSolver != null){
                return fullSpanFromSolver;
            }
        }

        //Fallback to default option for this data class, as defined at FlexLoader
        return FlexLoader.isFullSpan(getItemDataClass(position));
    }

    //endregion
}
