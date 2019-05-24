package es.rafaco.inappdevtools.library.storage.files;

//#ifdef MODERN
import androidx.core.content.FileProvider;
//#else
//@import android.support.v4.content.FileProvider;
//#endif

public class IadtFileProvider extends FileProvider {
    // This class is intentionally empty and only referenced in our library manifest
    // We extend FileProvider to avoid collision with another FileProvider included at host app
}
