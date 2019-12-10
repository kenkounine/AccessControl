package com.vietnamproject.accesscontrol;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.vietnamproject.accesscontrol.config.Define;
import com.vietnamproject.accesscontrol.util.SharedPref;
import com.vietnamproject.accesscontrol.was.JsonAsync;
import com.vietnamproject.accesscontrol.was.Param;
import com.vietnamproject.accesscontrol.was.PermissionActivity;
import com.vietnamproject.accesscontrol.was.WasManager;

import org.json.JSONObject;


public class IntroActivity extends PermissionActivity implements View.OnClickListener {

    private final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 0xFF;

    private EditText mEdit;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_intro );
        requestAllPermissions();

    }

    /**
     * 저장된 사용자 아이디를 가져온다.<br/>
     * 저장된 아이디가 없을 경우 아이디 입력창을 보여준다.
     */
    private void checkUserId() {

        String userId = SharedPref.getInstance().getString( this, Define.SharedKey.USER_ID, null );

        if( TextUtils.isEmpty( userId ) ) {

            mEdit = findViewById( R.id.et );
            View parent = findViewById( R.id.layout );
            View btn = findViewById( R.id.btn );

            btn.setOnClickListener( this );

            AlphaAnimation anim = new AlphaAnimation( 0F, 1F );

            anim.setDuration( 1000 );
            anim.setInterpolator( new AccelerateInterpolator() );
            parent.setVisibility( View.VISIBLE );
            parent.startAnimation( anim );


        } else tryLogin( userId );
    }

    /**
     * 서버에 로그인을 시도한다.
     * @param userId
     */
    private void tryLogin( final String userId ) {

        showProgress();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener( new OnCompleteListener<InstanceIdResult>() {

                    @Override
                    public void onComplete( @NonNull Task<InstanceIdResult> task ) {

                        if( !task.isSuccessful() ) {

                            dismissProgress();
                            Toast.makeText( IntroActivity.this, R.string.init_failed, Toast.LENGTH_SHORT ).show();

                        } else {

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            WasManager.getInstance().sendRegistrationToken(IntroActivity.this, userId, token, new JsonAsync.JsonAsyncListener() {

                                @Override
                                public void onResponse( JSONObject json, int respCode ) {

                                    Log.d( "WHKIM", "onResponse( " + json.toString() + ", " + respCode + " )" );

                                    dismissProgress();

                                    if( respCode == JsonAsync.JSONASYNC_200OK ) {
                                        setDeviceAdmin();
                                        try {

                                            int response = json.getInt( Param.RESPONSE_CODE );

                                            switch( response ) {

                                                case 0 :

                                                    setDeviceAdmin();

                                                    break;

                                                default : Toast.makeText( IntroActivity.this, getErrorMessage( response ), Toast.LENGTH_SHORT ).show(); break;

                                            }
                                        } catch( Exception e ) {

                                            Toast.makeText( IntroActivity.this, getString( R.string.login_error, e.getMessage() ), Toast.LENGTH_SHORT ).show();

                                        }

                                        SharedPref.getInstance().putString( IntroActivity.this, Define.SharedKey.USER_ID, userId );

                                    } else Toast.makeText( IntroActivity.this, getString( R.string.login_failed, respCode ), Toast.LENGTH_SHORT ).show();
                                }
                            } );
                        }
                    }
                } );
    }

    private void setDeviceAdmin() {

        ComponentName componentName = new ComponentName( this, AdminReceiver.class );
        Intent intent = new Intent( DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN );

        intent.putExtra( DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName );
        startActivityForResult( intent, DEVICE_ADMIN_ADD_RESULT_ENABLE );

    }

    @Override
    public void onClick( View v ) {

        String userId = mEdit.getText().toString().trim();

        if( TextUtils.isEmpty( userId ) ) Toast.makeText( this, R.string.input_id, Toast.LENGTH_SHORT ).show();
        else tryLogin( userId );

    }

    @Override
    protected void onPermissionsResult( int requestCode, boolean hasDeniedPermissions, @Nullable final String[] deniedPermissions ) {

        if( hasDeniedPermissions ) {

            new AlertDialog.Builder( this )
                    .setTitle( R.string.notification )
                    .setMessage( R.string.error_permission )
                    .setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick( DialogInterface dialog, int which ) {

                            dialog.dismiss();
                            requestPermissions( deniedPermissions );

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

        } else checkUserId();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        if( requestCode == DEVICE_ADMIN_ADD_RESULT_ENABLE ) {
            
//            final boolean adminActive = devicePolicyManager.isAdminActive(componentName);
//            boolean cameraDisabled = devicePolicyManager.getCameraDisabled(componentName);
            // TODO 감시 서비스 시작

        } else super.onActivityResult( requestCode, resultCode, data );
    }
}