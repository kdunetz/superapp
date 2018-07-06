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

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.activities.CameraActivity;
import com.ibm.iot.android.iotstarter.activities.AndroidBarcodeQrActivity;

import com.ibm.iot.android.iotstarter.activities.ConfirmOCRScan;
import com.ibm.iot.android.iotstarter.activities.MainActivity;
import com.ibm.iot.android.iotstarter.activities.ProfilesActivity;
import com.ibm.iot.android.iotstarter.activities.FindNeighborsActivity;
import com.ibm.iot.android.iotstarter.activities.DynamicFormActivity;
import com.ibm.iot.android.iotstarter.activities.DynamicTableActivity;
import com.ibm.iot.android.iotstarter.activities.SelectImageActivity;
import com.ibm.iot.android.iotstarter.activities.SettingsActivity;
import com.ibm.iot.android.iotstarter.activities.TableListActivity;


/**
 * This class provides common properties and functions for fragment subclasses used in the application.
 */
public class IoTStarterFragment extends Fragment {
    protected final static String TAG = IoTStarterFragment.class.getName();
    protected Context context;
    protected IoTStarterApplication app;
    protected Menu menu;
    protected BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
       // Log.d("debugme", "Parse output = " + Utility.parseOutReceipt("BUY ONE GET ONE FREE QUARTER POUNDER\nW/CHEESE OR EGG MCMUFFIN\nGo to wHw.mcdvoice.com within 7 days\nand tell us about your visit.\nValidation Code:\nExpires 30 days after receipt date.\nValid at participating US McDonald's\nSurvey Code:\n06345-12380-50618-07563-00168-0\nMcDonald's Restaurant #6345\n5651 BURKE CENTRE PKY\nBURKE, VA 22015\nTEL# 703 250 3652\n05/06/2018 07:56 AN\nndor 38\nKS# 12\n"));
       // Log.d("debugme", "Parse output = " + Utility.parseOutReceipt("Free WHOPPER Sandwich or Original Chicken Sandwich Purchase Required\n Survey Code: 039403-938493-938493-938493\n www.mybkexperience.com (English or Espanol"));
       // Log.d("debugme", "Parse output = " + Utility.parseOutReceipt("We'd like to offer you a free\\nChick-fil-A Chicken Sandwich\\n(Original or Spicy)\\nas a thank you for completing\\nour new shorter survey\\nwithin 2 days of your visit.\\nwww.mycfavisit.com\\nerial Num: 4580202-03374-2030-0514-86\\nAt the end of the survey, your email\\nwill be required in order for us\\nto send your free sandwich offer.\\n"));
       // Log.d("debugme", "Parse output = " + Utility.parseOutReceipt("\\nHOW ARE WE DOING?\\nTell us in the next 5 days at\\nwww.talktogiantfoods.com\\nUse the PIN # below to login\\n0516 2038 0758 3404 0183\\nAnd enter for a\\nChance to win $500\\n"));
    }

    /**
     * Update strings in the fragment based on IoTStarterApplication values.
     */
    protected void updateViewStrings() {
        Log.d(TAG, ".updateViewStrings() entered");
        int unreadCount = app.getUnreadCount();
        ((MainActivity) getActivity()).updateBadge(getActivity().getActionBar().getTabAt(2), unreadCount);
    }

    /**************************************************************************
     * Functions to handle the menu bar
     **************************************************************************/

    /**
     * Switch to the IoT fragment.
     */
    protected void openIoT() {
        Log.d(TAG, ".openIoT() entered");
        getActivity().getActionBar().setSelectedNavigationItem(1);
    }

    protected void openProfiles() {
        Log.d(TAG, ".handleProfiles() entered");
        Intent profilesIntent = new Intent(getActivity().getApplicationContext(), ProfilesActivity.class);
        startActivity(profilesIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, ".onCreateOptions() entered");
        this.menu = menu;
        getActivity().getMenuInflater().inflate(R.menu.menu, this.menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Process the selected iot_menu item.
     *
     * @param item The selected iot_menu item.
     * @return true in all cases.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, ".onOptionsItemSelected() entered");

        if (false && LoginFragment.class.getName().equals(app.getCurrentRunningActivity())) {
            app.setDeviceId(((EditText) getActivity().findViewById(R.id.deviceIDValue)).getText().toString());
            app.setOrganization(((EditText) getActivity().findViewById(R.id.organizationValue)).getText().toString());
            app.setAuthToken(((EditText) getActivity().findViewById(R.id.authTokenValue)).getText().toString());
        }

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_accel:
                app.toggleAccel();
                return true;
            //case R.id.action_profiles:
            //    openProfiles();
            //    return true;
            //case R.id.action_clear_profiles:
            //    app.clearProfiles();
            //    return true;
            case R.id.qrcode_reader:
                QRCodeReader();
                return true;
            case R.id.find_neighbors:
                findNeighbors();
                return true;
            case R.id.dynamic_form:
                dynamicForm();
                return true;
            case R.id.dynamic_table:
                //dynamicTable();
                dynamicTableList();
                return true;
            case R.id.select_image:
                selectImage();
                return true;
            case R.id.scan_receipt:
                scanReceipt();
                return true;
            case R.id.settings:
                settings();
                return true;
            case R.id.create_coupon:
                createCoupon();
                return true;
            case R.id.clear:
                app.setUnreadCount(0);
                app.getMessageLog().clear();
                updateViewStrings();
                return true;
            default:
                if (item.getTitle().equals(getResources().getString(R.string.app_name))) {
                    getActivity().openOptionsMenu();
                    return true;
                } else {
                    return super.onOptionsItemSelected(item);
                }
        }
    }

    protected void QRCodeReader() {
        Log.d(TAG, ".QRCodeReader() entered");
        Intent qrCodeReaderIntent = new Intent(getActivity().getApplicationContext(), AndroidBarcodeQrActivity.class);
        startActivity(qrCodeReaderIntent);
    }
    protected void findNeighbors() {
        Log.d(TAG, ".findNeighbors() entered");
        Intent findNeighborsIntent = new Intent(getActivity().getApplicationContext(), FindNeighborsActivity.class);
        startActivity(findNeighborsIntent);
    }
    protected void createCoupon() {
        Log.d(TAG, ".createCoupon() entered");
        try {
            Intent dynamicFormIntent = new Intent(getActivity().getApplicationContext(), ConfirmOCRScan.class);
            Bundle b = new Bundle();
            b.putString("json", "{}");
            dynamicFormIntent.putExtras(b);
            startActivity(dynamicFormIntent,b);
        }
        catch (Exception e)
        {
            Log.e(TAG, "BLAHBLAH", e);
            e.printStackTrace();
        }
    }
    protected void dynamicForm() {
        Log.d(TAG, ".dynamicForm() entered");
        try {
            Intent dynamicFormIntent = new Intent(getActivity().getApplicationContext(), DynamicFormActivity.class);
            Bundle b = new Bundle();
            b.putString("object_name", "object_one");
            b.putString("form_type", "display"); // display, create, edit
            b.putString("data_url", "https://new-node-red-demo-kad.mybluemix.net/getuser?name=bobby1");
            dynamicFormIntent.putExtras(b);
            startActivity(dynamicFormIntent,b);
        }
        catch (Exception e)
        {
            Log.e(TAG, "BLAHBLAH", e);
            e.printStackTrace();
        }
    }
    protected void dynamicTable() {
        Log.d(TAG, ".dynamicTable() entered");
        try {
            Intent dynamicTableIntent = new Intent(getActivity().getApplicationContext(), DynamicTableActivity.class);
            Bundle b = new Bundle();
            b.putString("object_name", "object_one");
            b.putString("form_type", "display"); // display, create, edit
            b.putString("data_url", "https://new-node-red-demo-kad.mybluemix.net/getAll?object_name=object_one");
            dynamicTableIntent.putExtras(b);
            //dynamicFormIntent.setData(Uri.parse("https://new-node-red-demo-kad.mybluemix.net/getuser?name=bobby1"));
            startActivity(dynamicTableIntent,b);
        }
        catch (Exception e)
        {
            Log.e(TAG, "BLAHBLAH", e);
            e.printStackTrace();
        }
    }
    protected void dynamicTableList() {
        Log.d(TAG, ".dynamicTableList() entered");
        try {
            Intent dynamicTableListIntent = new Intent(getActivity().getApplicationContext(), TableListActivity.class);
            Bundle b = new Bundle();
            b.putString("object_name", "");
            b.putString("form_type", "display"); // display, create, edit
            b.putString("data_url", "");
            dynamicTableListIntent.putExtras(b);
            startActivity(dynamicTableListIntent,b);
        }
        catch (Exception e)
        {
            Log.e(TAG, "BLAHBLAH", e);
            e.printStackTrace();
        }
    }
    protected void selectImage() {
        Log.d(TAG, ".selectImage() entered");
        try {
            Intent selectImageIntent = new Intent(getActivity().getApplicationContext(), SelectImageActivity.class);
            startActivity(selectImageIntent);
        }
        catch (Exception e)
        {
            Log.e(TAG, "BLAHBLAH", e);
            e.printStackTrace();
        }
    }
    protected void scanReceipt() {
        Log.d(TAG, ".scanReceipt() entered");
        try {
            Intent cameraIntent = new Intent(getActivity().getApplicationContext(), CameraActivity.class);
            startActivity(cameraIntent);
        }
        catch (Exception e)
        {
            Log.e(TAG, "BLAHBLAH", e);
            e.printStackTrace();
        }
    }
    protected void settings() {
        Log.d(TAG, ".settings() entered");
        try {
            Intent settingIntent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
            startActivity(settingIntent);
        }
        catch (Exception e)
        {
            Log.e(TAG, "BLAHBLAH", e);
            e.printStackTrace();
        }
    }
}
