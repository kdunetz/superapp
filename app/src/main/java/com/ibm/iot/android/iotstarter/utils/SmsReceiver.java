package com.ibm.iot.android.iotstarter.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by kevindunetz on 5/23/18.
 */

public class SmsReceiver extends BroadcastReceiver {

    //interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        String format = data.getString("format");

        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i], format);

            if (smsMessage != null) {
                String sender = smsMessage.getDisplayOriginatingAddress();
                //Check the sender to filter messages which we require to read
                Log.d("debugme", "in here received sms " + smsMessage.getMessageBody());

                String messageBody = smsMessage.getMessageBody();
                if (sender.equals("7034083959") || sender.equals("7034080934") || sender.equals("7032327433") || sender.equals("7034087246"))
                //Pass the message text to interface
                    mListener.messageReceived(messageBody);
            }
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
