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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.net.URI;
import org.apache.http.client.methods.HttpGet;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.ibm.iot.android.iotstarter.utils.RestTask;


import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.User;
import com.ibm.iot.android.iotstarter.utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Profiles activity lists saved connection profiles to use to connect to IoT.
 */
public class FindNeighborsActivity extends Activity {
    private final static String TAG = FindNeighborsActivity.class.getName();
    protected Context context;
    protected IoTStarterApplication app;
    protected JSONObject appUser;
    protected BroadcastReceiver broadcastReceiver;


    protected ListView listView;
    protected ArrayAdapter<String> listAdapter;
    //private static final String TEST_URL                   = "https://new-node-red-demo-kad.mybluemix.net/getAll?object_name=object_one";
    private static final String TEST_URL                   = "http://superapp-apis.appspot.com/superapp_users";

    private static final String ACTION_FOR_INTENT_CALLBACK = "THIS_IS_A_UNIQUE_KEY_WE_USE_TO_COMMUNICATE";

    ProgressDialog progress;


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
        context = getApplicationContext();


        setContentView(R.layout.find_neighbors);
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);

//        TextView tx= new TextView(this);
//      tx.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//        tx.setText("ANDROID APP");
//        layout.addView(tx);
        listView = (ListView) findViewById( R.id.list_view );

        getContent();

    }

    private void getContent()
    {
        // the request
        try
        {
            HttpGet httpGet = new HttpGet(new URI(TEST_URL));
            RestTask task = new RestTask(this, ACTION_FOR_INTENT_CALLBACK);
            task.execute(httpGet);
            progress = ProgressDialog.show(this, "Getting Data ...", "Waiting For Results...", true);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
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

        context = getApplicationContext();
        app = (IoTStarterApplication) getApplication();
        try {
            appUser = app.appUser;
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
     * Our Broadcast Receiver. We get notified that the data is ready this way.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // clear the progress indicator
            if (progress != null)
            {
                progress.dismiss();
            }
            Log.d(TAG, "IN HERE OTHER BROADCAST RECEIVER");
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            //ourTextView.setText(response);
            try {
                JSONArray jsonArray = new JSONArray(response);

                // Create ArrayAdapter using the planet list.
                listAdapter = new ArrayAdapter<String>(context, R.layout.simplerow);
                // Add more planets. If you passed a String[] instead of a List<String>
                // into the ArrayAdapter constructor, you must not add more items.
                // Otherwise an exception will occur.
                for (int x = 0; x < jsonArray.length(); x++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(x);
                    String userName = Utility.getJSONString(jsonObject, "username");
                    String searchKeyWords = Utility.getJSONString(jsonObject, "search_key_words");
                    if (Utility.getJSONString(appUser, "username") != userName && searchKeyWords.length() > 0 && Utility.getJSONString(appUser, "search_key_words").indexOf(searchKeyWords) >= 0)
                        listAdapter.add(Utility.getJSONString(jsonObject,"first_name") + " " + Utility.getJSONString(jsonObject,"last_name") + " - " + Utility.getJSONString(jsonObject,"search_key_words"));

                }

            } catch (Exception e)
            {
                Log.e(TAG, "JSON Error", e);
            }

            // Set the ArrayAdapter as the ListView's adapter.
            listView.setAdapter( listAdapter );
            //
            // my old json code was here. this is where you will parse it.
            //
        }
    };
}
