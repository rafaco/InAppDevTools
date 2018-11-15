package es.rafaco.devtools.logic.watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import es.rafaco.devtools.logic.watcher.Watcher;

public class ScreenChangeWatcher extends Watcher {

    private IntentFilter mFilter;
    private InnerListener mListener;
    private InnerReceiver mReceiver;

    public boolean isScreenOn = true;

    public ScreenChangeWatcher(Context context) {
        super(context);

        mFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mFilter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void setListener(Object listener) {
        mListener = (InnerListener) listener;
    }

    @Override
    public void start() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    @Override
    public void stop() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

   class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOn = false;
                if (mListener!=null) mListener.onScreenOff();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOn = true;
                if (mListener!=null) mListener.onScreenOn();
            }else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
                if (mListener!=null) mListener.onUserPresent();
            }
        }
    }

    public interface InnerListener {
        void onScreenOff();
        void onScreenOn();
        void onUserPresent();
    }
}
