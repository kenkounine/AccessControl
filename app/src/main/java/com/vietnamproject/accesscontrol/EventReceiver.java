package com.vietnamproject.accesscontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
            }
        }
    }
}