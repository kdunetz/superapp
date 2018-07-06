package com.ibm.iot.android.iotstarter.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.Deal;
import com.ibm.iot.android.iotstarter.utils.Utility;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kevindunetz on 3/19/16.
 */

public class MapFragment extends IoTStarterFragment implements OnMapReadyCallback {

    private final static String TAG = IoTStarterApplication.class.getName();

    MapView mapView;
    GoogleMap mMap;
    protected IoTStarterApplication app = null;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.map, container, false);
        app = (IoTStarterApplication) getActivity().getApplication();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);

        final Button button = (Button) v.findViewById(R.id.setRangeButton);
        button.setVisibility(Button.VISIBLE);
        button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //LayoutInflater layoutInflater
                //      = (LayoutInflater)getBaseContext()
                //    .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.update();
                popupWindow.setInputMethodMode(popupWindow.INPUT_METHOD_FROM_FOCUSABLE);
                final EditText editText   = (EditText)popupView.findViewById(R.id.deal); //check this point carefully on your program

                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                final Button btnSave = (Button) popupView.findViewById(R.id.saveButton);
                btnSave.setClickable(false);
                btnSave.setEnabled(false);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        btnSave.setEnabled(editText.length() > 0);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {

                    }
                });
                btnSave.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        popupWindow.dismiss();
                        try {

                            Deal deal = new Deal(false);
                            deal.setLongitude(app.getCurrentLocation().getLongitude() + "");
                            deal.setLatitude(app.getCurrentLocation().getLatitude() + "");
                            deal.setDeal(editText.getText().toString().trim());
                            String username = "";
                            if (app.appUser != null)
                                username = app.appUser.getString("username");
                            deal.setUserName(username);
                            deal.setType("custom_deal");
                            app.dealLocations.add(deal);
                            //app.db2.save(deal);
                            new ConnectToCloudant(deal, app).execute("");
                            Log.d(TAG, "KAD saved");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "KAD CRASH");
                        }
                        //System.out.println("You have inserted the document");
                    }
                });

                popupWindow.showAsDropDown(button, 50, -30);

            }
        });
        final Button recenter = (Button) v.findViewById(R.id.recenter);
        recenter.setVisibility(Button.VISIBLE);
        recenter.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Getting latitude of the current location
                double latitude = app.getCurrentLocation().getLatitude();

                // Getting longitude of the current location
                double longitude = app.getCurrentLocation().getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);

                mMap.animateCamera(cameraUpdate);
            };
        });


        mapView.onCreate(savedInstanceState);

        Log.d("KAD", " IN HERE KEVIN");
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);


        return v;
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("POOPOO", "POOPOO", e);
            return null;
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();

        super.onResume();
        app = (IoTStarterApplication) getActivity().getApplication();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private class ConnectToCloudant extends AsyncTask<String, Void, String> {
        IoTStarterApplication _app;
        Deal _deal = null;

        public ConnectToCloudant(Deal deal, IoTStarterApplication app) {
            super();
            _app = app;
            _deal = deal;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Create an ExampleDocument and save it in the database
                //db.(new ExampleDocument(true));
                _app.db2.save(_deal);
                //System.out.println("You have inserted the document");

            } catch (Exception ex) {
                Log.d(TAG, "KAD " + ex.getMessage());
                ex.printStackTrace();
            }

            return null;
        }


    }
    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("KAD", "Before");

        if (map == null) {
            return;
        }
        if (app == null) {
            Log.d("BLAHBLAH", "app is null");
            return;
        }

        Log.d("KAD", "After");

        mMap = map;

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //mMap.setMyLocationEnabled(true);


        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Updates the location and zoom of the MapView
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        //mMap.animateCamera(cameraUpdate);

        //mMap = googleMap;
        app.setGoogleMap(mMap);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        Log.d("debugme", "before changing bitmap MapFragment");
        BitmapDescriptor newBitmap = null;
        if (app.getCurrentUser() != null && app.getCurrentUser().equalsIgnoreCase("kevindunetz@gmail.com"))
            newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.kevindunetz);
        else if (app.getCurrentUser() != null && app.getCurrentUser().equalsIgnoreCase("ryandunetz@gmail.com"))
            newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ryandunetz);
        else if (app.getCurrentUser() != null && app.getCurrentUser().equalsIgnoreCase("rosadunetz@gmail.com"))
            newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.rosadunetz);
        else if (app.getCurrentUser() != null && app.getCurrentUser().equalsIgnoreCase("andrewdunetz@gmail.com"))
            newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.andrewdunetz);
        else {

            Log.d("debugme", "in here for some reason");
            newBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_logo_b);
        }
        //BitmapDescriptor newBitmap = BitmapDescriptorFactory.fromBitmap(getMapBitmap(app.getCurrentUser()));

        //BitmapDescriptor newBitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromURL("http://http://www.graphicsfuel.com/wp-content/uploads/2011/12/search-icon-512.png"));
        Location location = app.getCurrentLocation();
        if (location != null) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(position).icon(newBitmap).title(app.getCurrentUser()));
            app.addMapMarker(app.getCurrentUser(), marker); /* save in global variable so we can remove it later when we move */
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
            Utility.getPeopleInArea(mapView.getContext(), app, location);
            Utility.loadLocalBusinesses(mapView.getContext(), app, location);

        }
    }
}
