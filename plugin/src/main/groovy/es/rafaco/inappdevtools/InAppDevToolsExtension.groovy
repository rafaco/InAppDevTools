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

    // Build flags (affect plugin)
    Boolean enabled = true
    Boolean useNoop = false
    String[] exclude = ["release"]
    Boolean debug = false

    //User features fags
    String notes
    String teamName
    String teamEmail
    String teamDesc
    Map teamLinks

    Boolean sourceInclusion
    Boolean sourceInspection
    Boolean networkInterceptor
    Boolean viewInspection
    Boolean storageInspection

    // Runtime flags (used later on)
    Boolean overlayEnabled
    Boolean invocationByShake
    Boolean invocationByIcon
    Boolean callDefaultCrashHandler
    Boolean injectEventsOnLogcat
    
    Map toMap(){
        Map map = [:]
        if (enabled!=null) map.put("enabled", enabled)
        if (exclude!=null) map.put("exclude", exclude)
        if (useNoop!=null) map.put("noopEnabled", useNoop)
        if (debug!=null) map.put("debug", debug)

        if (notes!=null) map.put("notes", notes)
        if (teamName!=null) map.put("teamName", teamName)
        if (teamEmail!=null) map.put("teamEmail", teamEmail)
        if (teamDesc!=null) map.put("teamDesc", teamDesc)
        if (teamLinks!=null) map.put("teamLinks", teamLinks)

        if (overlayEnabled!=null) map.put("overlayEnabled", overlayEnabled)
        if (sourceInclusion!=null) map.put("sourceInclusion", sourceInclusion)
        if (sourceInspection!=null) map.put("sourceInspection", sourceInspection)
        if (viewInspection!=null) map.put("viewInspection", viewInspection)
        if (storageInspection!=null) map.put("storageInspection", storageInspection)
        if (networkInterceptor!=null) map.put("networkInterceptor", networkInterceptor)
        if (invocationByIcon!=null) map.put("invocationByIcon", invocationByIcon)
        if (invocationByShake!=null) map.put("invocationByShake", invocationByShake)
        if (callDefaultCrashHandler!=null) map.put("callDefaultCrashHandler", callDefaultCrashHandler)
        if (injectEventsOnLogcat!=null) map.put("injectEventsOnLogcat", injectEventsOnLogcat)
        return map
    }
}
