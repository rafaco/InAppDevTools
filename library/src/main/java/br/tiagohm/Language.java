/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * This is a modified source from project CodeView, which is available under
 * MIT License at https://github.com/tiagohm/CodeView
 *
 * Modifications:
 *
 *     * Added this attribution notice
 *     * Removed unused languages, keeping only included in assets: bash, cs, cpp, css,
 *       coffeescript, diff, xml, http, json, java, javascript, makefile, markdown, objectivec,
 *       php, perl, properties, python, ruby, sql, shell, dart, go, gradle, groovy, kotlin, lua,
 *       scala, scheme and plaintext
 *
 *
 * Modifications copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original copyright (c) 2016-2017 Tiago Melo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package br.tiagohm;


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

