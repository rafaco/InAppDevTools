/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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
 */

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
        long duration = finishTime - startTime;
        String result = "StopWatch for" +
                " " + name + " finished on "
                + Humanizer.getDuration(duration)
                + Humanizer.newLine();

        if (steps != null){
            duration = steps.get(0).second - startTime;
            result += "  0. Started: "
                    + Humanizer.getDuration(duration).toLowerCase()
                    + Humanizer.newLine();
            for (int i=0; i<steps.size(); i++){
                Pair<String, Long> currentStep = steps.get(i);
                long currentFinish = (i+1 < steps.size()) ? steps.get(i+1).second : finishTime;
                duration = currentFinish - currentStep.second;
                result += "  " + (steps.indexOf(currentStep)+1) + ". " + currentStep.first + ": "
                        + Humanizer.getDuration(duration).toLowerCase()
                        + Humanizer.newLine();
            }
            result += "  " + (steps.size()+1) + ". Finished: "
                    + Humanizer.getDuration(steps.get(0).second - startTime).toLowerCase()
                    + Humanizer.newLine();
        }
        return result;
    }
}
