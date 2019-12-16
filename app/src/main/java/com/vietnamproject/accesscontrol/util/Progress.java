package com.vietnamproject.accesscontrol.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.vietnamproject.accesscontrol.R;


public class Progress extends Dialog {

    public Progress( Context context ) { super( context, R.style.Progress ); }

    protected Progress( Context context, boolean cancelable, OnCancelListener cancelListener ) {

        super( context, R.style.Progress );
        setCancelable( cancelable );
        setOnCancelListener( cancelListener );

    }

    public static Progress show( Context context, CharSequence title, CharSequence message ) {

        return show( context, title, message, false );

    }

    public static Progress show( Context context, CharSequence title, CharSequence message, OnCancelListener cancelListener ) {

        return show( context, title, message, false, true, cancelListener );

    }

    public static Progress show( Context context, CharSequence title, CharSequence message, boolean indeterminate ) {

        return show( context, title, message, indeterminate, false, null );

    }

    public static Progress show( Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable ) {

        return show( context, title, message, indeterminate, cancelable, null );

    }

    public static Progress show( Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener ) {

        Progress popup = new Progress( context, cancelable, cancelListener );
        ProgressBar bar = new ProgressBar( context, null, android.R.attr.progressBarStyleLargeInverse );
        bar.getIndeterminateDrawable().setColorFilter( Color.parseColor("#FF54C7D9"), android.graphics.PorterDuff.Mode.MULTIPLY );

        if( cancelListener != null ) popup.setOnCancelListener( cancelListener );

        try {

            popup.setTitle( title );
            popup.addContentView( bar, new ViewGroup.LayoutParams( -2, -2 ) );
            popup.show();

        } catch( WindowManager.BadTokenException e ) {

            e.printStackTrace();

        }

        return popup;

    }
}