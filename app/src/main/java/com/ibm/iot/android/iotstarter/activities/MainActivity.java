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
package com.ibm.iot.android.iotstarter.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.fragments.DrawFragment;
import com.ibm.iot.android.iotstarter.fragments.IoTFragment;
import com.ibm.iot.android.iotstarter.fragments.LogFragment;
import com.ibm.iot.android.iotstarter.fragments.LoginFragment;
import com.ibm.iot.android.iotstarter.fragments.MapFragment;
import com.ibm.iot.android.iotstarter.utils.Constants;
import android.content.Intent;

import com.ibm.iot.android.iotstarter.utils.LocationUtils;
import com.ibm.iot.android.iotstarter.utils.RestTask;
import com.ibm.iot.android.iotstarter.utils.SmsListener;
import com.ibm.iot.android.iotstarter.utils.SmsReceiver;
import com.ibm.iot.android.iotstarter.utils.Utility;
/*
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;
*/

/* KAD May 15, for Android Text to Speech */
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// IBM Watson SDK
//import com.ibm.watson.developer_cloud.android.speech_to_text.v1.dto.SpeechConfiguration;
//import com.ibm.watson.developer_cloud.android.speech_to_text.v1.ISpeechDelegate;
//import com.ibm.watson.developer_cloud.android.speech_to_text.v1.SpeechToText;
//import com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech;
//import com.ibm.watson.developer_cloud.android.speech_common.v1.TokenProvider;
/**
 * MainActivity acts as the primary activity in the application that displays
 * the fragment of the currently selected action bar tab.
 */
public class MainActivity extends Activity implements ActionBar.TabListener, OnInitListener {
    private final static String TAG = MainActivity.class.getName();
    private DrawFragment drawFragment;
    private LogFragment logFragment;
    private LoginFragment loginFragment;
    private IoTFragment iotFragment;
    private MapFragment mapFragment;

    private String BluemixMobileBackendApplication_ROUTE = "https://hybrid-backend-kad.mybluemix.net";
    private String BluemixMobileBackendApplication_App_GUID= "b7a0d118-e3fd-40c3-8f86-0af1d14553a4";
    private static final int REQ_CODE_SPEECH_INPUT = 100;


    private static final String ACTION_FOR_GET_PEOPLE = "DATA_RESULT_FOR_GET_PEOPLE";

    private ArrayList<String> backStack;

    //private MFPPush push = null;
    /* KAD added for Speech May 15, 2016 */
    private String text;
    private String song;
    private long songDuration;
    private TextView textView;
    //private Button playAgainButton;

    private MediaPlayer mPlayer;

    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    public static final String OTP_REGEX = "[0-9]{1,6}";
    private IoTStarterApplication app = null;


