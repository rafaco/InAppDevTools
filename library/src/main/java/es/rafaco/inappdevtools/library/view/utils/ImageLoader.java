package es.rafaco.inappdevtools.library.view.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;

public class ImageLoader {

    private ImageLoader() { throw new IllegalStateException("Utility class"); }

    public static void loadInto(String path, ImageView imageView ){
        File imgFile = new  File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }
}
