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

package org.inappdevtools.library.view.components.items;

public class ButtonBorderlessFlexData extends ButtonFlexData {

    public ButtonBorderlessFlexData(String title, Runnable performer) {
        super(title, performer);
        setWrapContent(true);
    }

    public ButtonBorderlessFlexData(String title, int icon, Runnable performer) {
        super(title, icon, performer);
        setWrapContent(true);
    }

    public ButtonBorderlessFlexData(String title, int icon, int colorResId, Runnable performer) {
        super(title, icon, colorResId, performer);
        setWrapContent(true);
    }

    public ButtonBorderlessFlexData(String title, int icon, Runnable performer, Runnable callback) {
        super(title, icon, performer, callback);
        setWrapContent(true);
    }
}
