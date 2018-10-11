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

import android.app.AlertDialog;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.RestTask;
import com.ibm.iot.android.iotstarter.utils.Utility;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.Date;


/**
 * The Profiles activity lists saved connection profiles to use to connect to IoT.
 */
public class DynamicFormActivity extends Activity {
    private final static String TAG = DynamicFormActivity.class.getName();
    protected Context context;
    protected IoTStarterApplication app;
    protected BroadcastReceiver broadcastReceiver;

    protected ListView listView;
    protected ArrayAdapter<String> listAdapter;
    private static final String ACTION_FOR_INTENT_CALLBACK = "DYNAMIC_FORM";
    private static final String ACTION_FOR_DATA_RESULT = "DATA_RESULT";
    private static final String ACTION_FOR_SAVE_RESULT = "SAVE_RESULT";
    private static final String ACTION_FOR_DELETE_RESULT = "DELETE_RESULT";

    ProgressDialog progress;

    String _objectName;
    String _formType;
    String _dataURL;
    JSONObject _dataObject = null;
    JSONArray _metaDataArray = null;


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

        setContentView(R.layout.dynamic_form);
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);

        Bundle b = getIntent().getExtras();
Log.d("debugme", "start Dynamic Form Activity");
        _objectName = b.getString("object_name");
        _formType = b.getString("form_type");
        _dataURL = b.getString("data_url");

        if (_dataURL != null && _dataURL.length() > 0) {
            Log.d("debugme", "GOT DATA RESULT = " + _dataURL);
            Utility.callRESTAPI(this, _dataURL, "get", ACTION_FOR_DATA_RESULT, "");
        }
        app = (IoTStarterApplication) getApplication().getApplicationContext();

        Utility.callRESTAPI(this, app.metaDataURL + "?object=" + _objectName, "get", ACTION_FOR_INTENT_CALLBACK, "");

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
            Log.d("debugme", "IN HERE DynamicFormActivity BroadcastReceiver");
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            String callBack = intent.getAction();

            if (callBack.equals(ACTION_FOR_DELETE_RESULT)) {

                Intent dynamicTableIntent = new Intent(context, DynamicTableActivity.class);
                Bundle b = new Bundle();
                b.putString("object_name", _objectName);
                b.putString("form_type", "display"); // display, create, edit
                //b.putString("data_url", "https://new-node-red-demo-kad.mybluemix.net/getall?object_name=" + _objectName);
                b.putString("data_url", "http://superapp-apis.appspot.com/" + _objectName);

                dynamicTableIntent.putExtras(b);
                startActivity(dynamicTableIntent,b);
                unregisterReceiver(receiver);
                finish();
            }
            if (callBack.equals(ACTION_FOR_DATA_RESULT)) {
                Log.d(TAG, "GOT ACTION_FOR_DATA_RESULT");

                try {
                    _dataObject = new JSONObject(response);
                } catch (Exception e) {
                    Log.e(TAG, "GOT ACTION_FOR_DATA_RESULT", e);
                }
            }
            if (callBack.equals(ACTION_FOR_SAVE_RESULT))
            {
                Log.d(TAG, "GOT ACTION_SAVE_RESULT - " + response);
                new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.save_dynamic_form_title))
                        .setMessage(getResources().getString(R.string.save_dynamic_form_text_success))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                unregisterReceiver(receiver);
                                finish();
                                return;
                            }
                        })
                        //.setView(input)
                        .show();


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
            if (_formType.equals("create"))
                _dataObject = new JSONObject();
            if (_metaDataArray != null && _dataObject != null) {
                try {
                    Log.d("debugme", _metaDataArray.toString());
                    JSONObject jsonObj = (JSONObject) _metaDataArray.get(0);
                    JSONArray jsonArray1 = (JSONArray) jsonObj.getJSONArray("objects");
                    Log.d("debugme", jsonArray1.toString());
                    for (int y = 0; y < jsonArray1.length(); y++) {
                        JSONObject obj = (JSONObject) jsonArray1.get(y);
                        if (obj.getJSONObject("header").getString("object_name").equals(_objectName)) // find the form we are looking for
                        {
                            Log.d("debugme", " IN HERE for " + _objectName + " - " + y);
                            JSONArray jsonArray = obj.getJSONArray("fields");
                            TextView button = (TextView) findViewById(R.id.object_name);
                            String displayName = obj.getJSONObject("header").getString("object_display_name");
                            button.setText(displayName);


                            Button saveButton = (Button)findViewById(R.id.dynamicFormButton);
                            Button deleteButton = (Button)findViewById(R.id.delete_button);

                            if (_formType.equals("edit") || _formType.equals("create")) {
                                saveButton.setText("Save");
                                deleteButton.setVisibility(View.INVISIBLE);

                                saveButton.setOnClickListener(new Button.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        try {

                                            boolean failed = saveForm();
                                            Log.d(TAG, "KAD saved");

                                            Intent dynamicTableIntent = new Intent(v.getContext(), DynamicTableActivity.class);
                                            Bundle b = new Bundle();
                                            b.putString("object_name", _objectName);
                                            b.putString("form_type", "display"); // display, create, edit
                                            //b.putString("data_url", "https://new-node-red-demo-kad.mybluemix.net/getAll?object_name=" + _objectName);
                                            b.putString("data_url", "http://superapp-apis.appspot.com/" + _objectName);

                                            dynamicTableIntent.putExtras(b);
                                            startActivity(dynamicTableIntent,b);
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
                                            Intent dynamicFormIntent = new Intent(getApplicationContext(), DynamicFormActivity.class);
                                            Bundle b = new Bundle();
                                            b.putString("object_name", _objectName);
                                            b.putString("form_type", "edit"); // display, create, edit
                                            b.putString("data_url", _dataURL);
                                            dynamicFormIntent.putExtras(b);
                                            startActivity(dynamicFormIntent, b);
                                            Log.d(TAG, "KAD saved");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "KAD CRASH");
                                        }
                                        //System.out.println("You have inserted the document");
                                    }
                                });
                                deleteButton.setOnClickListener(new Button.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        try {
                                            //String url = "https://new-node-red-demo-kad.mybluemix.net/delete?object_name=" + _objectName;
                                            String url = "http://superapp-apis.appspot.com/" + _objectName + "/" + _dataObject.get("_id");

                                            Utility.callRESTAPI(v.getContext(), url, "delete", ACTION_FOR_DELETE_RESULT, _dataObject.toString());


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
                                    tx.setText(_dataObject.getString(fieldName));
                                    layout.addView(tx);
                                } else if (fieldType.equals("input")) {

                                    EditText ex = new EditText(context);
                                    if (_formType.equals("edit"))
                                        ex.setText(_dataObject.getString(fieldName));
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
                        else
                            continue; // KAD maybe take out
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
            JSONArray jsonArray1 = jsonObj.getJSONArray("objects");
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

             String date = Utility.getCurrentDateTime();
             try {
                dataObject.get("creation_date");
             }
             catch (Exception e)
             {
                Log.d("debugme", "Create date is not present.. so add it in");
                dataObject.put("creation_date", date);
             }

            dataObject.put("modification_date", date);
        } catch (Exception e)
        {
            Log.e("debugme", "Error Saving Form", e);
        }


        Log.d("debugme1", dataObject.toString());
        if (failedValidation)
        {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.save_dynamic_form_title))
                    .setMessage(getResources().getString(R.string.save_dynamic_form_text_failure))
                    .setPositiveButton("OK", null)
                    //.setView(input)
            .show();
        }
        else
        {
            //String url = "https://ddc1728e-316e-4a3e-a75e-e85be80a4e99-bluemix:44ea0e6682c63ea1e73b24c2fe1bf5b1a37c4e2ad205bb612a54d1d2e81d40ad@ddc1728e-316e-4a3e-a75e-e85be80a4e99-bluemix.cloudant.com/user";
            //String url = "https://new-node-red-demo-kad.mybluemix.net/save?object_name=" + _objectName;
            String url = "http://superapp-apis.appspot.com/" + _objectName;

            Log.d("debugme", url);

            Utility.callRESTAPI(this, url, "post", ACTION_FOR_SAVE_RESULT, dataObject.toString());
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
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_SAVE_RESULT));
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_DELETE_RESULT));

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
