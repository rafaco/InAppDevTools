package es.rafaco.inappdevtools.library.logic.utils;

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
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(README_URL));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IadtController.get().getContext().startActivity(i);
    }
}