    /**
     * Create the MainActivity. Initialize the action bar tabs and restore activity saved state.
     * @param savedInstanceState The saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String email = "";
        try {
            Bundle b = getIntent().getExtras();
            email = b.getString("email");
            Log.d("debugme", "email = " + email);
        } catch (Exception e)
        {
            Log.e("debugme", "alkdjf", e);
            e.printStackTrace();
        }
        // Initialize all the fragments 1 time when creating the activity
        loginFragment = new LoginFragment();
        iotFragment = new IoTFragment();
        logFragment = new LogFragment();
        drawFragment = new DrawFragment();
        mapFragment = new MapFragment();

        // backStack used for overriding back button
        backStack = new ArrayList<String>();
        app = (IoTStarterApplication)getApplication();
        app.setCurrentUser(email);
        app.engine = new TextToSpeech(this, this);

        // Setup the action bar
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab loginTab = actionBar.newTab();
        loginTab.setText(Constants.LOGIN_LABEL);
        loginTab.setTabListener(this);
        actionBar.addTab(loginTab);

        ActionBar.Tab iotTab = actionBar.newTab();
        iotTab.setText(Constants.IOT_LABEL);
        iotTab.setTabListener(this);
        actionBar.addTab(iotTab);

        // The Log tab uses a custom view so that a badge value can be displayed.
        ActionBar.Tab logTab = actionBar.newTab();
        logTab.setText(Constants.LOG_LABEL);
        logTab.setTabListener(this);
        logTab.setCustomView(createLogTabView());
        actionBar.addTab(logTab);

        ActionBar.Tab mapTab = actionBar.newTab();
        mapTab.setText(Constants.MAP_LABEL);
        mapTab.setTabListener(this);
        actionBar.addTab(mapTab);

        // Set current tab based on saved state. Mainly for screen rotations when activity is recreated.
        if(savedInstanceState != null) {
            int tabIndex = savedInstanceState.getInt("tabIndex");
            getActionBar().setSelectedNavigationItem(tabIndex);
        }

        //getActionBar().setSelectedNavigationItem(1);

        setContentView(R.layout.main);
/*
        try {
            //Initialize the Core SDK
            BMSClient.getInstance().initialize(getApplicationContext(), BluemixMobileBackendApplication_ROUTE, BluemixMobileBackendApplication_App_GUID);
            System.out.println("Starting up MFP");
        } catch (MalformedURLException e) {
            System.out.println("ERROR : Initialize the Core SDK");
            e.printStackTrace();
        }




        * Use Service "hybrid-backend-kad-imfpush" to Do Push to the device *å

        //Initialize client Push SDK for Java
        MFPPush.getInstance().initialize(getApplicationContext());
        push = MFPPush.getInstance();

        MFPPush.getInstance().listen(new MFPPushNotificationListener() {
            @Override
            public void onReceive(MFPSimplePushNotification mfpSimplePushNotification) {
                // Handle push notification here
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Push notification received")
                        .setMessage(mfpSimplePushNotification.getAlert())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });
        push.register(new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String deviceId) {
                System.out.println("KEVIN Registration successful - " + deviceId);
            }

            @Override
            public void onFailure(MFPPushException ex) {
                System.out.println("IN HERE KEVIN");
                ex.printStackTrace();
            }
        });
*/
//        Intent intent = new Intent(this, LocationServicesKAD.class);
//        startActivity(intent);

        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toast.makeText(this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            Log.d("BLAHBLAH", "before locUtils.connect()");

            LocationUtils locUtils = LocationUtils.getInstance(getApplicationContext());
            locUtils.connect();
            registerReceiver(receiver, new IntentFilter(ACTION_FOR_GET_PEOPLE));

            Log.d("BLAHBLAH", "after locUtils.connect()");
        }
        // Todo Location Already on  ... end

        if(!hasGPSDevice(this)){
            Toast.makeText(this,"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Log.e("keshav","Gps already enabled");
            Toast.makeText(this,"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            Log.e("keshav","Gps already enabled");
            Toast.makeText(this,"Gps already enabled",Toast.LENGTH_SHORT).show();

        }


        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {

                //From the received text string you may do string operations to get the required OTP
                //It depends on your SMS format
                Log.e("Message",messageText);
                app.engine.speak(messageText, TextToSpeech.QUEUE_FLUSH, null, null);
                Toast.makeText(MainActivity.this,"Message: "+messageText,Toast.LENGTH_LONG).show();

                // If your OTP is six digits number, you may use the below code

                Pattern pattern = Pattern.compile(OTP_REGEX);
                Matcher matcher = pattern.matcher(messageText);
                String otp = "";
                while (matcher.find())
                {
                    otp = matcher.group();
                }

                Toast.makeText(MainActivity.this,"OTP: "+ otp ,Toast.LENGTH_LONG).show();

            }
        });

