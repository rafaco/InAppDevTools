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

package es.rafaco.inappdevtools.library.logic.utils;

public class AppBuildConfigField {
    public static final String DEBUG = "DEBUG";
    public static final String APPLICATION_ID = "APPLICATION_ID";
    public static final String BUILD_TYPE = "BUILD_TYPE";
    public static final String FLAVOR = "FLAVOR";
    public static final String VERSION_CODE = "VERSION_CODE";
    public static final String VERSION_NAME = "VERSION_NAME";

    private AppBuildConfigField() {
        throw new IllegalStateException("Utility class");
    }
}
