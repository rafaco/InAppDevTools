/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.overlay.screens;

import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import org.inappdevtools.library.view.components.FlexAdapter;

import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.utils.MarginUtils;

public abstract class AbstractFlexibleScreen extends Screen {

    private RecyclerView flexContainer;
    protected FlexAdapter flexAdapter;

    public AbstractFlexibleScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart(ViewGroup bodyView) {
        initAdapter(bodyView);
        onAdapterStart();
    }

    protected abstract void onAdapterStart();

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.flexible_container;
    }

    private void initAdapter(ViewGroup bodyView) {
        flexAdapter = new FlexAdapter(getLayout(), getSpanCount(), new ArrayList<>());
        flexContainer = bodyView.findViewById(R.id.flexible);
        flexContainer.setHasFixedSize(true);
        if(!hasHorizontalMargin()) MarginUtils.removeHorizontalMargin(flexContainer);
        flexContainer.setAdapter(flexAdapter);
    }

    public FlexAdapter.Layout getLayout(){
        return FlexAdapter.Layout.GRID;
    }

    public int getSpanCount(){
        return 1;
    }

    public boolean hasHorizontalMargin(){
        return true;
    }

    public void setFullWidthSolver(FlexAdapter.FullWidthSolver solver) {
        flexAdapter.setFullWidthSolver(solver);
    }

    public void updateAdapter(List<Object> options) {
        flexContainer.removeAllViews();
        flexAdapter.replaceItems(options);
    }
}
