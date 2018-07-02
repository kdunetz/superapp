package com.ibm.iot.android.iotstarter.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.RestTask;
import com.ibm.iot.android.iotstarter.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;

public class SettingsActivity extends Activity {
    private final static String TAG = SettingsActivity.class.getName();
    protected IoTStarterApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        app = (IoTStarterApplication) getApplication();

        try {

            final EditText couponAlertDistance = (EditText) findViewById(R.id.coupon_alert_distance);
            final EditText dealAlertDistance = (EditText) findViewById(R.id.deal_alert_distance);
            final EditText maxDealLength = (EditText) findViewById(R.id.max_deal_length);
            final EditText localBusinessSearchRadius = (EditText) findViewById(R.id.local_business_search_radius);


            if (app != null) {
                HashMap hash = app.getSettings();

                if (hash != null) {
                    couponAlertDistance.setText(hash.get("coupon_alert_distance") + "");
                    dealAlertDistance.setText(hash.get("deal_alert_distance") + "");
                    maxDealLength.setText(hash.get("max_deal_length") + "");
                    localBusinessSearchRadius.setText(hash.get("local_business_search_radius") + "");
                }
            }
            Button button = (Button) findViewById(R.id.save_settings);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        app.saveSettings(Utility.parseInt(couponAlertDistance.getText().toString()), Utility.parseInt(dealAlertDistance.getText().toString()), Utility.parseInt( maxDealLength.getText().toString()), Utility.parseInt(localBusinessSearchRadius.getText().toString()));
                        finish();
                        Log.d(TAG, "KAD saved");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "KAD CRASH", e);
                    }
                    //System.out.println("You have inserted the document");
                }
            });

        } catch (Exception e) {
            Log.e("debugme", "Problem populating the EditView", e);
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();

    }

}
