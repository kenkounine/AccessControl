package com.vietnamproject.accesscontrol.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.vietnamproject.accesscontrol.R;


public class LockManager implements LockView.LockListener {

    private static final LockManager mInstance = new LockManager();

    private static final Runnable mR = new Runnable() {

        @Override
        public void run() {

            if( mLockView != null ) {

                mInstance.getWindowManager( mLockView.getContext() ).removeView( mLockView );

                mLockView = null;

            }
        }
    };

    private WindowManager mWindowManager;
    private InputMethodManager mImm;
    private final static Handler mH = new Handler();
    private static LockView mLockView;


    private LockManager() {}

    public static final LockManager getInstance() { return mInstance; }

    public void lock( Context context ) {

        if( mLockView == null ) {

            mLockView = ( LockView ) View.inflate( context, R.layout.lock_view, null );

            mLockView.setOnLockListener( this );

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            params.format = PixelFormat.TRANSLUCENT;
            params.type = Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_SYSTEM_ALERT : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params.gravity = Gravity.TOP;

            getWindowManager( context ).addView( mLockView, params );

        }
    }

    @Override
    public void unlock( Context context ) {

        if( mLockView != null ) {

            mLockView.unlock();
            mH.removeCallbacks( mR );
            mH.postDelayed( mR, 1000 * 3 );

        }
    }

    private WindowManager getWindowManager( Context context ) {

        if( mWindowManager == null ) mWindowManager = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );

        return mWindowManager;

    }
}