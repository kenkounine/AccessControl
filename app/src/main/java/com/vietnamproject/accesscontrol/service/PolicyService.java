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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vietnamproject.accesscontrol.R;
import com.vietnamproject.accesscontrol.util.NetworkPolicy;


public class PolicyService extends Service {

    public static final String ACTION_START_SERVICE = "START_SERVICE";
    public static final String ACTION_STOP_SERVICE = "STOP_SERVICE";

    private static ServiceThread mServiceThread;
    private NetworkPolicy mNetworkPolicy;


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

        if( mServiceThread != null ) {

            mServiceThread.interrupt();

            mServiceThread = null;

        }

        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 ) stopForeground( true );

        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) { return null; }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {

        if( intent != null ) {

            if( mNetworkPolicy == null ) mNetworkPolicy = new NetworkPolicy( this );

            String action = intent.getAction();

            if( ACTION_START_SERVICE.equals( action ) ) {

                mNetworkPolicy.checkNetworkPolicy( this, false );

                if( mServiceThread == null ) {

                    mServiceThread = new ServiceThread();

                    mServiceThread.start();

                }
            } else if( ACTION_STOP_SERVICE.equals( action ) ) {

                if( mServiceThread != null ) {

                    mServiceThread.interrupt();

                    mServiceThread = null;

                }

                stopSelf();

            }
        }

        return START_STICKY;

    }

    private class ServiceThread extends Thread {

        private boolean isInterrupted;


        public ServiceThread() {

            super();

        }

        @Override
        public void interrupt() {

            isInterrupted = true;

            super.interrupt();

        }

        @Override
        public void run() {

            while( !isInterrupted ) {

                try {

                    mNetworkPolicy.checkNetworkPolicy( PolicyService.this, true );
                    sleep( 2000 );

                } catch( InterruptedException e ) {

                    e.printStackTrace();

                }
            }
        }
    }
}