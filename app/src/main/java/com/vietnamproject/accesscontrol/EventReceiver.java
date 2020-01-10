package com.vietnamproject.accesscontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.vietnamproject.accesscontrol.was.JsonAsync;
import com.vietnamproject.accesscontrol.was.WasManager;

import org.json.JSONObject;


public class EventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {

        if( intent != null ) {

            String action = intent.getAction();

            if( Intent.ACTION_BOOT_COMPLETED.equals( action ) ) {

                WasManager.getInstance().requestLastCmd( context, new JsonAsync.JsonAsyncListener() {

                    @Override
                    public void onResponse( JSONObject json, int respCode ) {

                        if( respCode == JsonAsync.JSONASYNC_200OK ) {



                        }
                    }
                } );
            } else if( "android.net.conn.TETHER_STATE_CHANGED".equals( action ) ) {

                Log.d( "WHKIM", "AAAAAAAAAAAAAAAAAAAAAAAAAA" );

            }
        }
    }
}