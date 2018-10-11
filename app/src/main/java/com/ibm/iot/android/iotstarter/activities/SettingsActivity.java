package com.ibm.iot.android.iotstarter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.RestTask;
import com.ibm.iot.android.iotstarter.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;

public class SettingsActivity extends Activity {
    private final static String TAG = SettingsActivity.class.getName();
    protected IoTStarterApplication app;
    int speakerVolume = 0;

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
            final EditText searchKeywords = (EditText) findViewById(R.id.search_key_words);
            final SeekBar speakerVolume = (SeekBar) findViewById(R.id.seekBar);
            speakerVolume.setOnSeekBarChangeListener(seekBarChangeListener);

            if (app != null) {
                HashMap hash = app.getSettings();

                if (hash != null) {
                    couponAlertDistance.setText(hash.get("coupon_alert_distance") + "");
                    dealAlertDistance.setText(hash.get("deal_alert_distance") + "");
                    maxDealLength.setText(hash.get("max_deal_length") + "");
                    localBusinessSearchRadius.setText(hash.get("local_business_search_radius") + "");
                    speakerVolume.setProgress(Utility.parseInt(hash.get("speaker_volume") + ""));
                }
            }
            searchKeywords.setText(app.appUser.getString("search_key_words"));

            Button button = (Button) findViewById(R.id.save_settings);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        app.appUser.put("search_key_words", searchKeywords.getText().toString());
                        // SAVE THING TO DATABASE
                        Utility.saveUser(v.getContext(), app);
/*
                        String url = "https://new-node-red-demo-kad.mybluemix.net/save?object_name=object_one";
                        try {
                            Log.d("debugme", "Trying to save user - " + app.appUser);

                            Utility.callRESTAPI(v.getContext(), url, "post", "XXX", app.appUser.toString());
                        }
                        catch (Exception e)
                        {
                            Log.e("debugme","Problem saving User record to database", e);
                        }
                        */
                        if (app.saveSettings(Utility.parseInt(couponAlertDistance.getText().toString()), Utility.parseInt(dealAlertDistance.getText().toString()), Utility.parseInt( maxDealLength.getText().toString()), Utility.parseInt(localBusinessSearchRadius.getText().toString()), speakerVolume.getProgress())) {
                            app.engine.speak("Testing Volume", TextToSpeech.QUEUE_ADD, null, null);

                            finish();
                            Log.d(TAG, "KAD saved");
                        }
                        else
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
                            dialog.setMessage("Please enter values in all fields");
                            //dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            //    @Override
                            //   public void onClick(DialogInterface dialog, int which) {
                            //this will navigate user to the device location settings screen
                            //        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            //        startActivity(intent);
                            //    }
                            //});
                            AlertDialog alert = dialog.create();
                            alert.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "KAD CRASH", e);
                    }
                    //System.out.println("You have inserted the document");
                }
            });
            Button resetSettingsButton = (Button) findViewById(R.id.reset_settings);
            resetSettingsButton.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        couponAlertDistance.setText("200");
                        dealAlertDistance.setText("200");
                        maxDealLength.setText("30");
                        localBusinessSearchRadius.setText("400");
                        speakerVolume.setProgress(7);
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
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            speakerVolume = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

}
