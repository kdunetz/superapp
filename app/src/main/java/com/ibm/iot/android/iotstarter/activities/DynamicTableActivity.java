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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.graphics.Color;

import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import android.widget.TableRow;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.RestTask;
import com.ibm.iot.android.iotstarter.utils.Utility;
import com.ibm.iot.android.iotstarter.activities.DynamicFormActivity;


import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;


/**
 * The Profiles activity lists saved connection profiles to use to connect to IoT.
 */
public class DynamicTableActivity extends Activity {
    private final static String TAG = DynamicTableActivity.class.getName();
    protected Context context;
    protected IoTStarterApplication app;
    protected BroadcastReceiver broadcastReceiver;

    protected ListView listView;
    protected ArrayAdapter<String> listAdapter;
    private static final String ACTION_FOR_INTENT_CALLBACK = "DYNAMIC_TABLE";
    private static final String ACTION_FOR_DATA_RESULT = "DATA_RESULT_FOR_TABLE";

    ProgressDialog progress;
    String _objectName;
    String _formType;
    String _dataURL;
    JSONArray _dataObject = null;
    JSONArray _metaDataArray = null;
    ArrayList _data = new ArrayList();


    /**************************************************************************
     * Activity functions for establishing the activity
     **************************************************************************/

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, ".onCreate() entered");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.table);

        TextView createButton = (TextView) findViewById(R.id.create_button);

        createButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    Intent dynamicFormIntent = new Intent(getApplicationContext(), DynamicFormActivity.class);
                    Bundle b = new Bundle();
                    b.putString("object_name", _objectName);
                    b.putString("form_type", "create"); // display, create, edit
                    //b.putString("data_url", "https://new-node-red-demo-kad.mybluemix.net/getuser?name=bobby1");
                    dynamicFormIntent.putExtras(b);
                    startActivity(dynamicFormIntent,b);
                    Log.d(TAG, "KAD requested Dynamic Form Activity");

                    finish();
                    unregisterReceiver(receiver);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "KAD CRASH");
                }
                //System.out.println("You have inserted the document");
            }
        });

        Bundle b = getIntent().getExtras();
Log.d("debugme", "start Dynamic Table Activity");
        _objectName = b.getString("object_name");
        _formType = b.getString("form_type");
        _dataURL = b.getString("data_url");

