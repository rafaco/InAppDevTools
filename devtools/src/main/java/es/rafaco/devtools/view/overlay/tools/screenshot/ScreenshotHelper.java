package es.rafaco.devtools.view.overlay.tools.screenshot;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.view.overlay.tools.log.LogTool;
import es.rafaco.devtools.utils.ViewHierarchyUtils;

public class ScreenshotHelper {

    private static final String SCREENSHOTS_DIR_NAME = "Screenshots";
    private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s_%s.jpg";
    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";

    private Context context;

    public ScreenshotHelper(Context context) {
        this.context = context;
    }

    public File takeScreenshot() {
        Log.d(DevTools.TAG, "Permissions are: " + LogTool.isExternalStorageWritable());
        List<Pair<String, View>> rootViews = ViewHierarchyUtils.getRootViews(true);
        // create bitmap screen capture
        //View v1 = getWindow().getDecorView().getRootView();
        //ViewGroup v1 = (ViewGroup) ((ViewGroup) widgetsManager.getView(Widget.Type.FULL).findViewById(android.R.id.content)).getChildAt(0);

        View v1 = rootViews.get(0).second;

        Date now = new Date();
        String formattedDate = DateFormat.format("yyyy-MM-dd_hh:mm:ss", now).toString();

        try {
            v1.setDrawingCacheEnabled(true);
            v1.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            //File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), SCREENSHOTS_DIR_NAME);
            if (!folder.exists())
                folder.mkdir();

            long mImageTime = System.currentTimeMillis();
            String imageDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(mImageTime));
            String fileName = String.format(SCREENSHOT_FILE_NAME_TEMPLATE, imageDate, "DT");
            File imageFile = new File(folder, fileName);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            ContentResolver cr = context.getContentResolver();
            //MediaStoreUtils.insertImage(cr, )

            return imageFile;
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return null;
    }

    public void openFile(File imageFile) {
        Intent intent = buildViewIntent(imageFile);
        context.startActivity(intent);
    }

    private Intent buildViewIntent(File file){
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String type = mime.getMimeTypeFromExtension(extension);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = context.getApplicationContext().getPackageName() + ".devtools.provider";
            Uri contentUri = FileProvider.getUriForFile(context, authority, file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        return intent;
    }
}
