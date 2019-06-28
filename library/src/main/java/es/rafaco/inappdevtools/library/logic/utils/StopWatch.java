package es.rafaco.inappdevtools.library.logic.utils;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class StopWatch {
    String name;
    long startTime;
    List<Pair<String, Long>> steps;

    public StopWatch(String name) {
        this.name = name;
        this.startTime = DateUtils.getLong();
    }

    public void step(String name){
        if (steps == null){
            steps = new ArrayList<>();
        }
        steps.add(new Pair<>(name, DateUtils.getLong()));
    }

    public String finish(){
        long finishTime = DateUtils.getLong();
        String result = "StopWatch for" +
                " " + name + " finished on "
                + Humanizer.getElapsedTime(startTime, finishTime)
                + Humanizer.newLine();

        if (steps != null){
            result += "  0. Started: "
                    + Humanizer.getElapsedTime(startTime, steps.get(0).second).toLowerCase()
                    + Humanizer.newLine();
            for (int i=0; i<steps.size(); i++){
                Pair<String, Long> currentStep = steps.get(i);
                long currentFinish = (i+1 < steps.size()) ? steps.get(i+1).second : finishTime;
                result += "  " + (steps.indexOf(currentStep)+1) + ". " + currentStep.first + ": "
                        + Humanizer.getElapsedTime(currentStep.second, currentFinish).toLowerCase()
                        + Humanizer.newLine();
            }
            result += "  " + (steps.size()+1) + ". Finished: "
                    + Humanizer.getElapsedTime(startTime, steps.get(0).second).toLowerCase()
                    + Humanizer.newLine();
        }
        return result;
    }
}
