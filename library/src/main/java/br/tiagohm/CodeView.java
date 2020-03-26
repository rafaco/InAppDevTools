/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * This is a modified source from project CodeView, which is available under
 * MIT License at https://github.com/tiagohm/CodeView
 *
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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
 * Copyright (c) 2016-2017 Tiago Melo
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

/*
 * Changelog:
 *     - Added previous attribution notice and this changelog
 *     - Comments clean up and translation to English
 *     - Using VERSION.SDK_INT from OS_INFO instead of build
 *     - Added scrollToLine feature
 *     - Added findAllAsync compatible with api 15
 */

package br.tiagohm;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.rafaco.inappdevtools.library.R;

public class CodeView extends WebView {

    public interface OnHighlightListener {
        void onStartCodeHighlight();

        void onFinishCodeHighlight();

        void onLanguageDetected(Language language, int relevance);

        void onFontSizeChanged(int sizeInPx);

        void onLineClicked(int lineNumber, String content);
    }

    private String code = "";
    private String escapeCode;
    private Theme theme;
    private Language language;
    private float fontSize = 16;
    private boolean wrapLine = false;
    private OnHighlightListener onHighlightListener;
    private ScaleGestureDetector pinchDetector;
    private boolean zoomEnabled = false;
    private boolean showLineNumber = false;
    private int startLineNumber = 1;
    private int lineCount = 0;
    private int highlightLineNumber = -1;

    public CodeView(Context context) {
        this(context, null);
    }

