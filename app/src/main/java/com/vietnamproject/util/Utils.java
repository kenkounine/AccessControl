package com.vietnamproject.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class Utils {

    /**
     * 단말기의 고유값을 반환한다.
     * @param context
     * @return
     */
    @SuppressWarnings( "MissingPermission" )
    public static final String getDeviceId( Context context ) {

        TelephonyManager tm = ( TelephonyManager ) context.getSystemService( Context.TELEPHONY_SERVICE );

        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.O ) return tm.getDeviceId();
        else return tm.getImei();

    }
}