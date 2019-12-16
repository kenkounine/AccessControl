package com.vietnamproject.accesscontrol.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;

import com.vietnamproject.accesscontrol.config.Define;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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

    /**
     * 단말기의 휴대폰 번호를 반환 한다.
     * @param context
     * @return
     */
    public static String getPhoneNumber( Context context ) {

        try {

            TelephonyManager telephonyervice = ( TelephonyManager ) context.getSystemService( Context.TELEPHONY_SERVICE );
            String phoneNumber = telephonyervice.getLine1Number();

            switch( telephonyervice.getSimState() ) {

                case TelephonyManager.SIM_STATE_UNKNOWN : break;
                case TelephonyManager.SIM_STATE_ABSENT : break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED : break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED : break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED : break;
                case TelephonyManager.SIM_STATE_READY : break;
                default : break;

            }

            if( phoneNumber == null || phoneNumber.length() <= 0 ) phoneNumber = null;

            // +82가 붙는 전화번호일 경우 +82를 제거하고 앞에 0을 붙인다.
            else if( phoneNumber.indexOf( "+82" ) != -1 ) {

                int pnSize = phoneNumber.length();
                phoneNumber = "0" + phoneNumber.substring( 3, pnSize );

            } else if( !phoneNumber.startsWith( "0" ) ) phoneNumber = "0" + phoneNumber;

            return phoneNumber;

        } catch( SecurityException e ) {

            return null;

        } catch( Exception e ) {

            return null;

        }
    }

    /**
     * 앱의 버전 정보를 반환한다.
     * @param context
     * @return
     */
    public static String getVersionName( Context context ) {

        try {

            PackageInfo info = context.getPackageManager().getPackageInfo( context.getPackageName(), 0 );

            return info.versionName;

        } catch( PackageManager.NameNotFoundException e ) {

            return null;

        }
    }

    public static String decrypt( String key, String message ) {

        if( !TextUtils.isEmpty( message ) ) {

            byte[] tmpIV = { 0x43, ( byte ) 0x6d, 0x22, ( byte ) 0x9a, 0x22,
                    ( byte ) 0xf8, ( byte ) 0xcf, ( byte ) 0xfe, 0x15, 0x21,
                    ( byte ) 0x0b, 0x38, 0x01, ( byte ) 0xa7, ( byte ) 0xfc, 0x0e };
            byte[] keyBytes = key.getBytes();
            byte[] getKey = new byte[ 16 ];

            try{

                MessageDigest md=MessageDigest.getInstance( "SHA1" );

                md.update( keyBytes );

                byte[] hash = md.digest();

                md.update( hash );

                hash = md.digest();

                System.arraycopy( hash, 0, getKey, 0, 16 );

            } catch( Exception e ) {

                e.printStackTrace();

            }

            byte[] bytDecoded = Base64.decode( message, Base64.DEFAULT );
            byte[] byGetDec = new byte[ 128 ];

            System.arraycopy( bytDecoded, 0, byGetDec, 0, 128 );

            //AES128 CTR 암호화 방식 Decrypt
            byte[] decrypted = AESdecrypt(byGetDec, getKey, tmpIV);
            int nGetDataLen = ( int ) decrypted[ 4 ];
            byte[] fullDecrypt = new byte[ nGetDataLen - 8 ];

            System.arraycopy(decrypted, 8, fullDecrypt, 0,nGetDataLen - 8 );

            String strDecMsg = new String( fullDecrypt );

            // 복호화가 정상적으로 처리되었다면 앞 4byte 에는 ssom 이라는 문자열이 들어간다.
            if( strDecMsg.substring( 0, 4 ).equals( "ssom" ) ) return strDecMsg.substring( 4 );

        }

        return null;

    }

    private static byte[] AESdecrypt( byte[] cipherText, byte[] key, byte [] iv ) {

        try{

            Cipher c = Cipher.getInstance( "AES/CBC/NoPadding" );
            SecretKeySpec sks = new SecretKeySpec( key, "AES" );
            IvParameterSpec ips = new IvParameterSpec( iv );

            c.init( Cipher.DECRYPT_MODE, sks, ips );

            //Re-use of "cipherText"
            cipherText = c.doFinal( cipherText );

        } catch( Exception e ) {

            e.printStackTrace();

        }

        return cipherText;

    }

    public static int getCmdCode( String cmd ) {

        if( Define.CMD.CAMERA_LOCK.equals( cmd ) ) return Define.CMD_CODE.CAMERA_LOCK;
        else if( Define.CMD.CAMERA_UNLOCK.equals( cmd ) ) return Define.CMD_CODE.CAMERA_UNLOCK;
        else if( Define.CMD.WIFI_LOCK.equals( cmd ) ) return Define.CMD_CODE.WIFI_LOCK;
        else if( Define.CMD.WIFI_UNLOCK.equals( cmd ) ) return Define.CMD_CODE.WIFI_UNLOCK;
        else if( Define.CMD.BLUETOOTH_LOCK.equals( cmd ) ) return Define.CMD_CODE.BLUETOOTH_LOCK;
        else if( Define.CMD.BLUETOOTH_UNLOCK.equals( cmd ) ) return Define.CMD_CODE.BLUETOOTH_UNLOCK;
        else if( Define.CMD.ALIVE.equals( cmd ) ) return Define.CMD_CODE.ALIVE;
        else if( Define.CMD.STOP.equals( cmd ) ) return Define.CMD_CODE.STOP;
        else return 0;

    }
}