package com.android.il.almadar.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.il.almadar.EditMaintenanceActivity;
import com.android.il.almadar.R;
import com.android.il.almadar.admin.EditNewProblemActivity;
import com.android.il.almadar.technician.EditProblemActivity;

import java.util.List;

/**
 * Created by isra  on 3/22/2016.
 */
public class NotificationUtils {

    private String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils() {
    }

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, Intent intent, String objectId, String type) {

        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        if (isAppIsInBackground(mContext)) {
            // notification icon
            int icon = R.mipmap.ic_launcher;

            int smallIcon = R.mipmap.ic_launcher;

            int mNotificationId = AppConfig.NOTIFICATION_ID;

            Intent resultIntent = new Intent() ;
            switch (type){
                case "admin":
                    resultIntent = new Intent(mContext, EditNewProblemActivity.class);
                    // resultIntent.setClassName(context, "com.android.il.almadar.admin.EditNewProblemActivity");
                    resultIntent.putExtra("id", objectId);
                    break;
                case "user":
                    resultIntent = new Intent(mContext, EditMaintenanceActivity.class);
                    resultIntent.putExtra("id", objectId);
                    break;
                case "tech":
                    resultIntent = new Intent(mContext, EditProblemActivity.class);
                    resultIntent.putExtra("id", objectId);
                    break;
            }

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);
            Notification notification = mBuilder.setSmallIcon(smallIcon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(inboxStyle)
                    .setContentIntent(resultPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();
            Log.e("NotificationUtils", "We are here");
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(mNotificationId, notification);
        } else {

            Intent resultIntent = new Intent() ;
            switch (type){
                case "admin":
                    resultIntent = new Intent(mContext, EditNewProblemActivity.class);
                    // resultIntent.setClassName(context, "com.android.il.almadar.admin.EditNewProblemActivity");
                    resultIntent.putExtra("id", objectId);
                    break;
                case "user":
                    resultIntent = new Intent(mContext, EditMaintenanceActivity.class);
                    resultIntent.putExtra("id", objectId);
                    break;
                case "tech":
                    resultIntent = new Intent(mContext, EditProblemActivity.class);
                    resultIntent.putExtra("id", objectId);
                    break;
            }
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mContext.startActivity(resultIntent);
        }
    }

    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
