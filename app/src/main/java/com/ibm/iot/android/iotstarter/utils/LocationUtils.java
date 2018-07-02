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

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ibm.iot.android.iotstarter.fragments.IoTStarterFragment;

import android.content.IntentSender;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Iterator;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * LocationUtils enables and disables location services so that the application can publish latitude
 * and longitude data.
 */
public class LocationUtils extends Activity implements LocationListener {
    private final static String TAG = LocationUtils.class.getName();

    private static LocationUtils instance;
    private IoTStarterApplication app;
    private LocationManager locationManager;
    private Location lastLocation;
    private double cumulativeDistance = 0;
    private Context context;
    private Criteria criteria;
    private Vector v = null;
    private boolean lastSpoke = false;
    private boolean lastSpoke1 = false;
    private boolean lastSpoke2 = false;
    private Hashtable lastSpoken = new Hashtable();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    private int locationChangedCounter = 0;
    private static final String ACTION_FOR_GET_PEOPLE = "DATA_RESULT_FOR_GET_PEOPLE";


    private LocationUtils(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.criteria = getCriteria();
        this.app = (IoTStarterApplication) context.getApplicationContext();
    }

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    public static LocationUtils getInstance(Context context) {
        if (instance == null) {

            instance = new LocationUtils(context);
            instance.startTimer();
        }
        return instance;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                    }
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * Enable location services
     */
    public void connect() {
        Log.d(TAG, ".connect() entered");
//KAD not sure where to put this thing...May 31, 2018


        // Check if location provider is enabled
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (locationManager.isProviderEnabled(locationProvider) == false) {
            Log.d(TAG, "Location provider not enabled.");
            //app.setCurrentLocation(null);
            // KAD removed Sep 30 2017 app.setCurrentLocation(locationManager.getLastKnownLocation(locationProvider));
            // KAD need to put back in June 25 2018 app.setCurrentLocation(locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER));
            return;
        }
        Log.d(TAG, "Made it here");
        // register for location updates
        String bestProvider = locationManager.getBestProvider(criteria, false);


        //KAD NOT SURE IF THIS NEXT SECTION OF CODE IS NEEDED ANYMORE...I MOVED SOME OF THIS EARLIER
        if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d("debugme", "in here");
            if (ContextCompat.checkSelfPermission(this.context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("debugme", "in here too");
                //requestAccessFineLocationPermission();
//               ActivityCompat.requestPermissions(instance, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        PERMISSION_ACCESS_FINE_LOCATION);
            }
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            locationManager.requestLocationUpdates(locationProvider, Constants.LOCATION_MIN_TIME, Constants.LOCATION_MIN_DISTANCE, this);

            return;
        }
        locationManager.requestLocationUpdates(locationProvider, Constants.LOCATION_MIN_TIME, Constants.LOCATION_MIN_DISTANCE, this);

        app.setCurrentLocation(locationManager.getLastKnownLocation(locationProvider));

    }

