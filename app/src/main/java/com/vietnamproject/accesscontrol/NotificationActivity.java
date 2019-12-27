package com.vietnamproject.accesscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class NotificationActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "mode";

    public static final int MODE_LOCK = 0;
    public static final int MODE_UNLOCK = 1;

    private final Runnable mR = new Runnable() {

        @Override
        public void run() {

            finish();

        }
    };

    private Handler mH = new Handler();


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        supportRequestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_notification );

        Intent intent = getIntent();

        if( intent != null ) {

            ImageView iv = findViewById( R.id.iv );
            TextView tv = findViewById( R.id.tv );
            int type = intent.getIntExtra( EXTRA_MODE, -1 );

            if( type == MODE_LOCK ) {

                iv.setImageResource( R.drawable.ic_noti_lock );
                tv.setText( R.string.lock_mode );

            } else {

                iv.setImageResource( R.drawable.ic_noti_lock_unlock );
                tv.setText( R.string.unlock_mode );

            }

            mH.removeCallbacks( mR );
            mH.postDelayed( mR, 3000 );

        } else finish();
    }
}