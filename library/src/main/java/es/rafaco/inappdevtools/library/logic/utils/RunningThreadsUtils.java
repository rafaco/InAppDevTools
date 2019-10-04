package es.rafaco.inappdevtools.library.logic.utils;

import java.util.Arrays;
import java.util.List;

import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningThreadsUtils {

    private RunningThreadsUtils() { throw new IllegalStateException("Utility class"); }

    public static int getCount() {
        return getCurrentGroupThreads().size();
    }

    private static List<java.lang.Thread> getList() {
        return getAllThreads();
    }

    public static String getString() {
        StringBuilder result = new StringBuilder(Humanizer.newLine());
        List<Thread> allThreads = getAllThreads();

        ThreadGroup previousGroup = null;
        int previousGroupStart = 0;

        for (Thread info : allThreads) {
            if (info == null){
                continue;
            }
            else{
                String currentName = (info.getThreadGroup()==null) ? "" : info.getThreadGroup().getName();
                if (previousGroup == null
                        || !previousGroup.getName().equals(currentName)) {

                    insertPreviousGroupInfo(result, previousGroup, previousGroupStart);
                    previousGroup = info.getThreadGroup();
                    previousGroupStart = result.length();
                }

                result  .append(formatThreadId(info))
                        .append(" ")
                        .append(formatThreadDescription(info))
                        //.append(info.getTitle())
                        .append(" ")
                        .append(info.getState())
                        .append(Humanizer.newLine());
            }
        }

        if (previousGroup != null){
            insertPreviousGroupInfo(result, previousGroup, previousGroupStart);
        }
        return result.toString();
    }

    private static void insertPreviousGroupInfo(StringBuilder result, ThreadGroup previousGroup, int previousGroupStart) {
        if (previousGroup != null){
            result.insert(previousGroupStart,
                    Humanizer.newLine()
                            + formatGroup(previousGroup)
                            + Humanizer.newLine());
        }
    }

    private static String formatGroup(ThreadGroup group) {
        int groupCount = group.enumerate(new ThreadGroup[group.activeGroupCount()], false);
        int threadCount = group.enumerate(new Thread[group.activeCount()], false);
        return String.format( "Group %s has %s groups and %s active threads",
                    Humanizer.toCapitalCase(group.getName()), groupCount, threadCount);
    }

    private static String formatThreadId(Thread info){
        String id = String.valueOf(info.getId());
        while(id.length()<4){
            id = "  " + id;
        }
        return id;
    }

    private static String formatThreadDescription(Thread info){
        String standard = info.toString();
        return standard.replaceFirst("Thread", "");
    }



    public static List<Thread> getCurrentGroupThreads(){
        ThreadGroup targetGroup = getCurrentGroup();
        return getThreadsFromGroup(targetGroup);
    }

    public static List<Thread> getAllThreads(){
        ThreadGroup targetGroup = getRootGroup();
        return getThreadsFromGroup(targetGroup);
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

    public static List<Thread> getThreadsFromGroup(ThreadGroup group){
        int size = group.activeCount();
        if (group.getParent() != null){
            size++;
        }

        Thread[] threads = new Thread[size];
        while (group.enumerate(threads, true ) == threads.length) {
            threads = new Thread[threads.length * 2];
        }
        return Arrays.asList(threads);
    }
}
