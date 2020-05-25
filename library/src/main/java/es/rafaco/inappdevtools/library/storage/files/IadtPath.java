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

    public static final String APP_BUILD_CONFIG_FILE = "app_build_config.json";
    public static final String BUILD_INFO_FILE = "build_info.json";
    public static final String BUILD_CONFIG_FILE = "build_config.json";
    public static final String PLUGIN_LIST_FILE = "gradle_plugins.txt";
    public static final String DEPENDENCIES_FILE = "gradle_dependencies.txt";
    public static final String GIT_INFO_JSON_FILE = "git_info.json";
    public static final String GIT_REMOTE_BRANCH_TXT_FILE = "git_remote_branch.txt";
    public static final String GIT_LOCAL_BRANCH_DIFF_FILE = "git_local_branch.diff";
    public static final String GIT_LOCAL_BRANCH_TXT_FILE = "git_local_branch.txt";
    public static final String GIT_LOCAL_CHANGES_DIFF_FILE = "git_local_changes.diff";
    public static final String GIT_LOCAL_CHANGES_TXT_FILE = "git_local_changes.txt";

    public static final String BUILD_INFO = SUBFOLDER + "/" + BUILD_INFO_FILE;
    public static final String BUILD_CONFIG = SUBFOLDER + "/" + BUILD_CONFIG_FILE;
    public static final String PLUGIN_LIST = SUBFOLDER + "/" + PLUGIN_LIST_FILE;
    public static final String DEPENDENCIES = SUBFOLDER + "/" + DEPENDENCIES_FILE;
    public static final String GIT_INFO_JSON = SUBFOLDER + "/" + GIT_INFO_JSON_FILE;
    public static final String GIT_REMOTE_BRANCH_TXT = SUBFOLDER + "/" + GIT_REMOTE_BRANCH_TXT_FILE;
    public static final String GIT_LOCAL_BRANCH_TXT = SUBFOLDER + "/" + GIT_LOCAL_BRANCH_TXT_FILE;
    public static final String GIT_LOCAL_BRANCH_DIFF = SUBFOLDER + "/" + GIT_LOCAL_BRANCH_DIFF_FILE;
    public static final String GIT_LOCAL_CHANGES_DIFF = SUBFOLDER + "/" + GIT_LOCAL_CHANGES_DIFF_FILE;
    public static final String GIT_LOCAL_CHANGES_TXT = SUBFOLDER + "/" + GIT_LOCAL_CHANGES_TXT_FILE;

    public static final String ORIGINAL_MANIFEST = SOURCES + "/AndroidManifest.xml";
    public static final String MERGED_MANIFEST = GENERATED + "/merged_manifests/AndroidManifest.xml";
}
