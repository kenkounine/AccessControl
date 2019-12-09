package com.vietnamproject.accesscontrol.was;

import android.content.Context;
import android.text.TextUtils;

import com.vietnamproject.accesscontrol.R;

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
    public void sendCmdState( Context context, JsonAsync.JsonAsyncListener listener ) {

        JsonAsync async = new JsonAsync();

        async.request( getHost( context ) + context.getString( R.string.url_cmd_state ), listener );

    }

    /**
     * 레지아이디저장
     */
    public void sendRegistrationToken( Context context, JsonAsync.JsonAsyncListener listener ) {

        JsonAsync async = new JsonAsync();

        async.request( getHost( context ) + context.getString( R.string.url_registration ), listener );

    }
}