/*
        DisplayMetrics dm = getResources().getDisplayMetrics();
        REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);
*/
    }

    /**
     * Our Broadcast Receiver. We get notified that the data is ready this way.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }
            Log.d("debugme", "IN HERE DynamicTableActivity BroadcastReceiver");

            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            String callBack = intent.getAction();

            if (callBack.equals(ACTION_FOR_DATA_RESULT)) {
                Log.d(TAG, "GOT ACTION_FOR_DATA_RESULT");
                if (response.equals("GET_GLOBAL"))
                {
                    response = Utility.getGlobalStr();
                }
                try {
                    _dataObject = new JSONArray(response);
                } catch (Exception e) {
                    Log.e(TAG, "GOT ACTION_FOR_DATA_RESULT", e);
                }
            }
            if (callBack.equals(ACTION_FOR_INTENT_CALLBACK)) {
                Log.d(TAG, "GOT ACTION_FOR_INTENT_CALLBACK");

                //ourTextView.setText(response);
                try {
                    _metaDataArray = new JSONArray(response);
                } catch (Exception e) {
                    Log.e(TAG, "GOT ACTION_FOR_INTENT_CALLBACK", e);

                }
            }
            if (_metaDataArray != null && _dataObject != null) {
                try {
                    Log.d("debugme", _metaDataArray.toString());
                    JSONObject jsonObj = (JSONObject) _metaDataArray.get(0);
                    JSONArray jsonArray1 = (JSONArray) jsonObj.getJSONArray("objects");
                    Log.d("debugme", jsonArray1.toString());
                    for (int y = 0; y < jsonArray1.length(); y++) {
                        JSONObject obj = (JSONObject) jsonArray1.get(y);
                        Log.d("debugme", "each row - " + obj.toString());
                        final String objectName = obj.getJSONObject("header").getString("object_name");
                        // Write the Header Text
                        if (objectName.equals(_objectName)) // find the form we are looking for
                        {
                            String displayName = obj.getJSONObject("header").getString("object_display_name");
                            Button saveButton = (Button) findViewById(R.id.dynamicTableButton);
                            saveButton.setText(displayName + " (" + _dataObject.length() + ")");
                        }
                        else
                            continue;

                        TableLayout ll = (TableLayout) findViewById(R.id.table_layout);
                        ll.removeAllViews();

                        JSONArray jsonArray = obj.getJSONArray("fields");
        // KAD MOVE THIS TO A METHOD
                        TableRow row = new TableRow(context);
                        row.setBackgroundColor(Color.WHITE);
                        row.setPadding(0, 0, 0, 2);
                        row.setGravity(3);

                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 2, 0);
                        row.setLayoutParams(lp);

                        for (int z = 0; z < jsonArray.length(); z++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(z);
                            String fieldName = jsonObject.getString("field_name");
                            String fieldDisplayName = jsonObject.getString("field_display_name");
                            Log.d("debugme", fieldName);


                            LinearLayout cell = new LinearLayout(context);
                            cell.setBackgroundColor(Color.BLACK);
                            cell.setLayoutParams(lp);

                            TextView tv = new TextView(context);

                            tv.setText(fieldDisplayName);
                            tv.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
                            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                            tv.setTextSize(24);
                            tv.setPadding(20, 20, 20, 20);
                            cell.addView(tv);
                            row.addView(cell);

                        }
                        ll.addView(row, 0);

                        try {
                        for (int a = 0; a < _dataObject.length(); a++) {
                            final JSONObject dataObj = (JSONObject) _dataObject.getJSONObject(a);
                            row = new TableRow(context);
                            row.setId(a);
                            row.setBackgroundColor(Color.WHITE);
                            row.setPadding(0, 0, 0, 2);
                            row.setGravity(3);
                            Log.d("debugme","in here KAD - " + a);
                            row.setOnClickListener( new OnClickListener() {
                                @Override
                                public void onClick( View v ) {
                                    //Do Stuff

                                    Intent dynamicFormIntent = new Intent(v.getContext(), DynamicFormActivity.class);
                                    Bundle b = new Bundle();
                                    b.putString("object_name", objectName);
                                    b.putString("form_type", "display"); // display, create, edit
                                    try {
                                        b.putString("data_url", "https://new-node-red-demo-kad.mybluemix.net/getobject?object_name=" + _objectName + "&id=" + dataObj.getString("_id"));
                                    } catch (Exception e)
                                    {
                                        Log.e("debugme", "Problem with JSON Object", e);
                                    }
                                    dynamicFormIntent.putExtras(b);
                                    startActivity(dynamicFormIntent, b);

                                }
                            } );

                            lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 2, 0);
                            row.setLayoutParams(lp);

                            for (int z = 0; z < jsonArray.length(); z++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(z);
                                String fieldName = jsonObject.getString("field_name");
                                String fieldDisplayName = jsonObject.getString("field_display_name");
                                String fieldType = jsonObject.getString("field_type");
                                String fieldDefault = jsonObject.getString("field_default");
                                String fieldFlags = ""; //jsonObject.getString("field_flags");
                                Log.d("debugme", fieldName);


                                LinearLayout cell = new LinearLayout(context);
                                cell.setBackgroundColor(Color.BLACK);
                                cell.setLayoutParams(lp);


                                TextView tv = new TextView(context);
                                try {
                                    tv.setText(dataObj.getString(fieldName));
                                }
                                catch (Exception e) {
                                    tv.setText("Field Missing");
                                }
                                tv.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
                                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                                tv.setTextSize(18);

                                tv.setPadding(20, 20, 20, 20);
                                cell.addView(tv);
                                row.addView(cell);

                            }
                            Log.d("debugme", "a = " + a);
                            ll.addView(row, a + 1);
                        }

                    } catch (Exception e)
                    {
                        Log.e("debugme", "stacktrace", e);
                    }

                        if (false && obj.getJSONObject("header").getString("object_name").equals(_objectName)) // find the form we are looking for
                        {
                            jsonArray = obj.getJSONArray("fields");
                            TextView button = (TextView) findViewById(R.id.object_name);
                            String displayName = obj.getJSONObject("header").getString("object_display_name");
                            button.setText(displayName);

                            Button saveButton = (Button)findViewById(R.id.dynamicTableButton);
                            saveButton.setText(displayName);

                            if (_formType.equals("edit")) {
                                saveButton.setText("Save");
                                saveButton.setOnClickListener(new Button.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        try {

                                            boolean failed = saveForm();
                                            Log.d(TAG, "KAD saved");

                                            if (!failed)
                                               finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "KAD CRASH");
                                        }
                                        //System.out.println("You have inserted the document");
                                    }
                                });
                            }
                            if (_formType.equals("display"))
                            {
                                saveButton.setText("Edit");
                                saveButton.setOnClickListener(new Button.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        try {
                                            finish();
                                            Intent dynamicFormIntent = new Intent(getApplicationContext(), DynamicTableActivity.class);
                                            Bundle b = new Bundle();
                                            b.putString("object_name", _objectName);
                                            b.putString("form_type", "edit"); // display, create, edit
                                            b.putString("data_url", "https://new-node-red-demo-kad.mybluemix.net/getuser?name=bobby1");
                                            dynamicFormIntent.putExtras(b);
                                            //dynamicFormIntent.setData(Uri.parse("https://new-node-red-demo-kad.mybluemix.net/getuser?name=bobby1"));
                                            startActivity(dynamicFormIntent, b);
                                            Log.d(TAG, "KAD saved");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "KAD CRASH");
                                        }
                                        //System.out.println("You have inserted the document");
                                    }
                                });
                            }

                            //listAdapter = new ArrayAdapter<String>(context, R.layout.simplerow);
                            LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);


                            for (int x = 0; x < jsonArray.length(); x++) {
                                Log.d("debugme", x + "");
                                JSONObject jsonObject = (JSONObject) jsonArray.get(x);
                                String fieldName = jsonObject.getString("field_name");
                                String fieldDisplayName = jsonObject.getString("field_display_name");
                                String fieldType = jsonObject.getString("field_type");
                                String fieldDefault = jsonObject.getString("field_default");
                                String fieldFlags = jsonObject.getString("field_flags");

                                TextView tx = new TextView(context);
                                //      tx.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                                tx.setText(fieldDisplayName + (fieldFlags.equals("flagreq") == true? " (*)":")"));
                                layout.addView(tx);
                                if (_formType.equals("display")) {
                                    tx = new TextView(context);
                                    Log.d("debugme", _dataObject.toString());
                                    //tx.setText(_dataObject.getString(fieldName));
                                    layout.addView(tx);
                                } else if (fieldType.equals("input")) {

                                    EditText ex = new EditText(context);
                                    if (_formType.equals("edit"))
                                        ; //ex.setText(dataObj.getString(fieldName));
                                    else
                                    if (fieldDefault.length() > 0)
                                        ex.setText(fieldDefault);
                                    ex.setId(x);
                                    layout.addView(ex);
                                } else if (fieldType.equals("select")) {
                                    Log.d("debugme", "in here - ");

                                    Spinner spinner = new Spinner(context);

                                    String fieldValues = jsonObject.getString("field_values");
                                    String fieldDisplayValues = jsonObject.getString("field_display_values");
                                    String[] values = fieldValues.split(",");
                                    String[] displayValues = fieldDisplayValues.split(",");
                                    ArrayList<Contact> contacts = new ArrayList<>();

                                    for (int i = 0; i < values.length; i++) {
                                        contacts.add(new Contact(values[i], displayValues[i]));
                                    }

                                    ArrayAdapter<Contact> adapter =
                                            new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, contacts);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                    spinner.setAdapter(adapter);
                                    layout.addView(spinner);
                                    //listAdapter.add(jsonObject.getString("name") + " - " + jsonObject.getString("search_key_words"));
                                    //listView.setAdapter( listAdapter );
                                }

                            }
                        }
                    }


                } catch (Exception e) {
                    Log.e("debugme", "JSON Error", e);
                    e.printStackTrace();
                }
            }

        }
    };
    public boolean saveForm()
    {
        JSONObject dataObject = new JSONObject();
        boolean failedValidation = false;

        try {

            JSONObject jsonObj = (JSONObject) _metaDataArray.get(0);
            JSONArray jsonArray1 = (JSONArray) jsonObj.getJSONArray("objects");
            Log.d("debugme", jsonArray1.toString());
            for (int y = 0; y < jsonArray1.length(); y++) {
                JSONObject obj = (JSONObject) jsonArray1.get(y);
                if (obj.getJSONObject("header").getString("object_name").equals(_objectName)) // find the form we are looking for
                {
                    JSONArray jsonArray = obj.getJSONArray("fields");

                    for (int x = 0; x < jsonArray.length(); x++) {
                        Log.d("debugme", x + "");
                        JSONObject jsonObject = (JSONObject) jsonArray.get(x);
                        String fieldName = jsonObject.getString("field_name");
                        String fieldDisplayName = jsonObject.getString("field_display_name");
                        String fieldType = jsonObject.getString("field_type");
                        String fieldDefault = jsonObject.getString("field_default");
                        String fieldFlags = jsonObject.getString("field_flags");

                        if (fieldType.equals("input")) {
                            EditText ex = (EditText)findViewById(x);
                            dataObject.put(fieldName, ex.getText().toString());
                            if (fieldFlags.equals("flagreq") && ex.getText().toString().length() == 0)
                            {
                                failedValidation = true;
                            }
                        } else if (fieldType.equals("select")) {
                            Log.d("debugme", "in here - ");

                            Spinner spinner = new Spinner(context);

                            String fieldValues = jsonObject.getString("field_values");
                            String fieldDisplayValues = jsonObject.getString("field_display_values");
                            String[] values = fieldValues.split(",");
                            String[] displayValues = fieldDisplayValues.split(",");
                            ArrayList<Contact> contacts = new ArrayList<>();

                            for (int i = 0; i < values.length; i++) {
                                contacts.add(new Contact(values[i], displayValues[i]));
                            }

                            ArrayAdapter<Contact> adapter =
                                    new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, contacts);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinner.setAdapter(adapter);

                            //listAdapter.add(jsonObject.getString("name") + " - " + jsonObject.getString("search_key_words"));
                            //listView.setAdapter( listAdapter );
                        }

                    }
                }
            }
        } catch (Exception e)
        {
            Log.e("debugme", "alkjflas", e);
        }
        Log.d("debugme1", dataObject.toString());
        if (failedValidation)
        {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.save_dynamic_form_title))
                    .setMessage(getResources().getString(R.string.save_dynamic_form_text_failure))
                    //.setView(input)
            .show();
        }
        else
        {
            String url = "https://ddc1728e-316e-4a3e-a75e-e85be80a4e99-bluemix:44ea0e6682c63ea1e73b24c2fe1bf5b1a37c4e2ad205bb612a54d1d2e81d40ad@ddc1728e-316e-4a3e-a75e-e85be80a4e99-bluemix.cloudant.com";
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.save_dynamic_form_title))
                    .setMessage(getResources().getString(R.string.save_dynamic_form_text_success))
                    //.setView(input)
                    .show();
        }
        return failedValidation;

    }
    private class Contact {
        private String contact_name;
        private String contact_id;

        public Contact() {
        }

        public Contact(String contact_name, String contact_id) {
            this.contact_name = contact_name;
            this.contact_id = contact_id;
        }

        public String getContact_name() {
            return contact_name;
        }

        public void setContact_name(String contact_name) {
            this.contact_name = contact_name;
        }

        public String getContact_id() {
            return contact_id;
        }

        public void setContact_id(String contact_id) {
            this.contact_id = contact_id;
        }
        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return contact_name
         */
        @Override
        public String toString() {
            return contact_name;
        }
    }



    /**
     * Called when the activity is resumed.
     */
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK));
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_DATA_RESULT));

        if (_dataURL != null && _dataURL.length() > 0) {
            Log.d("debugme", "GOT DATA RESULT = " + _dataURL);
            Utility.callRESTAPI(this, _dataURL, "get", ACTION_FOR_DATA_RESULT, "");
        }

        Utility.callRESTAPI(this, app.metaDataURL + "?object=" + _objectName, "get", ACTION_FOR_INTENT_CALLBACK, "");

/*
        @SuppressWarnings("deprecation")
        final GestureDetector gestureDetector = new GestureDetector(
                new MyGestureDetector());

        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        list.setOnTouchListener(gestureListener);
        */


    }


    /**
     * Called when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        super.onDestroy();
    }
    @Override
    public void onPause() {
        Log.d(TAG, ".onPause() entered");

        super.onPause();
        //if (receiver != null)
        //    unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, ".onCreateOptions() entered");
        //getMenuInflater().inflate(R.menu.profiles_menu, menu);

        return true;
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

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_accel:
                app.toggleAccel();
                return true;
            case R.id.action_clear_profiles:
                app.clearProfiles();
                listAdapter.notifyDataSetInvalidated();
                return true;
            case R.id.clear:
                app.setUnreadCount(0);
                app.getMessageLog().clear();
                return true;
            default:
                if (item.getTitle().equals(getResources().getString(R.string.app_name))) {
                    openOptionsMenu();
                    return true;
                } else {
                    return super.onOptionsItemSelected(item);
                }
        }
    }



}
