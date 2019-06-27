package es.rafaco.inappdevtools.library.view.overlay.screens.console;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

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
        Log.v(Iadt.TAG, "Shell running: " + command);

        try {
            process = Runtime.getRuntime().exec(command);
            //TODO: LOW - was waitFor() needed? prevent crash handler to work
            //int waitResponse = process.waitFor();
            //Log.w(Iadt.TAG, "waitResponse: " + waitResponse);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer output = new StringBuffer();
            String line;
            while (!isCancelled && (line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
            Log.v(Iadt.TAG, "Shell result: " + output.length() + " lines.");
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
