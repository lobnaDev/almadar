package com.android.il.almadar.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.il.almadar.EditMaintenanceActivity;
import com.android.il.almadar.admin.EditNewProblemActivity;
import com.android.il.almadar.technician.EditProblemActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by isra on 3/22/2016.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    private final String TAG = CustomPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    private Intent parseIntent;

    public CustomPushReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push received: " + json);

            parseIntent = intent;

            parsePushJson(context, json);

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    /**
     * Parses the push notification json
     *
     * @param context
     * @param json
     */
    private void parsePushJson(Context context, JSONObject json) {
        try {
            boolean isBackground = json.getBoolean("is_background");
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String from = data.getString("from");
            String objectId = data.getString("object_id");
            String type = data.getString("user_type");


            if (!isBackground) {

                Intent resultIntent = new Intent() ;
                switch (type){
                    case "admin":
                        resultIntent = new Intent(context, EditNewProblemActivity.class);
                       // resultIntent.setClassName(context, "com.android.il.almadar.admin.EditNewProblemActivity");
                        resultIntent.putExtra("id", objectId);
                        break;
                    case "user":
                        resultIntent = new Intent(context, EditMaintenanceActivity.class);
                        resultIntent.putExtra("id", objectId);
                        break;
                    case "tech":
                        resultIntent = new Intent(context, EditProblemActivity.class);
                        resultIntent.putExtra("id", objectId);
                        break;
                }

                showNotificationMessage(context, title, message, resultIntent, objectId, type);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }


    /**
     * Shows the notification message in the notification bar
     * If the app is in background, launches the app
     *
     * @param context
     * @param title
     * @param message
     * @param intent
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent, String object_id, String role) {

        notificationUtils = new NotificationUtils(context);

        intent.putExtras(parseIntent.getExtras());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent, object_id, role);
    }
}