    public CodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isZoomEnabled()) {
            pinchDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CodeView, 0, 0);
        setWrapLine(attributes.getBoolean(R.styleable.CodeView_cv_wrap_line, false));
        setFontSize(attributes.getInt(R.styleable.CodeView_cv_font_size, 14));
        setZoomEnabled(attributes.getBoolean(R.styleable.CodeView_cv_zoom_enable, false));
        setShowLineNumber(attributes.getBoolean(R.styleable.CodeView_cv_show_line_number, false));
        setStartLineNumber(attributes.getInt(R.styleable.CodeView_cv_start_line_number, 1));
        highlightLineNumber = attributes.getInt(R.styleable.CodeView_cv_highlight_line_number, -1);
        attributes.recycle();

        pinchDetector = new ScaleGestureDetector(context, new PinchListener());

        setWebChromeClient(new WebChromeClient());
        getSettings().setJavaScriptEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        getSettings().setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    /**
     * Define a listener for main events
     */
    public CodeView setOnHighlightListener(OnHighlightListener listener) {
        if (listener != null) {
            if (onHighlightListener != listener) {
                onHighlightListener = listener;

                addJavascriptInterface(new Object() {
                    @JavascriptInterface
                    public void onStartCodeHighlight() {
                        if (onHighlightListener != null) {
                            onHighlightListener.onStartCodeHighlight();
                        }
                    }

                    @JavascriptInterface
                    public void onFinishCodeHighlight() {
                        if (onHighlightListener != null) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    fillLineNumbers();
                                    showHideLineNumber(isShowLineNumber());
                                    highlightLineNumber(getHighlightLineNumber());
                                }
                            });
                            onHighlightListener.onFinishCodeHighlight();
                        }
                    }

                    @JavascriptInterface
                    public void onLanguageDetected(String name, int relevance) {
                        if (onHighlightListener != null) {
                            onHighlightListener.onLanguageDetected(Language.getLanguageByName(name), relevance);
                        }
                    }

                    @JavascriptInterface
                    public void onLineClicked(int lineNumber, String content) {
                        if (onHighlightListener != null) {
                            onHighlightListener.onLineClicked(lineNumber, content);
                        }
                    }
                }, "android");
            }
        }
        else {
            removeJavascriptInterface("android");
        }
        return this;
    }

    /**
     * Retrieve font size in pixels
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * Define font size in pixels
     */
    public CodeView setFontSize(float fontSize) {
        if (fontSize < 8) fontSize = 8;
        this.fontSize = fontSize;
        if (onHighlightListener != null) {
            onHighlightListener.onFontSizeChanged((int) fontSize);
        }
        return this;
    }

    /**
     * Retrieve the code to show
     */
    public String getCode() {
        return code;
    }

    /**
     * Define the code to show
     */
    public CodeView setCode(String code) {
        if (code == null) code = "";
        this.code = code;
        this.escapeCode = TextUtils.htmlEncode(code);
        return this;
    }

    /**
     * Retrieve the theme
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Define the theme
     */
    public CodeView setTheme(Theme theme) {
        this.theme = theme;
        return this;
    }

    /**
     * Retrieve the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Define the language
     */
    public CodeView setLanguage(Language language) {
        this.language = language;
        return this;
    }

    /**
     * Check if wrap lines defined
     */
    public boolean isWrapLine() {
        return wrapLine;
    }

    /**
     * Define wrap lines
     */
    public CodeView setWrapLine(boolean wrapLine) {
        this.wrapLine = wrapLine;
        return this;
    }

    /**
     * Check if zoom is enabled
     */
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    /**
     * Define zoom enabled
     */
    public CodeView setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
        return this;
    }

    /**
     * Check if show line number enabled
     */
    public boolean isShowLineNumber() {
        return showLineNumber;
    }

    /**
     * Define show line number enabled
     */
    public CodeView setShowLineNumber(boolean showLineNumber) {
        this.showLineNumber = showLineNumber;
        return this;
    }

    /**
     * Retrieve first line offset
     */
    public int getStartLineNumber() {
        return startLineNumber;
    }

    /**
     * Define first line offset
     */
    public CodeView setStartLineNumber(int startLineNumber) {
        if (startLineNumber < 0) startLineNumber = 1;
        this.startLineNumber = startLineNumber;
        return this;
    }

    /**
     * Retrieve line count
     */
    public int getLineCount() {
        return lineCount;
    }

    /**
     * Swap current showLineNumber
     */
    public void toggleLineNumber() {
        showLineNumber = !showLineNumber;
        showHideLineNumber(showLineNumber);
    }

    /**
     * Apply attrs and show code
     */
    public void apply() {
        loadDataWithBaseURL("",
                toHtml(),
                "text/html",
                "UTF-8",
                "");
    }

    private String toHtml() {
        StringBuilder sb = new StringBuilder();
        //html
        sb.append("<!DOCTYPE html>\n")
                .append("<html>\n")
                .append("<head>\n");
        //style
        sb.append("<link rel='stylesheet' href='").append(getTheme().getPath()).append("' />\n");
        sb.append("<style>\n");
        //body
        sb.append("body {");
        sb.append("font-size:").append(String.format("%dpx;", (int) getFontSize()));
        sb.append("margin: 0px; line-height: 1.2;");
        sb.append("}\n");
        //.hljs
        sb.append(".hljs {");
        sb.append("}\n");
        //pre
        sb.append("pre {");
        sb.append("margin: 0px; position: relative;");
        sb.append("}\n");
        //line
        if (isWrapLine()) {
            sb.append("td.line {");
            sb.append("word-wrap: break-word; white-space: pre-wrap; word-break: break-all;");
            sb.append("}\n");
        }
        //others
        sb.append("table, td, tr {");
        sb.append("margin: 0px; padding: 0px;");
        sb.append("}\n");
        sb.append("code > span { display: none; }");
        sb.append("td.ln { text-align: right; padding-right: 2px; }");
        sb.append("td.line:hover span {background: #661d76; color: #fff;}");
        sb.append("td.line:hover {background: #661d76; color: #fff; border-radius: 2px;}");
        sb.append("td.destacado {background: #ffda11; color: #000; border-radius: 2px;}");
        sb.append("td.destacado span {background: #ffda11; color: #000;}");
        sb.append("</style>");
        //scripts
        sb.append("<script src='file:///android_asset/highlightjs/highlight.js'></script>");
        sb.append("<script>hljs.initHighlightingOnLoad();</script>");
        sb.append(getHtmlScriptToScroll());
        sb.append("</head>");
        //code
        sb.append("<body>");
        sb.append("<pre><code class='").append(language.getLanguageName()).append("'>")
                .append(insertLineNumber(escapeCode))
                .append("</code></pre>\n");
        return sb.toString();
    }

    private void executeJavaScript(String js) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:" + js, null);
        } else {
            loadUrl("javascript:" + js);
        }
    }

    private void changeFontSize(int sizeInPx) {
        executeJavaScript("document.body.style.fontSize = '" + sizeInPx + "px'");
    }

    private void fillLineNumbers() {
        executeJavaScript("var i; var x = document.querySelectorAll('td.ln'); for(i = 0; i < x.length; i++) {x[i].innerHTML = x[i].getAttribute('line');}");
    }

    private void showHideLineNumber(boolean show) {
        executeJavaScript(String.format(Locale.ENGLISH,
                "var i; var x = document.querySelectorAll('td.ln'); for(i = 0; i < x.length; i++) {x[i].style.display = %s;}",
                show ? "''" : "'none'"));
    }

    public int getHighlightLineNumber() {
        return highlightLineNumber;
    }

    public void highlightLineNumber(int lineNumber) {
        this.highlightLineNumber = lineNumber;
        executeJavaScript(String.format(Locale.ENGLISH,
                "var x = document.querySelectorAll('.destacado'); if(x && x.length == 1) x[0].classList.remove('destacado');"));
        if (lineNumber >= 0) {
            executeJavaScript(String.format(Locale.ENGLISH,
                    "var x = document.querySelectorAll(\"td.line[line='%d']\"); if(x && x.length == 1) x[0].classList.add('destacado');", lineNumber));
        }
    }

    private String insertLineNumber(String code) {
        Matcher m = Pattern.compile("(.*?)&#10;").matcher(code);
        StringBuffer sb = new StringBuffer();
        int pos = getStartLineNumber();
        lineCount = 0;
        while (m.find()) {
            m.appendReplacement(sb,
                    String.format(Locale.ENGLISH,
                            "<tr><td line='%d' class='hljs-number ln'></td><td line='%d' onclick='android.onLineClicked(%d, this.textContent);' class='line'>$1 </td></tr>&#10;",
                            pos, pos, pos));
            pos++;
            lineCount++;
        }

        return "<table>\n" + sb.toString().trim() + "</table>\n";
    }

    /**
     * Pinch events
     */
    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float fontSize;
        private int oldFontSize;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            fontSize = getFontSize();
            oldFontSize = (int) fontSize;
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            CodeView.this.fontSize = fontSize;
            super.onScaleEnd(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            fontSize = getFontSize() * detector.getScaleFactor();
            if (fontSize >= 8) {
                changeFontSize((int) fontSize);
                if (onHighlightListener != null && oldFontSize != (int) fontSize) {
                    onHighlightListener.onFontSizeChanged((int) fontSize);
                }
                oldFontSize = (int) fontSize;
            } else {
                fontSize = 8;
            }
            return false;
        }
    }


    //region [ SCROLL TO LINE ]

    private String getHtmlScriptToScroll() {
        return "<script>function scrollToLine(lineNumber){ " +
                "var x = document.querySelectorAll(\"td.line[line='\"+lineNumber+\"']\");" +
                "if(x && x.length > 0) x[0].parentElement.scrollIntoView();};</script>";
    }

    public void scrollToLine(int lineNumber){
        if (lineNumber >= 0) {
            executeJavaScript("scrollToLine('"+lineNumber+"');");
        }
    }

    //endregion

    @Override
    public void findAllAsync(String find) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            super.findAllAsync(find);
        } else {
            findAll(find);
            try{
                //Can't use getMethod() as it's a private method
                for(Method m : WebView.class.getDeclaredMethods()){
                    if(m.getName().equals("setFindIsUp")){
                        m.setAccessible(true);
                        m.invoke(this, true);
                        break;
                    }
                }
            }catch(Exception ignored){}
        }
        throw new RuntimeException("Stub!");
    }
}
