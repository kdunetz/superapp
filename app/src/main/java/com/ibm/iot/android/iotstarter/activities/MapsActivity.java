package com.ibm.iot.android.iotstarter.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final static String TAG = MapsActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "in onMapReady");

        mMap = googleMap;
        IoTStarterApplication app = (IoTStarterApplication) getApplication();
        app.setGoogleMap(mMap);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
//        BitmapDescriptor subwayBitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        BitmapDescriptor newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker);

        if (app.getCurrentLocation() != null) {
            Log.d(TAG," in getCurrentLocation");
            LatLng position = new LatLng(app.getCurrentLocation().getLatitude(), app.getCurrentLocation().getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(position).icon(newBitmap).title("My Location"));
            app.addMapMarker(app.getCurrentUser(), marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        }


    }
}
