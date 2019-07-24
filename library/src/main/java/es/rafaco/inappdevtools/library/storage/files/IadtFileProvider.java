package es.rafaco.inappdevtools.library.storage.files;

//#ifdef ANDROIDX
//@import androidx.core.content.FileProvider;
//#else
import android.support.v4.content.FileProvider;
//#endif

public class IadtFileProvider extends FileProvider {
    // This class is intentionally empty and only referenced in our library manifest
    // We extend it to avoid collision and to allow default FileProvider implementation
    // at host app
}
