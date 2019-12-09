package com.vietnamproject.accesscontrol.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;


public class SharedPref {

    private static final SharedPref INSTANCE = new SharedPref();

    private final String PREF_NAME = "com.gaoncnce.PICKMECAM";

    private SharedPreferences mSharedPreferences;


    private void ShredValues() {}

    public static final SharedPref getInstance() { return INSTANCE; }

    public SharedPreferences getSharedPreferences( Context context ) {

        if( mSharedPreferences == null ) mSharedPreferences = context.getSharedPreferences( PREF_NAME, Context.MODE_PRIVATE );

        return mSharedPreferences;

    }

    public SharedPreferences.Editor getEditor( Context context ) {

        return getSharedPreferences( context ).edit();

    }

    public boolean putBoolean( Context context, String key, boolean value ) {

        return getEditor( context ).putBoolean( key, value ).commit();

    }

    public boolean getBoolean( Context context, String key ) {

        return getBoolean( context, key, false );

    }

    public boolean getBoolean( Context context, String key, boolean defValue ) {

        return getSharedPreferences( context ).getBoolean( key, defValue );

    }

    public boolean putFloat( Context context, String key, float value ) {

        return getEditor( context ).putFloat( key, value ).commit();

    }

    public float getFloat( Context context, String key ) {

        return getFloat( context, key, 0F );

    }

    public float getFloat( Context context, String key, float defValue ) {

        return getSharedPreferences( context ).getFloat( key, defValue );

    }

    public boolean putInt( Context context, String key, int value ) {

        return getEditor( context ).putInt( key, value ).commit();

    }

    public int getInt( Context context, String key ) {

        return getInt( context, key, 0 );

    }

    public int getInt( Context context, String key, int defValue ) {

        return getSharedPreferences( context ).getInt( key, defValue );

    }

    public boolean putLong( Context context, String key, long value ) {

        return getEditor( context ).putLong( key, value ).commit();

    }

    public long getLong( Context context, String key ) {

        return getLong( context, key, 0L );

    }

    public long getLong( Context context, String key, long defValue ) {

        return getSharedPreferences( context ).getLong( key, defValue );

    }

    public boolean putString( Context context, String key, String value ) {

        return getEditor( context ).putString( key, value ).commit();

    }

    public String getString( Context context, String key ) {

        return getString( context, key, null );

    }

    public String getString( Context context, String key, String defValue ) {

        return getSharedPreferences( context ).getString( key, defValue );

    }

    public boolean putStringSet( Context context, String key, Set<String> value ) {

        return getEditor( context ).putStringSet( key, value ).commit();

    }

    public Set<String> getStringSet( Context context, String key ) {

        return getStringSet( context, key, null );

    }

    public Set<String> getStringSet( Context context, String key, Set<String> defValue ) {

        return getSharedPreferences( context ).getStringSet( key, defValue );

    }
}