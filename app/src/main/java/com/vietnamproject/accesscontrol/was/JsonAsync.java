package com.vietnamproject.accesscontrol.was;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class JsonAsync {

    public static final int JSONASYNC_200OK = 200;
    public static final int JSONASYNC_NETWORK_ERROR_CONNECT = -1;
    public static final int JSONASYNC_NETWORK_ERROR_WRITE = -2;
    public static final int JSONASYNC_NETWORK_ERROR_READ = -3;
    public static final int JSONASYNC_NETWORK_ERROR_ETC = -4;

    private JSONObject mJsonObject;
    private JsonConnection mJsonConnection;
    private JsonAsyncListener mJsonAsyncListener;


    public JsonAsync() {

        mJsonObject = new JSONObject();
        mJsonConnection = new JsonConnection();

    }

    public void setParam( JSONObject jsonData ) { mJsonObject = jsonData; }

    public JSONObject addParam(String name, Object value ) {

        try {

            mJsonObject.put( name, value );

        } catch( JSONException e ) { e.printStackTrace(); }

        return mJsonObject;

    }

    public JSONObject addParam(String name, double value ) {

        try {

            mJsonObject.put( name, value );

        } catch( JSONException e ) { e.printStackTrace(); }

        return mJsonObject;

    }

    public JSONObject addParam(String name, long value ) {

        try {

            mJsonObject.put( name, value );

        } catch( JSONException e ) { e.printStackTrace(); }

        return mJsonObject;

    }

    public JSONObject addParam(String name, int value ) {

        try {

            mJsonObject.put( name, value );

        } catch( JSONException e ) { e.printStackTrace(); }

        return mJsonObject;

    }

    public JSONObject addParam(String name, boolean value ) {

        try {

            mJsonObject.put( name, value );

        } catch( JSONException e ) { e.printStackTrace(); }

        return mJsonObject;

    }

    public JSONObject getParam() { return mJsonObject; }

    /**
     * 통신을 요청한다.
     * @param url
     * @param listener
     */
    public void request( String url, JsonAsyncListener listener ) {

        mJsonAsyncListener = listener;

        new RequestTask().execute( url );

    }

    /** 데이터 통신 결과를 알려주는 리스너 */
    public interface JsonAsyncListener {

        /**
         * 응답 결과를 전달 한다.
         * @param json
         * @param respCode
         */
        void onResponse( JSONObject json, int respCode );

    }

    private class RequestTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground( String... args ) {

            String url = args[ 0 ];

            if( !TextUtils.isEmpty( url ) ) {

                mJsonConnection = new JsonConnection();

                // network connect
                try {

                    mJsonConnection.setParam( mJsonObject ); // parameter add
                    mJsonConnection.jsonConnect( url ); // network connect

                } catch( IOException e ) {

                    e.printStackTrace();

                    return JSONASYNC_NETWORK_ERROR_CONNECT;

                }

                // network write
                try {

                    mJsonConnection.jsonOut();

                } catch( IOException e ) {

                    e.printStackTrace();

                    return JSONASYNC_NETWORK_ERROR_WRITE;

                }

                try {

                    int respCode = mJsonConnection.getResponseCode();

                    if( respCode == JSONASYNC_200OK ) {

                        mJsonConnection.jsonIn( new JsonConnection.OnResponseListener() {

                            @Override
                            public void onResponse() {

                                mJsonObject = mJsonConnection.getJsonData();

                                try {

                                    mJsonConnection.jsonDisconnect();

                                } catch( IOException e ) {

                                    e.printStackTrace();

                                }
                            }
                        } );
                    } else mJsonConnection.jsonDisconnect();

                    return respCode;

                } catch( IOException e ) {

                    e.printStackTrace();
                    return JSONASYNC_NETWORK_ERROR_ETC;

                }
            }

            return JSONASYNC_NETWORK_ERROR_READ;

        }

        @Override
        protected void onPostExecute( Integer respCode ) {

            if( respCode == JSONASYNC_200OK ) {

                // DO NOTHING

            } else {

                mJsonObject = null;

                try {

                    mJsonConnection.jsonCancel();

                } catch( IOException e ) { e.printStackTrace(); }
            }

            if( mJsonAsyncListener != null ) mJsonAsyncListener.onResponse( mJsonObject, respCode );

        }
    }
}