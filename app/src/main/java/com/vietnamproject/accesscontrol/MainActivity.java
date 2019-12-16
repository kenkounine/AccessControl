package com.vietnamproject.accesscontrol;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vietnamproject.accesscontrol.util.LockManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        LockManager.getInstance().lock( this );
        finish();

    }
}