package es.rafaco.inappdevtools.library.view.components.codeview;

import java.util.HashMap;
import java.util.Map;

public enum Language {

    AUTO(""),
    BASH("bash"),
    COFFEESCRIPT("coffeescript"),
    CPP("cpp"),
    C_SHARP("cs"),
    CSS("css"),
    DART("dart"),
    DIFF("diff"),
    GO("go"),
    GRADLE("gradle"),
    GROOVY("groovy"),
    HTML("html"),
    HTTP("http"),
    JAVA("java"),
    JAVASCRIPT("javascript"),
    JSON("json"),
    KOTLIN("kotlin"),
    LUA("lua"),
    MAKEFILE("makefile"),
    MARKDOWN("markdown"),
    OBJECTIVE_C("objectivec"),
    PERL("perl"),
    PLAINTEXT("plaintext"),
    PHP("php"),
    PROPERTIES("properties"),
    PYTHON("python"),
    RUBY("ruby"),
    SCALA("scala"),
    SCHEME("scheme"),
    //SCSS("scss"),
    //SWIFT("swift"),
    SHELL("shell"),
    SQL("sql"),
    XML("xml");

    private static final Map<String, Language> LANGUAGES = new HashMap<>();
    private final String name;

    static {
        for (Language language : values()) {
            if (language != AUTO) {
                LANGUAGES.put(language.name, language);
            }
        }
    }

    Language(String name) {
        this.name = name;
    }

    public static Language getLanguageByName(String name) {
        return LANGUAGES.get(name);
    }

    public String getLanguageName() {
        return name;
    }
}
