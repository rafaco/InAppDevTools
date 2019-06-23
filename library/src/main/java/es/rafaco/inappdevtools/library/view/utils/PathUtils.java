package es.rafaco.inappdevtools.library.view.utils;


import android.arch.persistence.room.Database;

public class PathUtils {

    public static String removeLastSlash(String path) {
        if (path.lastIndexOf("/") == path.length()-1)
            return removeLastChar(path);
        return path;
    }

    public static String removeLastChar(String path) {
        return path.substring(0, path.length()-1);
    }

    public static String getFileNameWithExtension(String path){
        String[] parts = path.split("[/]");
        boolean isFile = !path.endsWith("/");
        return isFile ? (parts[parts.length-1]) : "";
    }

    public static String getFileExtension(String path){
        if (path.contains(".")){
            int lastFound = path.lastIndexOf(".");
            String extension = path.substring(lastFound + 1);
            return extension;
        }
        return "";
    }

    /**
     * Get the file name or folder name
     * @param path
     * @return
     */
    public static String getLastLevelName(String path){
        if(path ==null) return "";
        String[] parts = removeLastSlash(path).split("[/]");
        if (parts.length<1) return "";
        return parts[parts.length-1];
    }
}
