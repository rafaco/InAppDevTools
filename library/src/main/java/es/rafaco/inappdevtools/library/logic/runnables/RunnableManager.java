package es.rafaco.inappdevtools.library.logic.runnables;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class RunnableManager {

    Context context;
    private static Runnable onForceCloseRunnable;
    private List<RunButton> runButtonList;

    public RunnableManager(Context context) {
        this.context = context;
    }

    public void add(RunButton config){
        if (runButtonList == null)
            runButtonList = new ArrayList<>();
        runButtonList.add(config);
    }

    public boolean remove(RunButton target){
        if (runButtonList !=null && runButtonList.contains(target)) {
            return runButtonList.remove(target);
        }
        return false;
    }

    public List<RunButton> getAll(){
        if (runButtonList == null)
            runButtonList = new ArrayList<>();
        return runButtonList;
    }

    public void addForceCloseRunnable(Runnable onForceClose){
        onForceCloseRunnable = onForceClose;
    }

    public Runnable getForceCloseRunnable(){
        return onForceCloseRunnable;
    }
}
