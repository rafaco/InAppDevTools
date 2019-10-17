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
    _1C("1c"),
    ABNF("abnf"),
    ACCESS_LOG("accesslog"),
    ACTIONSCRIPT("actionscript"),
    ADA("ada"),
    APACHE("apache"),
    APPLESCRIPT("applescript"),
    ARDUINO("arduino"),
    ARM_ASSEMBLY("armasm"),
    ASCII_DOC("asciidoc"),
    ASPECTJ("aspectj"),
    AUTOHOTKEY("autohotkey"),
    AUTOIT("autoit"),
    AVR_ASSEMBLER("avrasm"),
    AWK("awk"),
    AXAPTA("axapta"),
    BASH("bash"),
    BASIC("basic"),
    BNF("bnf"),
    BRAINFUCK("brainfuck"),
    C_AL("cal"),
    CAP_N_PROTO("capnproto"),
    CEYLON("ceylon"),
    CLEAN("clean"),
    CLOJURE("clojure"),
    CLOJURE_REPL("clojure_repl"),
    CMAKE("cmake"),
    COFFEESCRIPT("coffeescript"),
    COQ("coq"),
    CACHE_OBJECT_SCRIPT("cos"),
    CPP("cpp"),
    CRMSH("crmsh"),
    CRYSTAL("crystal"),
    C_SHARP("cs"),
    CSP("csp"),
    CSS("css"),
    D("d"),
    DART("dart"),
    DELPHI("delphi"),
    DIFF("diff"),
    DJANGO("django"),
    DNS("dns"),
    DOCKERFILE("dockerfile"),
    DOS("dos"),
    DSCONFIG("dsconfig"),
    DEVICE_TREE("dts"),
    DUST("dust"),
    EBNF("ebnf"),
    ELIXIR("elixir"),
    ELM("elm"),
    ERB("erb"),
    ERLANG("erlang"),
    ERLANG_REPL("erlang_repl"),
    EXCEL("excel"),
    FIX("fix"),
    FLIX("flix"),
    FORTRAN("fortran"),
    F_SHARP("fsharp"),
    GAMS("gams"),
    GAUSS("gauss"),
    GCODE("gcode"),
    GHERKIN("gherkin"),
    GLSL("glsl"),
    GO("go"),
    GOLO("golo"),
    GRADLE("gradle"),
    GROOVY("groovy"),
    HAML("haml"),
    HANDLEBARS("handlebars"),
    HASKELL("haskell"),
    HAXE("haxe"),
    HSP("hsp"),
    HTML("html"),
    HTMLBARS("htmlbars"),
    HTTP("http"),
    HY("hy"),
    INFORM_7("inform7"),
    INI("ini"),
    IRPF90("irpf90"),
    JAVA("java"),
    JAVASCRIPT("javascript"),
    JBOSS_CLI("jboss-cli"),
    JSON("json"),
    JULIA("julia"),
    KOTLIN("kotlin"),
    LASSO("lasso"),
    LDIF("ldif"),
    LEAF("leaf"),
    LESS("less"),
    LISP("lisp"),
    LIVECODESERVER("livecodeserver"),
    LIVESCRIPT("livescript"),
    LLVM("llvm"),
    LSL("lsl"),
    LUA("lua"),
    MAKEFILE("makefile"),
    MARKDOWN("markdown"),
    MATHEMATICA("mathematica"),
    MATLAB("matlab"),
    MAXIMA("maxima"),
    MEL("mel"),
    MERCURY("mercury"),
    MIPS_ASSEMBLY("mipsasm"),
    MIZAR("mizar"),
    MOJOLICIOUS("mojolicious"),
    MONKEY("monkey"),
    MOONSCRIPT("moonscript"),
    N1QL("n1ql"),
    NGINX("nginx"),
    NIMROD("nimrod"),
    NIX("nix"),
    NSIS("nsis"),
    OBJECTIVE_C("objectivec"),
    OCAML("ocaml"),
    OPENSCAD("openscad"),
    OXYGENE("oxygene"),
    PARSER3("parser3"),
    PERL("perl"),
    PF("pf"),
    PHP("php"),
    PONY("pony"),
    POWERSHELL("powershell"),
    PROCESSING("processing"),
    PROFILE("profile"),
    PROLOG("prolog"),
    PROTOCOL_BUFFERS("protobuf"),
    PUPPET("puppet"),
    PURE_BASIC("purebasic"),
    PYTHON("python"),
    Q("q"),
    QML("qml"),
    R("r"),
    RIB("rib"),
    ROBOCONF("roboconf"),
    ROUTEROS("routeros"),
    RSL("rsl"),
    RUBY("ruby"),
    ORACLE_RULES_LANGUAGE("ruleslanguage"),
    RUST("rust"),
    SCALA("scala"),
    SCHEME("scheme"),
    SCILAB("scilab"),
    SCSS("scss"),
    SHELL("shell"),
    SMALI("smali"),
    SMALLTALK("smalltalk"),
    SML("sml"),
    SQF("sqf"),
    SQL("sql"),
    STAN("stan"),
    STATA("stata"),
    STEP21("step21"),
    STYLUS("stylus"),
    SUBUNIT("subunit"),
    SWIFT("swift"),
    TAGGERSCRIPT("taggerscript"),
    TAP("tap"),
    TCL("tcl"),
    TEX("tex"),
    THRIFT("thrift"),
    TP("tp"),
    TWIG("twig"),
    TYPESCRIPT("typescript"),
    VALA("vala"),
    VB_NET("vbnet"),
    VBSCRIPT("vbscript"),
    VBSCRIPT_HTML("vbscript_html"),
    VERILOG("verilog"),
    VHDL("vhdl"),
    VIM("vim"),
    X86_ASSEMBLY("x86asm"),
    XL("xl"),
    XML("xml"),
    XQUERY("xquery"),
    YAML("yaml"),
    ZEPHIR("zephir");

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

