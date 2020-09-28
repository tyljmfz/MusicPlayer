package com.example.y84104146.mediaplayer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.wrappers.UMSSDKWrapper;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final String TAG = "SignupActivity";
    // 界面控件
    private EditText phoneEdit;
    private EditText passwordEdit;
    private EditText verifyCodeEdit;
    private EditText nicknameEdit;
    private Button getVerifiCodeButton;
    private Button createAccountButton;

    public String phone;
    public String password;

    private VerifyCodeManager codeManager;
    String result = "";

    private MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        myDatabaseHelper = new MyDatabaseHelper(this,"UserInfo.db",null,1);
        initViews();
        codeManager = new VerifyCodeManager(this, phoneEdit, getVerifiCodeButton);
    }

    private void initViews() {

        getVerifiCodeButton = getView(R.id.btn_send_verifi_code);
        getVerifiCodeButton.setOnClickListener(this);
        createAccountButton = getView(R.id.btn_create_account);
        createAccountButton.setOnClickListener(this);

        phoneEdit = getView(R.id.et_phone);
        phoneEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        verifyCodeEdit = getView(R.id.et_verifiCode);
        verifyCodeEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        nicknameEdit = getView(R.id.et_nickname);
        nicknameEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordEdit = getView(R.id.et_password);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_GO);
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {


                        commit();
                }
                return false;
            }
        });


    }

    public final <E extends View> E getView(int id) {
        try {
            return (E) findViewById(id);
        } catch (ClassCastException ex) {
            Log.e(TAG, "Could not cast View to concrete class.", ex);
            throw ex;
        }
    }

    private void commit() {

        // TODO:请求服务端注册账号

        UserInfo userInfo = new UserInfo();
        userInfo.setTelePhoneNumber(phoneEdit.getText().toString().trim());
        userInfo.setPassword(passwordEdit.getText().toString().trim());
        userInfo.setNikeName(nicknameEdit.getText().toString().trim());
        userInfo.save();
//        phone = phoneEdit.getText().toString().trim();
//        password = passwordEdit.getText().toString().trim();
//        if (checkInput(phone, password)) {
//            SQLiteDatabase database = myDatabaseHelper.getWritableDatabase();
//            ContentValues values = new ContentValues();
//            //                    "id integer primary key autoincrement," +
//            //                            "nickname text," +
//            //                            "password text)";
//            values.put("nickname", phone);
//            values.put("password", password);
//            database.insert("User", null, values);
//            values.clear();
//        }


    }


    //因为EventHandler 中afterEvent可能在子线程中  所以传到主线程的hanlder处理
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int event=msg.arg1;
            int result=msg.arg2;
            Object data=msg.obj;
            if(result==SMSSDK.RESULT_COMPLETE){//完成
                if(event== SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    Toast.makeText(SignUpActivity.this,"验证码已发送", Toast.LENGTH_SHORT).show();
                }else if(event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                    Toast.makeText(SignUpActivity.this,"验证通过", Toast.LENGTH_SHORT).show();
                }
            }else{
                //可设置Flag区分是请求验证码出错还是验证出错
                Toast.makeText(SignUpActivity.this,"出错", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private EventHandler eventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;//事件：使用的是发送还是提交
            msg.arg2 = result;//结果
            msg.obj = data;
            handler.handleMessage(msg);
        }
    };

    private boolean checkInput(String phone, String password) {
        if (TextUtils.isEmpty(phone)) { // 电话号码为空
            ToastUtils.showShort(this, R.string.phone_cannot_empty);
        } else {
            if (!RegexUtils.checkMobile(phone)) { // 电话号码格式有误
                ToastUtils.showShort(this, R.string.error_invalid_phone);
            }  else if (password == null || password.trim().equals("")) {
                Toast.makeText(this, R.string.password_cannot_empty,
                        Toast.LENGTH_LONG).show();
            }else if (password.length() < 6 || password.length() > 32
                    || TextUtils.isEmpty(password)) { // 密码格式
                ToastUtils.showShort(this,
                        R.string.error_incorrect_password);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_verifi_code:
                // TODO 请求接口发送验证码
                codeManager.getVerifyCode(VerifyCodeManager.REGISTER);
                break;
            case R.id.btn_create_account:
                commit();
                break;

            default:
                break;
        }
    }
}
