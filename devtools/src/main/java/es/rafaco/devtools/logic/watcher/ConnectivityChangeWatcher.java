package es.rafaco.devtools.logic.watcher;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import es.rafaco.devtools.logic.watcher.Watcher;

public class ConnectivityChangeWatcher extends Watcher {

    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private IntentFilter mFilter;
    private InnerListener mListener;
    private InnerReceiver mReceiver;

    public ConnectivityChangeWatcher(Context context) {
        super(context);

        mFilter = new IntentFilter(ACTION_CONNECTIVITY_CHANGE);
        mFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
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
            if(intent.getAction().equalsIgnoreCase(ACTION_CONNECTIVITY_CHANGE)) {

                NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnectedOrConnecting()) {
                    if (mListener!=null) mListener.onNetworkAvailable(getNetworkTypeString(networkInfo));
                }
                else {
                    if (mListener!=null) mListener.onNetworkLost(getNetworkTypeString(networkInfo));
                }
            }
            else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_AIRPLANE_MODE_CHANGED)){


            }
        }

       public String getNetworkTypeString(NetworkInfo info) {

           if(info==null ) //|| !info.isConnected())
               return "-"; //not connected
           if(info.getType() == ConnectivityManager.TYPE_WIFI)
               return "WIFI";
           if(info.getType() == ConnectivityManager.TYPE_MOBILE){
               int networkType = info.getSubtype();
               switch (networkType) {
                   case TelephonyManager.NETWORK_TYPE_GPRS:
                   case TelephonyManager.NETWORK_TYPE_EDGE:
                   case TelephonyManager.NETWORK_TYPE_CDMA:
                   case TelephonyManager.NETWORK_TYPE_1xRTT:
                   case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                       return "2G";
                   case TelephonyManager.NETWORK_TYPE_UMTS:
                   case TelephonyManager.NETWORK_TYPE_EVDO_0:
                   case TelephonyManager.NETWORK_TYPE_EVDO_A:
                   case TelephonyManager.NETWORK_TYPE_HSDPA:
                   case TelephonyManager.NETWORK_TYPE_HSUPA:
                   case TelephonyManager.NETWORK_TYPE_HSPA:
                   case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                   case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                   case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                   case TelephonyManager.NETWORK_TYPE_TD_SCDMA:  //api<25 : replace by 17
                       return "3G";
                   case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                   case TelephonyManager.NETWORK_TYPE_IWLAN:  //api<25 : replace by 18
                   case 19:  //LTE_CA
                       return "4G";
                   default:
                       return "?";
               }
           }
           return "?";
       }

       /*public NetworkInfo getActiveNetwork(Context context){
           ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo info = cm.getActiveNetworkInfo();
           return info;
       }*/
    }

    public interface InnerListener {
        void onNetworkLost(String networkType);
        void onNetworkAvailable(String networkType);
    }
}
