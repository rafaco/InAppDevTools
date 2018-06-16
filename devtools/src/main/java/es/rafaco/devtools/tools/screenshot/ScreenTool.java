package es.rafaco.devtools.tools.screenshot;

import android.content.ContentResolver;
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
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.tools.log.LogTool;
import es.rafaco.devtools.utils.ViewHierarchyUtils;

public class ScreenTool extends Tool {

    private Button shotButton;

    public ScreenTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Screen";
    }

    @Override
    public String getLayoutId() {
        return "tool_screen";
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onStart(View toolView) {
        initView(toolView);
    }


    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }


    private void initView(View toolView) {
        shotButton = toolView.findViewById(R.id.shot_button);
        shotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScreenshotButton();
            }
        });
    }

    private void onScreenshotButton() {
        Boolean hasPermission = PermissionActivity.startIfNeeded(getContext(), PermissionActivity.IntentAction.STORAGE);
        Log.d(DevTools.TAG, "hasPermission is: " + String.valueOf(hasPermission));

        //if (hasPermission)
            takeScreenshot();
    }


    private static final String SCREENSHOTS_DIR_NAME = "Screenshots";
    private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s_%s.jpg";
    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";

    private void takeScreenshot() {
        Log.d(DevTools.TAG, "Permissions are: " + LogTool.isExternalStorageWritable());
        List<Pair<String, View>> rootViews = ViewHierarchyUtils.getRootViews(true);
        // create bitmap screen capture
        //View v1 = getWindow().getDecorView().getRootView();
        //ViewGroup v1 = (ViewGroup) ((ViewGroup) widgetsManager.getView(Widget.Type.FULL).findViewById(android.R.id.content)).getChildAt(0);
        View v1 = rootViews.get(3).second;

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

            ContentResolver cr = getContext().getContentResolver();
            //MediaStoreUtils.insertImage(cr, )

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = buildViewIntent(imageFile);
        getContext().startActivity(intent);
    }


    private Intent buildViewIntent(File file){
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String type = mime.getMimeTypeFromExtension(extension);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = getContext().getApplicationContext().getPackageName() + ".devtools.provider";
            Uri contentUri = FileProvider.getUriForFile(getContext(), authority, file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        return intent;
    }
}
