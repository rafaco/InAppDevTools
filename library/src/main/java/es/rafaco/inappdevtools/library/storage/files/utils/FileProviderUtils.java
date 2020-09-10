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

package es.rafaco.inappdevtools.library.storage.files.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

//#ifdef ANDROIDX
//@import androidx.core.content.FileProvider;
//#else
import android.support.v4.content.FileProvider;
//#endif

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class FileProviderUtils {

    public static final String ROOT_FOLDER = "iadt";
    public static final String AUTHORITY_SUFFIX = ".iadt.files";

    public static String getAuthority(Context context) {
        return context.getApplicationContext().getPackageName() + AUTHORITY_SUFFIX;
    }

    private static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = getAuthority(context);
            return FileProvider.getUriForFile(context, authority, file);
        }
        else {
            return Uri.fromFile(file);
        }
    }

    public static String getMimeType(File file) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimeTypeFromExtension = mime.getMimeTypeFromExtension(extension);
        Log.d(Iadt.TAG, "MimeType: " + mimeTypeFromExtension);
        return mimeTypeFromExtension;
    }


    public static void viewExternally(String text, String filePath) {
        sendIntentAction(Intent.ACTION_VIEW, text, filePath);
    }

    public static void sendExternally(String text, String filePath) {
        sendIntentAction(Intent.ACTION_SEND, text, filePath);
    }

    private static void sendIntentAction(String action, String text, String filePath) {
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(text)){
            addPlainText(intent, text);
            intent.setType("text/plain");
        }
        Uri contentUri = null;
        if (!TextUtils.isEmpty(filePath)){
            contentUri = addFile(intent, filePath);
            putMimeType(intent, filePath); // This override "text/plain" if set
        }
        fireIntentWithChooser(intent, contentUri, "Choose an app...");
    }

    public static void sendEmail(String emailTo, String emailCC,
                                 String emailSubject, String emailBody,
                                 List<String> attachments) {

        final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("message/rfc822");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        intent.putExtra(Intent.EXTRA_CC, new String[]{emailCC});
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

        //TODO:
        // if (isHtml){ FileProviderUtils.addHtmlText(intent, getHtmlSample());

        //TODO: Following line works but produce a warning
        FileProviderUtils.addPlainText(intent, emailBody);

        //Solution to previous warning, but text don't work with gMail
        /*ArrayList<String> extra_text = new ArrayList<String>();
        extra_text.add(emailBody);
        intent.putStringArrayListExtra(Intent.EXTRA_TEXT, extra_text);*/

        List<Uri> uris = FileProviderUtils.addFiles(intent, attachments);
        FileProviderUtils.fireIntentWithChooser(intent, uris, "Choose an app...");
    }


    //region [ INTENT BUILDER ]

    public static void addPlainText(Intent intent, String text) {
        //intent.setType("text/html");
        /*ArrayList<CharSequence> charSequences = new ArrayList<>();
        charSequences.add(text);*/
        intent.putExtra(Intent.EXTRA_TEXT, text);
    }

    public static void addHtmlText(Intent intent, String html) {
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(html));
    }

    public static Uri addFile(Intent intent, String filePath) {
        Context context = IadtController.get().getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        File file = new File(filePath);
        Uri contentUri = getUriForFile(context, file);
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);

        return contentUri;
    }

    public static ArrayList<Uri> addFiles(Intent intent, List<String> filePaths) {
        Context context = IadtController.get().getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        ArrayList<Uri> uris = new ArrayList<>();
        if (filePaths != null && filePaths.size()>0){
            for (String filePath : filePaths) {
                File file = new File(filePath);
                Uri contentUri = getUriForFile(context, file);
                uris.add(contentUri);
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }
        return uris;
    }

    public static String putMimeType(Intent intent, String filePath) {
        File file = new File(filePath);
        String type = getMimeType(file);
        intent.setType(type);
        return type;
    }

    //endregion


    //region [ FIRE INTENT ]

    public static void fireIntentWithChooser(Intent emailIntent, Uri uri, String title) {
        List<Uri> uris = new ArrayList<>();
        uris.add(uri);
        fireIntentWithChooser(emailIntent, uris, title);
    }

    public static void fireIntentWithChooser(Intent intent, List<Uri> uris, String title) {
        Context context = IadtController.get().getContext();
        Intent chooserIntent = Intent.createChooser(intent, title);
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (uris!=null && uris.size()>0 && uris.get(0)!=null){
            FileProviderUtils.grantPermissionToChoosers(chooserIntent, uris);
        }
        if (chooserIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooserIntent);
        }
    }

    private static void grantPermissionToChoosers(Intent chooserIntent, List<Uri> uris) {
        Context context = IadtController.get().getContext();
        List<ResolveInfo> resInfoList = context.getPackageManager()
                .queryIntentActivities(chooserIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            for (Uri uri : uris) {
                context.grantUriPermission(packageName, uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    //endregion

    public static void deleteAll(){
        File file = new File(FileCreator.getIadtFolder());
        deleteRecursive(file);
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}
