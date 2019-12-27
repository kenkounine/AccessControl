package com.vietnamproject.accesscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.vietnamproject.accesscontrol.config.Define;


public class NotifyActivity extends BaseActivity {

    public static final String EXTRA_CMD_CODE = "cmdCode";

    private Runnable mR = new Runnable() {

        @Override
        public void run() {

            finish();

        }
    };

    private Handler mH = new Handler();


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_notify );

        Intent intent = getIntent();

        if( intent != null ) {

            int cmdCode = intent.getIntExtra( EXTRA_CMD_CODE, Define.CMD_CODE.NONE );

            View bg = findViewById( R.id.bg );
            ImageView iv = findViewById( R.id.iv );
            TextView tv = findViewById( R.id.tv );

            if( cmdCode == Define.CMD_CODE.CAMERA_LOCK ) {

                bg.setBackgroundColor( 0xFFFF0000 );
                iv.setImageResource( R.drawable.ic_lock );
                tv.setText( R.string.lock_mode );
                mH.removeCallbacks( mR );
                mH.postDelayed( mR, 5000 );

            } else if( cmdCode == Define.CMD_CODE.CAMERA_UNLOCK ) {

                bg.setBackgroundColor( 0xFF00292A );
                iv.setImageResource( R.drawable.ic_unlock );
                tv.setText( R.string.unlock_mode );
                mH.removeCallbacks( mR );
                mH.postDelayed( mR, 5000 );

            } else finish();
        } else finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mH.removeCallbacks( mR );

    }
}