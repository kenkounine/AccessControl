package com.vietnamproject.accesscontrol.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.vietnamproject.accesscontrol.config.Define;
import com.vietnamproject.accesscontrol.was.WasManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class NetworkPolicy {

    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;


    public NetworkPolicy( Context context ) {

        mConnectivityManager = ( ConnectivityManager ) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        mWifiManager = ( WifiManager ) context.getApplicationContext().getSystemService( Context.WIFI_SERVICE );

    }

    public void checkNetworkPolicy( Context context, boolean isReport ) {

        Log.d( "WHKIM", "checkNetworkPolicy( " + isReport + " )" );

        try {

            Method method = mWifiManager.getClass().getDeclaredMethod( "isWifiApEnabled" );
            boolean isApEnabled = Boolean.valueOf( method.invoke( mWifiManager ).toString() );

            if( isApEnabled ) {

                method = mConnectivityManager.getClass().getDeclaredMethod( "stopTethering", new Class[]{ Integer.TYPE } );

                method.invoke( mConnectivityManager, new Object[] { Integer.valueOf( 0 ) } );

                if( isReport ) WasManager.getInstance().sendCmdState( context, Define.CMD_CODE.HOTSPOT_OFF, 0, null );

            }
        } catch( NoSuchMethodException e ) {

            e.printStackTrace();

        } catch( InvocationTargetException e ) {

            e.printStackTrace();

        } catch( IllegalAccessException e ) {

            e.printStackTrace();

        } catch( NullPointerException e ) {

            e.printStackTrace();

        }
    }
}