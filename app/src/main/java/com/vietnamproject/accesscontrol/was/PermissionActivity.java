package com.vietnamproject.accesscontrol.was;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vietnamproject.accesscontrol.BaseActivity;

import java.util.ArrayList;
import java.util.List;


@TargetApi(23)
public class PermissionActivity extends BaseActivity {

    protected static final int RC_PERMISSION = 100;
    private static final int RC_DRAW_OVERLAYS = 101;
    private static final int RC_WRITE_SETTINGS = 102;

    private int mRequestCode = RC_PERMISSION;


    /**
     * {@link Manifest}에 정의된 요청 가능한 모든 권한을 사용자에게 요청한다.<br/>
     * @see #RC_PERMISSION
     * @see #requestAllPermissions(int)
     */
    protected void requestAllPermissions() {

        requestAllPermissions( RC_PERMISSION );

    }

    /**
     * {@link Manifest}에 정의된 요청 가능한 모든 권한을 사용자에게 요청한다.<br/>
     * @param requestCode request code
     */
    protected void requestAllPermissions( int requestCode ) {

        String[] permissions = getAllPermissions();

        if( permissions != null ) requestPermissions( requestCode, permissions );

    }

    /**
     * 하나의 권한을 사용자에게 요청한다.<br/>
     * @see #RC_PERMISSION
     * @see #requestPermission(int, String)
     * @param permission 요청할 권한
     */
    protected void requestPermission( @NonNull String permission ) {

        requestPermission( RC_PERMISSION, permission );

    }

    /**
     * 하나의 권한을 사용자에게 요청한다.<br/>
     * @see #requestPermissions(int, String[])
     * @param requestCode request code
     * @param permission 요청할 권한
     */
    protected void requestPermission( int requestCode, @NonNull String permission ) {

        requestPermissions( requestCode, new String[]{ permission } );

    }

    /**
     * 여러개의 권한을 사용자에게 요청한다.<br/>
     * @see #RC_PERMISSION
     * @see #requestPermissions(int, String[])
     * @param permissions 요청할 권한들
     */
    protected void requestPermissions( @NonNull String[] permissions ) {

        requestPermissions( RC_PERMISSION, permissions );

    }

