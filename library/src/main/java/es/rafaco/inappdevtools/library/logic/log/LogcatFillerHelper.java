package es.rafaco.inappdevtools.library.logic.log;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.view.overlay.screens.commands.ShellExecuter;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogLine;

public class LogcatFillerHelper {

    public void fillDB(final Runnable onFilled){
        new AsyncTask<Void,Void,List<Friendly>>() {

            @Override
            protected List<Friendly> doInBackground(Void... voids) {
                //File jsonFile = new File(getApplication().getFilesDir(), "downloaded.json");
                injectData(getData());
                return new ArrayList<>();
            }

            @Override
            protected void onPostExecute(List<Friendly> data) {
                onFilled.run();
            }
        }.execute();
    }

    public void injectData(final List<Friendly> data){
        Log.d("IadtLiveData", "LogcatFillerHelper getData setting values");
        IadtController.getDatabase().friendlyDao().insertAll(data);
        Log.d("IadtLiveData", "LogcatFillerHelper getData values settle");
    }

    public List<Friendly> getData() {

        Log.d("IadtLiveData", "LogcatFillerHelper getData");

        //File jsonFile = new File(getApplication().getFilesDir(), "downloaded.json");

        final List<Friendly> data = new ArrayList<>();
        String rawLogcat = getLogcatHead();
        Scanner scanner = new Scanner(rawLogcat);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            LogLine logLine = LogLine.newLogLine(line, false);
            data.add(logLine.parseToFriendly());
        }
        scanner.close();

        return data;
    }

    public String getLogcatHead(){
        ShellExecuter exe = new ShellExecuter();
        String command = "logcat -d -t 1000 -v time *:V";
        String output = exe.Executer(command);
        return output;
    }
}
