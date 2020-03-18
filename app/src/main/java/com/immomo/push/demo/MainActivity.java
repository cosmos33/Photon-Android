package com.immomo.push.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmos.photon.push.PhotonPushManager;
import com.cosmos.photon.push.msg.MoMessage;

import java.util.Set;
import java.util.TreeSet;

//import com.cosmos.photon.push.NotifyHelper;
//import com.cosmos.photon.push.notification.INotifyCode;
//import com.cosmos.photon.push.notification.MoNotify;


public class MainActivity extends AppCompatActivity implements MyApplication.PushTokenObserver {

    TextView tokenTv;
    EditText aliasEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            if (getIntent().getStringExtra("goto") != null) {
                Log.v("MainActivity", getIntent().getStringExtra("goto"));
            }
        }

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
    public void onReceiveToken(final String token) {
        if (!isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tokenTv.setText("token:" + token);
                }
            });
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
            case R.id.setTags:
                String alias3 = aliasEt.getText().toString();
                if (TextUtils.isEmpty(alias3)) {
                    PhotonPushManager.getInstance().setTag("tokenTag1");
                    PhotonPushManager.getInstance().setTag("tokenTag2");
                    Set<String> tags = new TreeSet<>();
                    tags.add("tokenTag3");
                    tags.add("tokenTag4");
                    tags.add("tokenTag5");
                    PhotonPushManager.getInstance().setTags(tags);
                } else {
                    PhotonPushManager.getInstance().setTagToAlias(alias3, "aliasTag1");
                    PhotonPushManager.getInstance().setTagToAlias(alias3, "aliasTag2");
                    Set<String> tags = new TreeSet<>();
                    tags.add("aliasTag3");
                    tags.add("aliasTag4");
                    tags.add("aliasTag5");
                    PhotonPushManager.getInstance().setTagsToAlias(alias3, tags);
                }

                break;

            case R.id.removeTags:
                String alias4 = aliasEt.getText().toString();
                if (TextUtils.isEmpty(alias4)) {
                    PhotonPushManager.getInstance().removeTag("tokenTag1");
                    Set<String> tags = new TreeSet<>();
                    tags.add("tokenTag4");
                    tags.add("tokenTag5");
                    PhotonPushManager.getInstance().removeTags(tags);
                } else {
                    PhotonPushManager.getInstance().removeTagToAlias(alias4, "aliasTag1");
                    Set<String> tags = new TreeSet<>();
                    tags.add("aliasTag4");
                    tags.add("aliasTag5");
                    PhotonPushManager.getInstance().removeTagsToAlias(alias4, tags);
                }
                break;
            case R.id.uploadLog:
                break;
            case R.id.checkNotify:
                NotificationManagerCompat nm = NotificationManagerCompat.from(this);
                boolean isNotifyEnabled = nm.areNotificationsEnabled();
                Toast.makeText(MainActivity.this, "isNotifyEnabled=" + isNotifyEnabled, Toast.LENGTH_SHORT).show();

                break;
            case R.id.notify:
//                testNotification();
                break;
        }
    }

    int counter = 0;

    //模拟一条应用的通知栏，可以更改testPackageName来更改dev和release
//    private void testNotification() {
//        String testPackageName = "com.immomo.push.demo";
//        MoNotify notify = new MoNotify();
//        notify.sound = 1;
//        notify.light = 1;
//        notify.vibrate = 1;
//        notify.type = "1";
//        notify.icon = "https://img1.doubanio.com/view/ark_article_cover/retina/public/107340747.jpg?v=1548408187";
//        notify.soundType = "ms5";
//        int count = counter++;
//        notify.title = "测试标题" + count;
//        notify.desc = "测试内容12345678" + count;
//        notify.toPkg = testPackageName;
////        notify.actionType = INotifyCode.ACTION_TYPE_INTENT_URI;
////        notify.action = "#Intent;component=" + testPackageName + "/com.immomo.momo.android.activity.WelcomeActivity;i.tof=4;i.pushType=1;end";
//        notify.actionType = INotifyCode.ACTION_TYPE_BROWER_URL;
//        notify.action = "http://www.immomo.com";
//        NotifyHelper.sendNotify(getApplicationContext(), notify);
//    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing()) {
            MyApplication.unregisterPushTokenObserver(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v("MainActivity", "onNewIntent");
    }
}
