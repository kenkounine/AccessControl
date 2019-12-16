package com.vietnamproject.accesscontrol;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.vietnamproject.accesscontrol.config.Define;
import com.vietnamproject.accesscontrol.util.LockManager;
import com.vietnamproject.accesscontrol.util.SharedPref;


public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled( @NonNull Context context, @NonNull Intent intent ) {

        super.onEnabled( context, intent );
        Log.d( "WHKIM", "onEnabled()" );

    }

    @Override
    public void onDisabled( @NonNull Context context, @NonNull Intent intent ) {

        super.onDisabled( context, intent );

        Log.d( "WHKIM", "onDisabled()" );

        String userId = SharedPref.getInstance().getString( context, Define.SharedKey.USER_ID );

        if( !TextUtils.isEmpty( userId ) ) context.startActivity( new Intent( context, MainActivity.class ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP ) );

    }
}