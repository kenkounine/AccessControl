package com.vietnamproject.accesscontrol;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.vietnamproject.accesscontrol.config.Define;
import com.vietnamproject.accesscontrol.util.DeviceAdminManager;
import com.vietnamproject.accesscontrol.util.SharedPref;
import com.vietnamproject.accesscontrol.util.Utils;
import com.vietnamproject.accesscontrol.was.JsonAsync;
import com.vietnamproject.accesscontrol.was.Param;
import com.vietnamproject.accesscontrol.was.WasManager;

import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;


public class IntroActivity extends PermissionActivity implements View.OnClickListener {

    private EditText mEdit;
    private EditText mEditPhone;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_intro );

        if( !checkNfc( getIntent() ) ) requestAllPermissions();

        TextView versionText = findViewById( R.id.tv_version );

        versionText.setText( getString( R.string.version, Utils.getVersionName( this ) ) );

    }

    @Override
    protected void onNewIntent( Intent intent ) {

        super.onNewIntent( intent );
        checkNfc( intent );

    }

    private boolean checkNfc( Intent intent ) {

        if( intent != null ) {

            Tag tag = intent.getParcelableExtra( NfcAdapter.EXTRA_TAG );

            if( tag != null ) {

                Ndef ndef = Ndef.get( tag );

                if( ndef != null ) {

                    try {

                        ndef.connect();

                        NdefMessage ndefMessage = ndef.getNdefMessage();
                        String cmd = new String( ndefMessage.getRecords()[ 0 ].getPayload() );

                        ndef.close();
                        DeviceAdminManager.getInstance().setPolicy( this, Utils.getCmdCode( cmd ) );
                        finish();

                        return true;

                    } catch( IOException | FormatException e ) { e.printStackTrace(); }
                }
            }
        }

        return false;

    }

    /**
     * 저장된 사용자 아이디를 가져온다.<br/>
     * 저장된 아이디가 없을 경우 아이디 입력창을 보여준다.
     */
    private void checkUserId() {

        String userId = SharedPref.getInstance().getString( this, Define.SharedKey.USER_ID, null );
        String phoneNumber = SharedPref.getInstance().getString( this, Define.SharedKey.USER_PHONE, null );

        if( TextUtils.isEmpty( userId ) || TextUtils.isEmpty( phoneNumber ) ) {

            mEdit = findViewById( R.id.et );
            mEditPhone = findViewById( R.id.et_phone );
            View parent = findViewById( R.id.layout );
            View btn = findViewById( R.id.btn );

            btn.setOnClickListener( this );

            AlphaAnimation anim = new AlphaAnimation( 0F, 1F );

            anim.setDuration( 1000 );
            anim.setInterpolator( new AccelerateInterpolator() );
            parent.setVisibility( View.VISIBLE );
            parent.startAnimation( anim );

        } else {

            int lastCmd = SharedPref.getInstance().getInt( this, Define.SharedKey.LAST_CMD, Define.CMD_CODE.NONE );

            if( lastCmd == Define.CMD_CODE.CAMERA_LOCK ) {

                ImageView iv = findViewById( R.id.iv_lock );
                final Drawable drawable = iv.getDrawable();

                final float[] from = new float[ 3 ];
                final float[] to = new float[ 3 ];
                final float[] hsv = new float[ 3 ];

                Color.colorToHSV( Color.parseColor("#FFFFFFFF" ), from );
                Color.colorToHSV( Color.parseColor("#FFFF0000" ), to );

                ValueAnimator animator = ValueAnimator.ofFloat( 0, 1 );

                animator.setDuration( 500 );
                animator.setRepeatCount( Animation.INFINITE );
                animator.setRepeatMode( ValueAnimator.REVERSE );
                animator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate( ValueAnimator animation ) {

                        float animatedFraction = animation.getAnimatedFraction();

                        hsv[ 0 ] = from[ 0 ] + ( to[ 0 ] - from[ 0 ] ) * animatedFraction;
                        hsv[ 1 ] = from[ 1 ] + ( to[ 1 ] - from[ 1 ] ) * animatedFraction;
                        hsv[ 2 ] = from[ 2 ] + ( to[ 2 ] - from[ 2 ] ) * animatedFraction;

                        drawable.setColorFilter( Color.HSVToColor( hsv ), PorterDuff.Mode.SRC_ATOP );

                    }
                } );

                animator.start();
                iv.setVisibility( View.VISIBLE );

            } else findViewById( R.id.btn_lock ).setVisibility( View.VISIBLE );

            tryLogin( userId, phoneNumber );

        }
    }

    /** 서버에 로그인을 시도한다. */
    private void tryLogin( final String userId, final String phoneNumber ) {

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

                            WasManager.getInstance().sendRegistrationToken(IntroActivity.this, userId, phoneNumber, token, new JsonAsync.JsonAsyncListener() {

                                @Override
                                public void onResponse( JSONObject json, int respCode ) {

                                    dismissProgress();

                                    if( respCode == JsonAsync.JSONASYNC_200OK ) {

                                        try {

                                            int response = json.getInt( Param.RESPONSE_CODE );

                                            switch( response ) {

                                                case 0 :

                                                    SharedPref.getInstance().putString( IntroActivity.this, Define.SharedKey.USER_ID, userId );
                                                    SharedPref.getInstance().putString( IntroActivity.this, Define.SharedKey.USER_PHONE, phoneNumber );

                                                    String gps = json.getString( Param.GPS );
                                                    View parent = findViewById( R.id.layout );

                                                    parent.setVisibility( View.GONE );
                                                    findViewById( R.id.btn_lock ).setVisibility( View.VISIBLE );
                                                    Toast.makeText( IntroActivity.this, getString( R.string.login_message, userId ), Toast.LENGTH_SHORT ).show();

                                                    if( !TextUtils.isEmpty( gps ) ) {

                                                        String[] location = gps.split( "," );
                                                        Set<String> set = new LinkedHashSet<>();

                                                        for( String s : location ) set.add( s );

                                                        SharedPref.getInstance().putStringSet( IntroActivity.this, Define.SharedKey.LOCATION, set );

                                                    }

//                                                    checkLastCmd();

                                                    break;

                                                default : Toast.makeText( IntroActivity.this, getErrorMessage( response ), Toast.LENGTH_SHORT ).show(); break;

                                            }
                                        } catch( Exception e ) {

                                            Toast.makeText( IntroActivity.this, getString( R.string.login_error, e.getMessage() ), Toast.LENGTH_SHORT ).show();

                                        }

                                        SharedPref.getInstance().putString( IntroActivity.this, Define.SharedKey.USER_ID, userId );

                                    } else Toast.makeText( IntroActivity.this, getString( R.string.login_failed, Integer.valueOf( respCode ) ), Toast.LENGTH_SHORT ).show();
                                }
                            } );
                        }
                    }
                } );
    }

    @Deprecated
    private void checkLastCmd() {

        WasManager.getInstance().requestLastCmd( this, new JsonAsync.JsonAsyncListener() {

            @Override
            public void onResponse( JSONObject json, int respCode ) {

                if( respCode == JsonAsync.JSONASYNC_200OK ) {

                    try {

                        int response = json.getInt( Param.RESPONSE_CODE );

                        if( response == Define.Error.NONE ) {

                            String cmd = json.getString( Param.CMD );

                            DeviceAdminManager.getInstance().setPolicy( IntroActivity.this, Integer.valueOf( cmd ) );

                        }
                    } catch( Exception e ) { e.printStackTrace(); }
                }
            }
        } );
    }

    @Override
    public void onClick( View v ) {

        String userId = mEdit.getText().toString().trim();
        String phoneNumber = mEditPhone.getText().toString().trim();

        if( TextUtils.isEmpty( userId ) ) Toast.makeText( this, R.string.input_id, Toast.LENGTH_SHORT ).show();
        else if( TextUtils.isEmpty( phoneNumber ) ) Toast.makeText( this, R.string.input_phone, Toast.LENGTH_SHORT  ).show();
        else tryLogin( userId, phoneNumber );

    }

    public void onLock( View v ) {

        DeviceAdminManager.getInstance().setPolicy( IntroActivity.this, Define.CMD_CODE.CAMERA_LOCK );
        finish();

    }

    @Override
    protected void onPermissionsResult( int requestCode, boolean hasDeniedPermissions, @Nullable final String[] deniedPermissions ) {

        if( hasDeniedPermissions ) {

            for( String permission : deniedPermissions ) Log.d( "WHKIM", "deniedPermission : " + permission );

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
}