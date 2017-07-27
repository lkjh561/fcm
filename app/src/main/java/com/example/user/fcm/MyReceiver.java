package com.example.user.fcm;

/**
 * Created by USER on 2017/7/3.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.example.user.fcm.MyFirebaseMessagingService.intent;

public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent arg1) {
        // TODO Auto-generated method stub
        // 讀取包含在Intent物件中的資料
        String name = intent.getStringExtra("message");


        // 因為這不是Activity元件，需要使用Context物件的時候，
        // 不可以使用「this」，要使用參數提供的Context物件
        Toast.makeText(context,name, Toast.LENGTH_SHORT).show();
    }
}


