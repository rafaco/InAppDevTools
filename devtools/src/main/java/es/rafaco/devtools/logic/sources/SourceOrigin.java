package es.rafaco.devtools.logic.sources;

import java.util.List;
import java.util.jar.JarFile;

public class SourceOrigin {
    public String name;
    public JarFile localJar;
    public List<SourceEntry> items;
}
