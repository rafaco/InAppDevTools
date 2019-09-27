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
