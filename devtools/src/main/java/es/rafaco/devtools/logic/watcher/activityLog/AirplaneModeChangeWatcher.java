package es.rafaco.devtools.logic.watcher.activityLog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AirplaneModeChangeWatcher {

    private Context mContext;
    private IntentFilter mFilter;
    private OnChangeListener mListener;
    private InnerReceiver mReceiver;


    public AirplaneModeChangeWatcher(Context context) {
        mContext = context;

        mFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    }

    public void setOnChangeListener(OnChangeListener listener) {
        mListener = listener;
        mReceiver = new InnerReceiver();
    }

    public void startWatch() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    public void stopWatch() {
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
                    mListener.onAirplaneModeOn();
                } else {
                    mListener.onAirplaneModeOff();
                }
            }
        }
    }

    public interface OnChangeListener {
        void onAirplaneModeOn();
        void onAirplaneModeOff();
    }
}
