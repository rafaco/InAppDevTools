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

package es.rafaco.inappdevtools.library.view.overlay.screens;

import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;

public abstract class AbstractFlexibleScreen extends Screen {

    private RecyclerView flexContainer;
    private FlexibleAdapter flexAdapter;

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
        flexAdapter = new FlexibleAdapter(getLayout(), getSpanCount(), new ArrayList<>());
        flexContainer = bodyView.findViewById(R.id.flexible);
        flexContainer.setHasFixedSize(true);
        flexContainer.setAdapter(flexAdapter);
    }

    public FlexibleAdapter.Layout getLayout(){
        return FlexibleAdapter.Layout.GRID;
    }

    public int getSpanCount(){
        return 1;
    }

    public void setFullWidthSolver(FlexibleAdapter.FullWidthSolver solver) {
        flexAdapter.setFullWidthSolver(solver);
    }

    public void updateAdapter(List<Object> options) {
        flexContainer.removeAllViews();
        flexAdapter.replaceItems(options);
    }
}
