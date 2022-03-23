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

package org.inappdevtools.library.logic.runnables;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.view.components.items.ButtonFlexData;

public class RunnableManager {

    Context context;

    public RunnableManager(Context context) {
        this.context = context;
    }


    //region [ CUSTOM RUNNABLES ]

    private List<ButtonFlexData> buttonDataList;

    public void add(ButtonFlexData config){
        if (buttonDataList == null)
            buttonDataList = new ArrayList<>();
        buttonDataList.add(config);
    }

    public boolean remove(ButtonFlexData target){
        if (buttonDataList !=null && buttonDataList.contains(target)) {
            return buttonDataList.remove(target);
        }
        return false;
    }

    public List<ButtonFlexData> getAll(){
        if (buttonDataList == null)
            buttonDataList = new ArrayList<>();
        return buttonDataList;
    }

    //endregion

    //region [ FORCE CLOSE RUNNABLE ]

    private static Runnable onForceCloseRunnable;

    public void setForceCloseRunnable(Runnable onForceClose){
        onForceCloseRunnable = onForceClose;
    }

    public Runnable getForceCloseRunnable(){
        return onForceCloseRunnable;
    }

    //region
}
