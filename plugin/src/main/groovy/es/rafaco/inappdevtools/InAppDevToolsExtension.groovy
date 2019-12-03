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

package es.rafaco.inappdevtools

class InAppDevToolsExtension {

    //User features fags
    String email
    String notes

    // Build flags (affect plugin)
    Boolean enabled = true
    Boolean enabledOnRelease
    Boolean debug = false
    Boolean sourceInclusion
    Boolean sourceInspection

    // Runtime flags (used later on)
    Boolean overlayEnabled
    Boolean invocationByShake
    Boolean invocationByIcon
    Boolean invocationByNotification
    Boolean callDefaultCrashHandler
}
