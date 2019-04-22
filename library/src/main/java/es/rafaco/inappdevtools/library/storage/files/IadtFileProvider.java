package es.rafaco.inappdevtools.library.storage.files;

import android.support.v4.content.FileProvider;

public class IadtFileProvider extends FileProvider
{
    // We extend FileProvider to avoid collision with existing from host app or from other libraries
    // This class is intentionally empty and only referenced in our library manifest
}