package com.vietnamproject.accesscontrol.util;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.vietnamproject.accesscontrol.AdminReceiver;
import com.vietnamproject.accesscontrol.NotifyActivity;
import com.vietnamproject.accesscontrol.config.Define;
import com.vietnamproject.accesscontrol.service.PolicyService;
import com.vietnamproject.accesscontrol.was.WasManager;

public class DeviceAdminManager {

    private static final DeviceAdminManager INSTANCE = new DeviceAdminManager();

    public static final int RC_DEVICE_ADMIN = 0xFF;

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;


    private DeviceAdminManager() {}

    public static final DeviceAdminManager getInstance() { return INSTANCE; }

    private DevicePolicyManager getDevicePolicyManager( Context context ) {

        if( mDevicePolicyManager == null ) mDevicePolicyManager = ( DevicePolicyManager ) context.getSystemService( Context.DEVICE_POLICY_SERVICE );

        return mDevicePolicyManager;

    }

    private ComponentName getComponentName( Context context ) {

        if( mComponentName == null ) mComponentName = new ComponentName( context, AdminReceiver.class );

        return mComponentName;

    }

    public boolean isAdminActive( Context context ) {

        return getDevicePolicyManager( context ) != null && getDevicePolicyManager( context ).isAdminActive( getComponentName( context ) );

    }

    public void requestAdminActive( Activity activity ) {

        Intent intent = new Intent( DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN );

        intent.putExtra( DevicePolicyManager.EXTRA_DEVICE_ADMIN, getComponentName( activity ) );
        activity.startActivityForResult( intent, RC_DEVICE_ADMIN );

    }

    private void startNotifyActivity( Context context, int cmdCode ) {

        Intent intent = new Intent( context, NotifyActivity.class );

        intent.putExtra( NotifyActivity.EXTRA_CMD_CODE, cmdCode );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
        context.startActivity( intent );

    }

    public void setPolicy( Context context, int cmdCode ) {

        try {

            String userId = SharedPref.getInstance().getString( context, Define.SharedKey.USER_ID );
            int errorCode = Define.Error.NONE;

            if( TextUtils.isEmpty( userId ) ) errorCode = Define.Error.NOT_LOGIN;
            else if( !isAdminActive( context ) ) errorCode = Define.Error.ADMIN_DISABLED;
            else {

                switch( cmdCode ) {

                    case Define.CMD_CODE.CAMERA_LOCK :

                        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 ) context.startForegroundService( new Intent( context, PolicyService.class ).setAction( PolicyService.ACTION_START_SERVICE ) );
                        else context.startService( new Intent( context, PolicyService.class ).setAction( PolicyService.ACTION_START_SERVICE ) );

                        SharedPref.getInstance().putInt( context, Define.SharedKey.LAST_CMD, Define.CMD_CODE.CAMERA_LOCK );
                        getDevicePolicyManager( context ).setCameraDisabled( getComponentName( context ), true );
                        startNotifyActivity( context, cmdCode );

                        break;

                    case Define.CMD_CODE.CAMERA_UNLOCK :

                        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 ) context.startForegroundService( new Intent( context, PolicyService.class ).setAction( PolicyService.ACTION_STOP_SERVICE ) );
                        else context.startService( new Intent( context, PolicyService.class ).setAction( PolicyService.ACTION_STOP_SERVICE ) );

                        SharedPref.getInstance().putInt( context, Define.SharedKey.LAST_CMD, Define.CMD_CODE.CAMERA_UNLOCK );
                        getDevicePolicyManager( context ).setCameraDisabled( getComponentName( context ), false );
                        startNotifyActivity( context, cmdCode );

                        break;

                    case Define.CMD_CODE.ALIVE :

                        int lastCmd = SharedPref.getInstance().getInt( context, Define.SharedKey.LAST_CMD, Define.CMD_CODE.NONE );

                        setPolicy( context, lastCmd );

                        break;

                    case Define.CMD_CODE.STOP :

                        SharedPref.getInstance().putString( context, Define.SharedKey.USER_ID, "" );
                        getDevicePolicyManager( context ).removeActiveAdmin( getComponentName( context ) );

                        break;

                }
            }

            WasManager.getInstance().sendCmdState( context, cmdCode, errorCode, null );

        } catch( Exception e ) {

            WasManager.getInstance().sendCmdState( context, cmdCode, Define.Error.PERMISSION_DENIED, null );

        }
    }
}