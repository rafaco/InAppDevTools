package es.rafaco.inappdevtools.library.logic.watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AirplaneModeChangeWatcher extends Watcher {

    private IntentFilter mFilter;
    private InnerListener mListener;
    private InnerReceiver mReceiver;


    public AirplaneModeChangeWatcher(Context context) {
        super(context);
        mFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
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
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_AIRPLANE_MODE_CHANGED)){
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                if(isAirplaneModeOn){
                    if (mListener!=null) mListener.onAirplaneModeOn();
                } else {
                    if (mListener!=null) mListener.onAirplaneModeOff();
                }
            }
        }
    }

    public interface InnerListener {
        void onAirplaneModeOn();
        void onAirplaneModeOff();
    }
}
