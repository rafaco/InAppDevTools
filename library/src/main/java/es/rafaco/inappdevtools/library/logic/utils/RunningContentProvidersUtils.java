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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningContentProvidersUtils {

    private RunningContentProvidersUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static String getString() {
        String result = Humanizer.newLine();
        List<ProviderInfo> providers = getList();

        for(ProviderInfo provider : providers){
            result += provider.authority + Humanizer.newLine();
        }
        return result;
    }

    public static int getCount() {
        List<ProviderInfo> providers = getList();
        return providers.size();
    }

    public static String getClassName(ProviderInfo info) {
        return info.name;
    }

    public static List<ProviderInfo> getList() {
        List<ProviderInfo> result = new ArrayList<>();

        String packageName = getContext().getPackageName();
        List<PackageInfo> installedPackages = getContext().getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        for (PackageInfo pack: installedPackages) {
            if (pack.packageName.equals(packageName)){
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo info : providers) {
                        result.add(info);
                    }
                }
            }
        }
        return result;
    }

    public static String getTitle(ProviderInfo info) {
        String shortAuthority = Humanizer.removeHead(info.authority, info.packageName + ".");
        String shortName = Humanizer.getLastPart(info.name, ".");

        return shortName + " (" + shortAuthority + ")";
    }

    public static String getContent(ProviderInfo info) {
        StringBuilder contentBuffer = new StringBuilder();

        contentBuffer.append("Exported: " + info.exported);
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Multiprocess: " + info.multiprocess);
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Name: " + info.name);
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Authority: " + info.authority);
        contentBuffer.append(Humanizer.newLine());

        if (info.metaData!=null) {
            Bundle bundle = info.metaData;
            contentBuffer.append("MetaData: ");
            for (String key: bundle.keySet()) {
                contentBuffer.append(key + ": " + bundle.get(key));
                contentBuffer.append(Humanizer.newLine());
            }
        }

        return contentBuffer.toString();
    }
}