        app.playCouponNotices();


    }



    /**
     * Callback for handling when a new tab is selected. Replace fragment_container content
     * with the new fragment. In the case of IoT tab, also replace fragment_containerDraw.
     * @param tab The selected tab
     * @param fragmentTransaction The transaction containing this tab selection
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.d(TAG, ".onTabSelected() entered");

        if (tab.getText().equals(Constants.LOGIN_LABEL)) {
            fragmentTransaction.replace(R.id.fragment_container, loginFragment);
            try {
                fragmentTransaction.remove(drawFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tab.getText().equals(Constants.IOT_LABEL)) {
            fragmentTransaction.replace(R.id.fragment_container, iotFragment);
            fragmentTransaction.replace(R.id.fragment_containerDraw, drawFragment);
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tab.getText().equals(Constants.LOG_LABEL)) {
            fragmentTransaction.replace(R.id.fragment_container, logFragment);
            try {
                fragmentTransaction.remove(drawFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // If switching to log tab, reset its badge value to 0
            updateBadge(tab, 0);
        } else if (tab.getText().equals(Constants.MAP_LABEL)) {
            //Intent intent = new Intent(this, MapsActivity.class);
            //startActivity(intent);
            fragmentTransaction.replace(R.id.fragment_container, mapFragment);

            try {
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    /**
     * Keep track of tab backStack when leaving tabs.
     * @param tab The tab being left
     * @param fragmentTransaction The transaction containing this tab selection
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.d(TAG, ".onTabUnselected() entered");
        int index = backStack.size()-1;
        if (tab.getText().equals(Constants.LOGIN_LABEL)) {
            if (!backStack.isEmpty() && Constants.LOGIN_LABEL.equals(backStack.get(index))) {
                backStack.remove(index);
            } else {
                backStack.add(Constants.LOGIN_LABEL);
            }
        } else if (tab.getText().equals(Constants.IOT_LABEL)) {
            if (!backStack.isEmpty() && Constants.IOT_LABEL.equals(backStack.get(index))) {
                backStack.remove(index);
            } else {
                backStack.add(Constants.IOT_LABEL);
            }
            try {
                fragmentTransaction.remove(drawFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tab.getText().equals(Constants.LOG_LABEL)) {
            if (!backStack.isEmpty() && Constants.LOG_LABEL.equals(backStack.get(index))) {
                backStack.remove(index);
            } else {
                backStack.add(Constants.LOG_LABEL);
            }
        }
    }

    /**
     * Do nothing for now
     * @param tab The tab being selected
     * @param fragmentTransaction The transaction containing this tab selection
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.d(TAG, ".onTabReselected() entered");
    }




    /**
     * Save the current state of the activity. This is used to store the index of the currently
     * selected tab.
     * @param outState The state of the activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int tabIndex = getActionBar().getSelectedNavigationIndex();
        outState.putInt("tabIndex", tabIndex);
    }

    @Override
    protected void onResume() {
        Log.d("BLAHBLAH", "in onResume");
        super.onResume();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)   {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to   startResolutionForResult().
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //startLocationUpdates();
                        Log.d("BLAHBLAH", "IN RESULT_OK");
                        Log.d("BLAHBLAH", "before locUtils.connect()");

                        LocationUtils locUtils = LocationUtils.getInstance(getApplicationContext());
                        locUtils.connect();
                        Log.d("BLAHBLAH", "after locUtils.connect()");
                        break;
                    case Activity.RESULT_CANCELED:
                        //enableGpsSetting();
                        Log.d("BLAHBLAH", "IN RESULT_CANCELED");
                        break;

                }
                break;
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    Log.d("debugme", "SPEECH RESULT = " + text);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Maintain a custom back stack for switching to previous tabs with the back button.
     * If back stack is empty, follow default activity behavior.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, ".onBackPressed() entered");
        //super.onBackPressed();
        if (!backStack.isEmpty()) {
            int selectedIndex = getActionBar().getSelectedNavigationIndex();
            int index = backStack.size()-1;
            String tabLabel = backStack.get(index);
            backStack.remove(index);
            if (selectedIndex == 0) {
                backStack.add(Constants.LOGIN_LABEL);
            } else if (selectedIndex == 1) {
                backStack.add(Constants.IOT_LABEL);
            } else if (selectedIndex == 2) {
                backStack.add(Constants.LOG_LABEL);
            } else if (selectedIndex == 3) {
                backStack.add(Constants.MAP_LABEL);
            }
            if (Constants.LOGIN_LABEL.equals(tabLabel)) {
                getActionBar().setSelectedNavigationItem(0);
            } else if (Constants.IOT_LABEL.equals(tabLabel)) {
                getActionBar().setSelectedNavigationItem(1);
            } else if (Constants.LOG_LABEL.equals(tabLabel)) {
                getActionBar().setSelectedNavigationItem(2);
            } else if (Constants.MAP_LABEL.equals(tabLabel)) {
                getActionBar().setSelectedNavigationItem(3);
            }

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        Log.d(TAG, ".onConfigurationChanged entered()");
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Creates the custom view for the Log Tab to support a badge value
     * @return view The custom view for the tab.
     */
    public View createLogTabView() {
        FrameLayout view = (FrameLayout) this.getLayoutInflater().inflate(R.layout.log_badge, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((TextView) view.findViewById(R.id.tab_title)).setText(Constants.LOG_LABEL);
        updateBadge((TextView) view.findViewById(R.id.tab_badge), 0);
        return view;
    }

    /**
     * Update the badge value on the specified action bar tab.
     * @param tab The tab to update.
     * @param badgeNumber The new badge value.
     */
    public void updateBadge(ActionBar.Tab tab, int badgeNumber) {
        updateBadge((TextView) tab.getCustomView().findViewById(R.id.tab_badge), badgeNumber);
    }

    /**
     * Update the badge value on the specified action bar tab.
     * @param view The view to update.
     * @param badgeNumber The new badge value.
     */
    private void updateBadge(TextView view, int badgeNumber) {
        if (badgeNumber > 0) {
            view.setVisibility(View.VISIBLE);
            view.setText(Integer.toString(badgeNumber));
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "in onInit for speak");
        //com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance().synthesize("Hello There");
      //  textToSpeech.setOnUtteranceCompletedListener(this);
	//speakAndPlayMusic();
       // engine .speak("Hello there.  Now is the time for all good men to come to the aid of their country", TextToSpeech.QUEUE_FLUSH, null, null);

    }
    public URI getHost(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean initTTS() {

        // DISCLAIMER: please enter your credentials or token factory in the lines below

        String username = getString(R.string.TTSUsername);
        String password = getString(R.string.TTSPassword);
        String tokenFactoryURL = getString(R.string.defaultTokenFactory);
        String serviceURL = "https://stream.watsonplatform.net/text-to-speech/api";

      //  com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance().initWithContext(this.getHost(serviceURL));

        // token factory is the preferred authentication method (service credentials are not distributed in the client app)
        if (tokenFactoryURL.equals(getString(R.string.defaultTokenFactory)) == false) {
            //com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance().setTokenProvider(new MyTokenProvider(tokenFactoryURL));
        }
        // Basic Authentication
        else if (username.equals(getString(R.string.defaultUsername)) == false) {
        //    com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance().setCredentials(username, password);
        } else {
            // no authentication method available
            return false;
        }
   //     com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance().setLearningOptOut(false); // Change to true to opt-out

        //com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance().setVoice(getString(R.string.voiceDefault));
        return true;
    }
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1 * 1000);
            locationRequest.setFastestInterval(1 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    Log.d("BLAHBLAH","In onResult()");
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                Log.d("BLAHBLAH", " IN RESOLUTION REQUIRED");
                                status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                                Log.d("BLAHBLAH", " AFTER RESOLUTION REQUIRED");

                                //finish();


                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SUCCESS:
//NEVER GETS CALLED
                        break;
                    }
                }
            });
        }
    }
    /**
     * Our Broadcast Receiver. We get notified that the data is ready this way.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONArray jsonArray = new JSONArray();
            Log.d("debugme", "IN HERE DynamicTableActivity BroadcastReceiver");

            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            String callBack = intent.getAction();

            if (callBack.equals(ACTION_FOR_GET_PEOPLE)) {
                Log.d(TAG, "GOT ACTION_FOR_GET_PEOPLE");
                if (response.equals("GET_GLOBAL")) {
                    response = Utility.getGlobalStr();
                }
                try {
                    jsonArray = new JSONArray(response);
                    GoogleMap googleMap = app.getGoogleMap();

                    for (int x = 0; x < jsonArray.length(); x++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                        //Marker now = app.getMapMarker(app.getCurrentUser());

                        //if (now != null) {
                        //    now.remove();
                        //}

                        // Getting latitude of the current location
                        double latitude = Double.parseDouble(jsonObject.get("latitude").toString());

                        // Getting longitude of the current location
                        double longitude = Double.parseDouble(jsonObject.get("longitude").toString());
                        String title = jsonObject.get("name").toString();

                        LatLng latLng = new LatLng(latitude, longitude);
                        BitmapDescriptor newBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_logo_b);

                        Marker now = googleMap.addMarker(new MarkerOptions().icon(newBitmap).position(latLng).title(title));
                        //app.addMapMarker(app.getCurrentUser(), now);

                    }
                } catch (Exception e) {
                    Log.e(TAG, "GOT ACTION_FOR_GET_PEOPLE", e);
                }
            }
        }
    };




}
