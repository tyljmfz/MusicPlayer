package com.example.y84104146.mediaplayer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXShare {
    public static final String APP_ID = "wx10e48798def4a607";
    public static final String ACTION_SHARE_RESPONSE = "action_wx_share_response";
    public static final String EXTRA_RESULT = "result";
    private final Context context;
    private final IWXAPI iwxapi;
    private AppRegister.OnResponseListener listener;
    private ResponseReceiver receiver;

    public WXShare(Context context){
        iwxapi = WXAPIFactory.createWXAPI(context,APP_ID);
        this.context = context;
    }

    public WXShare register(){
        iwxapi.registerApp(APP_ID);
        receiver = new ResponseReceiver();
        IntentFilter filter = new IntentFilter(ACTION_SHARE_RESPONSE);
        context.registerReceiver(receiver,filter);
        return this;
    }

    public void unregister(){
        try{
            iwxapi.unregisterApp();
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public WXShare share(String text){
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = mediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;

        boolean result = iwxapi.sendReq(req);
        Log.e("text Shared:" + result, "");
        return this;
    }

//    flag==0 微信好友;      flag==1 朋友圈
    public WXShare shareUrl(int flag, Context context, String url, String title, String description){
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;

        WXMediaMessage mediaMessage = new WXMediaMessage(webpageObject);
        mediaMessage.title = title;
        mediaMessage.description = description;

        Bitmap testPic = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sun28);
        mediaMessage.setThumbImage(testPic);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String .valueOf(System.currentTimeMillis());
        req.message = mediaMessage;
        req.scene = flag == 0? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        iwxapi.sendReq(req);
        return this;
    }

    public IWXAPI getIwxapi() {
        return iwxapi;
    }

    public void setListener(AppRegister.OnResponseListener listener){
        this.listener = listener;
    }

    private String buildTransaction(final String type){
        return (type == null)? String.valueOf(System.currentTimeMillis()) : (type + System.currentTimeMillis());
    }

    private class ResponseReceiver extends BroadcastReceiver{

        @Override
        public void onReceive (Context context, Intent intent){
            Response response = intent.getParcelableExtra(EXTRA_RESULT);
            Log.e("type: " + response.getType(), "type: " + response.getType());
            Log.e("errCode: " + response.errCode, "errCode: " + response.errCode);
            String result;
            if(listener != null){
                if(response.errCode == BaseResp.ErrCode.ERR_OK){
                    listener.onSuccess();
                }else if(response.errCode == BaseResp.ErrCode.ERR_USER_CANCEL){
                    listener.onCancel();
                }else {
                    switch (response.errCode){
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            result = "暂无权限，发送被拒绝";
                            break;
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                            result = "不支持的类型";
                            break;
                        default:
                            result = "发送返回";
                            break;
                    }
                    listener.onFail(result);
                }
            }
        }
    }

    public static class Response extends BaseResp implements Parcelable{
        public int errCode;
        public String errStr;
        public String transaction;
        public String openId;

        private int type;
        private boolean checkResult;

        public Response(BaseResp baseResp){
            errCode = baseResp.errCode;
            errStr = baseResp.errStr;
            transaction = baseResp.transaction;
            openId = baseResp.openId;
            type = baseResp.getType();
            checkResult = baseResp.checkArgs();
        }

        @Override
        public int getType(){
            return type;
        }

        @Override
        public boolean checkArgs(){
            return checkResult;
        }

        @Override
        public int describeContents(){
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags){
            dest.writeInt(this.errCode);
            dest.writeString(this.errStr);
            dest.writeString(this.transaction);
            dest.writeString(this.openId);
            dest.writeInt(this.type);
            dest.writeByte(this.checkResult? (byte)1 : (byte)0);
        }

        protected Response(Parcel in){
            this.errCode = in.readInt();
            this.errStr = in.readString();
            this.transaction = in.readString();
            this.openId = in.readString();
            this.type = in.readInt();
            this.checkResult = (in.readByte() != 0);
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel parcel) {
                return new Response(parcel);
            }

            @Override
            public Response[] newArray(int i) {
                return new Response[i];
            }
        };
    }
}
