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

package es.rafaco.inappdevtools.library.logic.runnables;

public class RunButton {

    public RunButton(String title, Runnable performer) {}

    public RunButton(String title, int icon, Runnable performer) {}

    public RunButton(String title, int icon, Runnable performer, Runnable callback) {}

    public RunButton(String title, int icon, int color, Runnable performer, Runnable callback) {}

    public String getTitle() {
        return new String();
    }

    public void setTitle(String title) {}

    public Runnable getPerformer() {
        return null;
    }

    public void setPerformer(Runnable performer) {}

    public Runnable getCallback() {
        return null;
}

    public void setCallback(Runnable callback) {}

    public int getIcon() {
        return 0;
    }

    public void setIcon(int icon) {}

    public int getColor() {
        return 0;
    }

    public void setColor(int icon) {}

    public void run(){}
}
