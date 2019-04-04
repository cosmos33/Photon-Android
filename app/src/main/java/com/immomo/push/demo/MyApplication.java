package com.immomo.push.demo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cosmos.photon.push.PhotonPushManager;
import com.cosmos.photon.push.log.LogUtil;
import com.cosmos.photon.push.msg.MoMessage;
import com.cosmos.photon.push.notification.MoNotify;
import com.cosmos.photon.push.PushMessageReceiver;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyApplication extends Application {

    private static SharedPreferences preferences;

    public static final String APP_ID = "26e61d33cefc4e2cab629715b6aa260f";

    private static String token;

    @Override
    public void onCreate() {
        super.onCreate();

        // open debug log
        {
            LogUtil.init(this, new File(getFilesDir(), "MDLOG.log").getAbsolutePath());
            LogUtil.setLogOpen(true);
        }

        preferences = getSharedPreferences("demo_prefs", Context.MODE_MULTI_PROCESS);
        PhotonPushManager.CHANNEL_MODE = false;


        // init sdk
        PhotonPushManager.getInstance().init(this, APP_ID, new PushMessageReceiver() {

            @Override
            public boolean onNotificationShow(MoNotify notify) {
                return super.onNotificationShow(notify);
            }

            @Override
            public boolean onNotificationMessageClicked(MoNotify notify) {
                return false;
            }

            @Override
            public void onReceivePassThroughMessage(MoMessage message) {
                super.onReceivePassThroughMessage(message);
                for (PushTokenObserver pushTokenObserver : observers) {
                    pushTokenObserver.onReceiveMessage(message);
                }
            }

            @Override
            public void onCommand(int type, int result, String message) {
            }

            @Override
            public void onToken(int result, String token, String message) {
                MyApplication.token = token;
                for (PushTokenObserver pushTokenObserver : observers) {
                    pushTokenObserver.onReceiveToken(token);
                }
                Log.i(MyApplication.class.getSimpleName(), String.format("type:%d token:%s message:%s", result, token, message));
                if (result == 0) {
                    showToast("注册成功");
                } else {
                    showToast(message);
                }
            }

            @Override
            public boolean isMiPushOpen() {
                return true;
            }

            @Override
            public boolean isHuaweiPushOpen() {
                return true;
            }

            @Override
            public boolean isVivoPushOpen() {
                return true;
            }

            @Override
            public boolean isOppoPushOpen() {
                return true;
            }
        });

        String currentUserId = getUserId();

        if (!TextUtils.isEmpty(currentUserId)) {
            PhotonPushManager.getInstance().registerWithAlias(currentUserId);
        }

    }

    public static String getUserId() {
        return preferences.getString("userid", null);
    }

    public static void saveUserId(String uid) {
        preferences.edit().putString("userid", uid).apply();
    }


    private void showToast(final String message) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    interface PushTokenObserver {
        void onReceiveToken(String token);
        void onReceiveMessage(MoMessage moMessage);
    }

    private final static List<PushTokenObserver> observers = new CopyOnWriteArrayList<>();

    public static void registerPushTokenObserver(PushTokenObserver observer) {
        observers.add(observer);
    }

    public static void unregisterPushTokenObserver(PushTokenObserver observer) {
        observers.remove(observer);
    }

    public static String getPushToken() {
        return token;
    }


}
