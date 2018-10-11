/*******************************************************************************
 * Copyright (c) 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Mike Robertson - initial contribution
 *******************************************************************************/
package com.ibm.iot.android.iotstarter.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.activities.MainActivity;
import com.ibm.iot.android.iotstarter.utils.Constants;
import com.ibm.iot.android.iotstarter.utils.LocationUtils;
import com.ibm.iot.android.iotstarter.utils.MessageFactory;
import com.ibm.iot.android.iotstarter.utils.MqttHandler;
import com.ibm.iot.android.iotstarter.utils.TopicFactory;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * The IoT Fragment is the main fragment of the application that will be displayed while the device is connected
 * to IoT. From this fragment, users can send text event messages. Users can also see the number
 * of messages the device has published and received while connected.
 */
public class IoTFragment extends IoTStarterFragment {
    private final static String TAG = IoTFragment.class.getName();
    private static final int REQ_CODE_SPEECH_INPUT = 100;




    /**************************************************************************
     * Fragment functions for establishing the fragment
     **************************************************************************/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.iot, container, false);
    }

    /**
     * Called when the fragment is resumed.
     */
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        app = (IoTStarterApplication) getActivity().getApplication();
        app.setCurrentRunningActivity(TAG);

        if (broadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering iotBroadcastReceiver");
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for iotBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_IOT));

        // initialise
        initializeIoTActivity();
    }

    /**
     * Called when the fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        try {
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException iae) {
            // Do nothing
        }
        super.onDestroy();
    }

    /**
     * Initializing onscreen elements and shared properties
     */
    private void initializeIoTActivity() {
        Log.d(TAG, ".initializeIoTFragment() entered");

        context = getActivity().getApplicationContext();

        updateViewStrings();

        // setup button listeners
        Button button = (Button) getActivity().findViewById(R.id.sendText);

        ImageButton voiceResponse = (ImageButton) getActivity().findViewById((R.id.get_voice_command));

        voiceResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });



    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to   startResolutionForResult().
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("debugme", "SPEECH RESULT = " + result.get(0));
                    String text = result.get(0);
                    if (text.equalsIgnoreCase("Enter receipt"))
                    {

                    }
                    Toast.makeText(context, text, Toast.LENGTH_LONG);

                    if (true) {
                        if (text.equalsIgnoreCase("help"))
                        {
                            app.engine.speak("You can say send text to PERSON and then the actual text message", TextToSpeech.QUEUE_ADD, null, null);

                        }
                        else
                        if (text.toUpperCase().startsWith("SEND TEXT TO RICARDO") && text.length() > 20)
                            sendSMS("7037282830", text.substring(21));
                        else
                        if (text.toUpperCase().startsWith("SEND TEXT TO KEVIN") && text.length() > 18)
                            sendSMS("7034083959", text.substring(19));
                        else
                        if (text.toUpperCase().startsWith("SEND TEXT TO ROSA") && text.length() > 17)
                            sendSMS("7034080934", text.substring(18));
                        else
                        if (text.toUpperCase().startsWith("SEND TEXT TO ANDREW") && text.length() > 19)
                            sendSMS("7032327433", text.substring(20));
                        else
                        if (text.toUpperCase().startsWith("SEND TEXT TO RYAN") && text.length() > 17)
                            sendSMS("7034087246", text.substring(18));
                        else
                            app.engine.speak("Unrecognized command", TextToSpeech.QUEUE_ADD, null, null);

                    }

                }
                break;
        }
    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(context, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a) {

        }
    }

    /**
     * Update strings in the fragment based on IoTStarterApplication values.
     */

    @Override
    protected void updateViewStrings() {
        Log.d(TAG, ".updateViewStrings() entered");
        // DeviceId should never be null at this point.

        if (app.getDeviceId() != null) {
            String username = "";
            //((TextView) getActivity().findViewById(R.id.deviceIDIoT)).setText(app.getDeviceId());
            try {
                if (app.appUser != null)
                    username = app.appUser.getString("username");
            } catch (Exception e)
            {}

            ((TextView) getActivity().findViewById(R.id.deviceIDIoT)).setText(username);

        } else {
            ((TextView) getActivity().findViewById(R.id.deviceIDIoT)).setText("-");
        }
        if (app.getCurrentLocation() != null && (TextView) getActivity().findViewById(R.id.lat) != null) {
            ((TextView) getActivity().findViewById(R.id.lat)).setText("lat: " + app.getCurrentLocation().getLatitude());
            ((TextView) getActivity().findViewById(R.id.lon)).setText("lon: " + app.getCurrentLocation().getLongitude());
        }


        // Update publish count view.
        processPublishIntent();

        // Update receive count view.
        processReceiveIntent();

        int unreadCount = app.getUnreadCount();
        ((MainActivity) getActivity()).updateBadge(getActivity().getActionBar().getTabAt(2), unreadCount);
    }

    /**************************************************************************
     * Functions to handle button presses
     **************************************************************************/

    /**
     * Handle pressing of the send text button. Prompt the user to enter text
     * to send.
     */
    private void handleSendText() {
        Log.d(TAG, ".handleSendText() entered");
        if (app.getConnectionType() != Constants.ConnectionType.QUICKSTART) {
            final EditText input = new EditText(context);
            input.setTextColor(Color.WHITE);
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.send_text_title))
                    .setMessage(getResources().getString(R.string.send_text_text))
                    .setView(input)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Editable value = input.getText();
                            String messageData = MessageFactory.getTextMessage(value.toString());
                            MqttHandler mqtt = MqttHandler.getInstance(context);
                            mqtt.publish(TopicFactory.getEventTopic(Constants.TEXT_EVENT), messageData, false, 0);
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do nothing.
                }
            }).show();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.send_text_title))
                    .setMessage(getResources().getString(R.string.send_text_invalid))
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        }
    }

    /**************************************************************************
     * Functions to process intent broadcasts from other classes
     **************************************************************************/

    /**
     * Process the incoming intent broadcast.
     * @param intent The intent which was received by the fragment.
     */
    private void processIntent(Intent intent) {
        Log.d(TAG, ".processIntent() entered");

        // No matter the intent, update log button based on app.unreadCount.
        updateViewStrings();

        String data = intent.getStringExtra(Constants.INTENT_DATA);
        assert data != null;
        if (data.equals(Constants.INTENT_DATA_PUBLISHED)) {
            processPublishIntent();
        } else if (data.equals(Constants.INTENT_DATA_RECEIVED)) {
            processReceiveIntent();
        } else if (data.equals(Constants.ACCEL_EVENT)) {
            processAccelEvent();
        } else if (data.equals(Constants.COLOR_EVENT)) {
            Log.d(TAG, "Updating background color");
            getView().setBackgroundColor(app.getColor());
        } else if (data.equals(Constants.POSITION_EVENT)) {
            Log.d(TAG, "Received Position Event");
            getView().setBackgroundColor(app.getColor());
        } else if (data.equals(Constants.ALERT_EVENT)) {
            String message = intent.getStringExtra(Constants.INTENT_DATA_MESSAGE);
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.alert_dialog_title))
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        }
    }

    /**
     * Intent data contained INTENT_DATA_PUBLISH
     * Update the published messages view based on app.getPublishCount()
     */
    private void processPublishIntent() {
        Log.v(TAG, ".processPublishIntent() entered");
        String publishedString = this.getString(R.string.messages_published);
        publishedString = publishedString.replace("0",Integer.toString(app.getPublishCount()));
        ((TextView) getActivity().findViewById(R.id.messagesPublishedView)).setText(publishedString);
    }

    /**
     * Intent data contained INTENT_DATA_RECEIVE
     * Update the received messages view based on app.getReceiveCount();
     */
    private void processReceiveIntent() {
        Log.v(TAG, ".processReceiveIntent() entered");
        String receivedString = this.getString(R.string.messages_received);
        receivedString = receivedString.replace("0",Integer.toString(app.getReceiveCount()));
        ((TextView) getActivity().findViewById(R.id.messagesReceivedView)).setText(receivedString);
    }

    /**
     * Update acceleration view strings
     */
    private void processAccelEvent() {
        Log.v(TAG, ".processAccelEvent()");
        float[] accelData = app.getAccelData();
        ((TextView) getActivity().findViewById(R.id.accelX)).setText("x: " + accelData[0]);
        ((TextView) getActivity().findViewById(R.id.accelY)).setText("y: " + accelData[1]);
        ((TextView) getActivity().findViewById(R.id.accelZ)).setText("z: " + accelData[2]);
    }
}
