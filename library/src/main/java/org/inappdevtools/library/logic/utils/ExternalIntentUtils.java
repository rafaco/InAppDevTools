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

package org.inappdevtools.library.logic.utils;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.inappdevtools.library.IadtController;

public class ExternalIntentUtils {

    private static final String REPO_URL = "https://github.com/rafaco/InAppDevTools";
    private static final String README_URL = "https://github.com/rafaco/InAppDevTools/blob/master/README.md";
    private static final String ISSUES_URL = "https://github.com/rafaco/InAppDevTools/issues";
    private static final String WEBSITE_URL = "https://InAppDevTools.org";

    private ExternalIntentUtils() { throw new IllegalStateException("Utility class"); }

    public static void shareText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooserIntent = Intent.createChooser(intent, "Share with...");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IadtController.get().getContext().startActivity(chooserIntent);
    }

    public static void shareLibrary() {
        shareText(REPO_URL);
    }

    public static void viewReadme() {
        viewUrl(README_URL);
    }

    public static void viewIssues() {
        viewUrl(ISSUES_URL);
    }

    public static void viewWebsite() {
        viewUrl(WEBSITE_URL);
    }

    public static void viewUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IadtController.get().getContext().startActivity(i);
    }

    public static void shareDemo() {
        //"TODO after publication on Play Store"
    }

    public static void composeEmail(String address, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        String[] addresses = new String[]{address};
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Context context = IadtController.get().getContext();
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void search(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IadtController.get().getContext().startActivity(intent);
    }
}
