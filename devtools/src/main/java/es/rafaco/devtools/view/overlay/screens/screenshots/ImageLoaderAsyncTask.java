package es.rafaco.devtools.view.overlay.screens.screenshots;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

import es.rafaco.devtools.utils.UiUtils;

public class ImageLoaderAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private final ImageView imageView;

    public ImageLoaderAsyncTask(ImageView view) {
        super();
        imageView = view;
    }

    @Override
    protected Bitmap doInBackground(String... paths) {
        File imgFile = new  File(paths[0]);
        if(imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Point screenDimensions = UiUtils.getDisplaySize(imageView.getContext());
            int sizeX = (screenDimensions.x /2) - 54;
            Bitmap scaled = getProportionalBitmap(bitmap, sizeX, "X");

            /*Log.d("RAFA", "Original size: " + bitmap.getWidth() + " x " + bitmap.getHeight());
            Log.d("RAFA", "Screen size: " + screenDimensions.x + " x " + screenDimensions.y);
            Log.d("RAFA", "Scaled size: " + scaled.getWidth() + " x " + scaled.getHeight());*/

            /*int scaleToUse = 20; // this will be our percentage
            int sizeY = screenResolution.y * scaleToUse / 100;
            int sizeX = bitmap.getWidth() * sizeY / bitmap.getHeight();
            Bitmap scaled = Bitmap.createScaledBitmap(bmp, sizeX, sizeY, false);*/

            return scaled;
        }
        return null;
    }

    public Bitmap getProportionalBitmap(Bitmap bitmap,
                                        int newDimensionXorY,
                                        String XorY) {
        if (bitmap == null)
            return null;

        float xyRatio;
        int newWidth, newHeight;

        if (XorY.toLowerCase().equals("x")) {
            xyRatio = (float) newDimensionXorY / bitmap.getWidth();
            newHeight = (int) (bitmap.getHeight() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newDimensionXorY, newHeight, true);
        } else if (XorY.toLowerCase().equals("y")) {
            xyRatio = (float) newDimensionXorY / bitmap.getHeight();
            newWidth = (int) (bitmap.getWidth() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newWidth, newDimensionXorY, true);
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result){
        if (result != null){
            imageView.setImageBitmap(result);
        }
    }
}

