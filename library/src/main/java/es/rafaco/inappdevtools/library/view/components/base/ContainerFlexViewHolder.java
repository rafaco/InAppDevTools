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

package es.rafaco.inappdevtools.library.view.components.base;

import android.support.annotation.CallSuper;
import android.view.View;
import android.view.ViewGroup;

import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexDescriptor;
import es.rafaco.inappdevtools.library.view.components.FlexLoader;

public abstract class ContainerFlexViewHolder extends FlexItemViewHolder {

    public ContainerFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
    }

    @CallSuper
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);
        ContainerFlexData data = (ContainerFlexData) abstractData;
        bindChild(data);
    }

    public abstract ViewGroup getChildContainer();

    private void bindChild(ContainerFlexData data) {
        if (getChildContainer() == null){
            //TODO: log develop warning
            return;
        }

        getChildContainer().removeAllViews();
        if (data.getChild() == null) {
            return;
        }

        FlexDescriptor desc = FlexLoader.getDescriptor(data.getChild().getClass());
        if (desc != null){
            desc.addToView(data.getChild(), getChildContainer());
        }
    }
}
