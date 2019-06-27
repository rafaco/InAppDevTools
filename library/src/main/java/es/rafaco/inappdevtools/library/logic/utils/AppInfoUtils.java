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
                    sb.append(x509Cert.toString() + newLine);

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
