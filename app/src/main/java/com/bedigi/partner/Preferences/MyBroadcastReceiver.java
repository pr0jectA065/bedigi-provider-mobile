package com.bedigi.partner.Preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBroadcastReceiver extends BroadcastReceiver
{
    private String tag = "receiverclass";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(tag,"message is dfdsfdsfdsfsdfs= ");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent . getExtras ();
            Status status =(Status) extras . get (SmsRetriever.EXTRA_STATUS);
            switch(status.getStatusCode()) {
                case CommonStatusCodes. SUCCESS :
                    // Get SMS message contents
                    String message =(String) extras . get (SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.e(tag,"message is dfdsfdsfdsfsdfs= "+message);
                    String sms_str ="";
                    Pattern p = Pattern.compile("(|^)\\d{4}");
                    sms_str = message;
                    Intent smsIntent =null;
                    if(sms_str!=null)
                    {
                        Matcher m = p.matcher(sms_str);
                        if(m.find()) {
                            smsIntent = new Intent("otp");
                            smsIntent.putExtra("message",m.group(0));
                            // otp.setText(m.group(0));
                        }
                        else
                        {
                            //something went wrong
                        }



                        LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);


                    }
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server for SMS authenticity.
                    break;
                case CommonStatusCodes. TIMEOUT :
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;

            }
        }
    }
}
