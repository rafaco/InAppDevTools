package es.rafaco.devtools.logic.activityLog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class DeviceButtonsWatcher {

    static final String TAG = "hg";
    private Context mContext;
    private IntentFilter mFilter;
    private OnButtonPressedListener mListener;
    private InnerReceiver mReceiver;

    public DeviceButtonsWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public void setOnButtonPressedListener(OnButtonPressedListener listener) {
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
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_DREAM_KEY = "dream";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    Log.e(TAG, "action:" + action + ",reason:" + reason);
                    if (mListener != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            mListener.onHomePressed();
                        }
                        else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            mListener.onRecentPressed();
                        }
                        else if (reason.equals(SYSTEM_DIALOG_REASON_DREAM_KEY)) {
                            mListener.onDreamPressed();
                        }
                        else{
                            mListener.onUnknownPressed(reason);
                        }
                    }
                }
            }
        }
    }

    public interface OnButtonPressedListener {
        void onHomePressed();
        void onRecentPressed();
        void onDreamPressed();
        void onUnknownPressed(String info);
    }
}
