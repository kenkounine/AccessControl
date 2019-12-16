package com.vietnamproject.accesscontrol.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.vietnamproject.accesscontrol.R;
import com.vietnamproject.accesscontrol.config.Define;


public class LockView extends RelativeLayout implements View.OnClickListener, View.OnKeyListener {

    private final int MODE_NORMAL = 0;
    private final int MODE_INPUT = 1;

    private InputMethodManager mImm;
    private LockListener mListener;
    private View mLockIcon;
    private View mUnlockIcon;
    private EditText mInput;
    private int mMode = MODE_NORMAL;


    public LockView( Context context, AttributeSet attrs ) {

        super( context, attrs );

        mImm = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );

    }

    public void setOnLockListener( LockListener listener ) {

        mListener = listener;

    }

    @Override
    public boolean dispatchKeyEvent( KeyEvent event ) {

        Log.d( "WHKIM", "dispatchKeyEvent( " + event + " )" );

        if( event.getAction() == KeyEvent.ACTION_UP ) {

            switch( event.getKeyCode() ) {

                case KeyEvent.KEYCODE_ENTER :

                    String input = mInput.getText().toString().trim();

                    if( Define.DEFAULT_PASSWORD.equals( input ) ) {

                        if( mListener != null ) mListener.unlock( getContext() );

                    } else mInput.setText( "" );

                    break;

            }
        }

        return super.dispatchKeyEvent( event );

    }

    @Override
    public boolean onKey( View v, int keyCode, KeyEvent event ) {

        if( event.getAction() == KeyEvent.ACTION_UP ) {

            switch( keyCode ) {

                case KeyEvent.KEYCODE_BACK :

                    if( mMode == MODE_INPUT ) {

                        mMode = MODE_NORMAL;

                        changeMode();

                    }

                    break;

            }
        }

        return false;

    }

    private void changeMode() {

        Log.d( "WHKIM", "changeMode()" );

        if( mMode == MODE_NORMAL ) {

            mLockIcon.setVisibility( View.VISIBLE );
            mInput.setVisibility( View.GONE );

        } else {

            mLockIcon.setVisibility( View.GONE );
            mInput.setVisibility( View.VISIBLE );
            mInput.requestFocus();
            mImm.showSoftInput( mInput, InputMethodManager.SHOW_IMPLICIT );

        }
    }

    public void unlock() {

        mImm.hideSoftInputFromWindow( mInput.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY );
        mLockIcon.setVisibility( View.GONE );
        mInput.setVisibility( View.GONE );
        mUnlockIcon.setVisibility( View.VISIBLE );

    }

    @Override
    protected void onLayout( boolean changed, int l, int t, int r, int b ) {

        super.onLayout( changed, l, t, r, b );

        if( changed ) {

            mLockIcon = findViewById( R.id.ib_lock );
            mUnlockIcon = findViewById( R.id.ib_unlock );
            mInput = findViewById( R.id.et );

            mLockIcon.setOnClickListener( this );
            mInput.setOnKeyListener( this );

        }
    }

    @Override
    public void onClick( View v ) {

        Log.d( "WHKIM", "onClick()" );

        switch( v.getId() ) {

            case R.id.ib_lock :

                if( mMode == MODE_NORMAL ) {

                    mMode = MODE_INPUT;

                    changeMode();

                }

                break;

        }
    }

    public interface LockListener {

        void unlock( Context context );

    }
}