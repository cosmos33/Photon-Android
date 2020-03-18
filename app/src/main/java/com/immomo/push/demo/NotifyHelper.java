package com.immomo.push.demo;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.cosmos.photon.push.PhotonPushManager;
import com.cosmos.photon.push.notification.MoNotify;

import static android.content.Context.NOTIFICATION_SERVICE;

//import com.cosmos.photon.push.channel.ChannelConstant;
//import com.cosmos.photon.push.notification.NotificationBuilder;

public class NotifyHelper {
    private static NotificationManager nm = null;

    public static void sendNotifi(Context context, MoNotify moNotify) {
//        Intent intent = new Intent(context, PushClickReceiver.class);
//        intent.putExtra(ChannelConstant.Keys.KEY_PUSH_DATA, moNotify);
//
//        sendNotify(context, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher),
//                "title", "content", "tag", true, intent, moNotify.channelId, moNotify);
    }

    private static void sendNotify(Context context,
                                   Bitmap largeIcon,
                                   String title,
                                   String content,
                                   String notifyTag,
                                   boolean stayNotificationBar,
                                   Intent intent,
                                   String channelId, MoNotify moNotify) {
        if (nm == null) {
            nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
//        NotificationBuilder nb = new NotificationBuilder(context);
//
//        nb.setSound(null, null);
//
//        nb.setVibrate(new long[]{50, 100});
//        // 呼吸灯
//        nb.setLights(Color.BLUE, 500, 1500);
//
//        int iconResId = android.R.drawable.stat_notify_chat;
//
//        nb.setContentTitle(title);
//        nb.setContentText(content);
//        nb.setIcon(iconResId);
//        nb.setLargeIcon(largeIcon);
//        nb.setChannelId(channelId);
//
//        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        nb.setContentIntent(pendIntent);
//
//        Notification notification = nb.getNotification();
//        if (notification == null) {
//            return;
//        }
//        if (stayNotificationBar) {
//            notification.flags = Notification.FLAG_NO_CLEAR;
//        }
//        //尝试修复部分用户发送notify crash的问题
//        nm.notify(notifyTag, 0, notification);

        PhotonPushManager.getInstance().onPushShow(moNotify, true);
    }
}
