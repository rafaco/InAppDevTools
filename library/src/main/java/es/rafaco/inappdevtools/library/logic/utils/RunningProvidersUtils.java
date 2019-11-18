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

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningProvidersUtils {

    private RunningProvidersUtils() { throw new IllegalStateException("Utility class"); }

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

    private static List<ProviderInfo> getList() {
        List<ProviderInfo> result = new ArrayList<>();

        String packageName = getContext().getPackageName();
        for (PackageInfo pack: getContext().getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
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
}
