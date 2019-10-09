package es.rafaco.inappdevtools.library.logic.utils;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import es.rafaco.inappdevtools.library.IadtController;

public class ExternalIntentUtils {

    public static final String REPO_URL = "https://github.com/rafaco/InAppDevTools";
    public static final String README_URL = "https://github.com/rafaco/InAppDevTools/blob/master/README.md";

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
