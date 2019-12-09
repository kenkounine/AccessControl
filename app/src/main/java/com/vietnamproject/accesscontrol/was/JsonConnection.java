package com.vietnamproject.accesscontrol.was;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class JsonConnection {

    public enum METHOD {

        POST( "POST" ),
        GET( "GET" );


        private final String name;

        METHOD( String name ) { this.name = name; }

        public String getName() { return name; }

    }

    private JsonConnectionImpl mConnection;
    protected JSONObject mJsonData;


    public void setParam( JSONObject json ) { mJsonData = json; }

    public int getResponseCode() { return mConnection.getResponseCode(); }

    public JSONObject getJsonData() { return mJsonData; }

    public void jsonConnect( String url ) throws IOException { jsonConnect( url, METHOD.POST ); }

    public void jsonConnect( String url, METHOD method ) throws IOException { jsonConnect( url, method, null ); }

    public void jsonConnect( String url, METHOD method, Map<String, String> properties ) throws IOException {

        if( url.toLowerCase().startsWith( "https" ) ) mConnection = new HttpsConnection();
        else mConnection = new HttpConnection();

        mConnection.connect( url, method, properties );

    }

    public void jsonDisconnect() throws IOException, NullPointerException {

        mConnection.disconnect();

    }

    public void jsonIn( OnResponseListener listener ) throws IOException, NullPointerException {

        mConnection.in( listener );

    }

    public void jsonOut() throws IOException, NullPointerException {

        mConnection.out();

    }

    public void jsonCancel() throws IOException {

        if( mConnection != null ) mConnection.cancel();

    }

    public interface OnResponseListener {

        void onResponse();

    }

    private abstract class JsonConnectionImpl {

        protected String CHARACTER_SET = "UTF-8";
        protected final int TIMEOUT_MILLIS = 1000 * 30;

        protected HttpURLConnection mUrlConnection;
        protected DataOutputStream mDataOutputStream;
        protected OutputStream mOutputStream;
        protected BufferedReader mBufferedReader;

        protected URL mUrl;
        protected METHOD mMethod;
        protected Map<String, String> mProperties;
        protected int mRespCode;


        /*package*/ int getResponseCode() { return mRespCode; }

        /*package*/ void connect(String addr, METHOD method, Map<String, String> properties ) throws IOException {

            mUrl = new URL( addr );
            mMethod = method;

            if( properties == null ) {

                mProperties = new HashMap<>();

                mProperties.put( "Content-Language", CHARACTER_SET );
                mProperties.put( "Content-Type", "application/json" );

            } else mProperties = properties;

            connect();

        }

        protected void disconnect() throws IOException {

            if( mUrlConnection != null ) {

                mUrlConnection.disconnect();
                mUrlConnection = null;

            }

            if( mOutputStream != null ) {

                mOutputStream.close();
                mOutputStream = null;

            }

            if( mBufferedReader != null ) {

                mBufferedReader.close();
                mBufferedReader = null;

            }
        }

        protected void in( OnResponseListener listener ) throws IOException, NullPointerException {

            mBufferedReader = new BufferedReader( new InputStreamReader( mUrlConnection.getInputStream() ) );
            StringBuilder builder = new StringBuilder();
            String line;

            for( ; ; ) {

                line = mBufferedReader.readLine();

                if( line == null ) break;

                builder.append( line + '\n' );

            }

            try {

                mJsonData = new JSONObject( builder.toString().trim() );

            } catch( JSONException e ) {

                e.printStackTrace();

            }

            listener.onResponse();

        }

        protected void out() throws IOException, NullPointerException {

            mDataOutputStream = new DataOutputStream( mUrlConnection.getOutputStream() );

            mDataOutputStream.write( mJsonData.toString().getBytes( CHARACTER_SET ) );
            mDataOutputStream.flush();

            mRespCode = mUrlConnection.getResponseCode();

        }

        protected void cancel() throws IOException {

            if( mDataOutputStream != null ) {

                mDataOutputStream.close();
                mDataOutputStream = null;

            }

            if( mOutputStream != null ) {

                mOutputStream.close();
                mOutputStream = null;

            }

            if( mBufferedReader != null ) {

                mBufferedReader.close();
                mBufferedReader = null;

            }

            if( mUrlConnection != null ) {

                mUrlConnection.disconnect();
                mUrlConnection = null;

            }
        }

        protected abstract void connect() throws IOException;

    }

    private class HttpConnection extends JsonConnectionImpl {

        @Override
        protected void connect() throws IOException {

            mUrlConnection = ( HttpURLConnection ) mUrl.openConnection();

            mUrlConnection.setUseCaches( false );
            mUrlConnection.setDoInput( true );
            mUrlConnection.setDoOutput( mMethod == METHOD.POST );
            mUrlConnection.setConnectTimeout( TIMEOUT_MILLIS );
            mUrlConnection.setReadTimeout( TIMEOUT_MILLIS );
            mUrlConnection.setRequestMethod( mMethod.getName() );

            for( String key : mProperties.keySet() ) mUrlConnection.setRequestProperty( key, mProperties.get( key ) );

        }
    }

    private class HttpsConnection extends JsonConnectionImpl {

        private final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session ) {

                return true;

            }
        };

        private void trustAllHosts() {

            TrustManager[] trustAllCerts = new TrustManager[] {

                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted( X509Certificate[] chain, String authType ) {}

                        @Override
                        public void checkServerTrusted( X509Certificate[] chain, String authType ) {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {

                            return new X509Certificate[ 0 ];

                        }
                    }
            };

            try {

                SSLContext ssl = SSLContext.getInstance( "SSL" );

                ssl.init( null, trustAllCerts, new java.security.SecureRandom() );
                HttpsURLConnection.setDefaultSSLSocketFactory( ssl.getSocketFactory() );

            } catch( Exception e ) {

                e.printStackTrace();

            }
        }

        @Override
        protected void connect() throws IOException {

            trustAllHosts();

            HttpsURLConnection httpsConn = (HttpsURLConnection) mUrl.openConnection();

            httpsConn.setHostnameVerifier( DO_NOT_VERIFY );
            httpsConn.setUseCaches( false );
            httpsConn.setDoInput( true );
            httpsConn.setDoOutput( mMethod == METHOD.POST );
            httpsConn.setConnectTimeout( TIMEOUT_MILLIS );
            httpsConn.setReadTimeout( TIMEOUT_MILLIS );
            httpsConn.setRequestMethod( mMethod.getName() );

            for( String key : mProperties.keySet() ) httpsConn.setRequestProperty( key, mProperties.get( key ) );

            httpsConn.connect();

            mUrlConnection = httpsConn;

        }
    }
}