    /**
     * 여러개의 권한을 사용자에게 요청한다.<br/>
     * 일반 권한일 경우 {@link #onPermissionsResult(int, boolean, String[])} 를 통해 결과를 전달 한다.<br/>
     * 특별한 권한일 경우 {@link #onSpecialPermissionResult(int, String, boolean)} 를 통해 결과를 전달 한다.<br/>
     * 특별한 권한일 경우 권한 갯수에 따라 {@link #onSpecialPermissionResult(int, String, boolean)} 가 호출된다.
     * @param requestCode request code
     * @param permissions 요청할 권한
     */
    protected void requestPermissions( int requestCode, @NonNull String[] permissions ) {

        mRequestCode = requestCode;

        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 ) { // 버전이 23이상이면 사용자에게 권한을 요청한다.

            List<String> requestPermissions = new ArrayList<>();

            for( String permission : permissions ) {

                if( checkSelfPermission( permission ) != PackageManager.PERMISSION_GRANTED ) {

                    // 일반 권한인 경우 요청 리스트에 추가한다.
                    // 특별한 권한인 경우는 바로 사용자에게 권한 허용을 요청한다.
                    if( !isSpecialPermission( permission ) ) requestPermissions.add( permission );

                }
            }

            int size = requestPermissions.size();

            if( size > 0 ) requestPermissions( requestPermissions.toArray( new String[ size ] ), requestCode );
            else onPermissionsResult( requestCode, false, null );

        } else { // 23보다 낮은 권한이 경우 모두 허용으로 전달 한다.

            for( String permission : permissions ) {

                if( isSpecialPermission( permission ) ) onSpecialPermissionResult( mRequestCode, permission, true );

            }

            onPermissionsResult( requestCode, false, null );

        }
    }

    /**
     * 요청하는 권한이 특별한 권한인지 확인한다.<br/>
     * 특별한 권한인 경우 시스템에 해당 권한을 확인한 후 요청한다.
     * @param permission
     * @return true : 특별한 권한, false : 일반 권한
     */
    private boolean isSpecialPermission( String permission ) {

        if( Manifest.permission.SYSTEM_ALERT_WINDOW.equals( permission ) ) {

            if( !Settings.canDrawOverlays( this ) ) {

                Intent intent = new Intent( Settings.ACTION_MANAGE_OVERLAY_PERMISSION );

                intent.setData( Uri.parse( "package:" + getPackageName() ) );
                startActivityForResult( intent, RC_DRAW_OVERLAYS );

            } else onSpecialPermissionResult( mRequestCode, permission, true );

            return true;

        } else if( Manifest.permission.WRITE_SETTINGS.equals( permission ) ) {

            if( !Settings.System.canWrite( this ) ) {

                Intent intent = new Intent( Settings.ACTION_MANAGE_WRITE_SETTINGS );

                intent.setData( Uri.parse( "package:" + getPackageName() ) );
                startActivityForResult( intent, RC_WRITE_SETTINGS );

            } else onSpecialPermissionResult( mRequestCode, permission, true );

            return true;

        }

        return false;

    }

    /**
     * {@link Manifest} 에 정의된 모든 권한을 반환한다.
     * @return
     */
    protected String[] getAllPermissions() {

        try {

            PackageInfo packageInfo = getPackageManager().getPackageInfo( getPackageName(), PackageManager.GET_PERMISSIONS );

            if( packageInfo != null ) return packageInfo.requestedPermissions;

        } catch( PackageManager.NameNotFoundException e ) {

            e.printStackTrace();

        }

        return null;

    }

    /**
     * 특별한 권한에 대한 요청 결과를 전달 받는다.
     * @Override
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        switch( requestCode ) {

            case RC_DRAW_OVERLAYS:

                onSpecialPermissionResult( mRequestCode, Manifest.permission.SYSTEM_ALERT_WINDOW, Settings.canDrawOverlays( this ) );

                break;

            case RC_WRITE_SETTINGS:

                onSpecialPermissionResult( mRequestCode, Manifest.permission.WRITE_SETTINGS, Settings.System.canWrite( this ) );

                break;

            default:

                super.onActivityResult( requestCode, resultCode, data );

                break;

        }
    }

    /**
     * 일반 권한에 대한 요청 결과를 전달 받는다.
     * @Override
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {

        if( requestCode == mRequestCode ) {

            List<String> deniedPermissions = new ArrayList<>();
            int len = permissions.length;

            for( int i = 0; i < len; i++ ) {

                if( grantResults[ i ] != PackageManager.PERMISSION_GRANTED ) deniedPermissions.add( permissions[ i ] );

            }

            len = deniedPermissions.size();

            onPermissionsResult( requestCode, len > 0, deniedPermissions.toArray( new String[ len ] ) );

        } else super.onRequestPermissionsResult( requestCode, permissions, grantResults );
    }

    /**
     * 일반 권한의 요청 결과를 전달 한다.<br/>
     * 요청 갯수에 상관없이 한번만 전달한다.
     * @param requestCode
     * @param hasDeniedPermissions 사용자가 거부한 권한이 있는지 여부.
     * @param deniedPermissions 사용자가 거부한 권한들.
     */
    protected void onPermissionsResult( int requestCode, boolean hasDeniedPermissions, @Nullable String[] deniedPermissions ) {}

    /**
     * 특별한 권한의 요청 결과를 전달 한다.<br/>
     * 요청 갯수만큼 전달 된다.
     * @param requestCode
     * @param permission 특별한 권한
     * @param isPermissionGranted 사용자 허용 여부.
     */
    protected void onSpecialPermissionResult( int requestCode, String permission, boolean isPermissionGranted ) {}

}