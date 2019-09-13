package es.rafaco.inappdevtools.library.logic.runnables;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class RunnableManager {

    Context context;
    private static Runnable onForceCloseRunnable;
    private List<RunnableItem> customRunnables;


    public RunnableManager(Context context) {
        this.context = context;
    }

    public void add(RunnableItem config){
        if (customRunnables == null)
            customRunnables = new ArrayList<>();
        customRunnables.add(config);
    }

    public boolean remove(RunnableItem target){
        if (customRunnables !=null && customRunnables.contains(target)) {
            return customRunnables.remove(target);
        }
        return false;
    }

    public List<RunnableItem> getAll(){
        if (customRunnables == null)
            customRunnables = new ArrayList<>();
        return customRunnables;
    }

    public void addForceCloseRunnable(Runnable onForceClose){
        onForceCloseRunnable = onForceClose;
    }

    public Runnable getForceCloseRunnable(){
        return onForceCloseRunnable;
    }
}
