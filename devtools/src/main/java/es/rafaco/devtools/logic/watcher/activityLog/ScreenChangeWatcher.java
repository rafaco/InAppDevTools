package es.rafaco.devtools.logic.watcher.activityLog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenChangeWatcher {

    private Context mContext;
    private IntentFilter mFilter;
    private OnChangeListener mListener;
    private InnerReceiver mReceiver;

    public boolean isScreenOn = true;

    public ScreenChangeWatcher(Context context) {
        mContext = context;

        mFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mFilter.addAction(Intent.ACTION_USER_PRESENT);
    }

    public void setOnButtonPressedListener(OnChangeListener listener) {
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
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOn = false;
                mListener.onScreenOff();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOn = true;
                mListener.onScreenOn();
            }else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
                mListener.onUserPresent();
            }
        }
    }

    public interface OnChangeListener {
        void onScreenOff();
        void onScreenOn();
        void onUserPresent();
    }
}
