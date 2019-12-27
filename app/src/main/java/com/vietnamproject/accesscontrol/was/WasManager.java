package com.vietnamproject.accesscontrol.was;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.vietnamproject.accesscontrol.R;
import com.vietnamproject.accesscontrol.util.Utils;

public class WasManager {

    private static final WasManager INSTANCE = new WasManager();


    private WasManager() {}

    public static final WasManager getInstance() { return INSTANCE; }

    private String mHost;


    private String getHost( Context context ) {

        if( TextUtils.isEmpty( mHost ) ) mHost = context.getString( R.string.url_host );

        return mHost;

    }

    /**
     * 정책확인
     */
    public void requestPolicy( Context context, JsonAsync.JsonAsyncListener listener ) {

        JsonAsync async = new JsonAsync();

        async.request( getHost( context ) + context.getString( R.string.url_policy ), listener );

    }

    /**
     * 최종명령확인
     */
    public void requestLastCmd( Context context, JsonAsync.JsonAsyncListener listener ) {

        JsonAsync async = new JsonAsync();

        async.addParam( Param.DEVICE_ID, Utils.getDeviceId( context ) );
        async.request( getHost( context ) + context.getString( R.string.url_last_cmd ), listener );

    }

    /**
     * 버전확인
     */
    public void requestVersion( Context context, JsonAsync.JsonAsyncListener listener ) {

        JsonAsync async = new JsonAsync();

        async.request( getHost( context ) + context.getString( R.string.url_version ), listener );

    }

    /**
     * 명령상태보고
     */
    public void sendCmdState( Context context, int cmd, int errorCode, JsonAsync.JsonAsyncListener listener ) {

        JsonAsync async = new JsonAsync();

        async.addParam( Param.DEVICE_ID, Utils.getDeviceId( context ) );
        async.addParam( Param.CMD, String.valueOf( cmd ) );
        async.addParam( Param.ERROR, String.valueOf( errorCode ) );
        async.addParam( Param.STATE, "E" );
        async.request( getHost( context ) + context.getString( R.string.url_cmd_state ), listener );

    }

    /**
     * 로그인
     */
    public void sendRegistrationToken( Context context, String userId, String phoneNumber, String token, JsonAsync.JsonAsyncListener listener ) {

        JsonAsync async = new JsonAsync();

        async.addParam( Param.FIREBASE_TOKEN, token );
        async.addParam( Param.ALLIMTALK_TOKEN, "" );
        async.addParam( Param.DEVICE_ID, Utils.getDeviceId( context ) );
        async.addParam( Param.MODEL, Build.MODEL );
        async.addParam( Param.VERSION, Utils.getVersionName( context ) );
        async.addParam( Param.PHONE_NUMBER, phoneNumber );
        async.addParam( Param.USER_ID, userId );
        async.request( getHost( context ) + context.getString( R.string.url_registration ), listener );

    }
}