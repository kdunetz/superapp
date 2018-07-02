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

import java.net.URI;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.ViewGroup;
import android.widget.*;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.RestTask;
import com.ibm.iot.android.iotstarter.utils.User;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * The Profiles activity lists saved connection profiles to use to connect to IoT.
 */
public class EditSearchCriteriaActivity extends Activity {
    private final static String TAG = EditSearchCriteriaActivity.class.getName();
    protected Context context;
    protected IoTStarterApplication app;
    protected BroadcastReceiver broadcastReceiver;

    protected ListView listView;
    protected ArrayAdapter<String> listAdapter;

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

        setContentView(R.layout.edit_search_criteria);
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);

        TextView tx= new TextView(this);
//      tx.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        tx.setText("ANDROID APP");
        layout.addView(tx);




    }


    /**
     * Called when the activity is resumed.
     */
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        context = getApplicationContext();
        app = (IoTStarterApplication) getApplication();
        try {
            JSONObject user = app.appUser;

            Button button = (Button) findViewById(R.id.saveSearchCriteria);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleSave(v);
                }
            });

            EditText editText = (EditText) findViewById(R.id.usernameValue);
            editText.setText(user.getString("name"));
            editText = (EditText) findViewById(R.id.firstnameValue);
            editText.setText(user.getString("first_name"));
            editText = (EditText) findViewById(R.id.lastnameValue);
            editText.setText(user.getString("last_name"));
            editText = (EditText) findViewById(R.id.searchKeywords);
            editText.setText(user.getString("search_key_words"));
        } catch (Exception e)
        {
            Log.e(TAG,"error parsing JSON", e);
        }
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        super.onDestroy();
    }

    /**
     * Callback for when the save button is pressed. Prompt the user for a profile name.
     * If the chosen name is already in use, prompt the user to overwrite the existing profile.
     */
    private class ConnectToCloudant extends AsyncTask<String, Void, String> {
        IoTStarterApplication _app;
        User _user = null;

        public ConnectToCloudant(User user, IoTStarterApplication app) {
            super();
            _app = app;
            _user = user;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Create an ExampleDocument and save it in the database
                //db.(new ExampleDocument(true));
                _app.userDB.update(_user);
                //System.out.println("You have inserted the document");

            } catch (Exception ex) {
                Log.d(TAG, "KAD " + ex.getMessage());
                ex.printStackTrace();
            }

            return null;
        }

    }
    private class ConnectToRESTAPI extends AsyncTask<String, Void, String> {
        IoTStarterApplication _app;
        User _user = null;
        JSONObject _json = null;

        public ConnectToRESTAPI(JSONObject json, IoTStarterApplication app) {
            super();
            _app = app;
            _json = json;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                try {
                    HttpPost httpPost = new HttpPost("https://new-node-red-demo-kad.mybluemix.net/updateuser");
                    Log.d("debugme", "POST DATA = " + _json.toString());
                    StringEntity entity = new StringEntity(_json.toString(), HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(httpPost);
                    Log.d("debugme", response.getStatusLine().toString());
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            } catch (Exception ex) {
                Log.d(TAG, "KAD " + ex.getMessage());
                ex.printStackTrace();
            }
finish();
            return null;
        }

    }

    private void handleSave(View v)  {
        Log.d(TAG, ".handleSave() entered");

        final EditText userName   = (EditText)findViewById(R.id.usernameValue);
        final EditText firstName   = (EditText)findViewById(R.id.firstnameValue);
        final EditText lastName   = (EditText)findViewById(R.id.lastnameValue);
        final EditText search_key_words   = (EditText)findViewById(R.id.searchKeywords);

        app = (IoTStarterApplication) getApplication();
        JSONObject user = app.appUser;
        try {
            // Build the JSON object to pass parameters

            user.put("username", userName.getText().toString());
            user.put("first_name", firstName.getText().toString());
            user.put("last_name", lastName.getText().toString());
            user.put("search_key_words", search_key_words.getText().toString());
            user.put("last_modified", android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()));
            new ConnectToRESTAPI(user, app).execute("");

// Create the POST object and add the parameters

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, user.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, ".onCreateOptions() entered");

        return true;
    }

    public String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
            Log.d("debugmebefore",result);

        } catch (Exception e) {
            Log.d("debugme", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        IoTStarterApplication _app = null;
        JSONObject appUser = null;

        public HttpAsyncTask(IoTStarterApplication app) {
            super();
            _app = app;
            appUser = _app.appUser;

        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d("debugme", " IN ASYNC KAD");
            String result = GET(urls[0]);
            try {
                appUser = new JSONArray(result).getJSONObject(0);

            Log.d("debugme", appUser.getString("username"));
            } catch (Exception e) {
                Log.e("debugme", "IN HERE", e);
            }
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            Log.d("debugmeafter", appUser.toString());
            return result;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                appUser = new JSONObject(result);

                //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
                Log.d(TAG, appUser.toString());
            } catch (Exception e)
            {
                Log.e(TAG, "Error", e);
            }
        }
    }

}
