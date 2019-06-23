package es.rafaco.inappdevtools.library.view.overlay.screens.commands;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class ShellExecuter {

    public ShellExecuter() {

    }

    public String Executer(String command) {
        String response = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            //TODO: LOW - was waitFor() needed? prevent crash handler to work
            //int waitResponse = p.waitFor();
            //Log.w(Iadt.TAG, "waitResponse: " + waitResponse);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuffer output = new StringBuffer();
            String line;
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
            Log.v(Iadt.TAG, "ShellExecuter result: " + output.length() + " lines.");
            response = output.toString();
        } catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }

        return response;
    }
}
