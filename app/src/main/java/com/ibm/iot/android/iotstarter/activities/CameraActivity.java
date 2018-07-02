package com.ibm.iot.android.iotstarter.activities;

/**
 * Created by kevindunetz on 5/15/18.
 */

/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
    import android.util.Log;

    import com.ibm.iot.android.iotstarter.IoTStarterApplication;
    import com.ibm.iot.android.iotstarter.R;
    import com.ibm.iot.android.iotstarter.fragments.Camera2BasicFragment;
    import com.ibm.iot.android.iotstarter.utils.RestTask;
    import com.ibm.iot.android.iotstarter.utils.Utility;

    import org.json.JSONObject;

public class CameraActivity extends AppCompatActivity {
    private final static String TAG = CameraActivity.class.getName();
    private static final String ACTION_FOR_INTENT_CALLBACK = "GOOGLE_API_CALL";
    //private static final String ACTION_FOR_SAVE_CALLBACK = "PICTURE_SAVE";

    protected IoTStarterApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        app = (IoTStarterApplication) getApplication();

        registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK));
        //registerReceiver(receiver, new IntentFilter(ACTION_FOR_SAVE_CALLBACK));

    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("debugme", "IN HERE CameraActivity BroadcastReceiver");
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            if (Utility.getProgressDialog() != null) {
               Utility.getProgressDialog().dismiss();
                Utility.setProgressDialog(null);
            }
            try {
                if (response.equals("GET_GLOBAL"))
                    response = Utility.getGlobalStr();
            }
            catch (Exception e)
            {
                Log.e("debugme","Adsf", e);
                e.printStackTrace();
            }
            Log.d("debugme", "RESPONSE = " + response);

            String callBack = intent.getAction();

            //response = "BUY ONE GET ONE FREE QUARTE? POUNDER\nW/CHEESE OR EG MCAUF IN\nGo to www.mcdvoice.cum with ri 7 days\nand tell us aou your isit\nValidation Code:_\nExpires 30 days after rece pt date\nValid at participatig US M onald's\nSurvey C de\n06345-03990-42118 12517- 10009-4\nMcDonald's Rest uranit lE345\n5651 BURK: CENTRE PY\nBURKE, VA 22015\nTEL# 703 210 3652\nKS# 3\nSide\n0/21/20 E 12:51 PM\nOrder 99\nFREE Large Fries with any puchase\n";

            if (callBack.equals(ACTION_FOR_INTENT_CALLBACK) && response.length() > 15) {
                Log.d("debugme", "GOT ACTION_FOR_INTENT_CALLBACK");

                //ourTextView.setText(response);
                try {
                    //JSONObject jsonObject = new JSONObject(response);
                    String value = response.substring(response.indexOf("description") + 15);
                    if (value.indexOf("\",") > 0)
                       value = value.substring(0, value.indexOf("\","));
                    JSONObject parsedData = Utility.parseOutReceipt(value);
                    //Log.d("debugme", jsonArray.toString());
                    Log.d("debugme", "response = " + value);
                    //TextView tv = (TextView) findViewById(R.id.decoded_text);
                    //tv.setText(jsonArray.toString());

                    //value = value.replace("\\n", System.getProperty("line.separator"));
                    value = value.replaceAll("\\\\n", " ");

                    //tv.setText(value);
                    //TODO GET LAT LONG
                    String latitude = app.getCurrentLocation().getLatitude() + "";
                    String longitude = app.getCurrentLocation().getLongitude() + "";
                    JSONObject json = new JSONObject();
                    json.put("latitude", latitude);
                    json.put("longitude", longitude);
                    if (value.length() > 1024)
                        value = value.substring(0,1024);
                    json.put("deal", value);
                    json.put("creation_date", Utility.getCurrentDateTime());
                    json.put("username", app.getCurrentUser());
                    json.put("website", Utility.getJSONString(parsedData, "website"));

                    json.put("date", Utility.getJSONString(parsedData, "date"));
                    json.put("promo_code", Utility.getJSONString(parsedData, "promo_code"));
                    json.put("num_days", Utility.getJSONString(parsedData, "num_days"));
                    json.put("coupon_expiration_days", Utility.getJSONString(parsedData, "coupon_expiration_days"));

                    json.put("company_name", Utility.getJSONString(parsedData, "company_name"));
                    if (response.length() < 10) response = "Dummy Data";

                    Intent confirmOCRIntent = new Intent(context, ConfirmOCRScan.class);
                    Bundle b = new Bundle();
                    b.putString("json", json.toString());
                    confirmOCRIntent.putExtras(b);
                    startActivity(confirmOCRIntent, b);
finish();
                    if (false) {
                        String url = "https://new-node-red-demo-kad.mybluemix.net/save?object_name=deal";
                        Log.d("debugme", url);
                        Log.d("debugme", json.toString());
                        //Utility.callRESTAPI(context, url, "post", ACTION_FOR_SAVE_CALLBACK, json.toString());
                    }

                } catch (Exception e) {
                    Log.e("debugme", "GOT ACTION_FOR_INTENT_CALLBACK", e);

                }
            }
        }
    };


}
