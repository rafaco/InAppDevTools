/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.inappdevtools.library.view.utils.Humanizer;

public class RunningThreadGroupsUtils {

    private RunningThreadGroupsUtils() { throw new IllegalStateException("Utility class"); }

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
        StringBuilder contentBuilder = new StringBuilder();

        contentBuilder.append("Name: " + info.getName());
        contentBuilder.append(Humanizer.newLine());

        ThreadGroup parent = info.getParent();
        boolean isRoot = parent!=null;
        contentBuilder.append("Parent group: " + (!isRoot ? "null (root)" : parent.getName()));
        contentBuilder.append(Humanizer.newLine());

        contentBuilder.append("Child groups count: " + info.activeGroupCount());
        contentBuilder.append(Humanizer.newLine());

        contentBuilder.append("Threads count: " + info.activeCount());
        contentBuilder.append(Humanizer.newLine());

        contentBuilder.append("MaxPriority: " + info.getMaxPriority());
        contentBuilder.append(Humanizer.newLine());

        contentBuilder.append("isDaemon: " + info.isDaemon());
        contentBuilder.append(Humanizer.newLine());

        contentBuilder.append("isDestroyed: " + info.isDestroyed());
        contentBuilder.append(Humanizer.newLine());

        contentBuilder.append("toString(): " + info.toString());
        contentBuilder.append(Humanizer.newLine());

        return contentBuilder.toString();
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
