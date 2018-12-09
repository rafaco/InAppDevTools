package es.rafaco.inappdevtools.logic.watcher;

import android.content.Context;

public abstract class Watcher {
    protected Context mContext;

    public Watcher(Context mContext) {
        this.mContext = mContext;
    }

    public abstract void setListener(Object listener);
    public abstract void start();
    public abstract void stop();
}