    private void requestAccessFineLocationPermission() {
        if (false) { //shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            //new IoTStarterFragment().ConfirmationDialog().show(this, FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Disable location services
     */
    public void disconnect() {
        Log.d(TAG, ".disconnect() entered");

        String locationProvider = LocationManager.GPS_PROVIDER;
        if (locationManager.isProviderEnabled(locationProvider)) {
            locationManager.removeUpdates(this);
        }
    }

    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

    public LatLng locationToLatLng(Location location) {

        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        return latLng;
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, ".onLocationChanged() entered");
        double distanceMoved = 0.0;
        lastLocation = location;

        distanceMoved = distanceBetween(locationToLatLng(location), locationToLatLng(lastLocation));
        cumulativeDistance += distanceMoved;

        //app.getMessageLog().add("Moved");

        //publish location details
        app.setCurrentLocation(location);
        final GoogleMap googleMap = app.getGoogleMap();


        locationChangedCounter++;  // I only want to do some things every 5 or so location changes
Log.d("debugme", locationChangedCounter + "");

        /* initialize my latest position on first try..then do every 50 times I move */
        if (locationChangedCounter < 2 || locationChangedCounter % 50 == 0) {
            String url = "https://new-node-red-demo-kad.mybluemix.net/save?object_name=object_one";
            try {
                app.appUser.put("latitude", location.getLatitude() + "");
                app.appUser.put("longitude", location.getLongitude() + "");
                Log.d("debugme", "Trying to save current location for user - " + app.appUser);

                Utility.callRESTAPI(context, url, "post", "XXX", app.appUser.toString());
            } catch (Exception e)
            {
                Log.e("debugme", "Problem saving user location", e);
                e.printStackTrace();
            }

        }
        if (googleMap != null && (locationChangedCounter < 3 || locationChangedCounter % 50 == 0)) {
            Utility.getPeopleInArea(this, app, location);
        }

        if (locationChangedCounter == 1 || locationChangedCounter % 50 == 0) {

            Utility.loadLocalBusinesses(this, app, location);
            Log.d("debugme", "Getting localBusinesses - " + app.localBusinesses);
        }

        Marker now = app.getMapMarker(app.getCurrentUser());

        if (now != null) {
            now.remove();

        }

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);

        try {
            for (int x = 0; x < app.dealLocations.size(); x++) {
                Deal deal = (Deal) app.dealLocations.elementAt(x);
                LatLng dealLocation5 = new LatLng(deal.getLatitude(), deal.getLongitude());
                Double dist5 = distanceBetween(latLng, dealLocation5);
                String message = "Found a deal KAD - " + x +
                        "," + dist5 + "," + deal.getDeal() + "," + longitude;

                Log.d(TAG, message);
                //app.getMessageLog().add(message);
                if (dist5 < app.dealDistance && !deal.getLastSpoke()) { // 200
                    app.getMessageLog().add("Entering Playing " + deal.getDeal());
                    if (deal.playOnFrontSide() && deal.getDeal().length() < app.maxDealLength)
                        app.engine.speak(deal.getDeal(), TextToSpeech.QUEUE_ADD, null, null);
                    deal.setLastSpoke(true);
                }
                if (dist5 > app.dealDistance && deal.getLastSpoke()) { //200
                    app.getMessageLog().add("Leaving Playing " + deal.getDeal());
                    if (deal.playOnBackSide() && deal.getDeal().length() < app.maxDealLength)
                        app.engine.speak(deal.getDeal(), TextToSpeech.QUEUE_ADD, null, null);
                    deal.setLastSpoke(false);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "CRASHED");
            e.printStackTrace();
        }

        try {
            for (int y = 0; y < app.dealLocations.size(); y++)
            {
                Deal deal = (Deal) app.dealLocations.elementAt(y);

                if (!app.appUser.getString("username").equals(deal.getUserName()))
                    continue;

                String days = deal.getCouponExpirationDays();
                String date = deal.getCreationDate();
                String companyName = deal.getCompanyName();
                Log.d("debugme","Checking deals for " + companyName);
                if (Utility.couponActive(date, days)) {

Log.d("debugme", "Found deal for " + companyName);
                    for (int x = 0; x < app.localBusinesses.length(); x++) {
                        Log.d("debugme", "in localBusinesses");
                        JSONObject jsonObject = (JSONObject) app.localBusinesses.getJSONObject(x);
                        try {
                            Log.d("debugme", "Found deal 1");
                            if (jsonObject.isNull("latitude") || jsonObject.isNull(("longitude")))
                                continue;
                            Log.d("debugme", "Found deal 2 " + jsonObject.toString());
                                Log.d("debugme", "Found deal 3 " + jsonObject.toString());

                            String name = jsonObject.getString("name").toString();
                            Log.d("debugme", "Found deal 4");
                            Log.d("debugme123", "Found deal before if statements :" + name + "," + companyName + "," + deal.getUserName() + "," + app.appUser.getString("username"));

                            if (!name.equals(companyName)) continue;

                            String lat = jsonObject.getString("latitude");
                            String lon = jsonObject.getString("longitude");

                            LatLng dealLocation5 = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
                            Double dist = distanceBetween(latLng, dealLocation5);

                            Log.d("debugme123", "Found deal before if statements :" + name + "," + companyName + "," + deal.getUserName() + "," + app.appUser.getString("username"));

                            String message = "Looking for close " + name + " - " + x +
                                    "," + dist + "," + "," + latitude + "," + longitude;

                            Log.d("debugme123", message);
                            //app.getMessageLog().add(message);

                            /* This following code block tries to track the actual restaurant as you pass by....but hard to do since I refresh
                            the array every so often. Thought of trying to preserve elements in the array but in the end decided to just track at
                            the deal level.

                             */
if (false) {
    if (dist < app.couponAlertDistanceMeters && (!jsonObject.has("last_spoke") || jsonObject.get("last_spoke").toString().equals("false"))) {
        app.engine.speak(name + " near kevin. You have a coupon", TextToSpeech.QUEUE_FLUSH, null, null);
        Log.d("debugme123", "IN HERE");
        jsonObject.put("last_spoke", "true");
    }
    if (dist >= app.couponAlertDistanceMeters && (jsonObject.has("last_spoke") && jsonObject.get("last_spoke").toString().equals("true"))) {

        app.engine.speak(name + " near kevin. You have a coupon", TextToSpeech.QUEUE_FLUSH, null, null);
        jsonObject.put("last_spoke", "false");
    }
}
Log.d("debugme123", "BEFORE dist = " + dist + "," + app.couponAlertDistanceMeters + ",near " + deal.toString());
Log.d("debugme123", lastSpoken.toString() + " --- " + lastSpoken.containsKey(deal.getID()));

                            Log.d("debugme1234", lastSpoken.toString() + " --- " + lastSpoken.containsKey(deal.getID()));

                            if (dist < app.couponAlertDistanceMeters && !lastSpoken.containsKey(deal.getID()+lat+lon)) {
                                Log.d("debugme12345", "BEFORE x = " + x + ", dist = " + dist + "," + app.couponAlertDistanceMeters + ",near " + deal.toString());

                                app.engine.speak(name + " near. You have a coupon", TextToSpeech.QUEUE_ADD, null, null);
                                Log.d("debugme1234", "x = " + x + ",IN HERE" + deal.getDeal());
                                //deal.setLastSpoke(true);
                                lastSpoken.put(deal.getID()+lat+lon, "true");
                            }
                            Log.d("debugme1234", lastSpoken.toString());
                            Log.d("debugme1234", "AFTER dist = " + dist + ",near " + deal.toString());

                            if (dist >= app.couponAlertDistanceMeters && lastSpoken.containsKey(deal.getID()+lat+lon)) {
                                app.engine.speak(name + " near leaving. You have a coupon", TextToSpeech.QUEUE_ADD, null, null);
                                Log.d("debugme1234", "x = " + x + ", IN HERE LEAVING " + deal.getDeal());
                                //deal.setLastSpoke(false);
                                lastSpoken.remove(deal.getID()+lat+lon);
                            }
                        } catch (Exception e)
                        {
                            Log.e("debugme1234", "Found deal -....object values not all there", e);
                        }
                    }
                }

            }
        } catch (Exception e) {
            Log.e("debugme123", "Having problems finding local Fast Food", e);
            e.printStackTrace();
        }

        if (googleMap != null) {
            Log.d("BLAHBLAH", "IN HERE googleMap != null");
            Log.d("debugme", "before changing bitmap LocationUtils");

            BitmapDescriptor newBitmap = null;
            //BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker);
            if (app.getCurrentUser() != null && app.getCurrentUser().equalsIgnoreCase("kevindunetz@gmail.com"))
                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.kevindunetz);
            else if (app.getCurrentUser() != null && app.getCurrentUser().equalsIgnoreCase("ryandunetz@gmail.com"))
                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ryandunetz);
            else if (app.getCurrentUser() != null && app.getCurrentUser().equalsIgnoreCase("andrewdunetz@gmail.com"))
                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.andrewdunetz);
            else if (app.getCurrentUser() != null && app.getCurrentUser().equalsIgnoreCase("rosadunetz@gmail.com"))
                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.rosadunetz);

            now = googleMap.addMarker(new MarkerOptions().icon(newBitmap).position(latLng).title(app.getCurrentUser()));
            app.addMapMarker(app.getCurrentUser(), now);
            // Showing the current location in Google Map
            // KAD temp googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            // KAD took out annoying zoom googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
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
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 5);   //25, 25, 5);
                googleMap.animateCamera(cu);
            }

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, ".onStatusChanged() entered");

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, ".onProviderEnabled() entered");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, ".onProviderDisabled() entered");

    }

    /**
     * Helper method to create a criteria for location change listener
     *
     * @return criteria constructed for the listener
     */
    public Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(false);
        return criteria;
    }

    Timer timer;
    TimerTask timerTask;



        //we are going to use a handler to be able to run in our TimerTask

    final Handler handler = new Handler();


    public void startTimer() {

        //set a new Timer

        timer = new Timer();

        //initialize the TimerTask's job

        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms

        timer.schedule(timerTask, 1000, 4000); //

    }
    public void stoptimertask() {

        //stop the timer, if it's not already null

        if (timer != null) {

            timer.cancel();
            timer = null;

        }

    }

        public void initializeTimerTask() {

        timerTask = new TimerTask() {

            public void run() {

                //use a handler to run a toast that shows the current timestamp

                handler.post(new Runnable() {

                    public void run() {
                        Log.d("debugme","Timer went off");
onLocationChanged(app.getCurrentLocation());
                    }

                });

            }

        };

    }


}