/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.logic.events.detectors.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class ConnectivityChangeEventDetector extends EventDetector {

    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private IntentFilter mFilter;
    private InnerReceiver mReceiver;

    public ConnectivityChangeEventDetector(EventManager manager) {
        super(manager);

        mFilter = new IntentFilter(ACTION_CONNECTIVITY_CHANGE);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.CONNECTIVITY_UP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Network", "Connected",
                        "Connected to a " + param + " network");
            }
        });

        eventManager.subscribe(Event.CONNECTIVITY_DOWN, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Network", "Disconnected",
                        "Disconnected from " + param + " network");
            }
        });
    }

    @Override
    public void start() {
        if (mReceiver != null) {
            getContext().registerReceiver(mReceiver, mFilter);
        }
    }

    @Override
    public void stop() {
        if (mReceiver != null) {
            getContext().unregisterReceiver(mReceiver);
        }
    }

   class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnectedOrConnecting()) {
                eventManager.fire(Event.CONNECTIVITY_UP, getNetworkTypeString(networkInfo));
            }
            else {
                eventManager.fire(Event.CONNECTIVITY_DOWN, getNetworkTypeString(networkInfo));
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
    }
}
