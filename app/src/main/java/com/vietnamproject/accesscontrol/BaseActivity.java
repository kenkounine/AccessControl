package com.vietnamproject.accesscontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vietnamproject.accesscontrol.config.Define;
import com.vietnamproject.accesscontrol.util.DeviceAdminManager;
import com.vietnamproject.accesscontrol.util.Progress;


public class BaseActivity extends AppCompatActivity {

    private static Progress mProgress;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE );

        if( !DeviceAdminManager.getInstance().isAdminActive( this ) ) DeviceAdminManager.getInstance().requestAdminActive( this );

    }

    protected void showProgress() {

        if( mProgress == null ) mProgress = Progress.show( this, "", "" );

    }

    protected void dismissProgress() {

        if( mProgress != null ) {

            if( mProgress.isShowing() ) mProgress.dismiss();

            mProgress = null;

        }
    }

    protected String getErrorMessage( int errorCode ) {

        switch( errorCode ) {

            case Define.Error.DB_INSERT : return getString( R.string.error_db_insert );

            case Define.Error.DATA_PARSE : return getString( R.string.error_data_parse );

            case Define.Error.INVALID_PARAM : return getString( R.string.error_invalid_param );

            case Define.Error.EMPTY_PARAM : return getString( R.string.error_empty_param );

            case Define.Error.SYSTEM_ERROR : return getString( R.string.error_system );

            case Define.Error.NOT_SUPPORTED : return getString( R.string.error_not_supported );

            case Define.Error.EMPTY_DEVICE_INFO : return getString( R.string.error_empty_device_info );

            case Define.Error.EMPTY_USER_INFO : return getString( R.string.error_empty_user_info );

            default : return getString( R.string.error_etc, errorCode );

        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        if( requestCode == DeviceAdminManager.RC_DEVICE_ADMIN ) {

            if( !DeviceAdminManager.getInstance().isAdminActive( this ) ) {

                new AlertDialog.Builder( this )
                        .setCancelable( false )
                        .setTitle( R.string.notification )
                        .setMessage( R.string.error_device_admin )
                        .setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick( DialogInterface dialog, int which ) {

                                dialog.dismiss();
                                DeviceAdminManager.getInstance().requestAdminActive( BaseActivity.this );

                            }
                        } )
                        .setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick( DialogInterface dialog, int which ) {

                                dialog.dismiss();
                                finish();

                            }
                        } )
                        .show();

            }
        } else super.onActivityResult( requestCode, resultCode, data );
    }
}