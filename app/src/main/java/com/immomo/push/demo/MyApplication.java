package com.immomo.push.demo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cosmos.photon.push.PhotonPushManager;
import com.cosmos.photon.push.PushMessageReceiver;
import com.cosmos.photon.push.log.LogUtil;
import com.cosmos.photon.push.msg.MoMessage;
import com.cosmos.photon.push.notification.MoNotify;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyApplication extends Application {

    private static SharedPreferences preferences;

    public static final String APP_ID = "26e61d33cefc4e2cab629715b6aa260f";

    private static String token;
    private boolean showNotifyFromMe = false;//业务方自己控制通知展示

    @Override
    public void onCreate() {
        super.onCreate();

        // open debug log
        {
            LogUtil.init(this, new File(getFilesDir(), "MDLOG.log").getAbsolutePath());
            LogUtil.setLogOpen(true);
        }

        preferences = getSharedPreferences("demo_prefs", Context.MODE_MULTI_PROCESS);
        PhotonPushManager.CHANNEL_MODE = true;


        // init sdk
        PhotonPushManager.getInstance().init(this, APP_ID, new PushMessageReceiver() {

            @Override
            public boolean onNotificationShow(MoNotify notify) {
                if (showNotifyFromMe) {
                    NotifyHelper.sendNotifi(MyApplication.this, notify);
                    return true;
                }
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
                Log.i(MyApplication.class.getSimpleName(), String.format("onCommand type=%d result=%d message=%s", type, result, message));
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

            @Override
            public boolean isMeizuPushOpen() {
                return true;
            }
        });

        String currentUserId = getUserId();

        if (!TextUtils.isEmpty(currentUserId)) {
            PhotonPushManager.getInstance().registerWithAlias(currentUserId);
        }
        createMSGChannel(this);
        createDefaultChannel(this);
        createOtherChannel(this);

    }

    public static final String NOTIFICATION_CHANNEL_ID_DEFAULT = "notification.default";
    public static final String NOTIFICATION_CHANNEL_ID_MSG = "notification.msg";
    public static final String NOTIFICATION_CHANNEL_ID_OTHERS = "notification.others";

    void createDefaultChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = nm.getNotificationChannel(NOTIFICATION_CHANNEL_ID_DEFAULT);
        if (null == notificationChannel) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_DEFAULT, "默认通知", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("消息推送默认通知类别");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{50, 100});

            nm.createNotificationChannel(notificationChannel);
        }
        logChannelInfo(notificationChannel);
    }

    private void logChannelInfo(NotificationChannel notificationChannel) {
        CharSequence name = notificationChannel.getName();
        int importance = notificationChannel.getImportance();
        Uri sound = notificationChannel.getSound();
        long[] vibrationPattern = notificationChannel.getVibrationPattern();
        boolean shouldVibrate = notificationChannel.shouldVibrate();
        Log.i("channel config", "name:" + name);
        Log.i("channel config", "importance:" + importance);
        Log.i("channel config", "sound:" + sound);
        if (vibrationPattern != null && vibrationPattern.length != 0) {
            for (long l : vibrationPattern) {
                Log.i("channel config", "vibrationPattern:" + l);
            }
        }
        Log.i("channel config", "shouldVibrate:" + shouldVibrate);
    }

    void createMSGChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = nm.getNotificationChannel(NOTIFICATION_CHANNEL_ID_MSG);
        if (null == notificationChannel) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_MSG, "新消息通知", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("收到新消息时使用的通知类别");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{50, 100});

            notificationChannel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ms2), new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build());


            nm.createNotificationChannel(notificationChannel);
        }
        logChannelInfo(notificationChannel);
    }

    void createOtherChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = nm.getNotificationChannel(NOTIFICATION_CHANNEL_ID_OTHERS);
        if (null == notificationChannel) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_OTHERS, "其他通知", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("其他不重要消息的通知类别");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);

            nm.createNotificationChannel(notificationChannel);
        }
        logChannelInfo(notificationChannel);
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
