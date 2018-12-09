package es.rafaco.inappdevtools.logic.sources;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import androidx.core.content.ContextCompat;
import es.rafaco.inappdevtools.R;
import es.rafaco.inappdevtools.logic.steps.FriendlyLog;
import io.github.kbiakov.codeview.adapters.AbstractCodeAdapter;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import io.github.kbiakov.codeview.highlight.Font;

public class StacktraceAdapter extends AbstractCodeAdapter<String> {

    private List<String> codeLines;

    public StacktraceAdapter(@NotNull Context context, @NotNull String code) {
        super(context, Options.Default.get(context)
                .withCode(code)
                .withShadows()
                .withLanguage("java")
                //.withFormat(Format.Default.getCompact())
                .withFont(Font.Inconsolata)
                .withTheme(ColorTheme.MONOKAI));
        this.codeLines = Arrays.asList(code.split("\n"));
    }

    @NotNull
    @Override
    public AbstractCodeAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        AbstractCodeAdapter.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        disableLineNumbers(holder);
        adjustMargins(holder);
        return holder;
    }

    /*stacktraceView.setOptions(globalCodeOptions
                .withCode(crash.getStacktrace()));
                .shortcut(8, "Show more"));
                .addCodeLineClickListener(new OnCodeLineClickListener() {
                    @Override
                    public void onCodeLineClicked(int i, @NotNull String s) {
                        DevTools.showMessage("Clicked line " + i + ": " + s);
                        if (i == 8) {
                            stacktraceView.getOptions().setShortcut(false);
                        }
                    }
                }));*/

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int pos) {
        super.onBindViewHolder(holder, pos);

        int contextualizedColor;
        if (canOpenSource(pos - 1)){
            contextualizedColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.rally_blue_darker);
            holder.itemView.setBackgroundColor(contextualizedColor);
        }

        /*holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });*/
    }

    @NotNull
    @Override
    public View createFooter(@NotNull Context context, String entity, boolean isFirst) {
        return null;
    }


    /*GestureDetectorCompat mDetector = new GestureDetectorCompat(getContext().getApplicationContext(),
            new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            OverlayUIService.performNavigation(SourceDetailScreen.class,
                    SourceDetailScreen.buildParams("jar", text, -1);
            return true;
        }
    };*/


    private void disableLineNumbers(ViewHolder holder) {
        holder.getTvLineNum().setVisibility(View.GONE);
    }

    private void adjustMargins(ViewHolder holder) {
        RelativeLayout.LayoutParams parameter = (RelativeLayout.LayoutParams) holder.getTvLineContent().getLayoutParams();
        parameter.setMargins(15, 0, 0, 15);
        holder.getTvLineContent().setLayoutParams(parameter);
    }


    private String getCodeLine(int n) {
        if(n>=0 && n<codeLines.size()-1){
            return codeLines.get(n);
        }
        return "";
    }

    public boolean canOpenSource(int n){
        String line = getCodeLine(n);
        return canOpenSource(line);
    }

    public boolean canOpenSource(String line){
        return line.startsWith("\t") && line.contains("es.rafaco.devtool");
    }

    public String extractPath(int n){
        String line = getCodeLine(n);
        String fileName = extractFileName(n);

        int pathFrom = line.indexOf("es.rafaco.devtool");
        int pathTo = line.indexOf(fileName) + fileName.length();

        String path = line.substring(pathFrom, pathTo);
        path = path.replace(".", "/");
        path = path + ".java";

        String leftover = line.substring(pathTo, line.indexOf("("));
        FriendlyLog.log("D", "user", "user", "Leftover from extraction: " + leftover);
        return path;
    }

    public String extractFileName(int n){
        String file = extractFile(n);
        return file.substring(0, file.indexOf("."));
    }

    public String extractFile(int n){
        String line = getCodeLine(n);
        return line.substring(line.lastIndexOf("(") + 1, line.lastIndexOf(":"));
    }

    public int extractLineNumber(int n){
        String line = getCodeLine(n);
        return Integer.parseInt(line.substring(line.lastIndexOf(":") + 1, line.lastIndexOf(")")));
    }
}
