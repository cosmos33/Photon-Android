package com.immomo.push.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cosmos.photon.push.PhotonPushManager;
import com.cosmos.photon.push.msg.MoMessage;

public class MainActivity extends AppCompatActivity implements MyApplication.PushTokenObserver {

    TextView tokenTv;
    EditText aliasEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // statistics notification click event
        PhotonPushManager.getInstance().logPushClick(getIntent());

        setContentView(R.layout.activity_main);
        aliasEt = (EditText) findViewById(R.id.et_alias);
        tokenTv = (TextView) findViewById(R.id.tv_token);

        tokenTv.setText("token:" + MyApplication.getPushToken());

        String currentUserId = MyApplication.getUserId();
        if (!TextUtils.isEmpty(currentUserId)) {
            aliasEt.setText(currentUserId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 101);
        }

        MyApplication.registerPushTokenObserver(this);
    }

    @Override
    public void onReceiveToken(String token) {
        if (!isFinishing()) {
            tokenTv.setText("token:" + token);
        }
    }

    @Override
    public void onReceiveMessage(final MoMessage moMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("透传消息");
                builder.setMessage(moMessage.text);
                builder.show();
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_self:
                String alias = aliasEt.getText().toString();
                if (!TextUtils.isEmpty(alias)) {
                    MyApplication.saveUserId(alias);
                }

                PhotonPushManager.getInstance().register();
                break;

            case R.id.register_self_alias:
                String alias0 = aliasEt.getText().toString();
                if (!TextUtils.isEmpty(alias0)) {
                    MyApplication.saveUserId(alias0);

                    PhotonPushManager.getInstance().registerWithAlias(alias0);
                }
                break;

            case R.id.unregister_self:
                aliasEt.setText("");
                MyApplication.saveUserId("");

                PhotonPushManager.getInstance().unRegister();
                break;

            case R.id.alias:
                String alias1 = aliasEt.getText().toString();
                if (!TextUtils.isEmpty(alias1)) {
                    MyApplication.saveUserId(alias1);

                    PhotonPushManager.getInstance().setAlias(alias1);
                }
                break;

            case R.id.unalias:
                String alias2 = aliasEt.getText().toString();
                if (!TextUtils.isEmpty(alias2)) {
                    PhotonPushManager.getInstance().unAlias(alias2);

                    aliasEt.setText("");
                    MyApplication.saveUserId("");
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing()) {
            MyApplication.unregisterPushTokenObserver(this);
        }
    }
}
