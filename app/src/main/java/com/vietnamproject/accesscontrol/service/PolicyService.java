package com.vietnamproject.accesscontrol.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vietnamproject.accesscontrol.R;


public class PolicyService extends Service {

    @Override
    public void onCreate() {

        super.onCreate();

        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 ) {

            NotificationManager nm = ( NotificationManager ) getSystemService( Context.NOTIFICATION_SERVICE );
            String appName = getString( R.string.app_name );
            NotificationChannel channel = new NotificationChannel( appName, appName, NotificationManager.IMPORTANCE_HIGH );
            PendingIntent pi = PendingIntent.getActivity( this, 8019, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT );
            Notification notification = new NotificationCompat.Builder( this, appName )
                                        .setSmallIcon( R.mipmap.ic_launcher )
                                        .setContentTitle( appName )
                                        .setContentText( appName )
                                        .setContentIntent( pi )
                                        .build();

            nm.createNotificationChannel( channel );
            startForeground( 1, notification );

        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 ) stopForeground( true );

        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) { return null; }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {

        Log.d( "WHKIM", "onStartCommand( " + intent + ", " + flags + ", " + startId + " )" );

        return super.onStartCommand( intent, flags, startId );

    }
}