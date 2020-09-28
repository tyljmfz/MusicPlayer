package com.example.y84104146.mediaplayer.wxapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.y84104146.mediaplayer.AppRegister;
import com.example.y84104146.mediaplayer.R;
import com.example.y84104146.mediaplayer.WXShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI iwxapi;
    private WXShare wxShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);

        wxShare = new WXShare(this);
        wxShare.setListener(new AppRegister.OnResponseListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(String message) {

            }
        });
        Log.e("WXEntryActivity","WXEntryActivity");
        WXShare share = new WXShare(this);
        iwxapi = share.getIwxapi();
        wxShare.shareUrl(0, this, "https://open.weixin.qq.com","微信分享","I am so crazy");
        try{
            if(!iwxapi.handleIntent(getIntent(), this)){
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart(){
        wxShare.register();
        super.onStart();
    }

    @Override
    protected void onResume(){
        wxShare.register();
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        wxShare.unregister();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        wxShare.unregister();
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Log.e("onNewIntent","onNewIntent");
        setIntent(intent);
        if(!iwxapi.handleIntent(intent, this)){
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq){

    }

    @Override
    public void onResp(BaseResp baseResp){
        Intent intent = new Intent(WXShare.ACTION_SHARE_RESPONSE);
        intent.putExtra(WXShare.EXTRA_RESULT, new WXShare.Response(baseResp));
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onDetachedFromWindow () {
        try {
            super.onDetachedFromWindow();
        }
        catch (IllegalArgumentException e) {
//            stopFlipping();
            Log.d("WXEntryActivity","fail");
        }
    }

}
