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

package es.rafaco.inappdevtools.library.storage.files;

public class IadtPath {

    public static final String SUBFOLDER = "iadt";

    public static final String ASSETS = "assets";
    public static final String SOURCES = "Sources";
    public static final String RESOURCES = "Resources";
    public static final String GENERATED = "Generated";

    public static final String SOURCES_TAIL = "_sources.zip";
    public static final String RESOURCES_TAIL = "_resources.zip";
    public static final String GENERATED_TAIL = "_generated.zip";

    public static final String BUILD_INFO = SUBFOLDER + "/" + "build_info.json";
    public static final String BUILD_CONFIG = SUBFOLDER + "/" + "build_config.json";
    public static final String GIT_CONFIG = SUBFOLDER + "/" + "git_info.json";
    public static final String PLUGIN_LIST = SUBFOLDER + "/" + "gradle_plugins.txt";
    public static final String LOCAL_COMMITS = SUBFOLDER + "/" + "local_commits.txt";
    public static final String LOCAL_CHANGES = SUBFOLDER + "/" + "local_changes.diff";
}
