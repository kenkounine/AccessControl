package com.vietnamproject.accesscontrol.service;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vietnamproject.accesscontrol.config.Define;
import com.vietnamproject.accesscontrol.util.DeviceAdminManager;
import com.vietnamproject.accesscontrol.util.Utils;

import org.json.JSONObject;

import java.util.Map;


public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken( @NonNull String s ) {

        // DO NOTHING
    }

    @Override
    public void onMessageReceived( @NonNull RemoteMessage remoteMessage ) {

        // TODO 수신받은 메시지를 처리한다.
        Log.d( Define.TAG, "onMessageReceived" );

        try {

            Map<String, String> data = remoteMessage.getData();
            JSONObject json = new JSONObject( data.get( "data" ) );
            String cmd = json.getString( Define.CMD.COMMAND );

            if( cmd.startsWith( Define.PREFIX ) ) {

                byte[] bytes = Base64.decode( cmd.substring( Define.PREFIX.length() ), Base64.DEFAULT );
                cmd = Utils.decrypt( Utils.getDeviceId( this ) + "accesscontrol", new String( bytes, "UTF-8" ) );

                DeviceAdminManager.getInstance().setPolicy( this, Utils.getCmdCode( cmd ) );

            }
        } catch( Exception e ) {

            e.printStackTrace();

        }
    }
}