package com.android.il.almadar.technician;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class MessageReceiver extends BroadcastReceiver{

    ProgressDialog progressDialog;

    public MessageReceiver(){

    }
    public MessageReceiver(Context context){


    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("EditProblem", "Inside Spinner inside receiver");
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Boolean success = intent.getBooleanExtra("success", false);
        progressDialog.dismiss();
        if (!success) {
            Toast.makeText(context, "Messaging service failed to start", Toast.LENGTH_LONG).show();
        }
    }
}
