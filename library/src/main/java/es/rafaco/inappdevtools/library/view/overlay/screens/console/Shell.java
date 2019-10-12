package es.rafaco.inappdevtools.library.view.overlay.screens.console;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class Shell {

    public static final String BASH_PATH = "/system/bin/sh";
    public static final String BASH_ARGS = "-c";

    Process process;
    private boolean isCancelled = false;

    public Shell() {
    }

    public String run(String command) {
        return run(new String[] { command });
    }

    public String run(String[] command) {
        String response = "";

        if (IadtController.get().isDebug()){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < command.length; i++) {
                stringBuilder.append(command[i] + " ");
            }
            ThreadUtils.printOverview("Shell");
            Log.v(Iadt.TAG, "Shell running: " + stringBuilder.toString());
        }

        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer output = new StringBuffer();
            String line;
            while (!isCancelled && (line = reader.readLine())!= null) {
                output.append(line + Humanizer.newLine());
            }
            if (IadtController.get().isDebug()){
                Log.v(Iadt.TAG, "Shell result: " + output.length() + " lines.");
                if (output.length() == 0){
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    StringBuffer errorOutput = new StringBuffer();
                    String errorLine;
                    while (!isCancelled && (errorLine = errorReader.readLine())!= null) {
                        errorOutput.append(errorLine + Humanizer.newLine());
                    }
                    if (errorOutput.length() != 0) {
                        Log.w(Iadt.TAG, "Shell error: " + errorOutput.toString());
                    }
                }
            }
            response = output.toString();
        } catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }

        return response;
    }

    public void cancel(){
        isCancelled = true;
    }

    public void destroy(){
        cancel();
        if (process != null) process.destroy();
    }

    public static String[] formatBashCommand(String command){
        return new String[] { BASH_PATH, BASH_ARGS, command};
    }
}
