/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2021 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.config

class IadtConfigFields {

    //// Build flags (affect plugin behaviour)
    static final String ENABLED = 'enabled'
    static final String USE_NOOP = 'useNoop'
    static final String EXCLUDE = 'exclude'
    static final String DEBUG = 'debug'

    //// Customization flags (runtime)
    static final String NOTES = 'notes'
    static final String TEAM_NAME = 'teamName'
    static final String TEAM_EMAIL = 'teamEmail'
    static final String TEAM_DESC = 'teamDesc'
    static final String TEAM_LINKS = 'teamLinks'

    //// Feature switches (build)
    static final String SOURCE_INCLUSION = 'sourceInclusion'
    static final String SOURCE_INSPECTION = 'sourceInspection'
    static final String NETWORK_INTERCEPTOR = 'networkInterceptor'
    static final String VIEW_INSPECTION = 'viewInspection'
    static final String STORAGE_INSPECTION = 'storageInspection'

    // Feature switches (runtime)
    static final String OVERLAY_ENABLED = 'overlayEnabled'
    static final String INVOCATION_BY_SHAKE = 'invocationByShake'
    static final String INVOCATION_BY_ICON = 'invocationByIcon'
    static final String CALL_DEFAULT_CRASH_HANDLER = 'callDefaultCrashHandler'
    static final String INJECT_EVENTS_ON_LOGCAT = 'injectEventsOnLogcat'
}