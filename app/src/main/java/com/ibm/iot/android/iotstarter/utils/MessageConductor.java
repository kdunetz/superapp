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
package com.ibm.iot.android.iotstarter.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.activities.*;
import com.ibm.iot.android.iotstarter.fragments.IoTFragment;
import com.ibm.iot.android.iotstarter.fragments.LogFragment;
import com.ibm.iot.android.iotstarter.fragments.LoginFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Steer incoming MQTT messages to the proper activities based on their content.
 */
public class MessageConductor {

    private final static String TAG = MessageConductor.class.getName();
    private static MessageConductor instance;
    private Context context;
    private IoTStarterApplication app;
    private TextToSpeech engine = null;

    private MessageConductor(Context context) {
        this.context = context;
        app = (IoTStarterApplication) context.getApplicationContext();
    }

    public static MessageConductor getInstance(Context context) {
        if (instance == null) {
            instance = new MessageConductor(context);
        }
        return instance;
    }

    /**
     * Steer incoming MQTT messages to the proper activities based on their content.
     *
     * @param payload The log of the MQTT message.
     * @param topic The topic the MQTT message was received on.
     * @throws JSONException If the message contains invalid JSON.
     */
    public void steerMessage(String payload, String topic) throws JSONException {
        Log.d(TAG, ".steerMessage() entered");
        JSONObject top = new JSONObject(payload);
        JSONObject d = top.getJSONObject("d");

        if (topic.contains(Constants.COLOR_EVENT)) {
            Log.d(TAG, "Color Event");
            int r = d.getInt("r");
            int g = d.getInt("g");
            int b = d.getInt("b");
            // alpha value received is 0.0 < a < 1.0 but Color.agrb expects 0 < a < 255
            int alpha = (int)(d.getDouble("alpha")*255.0);
            if ((r > 255 || r < 0) ||
                    (g > 255 || g < 0) ||
                    (b > 255 || b < 0) ||
                    (alpha > 255 || alpha < 0)) {
                return;
            }

            app.setColor(Color.argb(alpha, r, g, b));

            String runningActivity = app.getCurrentRunningActivity();
            if (runningActivity != null && runningActivity.equals(IoTFragment.class.getName())) {
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.COLOR_EVENT);
                context.sendBroadcast(actionIntent);
            }
        } else if (topic.contains(Constants.LIGHT_EVENT)) {
            app.handleLightMessage();
        } else if (topic.contains(Constants.CRASH_EVENT)) {
            String dummy = null;
            Log.i("", dummy.toString());
        } else if (topic.contains(Constants.POSITION_EVENT)) {
            //app.getMessageLog().add(d.getString("user"));
            String user = d.getString("user");
            /* Only process these messages for other people's events */
            if (!user.equals(app.getCurrentUser())) {
                GoogleMap googleMap = app.getGoogleMap();
                Marker now = app.getMapMarker(user);

                if (now != null) {
                    now.remove();

                }

                // Getting latitude of the current location
                double latitude = d.getDouble("lat");

                // Getting longitude of the current location
                double longitude = d.getDouble("lon");

                // Creating a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);
                BitmapDescriptor newBitmap = null;
                if (googleMap != null) {
                    if (user.equals("kevindunetz@gmail.com")) {
                        newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.kevindunetz);
                    } else if (user.equalsIgnoreCase("ryandunetz@gmail.com")) {
                        newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ryandunetz);
                    } else if (user.equalsIgnoreCase("andrewdunetz@gmail.com")) {
                        newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.andrewdunetz);
                    } else if (user.equalsIgnoreCase("rosadunetz@gmail.com")) {
                        newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.rosadunetz);
                    } else {
                        newBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_logo_b);
                    }

                    now = googleMap.addMarker(new MarkerOptions().icon(newBitmap).position(latLng).title(user));

                    app.addMapMarker(user, now);
                    // Showing the current location in Google Map
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Zoom in the Google Map
                    //googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                if (false) {
                    //Calculate the markers to get their position
                    LatLngBounds.Builder b = new LatLngBounds.Builder();
                    HashMap<String, Marker> markers = app.getMapMarkers();
                    Iterator<Marker> x = markers.values().iterator();
                    while (x.hasNext()) {
                        Log.d(TAG, "POOPOO - IN HERE");
                        Marker m = x.next();
                        b.include(m.getPosition());
                    }
                    LatLngBounds bounds = b.build();
//Change the padding as per needed
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);   //25, 25, 5);
                    googleMap.animateCamera(cu);

                }
            }
        }

        } else if (topic.contains(Constants.TEXT_EVENT)) {
            int unreadCount = app.getUnreadCount();
            app.setUnreadCount(++unreadCount);

            // save payload in an arrayList
            List messageRecvd = new ArrayList<String>();
            messageRecvd.add(payload);

            app.getMessageLog().add(d.getString("text"));

            String runningActivity = app.getCurrentRunningActivity();
            if (runningActivity != null && runningActivity.equals(LogFragment.class.getName())) {
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.TEXT_EVENT);
                context.sendBroadcast(actionIntent);
            }

            Intent unreadIntent;
            if (runningActivity.equals(LogFragment.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
            } else if (runningActivity.equals(LoginFragment.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
            } else if (runningActivity.equals(IoTFragment.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            } else if (runningActivity.equals(ProfilesActivity.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_PROFILES);
            } else {
                return;
            }

            String messageText = d.getString("text");
            if (messageText != null) {
                unreadIntent.putExtra(Constants.INTENT_DATA, Constants.UNREAD_EVENT);
                context.sendBroadcast(unreadIntent);
            }
        } else if (topic.contains(Constants.ALERT_EVENT)) {
            // save payload in an arrayList
            int unreadCount = app.getUnreadCount();
            app.setUnreadCount(++unreadCount);

            List messageRecvd = new ArrayList<String>();
            messageRecvd.add(payload);

            app.getMessageLog().add(d.getString("text"));
            app.engine.speak(d.getString("text"), TextToSpeech.QUEUE_FLUSH, null, null);

            // KAD speak it

            String runningActivity = app.getCurrentRunningActivity();
            if (runningActivity != null) {
                if (runningActivity.equals(LogFragment.class.getName())) {
                    Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
                    actionIntent.putExtra(Constants.INTENT_DATA, Constants.TEXT_EVENT);
                    context.sendBroadcast(actionIntent);
                }

                Intent alertIntent;
                if (runningActivity.equals(LogFragment.class.getName())) {
                    alertIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
                } else if (runningActivity.equals(LoginFragment.class.getName())) {
                    alertIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
                } else if (runningActivity.equals(IoTFragment.class.getName())) {
                    alertIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                } else if (runningActivity.equals(ProfilesActivity.class.getName())) {
                    alertIntent = new Intent(Constants.APP_ID + Constants.INTENT_PROFILES);
                } else {
                    return;
                }

                String messageText = d.getString("text");
                if (messageText != null) {
                    alertIntent.putExtra(Constants.INTENT_DATA, Constants.ALERT_EVENT);
                    alertIntent.putExtra(Constants.INTENT_DATA_MESSAGE, d.getString("text"));
                    context.sendBroadcast(alertIntent);
                }


            }
        }
        else if (topic.contains(Constants.SPEAK_EVENT)) {
            Log.d(TAG, "Speak Event");
            try {
                String phrase = d.getString("text");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    app.engine.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } catch (Exception e) {
                Log.e(TAG, "Couldn't play Speech", e);
            }
        }
    }
}
