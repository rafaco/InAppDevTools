package es.rafaco.inappdevtools.library.view.components.codeview;

import java.util.Arrays;
import java.util.List;

public class Theme {

    public static final Theme ANDROIDSTUDIO = new Theme("androidstudio");

    public static final List<Theme> ALL = Arrays.asList(ANDROIDSTUDIO);

    private final String name;

    public Theme(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return "file:///android_asset/codeview/styles/" + getName() + ".css";
    }
}
