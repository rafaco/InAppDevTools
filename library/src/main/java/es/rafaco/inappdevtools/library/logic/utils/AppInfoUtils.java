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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class AppInfoUtils {

    private AppInfoUtils() { throw new IllegalStateException("Utility class"); }

    public static String getUUID(Context context){
        String uuid = Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return uuid;
    }

    public static String getSigningInfo(Context context){
        StringBuilder sb = new StringBuilder();
        String newLine = Humanizer.newLine();
        final PackageManager packageManager = context.getPackageManager();
        PackageInfo p = null;

        try {
            p = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            FriendlyLog.logException("Exception", e);
        }

        if (p!= null){
            //final String strName = p.applicationInfo.loadLabel(packageManager).toString();
            //final String strVendor = p.packageName;
            //sb.append(newLine + strName + " / " + strVendor + newLine);

            final Signature[] arrSignatures = p.signatures;
            for (final Signature sig : arrSignatures) {
                final byte[] rawCert = sig.toByteArray();
                InputStream certStream = new ByteArrayInputStream(rawCert);

                try {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                    X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(certStream);
                    sb.append(x509Cert.toString());

                    //sb.append("Certificate subject: " + x509Cert.getSubjectDN() + newLine);
                    //sb.append("Certificate issuer: " + x509Cert.getIssuerDN() + newLine);
                    //sb.append("Certificate serial number: " + x509Cert.getSerialNumber() + newLine);
                    //sb.append(newLine);
                }
                catch (CertificateException e) {
                    Log.e(Iadt.TAG, "Error retrieving certificate" + newLine);
                    FriendlyLog.logException("Exception", e);
                    return "Error retrieving certificate";
                }
            }
        }
        return sb.toString();
    }

    /**
     * Get Manifest metadata
     * Usage: <meta-data android:name="custom_metadata" android:value="false" />
     * and then: boolean enabled = getMetadata().getBoolean("custom_metadata", true);
     * @param context
     * @return
     */
    public static Bundle getManifestMetadata(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            FriendlyLog.logException("Exception at getMetadata", e);
        }

        if (appInfo == null){
            return null;
        }

        return appInfo.metaData;
    }
}
