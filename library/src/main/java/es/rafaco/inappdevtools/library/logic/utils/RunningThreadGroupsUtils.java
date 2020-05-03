/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
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
 */

package es.rafaco.inappdevtools.library.logic.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningThreadGroupsUtils {

    private RunningThreadGroupsUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static List<ThreadGroup> getList() {
        List<ThreadGroup> result = new ArrayList<>();
        ThreadGroup rootGroup = getRootGroup();
        result.add(rootGroup);

        if (rootGroup.activeGroupCount()>0){
            ThreadGroup[] childGroups = new ThreadGroup[rootGroup.activeGroupCount()];
            while (rootGroup.enumerate(childGroups, true ) == childGroups.length) {
                childGroups = new ThreadGroup[childGroups.length*2];
            }
            result.addAll(Arrays.asList(childGroups));
            result.removeAll(Collections.singleton(null));
        }

        return result;
    }

    public static int getCount() {
        return getList().size();
    }

    public static String getClassName(ThreadGroup info) {
        return Humanizer.emptyString();
    }

    public static String getTitle(ThreadGroup info) {
        return info.getName();
    }

    public static String getContent(ThreadGroup info) {
        StringBuffer contentBuffer = new StringBuffer();

        contentBuffer.append("Name: " + info.getName());
        contentBuffer.append(Humanizer.newLine());

        ThreadGroup parent = info.getParent();
        boolean isRoot = parent!=null;
        contentBuffer.append("Parent group: " + (!isRoot ? "null (root)" : parent.getName()));
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Child groups count: " + info.activeGroupCount());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Threads count: " + info.activeCount());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("MaxPriority: " + info.getMaxPriority());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("isDaemon: " + info.isDaemon());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("isDestroyed: " + info.isDestroyed());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("toString(): " + info.toString());
        contentBuffer.append(Humanizer.newLine());

        return contentBuffer.toString();
    }

    public static String getString() {
        StringBuilder result = new StringBuilder("\n");
        List<ThreadGroup> items = getList();

        for (ThreadGroup info : items) {
            result.append(getContent(info));
            result.append(Humanizer.newLine());
        }
        return result.toString();
    }

    public static String formatOneLine(ThreadGroup group) {
        int groupCount = group.enumerate(new ThreadGroup[group.activeGroupCount()], false);
        int threadCount = group.enumerate(new Thread[group.activeCount()], false);
        return String.format( "Group %s has %s groups and %s active threads",
                    Humanizer.toCapitalCase(group.getName()), groupCount, threadCount);
    }

    private static ThreadGroup getCurrentGroup() {
        return Thread.currentThread().getThreadGroup();
    }

    public static ThreadGroup getRootGroup(){
        ThreadGroup rootGroup = getCurrentGroup();
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }
        return rootGroup;
    }
}
