package com.example.y84104146.mediaplayer;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class AppRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        final IWXAPI iwxapi = WXAPIFactory.createWXAPI(context,null);
        iwxapi.registerApp(WXShare.APP_ID);
    }

    public interface OnResponseListener{
        void onSuccess();
        void onCancel();
        void onFail(String message);
    }
}
