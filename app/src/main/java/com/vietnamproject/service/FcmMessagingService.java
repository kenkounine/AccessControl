package com.vietnamproject.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken( @NonNull String s ) {

        // TODO 서버로 fcm 토큰을 전달 한다.

        super.onNewToken( s );

    }

    @Override
    public void onMessageReceived( @NonNull RemoteMessage remoteMessage ) {

        // TODO 수신받은 메시지를 처리한다.

    }
}