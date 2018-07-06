package com.ibm.iot.android.iotstarter.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.Deal;
import com.ibm.iot.android.iotstarter.utils.RestTask;
import com.ibm.iot.android.iotstarter.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConfirmOCRScan extends Activity {
    private final static String TAG = ConfirmOCRScan.class.getName();
    private static final String ACTION_FOR_SAVE_RESULT = "SAVE_RESULT";
    private static JSONObject _jsonObject;
    protected IoTStarterApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ocrscan);

        Bundle b = getIntent().getExtras();
        Log.d("debugme", "start ConfirmOCRScan Activity");
        String _json = b.getString("json");
        try {

            final JSONObject jsonObj = new JSONObject(_json);
            final EditText companyName = (EditText) findViewById(R.id.company_name);
            final EditText ocr = (EditText) findViewById(R.id.ocr_data);
            final EditText website = (EditText) findViewById(R.id.website);

            final EditText promoCode = (EditText) findViewById(R.id.promo_code);
            final EditText date = (EditText) findViewById(R.id.date);
            final EditText numberDays = (EditText) findViewById(R.id.num_days);
            final EditText couponExpirationDays = (EditText) findViewById(R.id.coupon_expiration_date_value);


            companyName.setText(Utility.getJSONString(jsonObj, "company_name"));
            ocr.setText(Utility.getJSONString(jsonObj, "deal"));
            website.setText(Utility.getJSONString(jsonObj, "website"));
            promoCode.setText(Utility.getJSONString(jsonObj, "promo_code"));
            date.setText(Utility.getJSONString(jsonObj, "date"));
            if (Utility.getJSONString(jsonObj, "date").length() == 0)
                date.setText(Utility.getCurrentDateTime());

            numberDays.setText(Utility.getJSONString(jsonObj, "num_days"));
            Log.d("debugme", "coupon days" + (Utility.getJSONString(jsonObj, "coupon_expiration_days")));
            couponExpirationDays.setText((Utility.getJSONString(jsonObj, "coupon_expiration_days", "30")));

            /*
            Button websiteButton = (Button)findViewById((R.id.website_button));
            websiteButton.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        String url = "";
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mcdvoice.com?CN1=06345&CN2=03990&CN3=42118&CN4=12517&CN5=000009&CN6=4"));
                        startActivity(browserIntent);

                        Log.d(TAG, "KAD website button pushed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "KAD CRASH");
                    }
                    //System.out.println("You have inserted the document");
                }
            });
*/
            Button button = (Button) findViewById(R.id.save_ocr_record);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        boolean error = false;
                        if (companyName.getText().toString().length() == 0)
                            error = true;
                        if (ocr.getText().toString().length() == 0)
                            error = true;
                        if (couponExpirationDays.getText().toString().length() == 0)
                            error = true;
                        if (date.getText().toString().length() == 0)
                            error = true;

                        if (error)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ConfirmOCRScan.this);
                            dialog.setMessage("Please enter all required fields");
                            //dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            //    @Override
                            //   public void onClick(DialogInterface dialog, int which) {
                                    //this will navigate user to the device location settings screen
                            //        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            //        startActivity(intent);
                            //    }
                            //});
                            AlertDialog alert = dialog.create();
                            alert.show();
                        }
                        else
                        {

                            String username = "";
                            if (app != null && app.appUser != null)
                                username = app.appUser.getString("username");
                            jsonObj.put("username", username);
                            jsonObj.put("company_name", companyName.getText().toString());
                            jsonObj.put("deal", ocr.getText().toString());
                            jsonObj.put("website", website.getText().toString());
                            jsonObj.put("promo_code", promoCode.getText().toString());
                            jsonObj.put("date", date.getText().toString());
                            jsonObj.put("num_days", numberDays.getText().toString());
                            jsonObj.put("coupon_expiration_days", couponExpirationDays.getText().toString());
                            if (jsonObj.get("website").toString().length() == 0 &&
                                jsonObj.get("promo_code").toString().length() == 0 &&
                                jsonObj.get("num_days").toString().length() == 0)
                            {
                                jsonObj.put("play_sides", 1);
                            }
                            jsonObj.put("_type", "receipt");


                            String url = "https://new-node-red-demo-kad.mybluemix.net/save?object_name=deal";
    _jsonObject = jsonObj;
                            Utility.callRESTAPI(v.getContext(), url, "post", ACTION_FOR_SAVE_RESULT, jsonObj.toString());


                            Log.d(TAG, "KAD saved");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "KAD CRASHED", e);
                    }
                    //System.out.println("You have inserted the document");
                }
            });

            button = (Button) findViewById(R.id.take_another_picture);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(cameraIntent);
                }
            });


        } catch (Exception e) {
            Log.e("debugme", "Problem populating the EditView", e);
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_SAVE_RESULT));

    }

    /**
     * Our Broadcast Receiver. We get notified that the data is ready this way.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // clear the progress indicator
            //if (progress != null) {
            //    progress.dismiss();
            //}
            Log.d("debugme", "IN HERE ConfirmOCRScan BroadcastReceiver");

            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            String callBack = intent.getAction();

            if (callBack.equals(ACTION_FOR_SAVE_RESULT)) {
                Log.d(TAG, "GOT ACTION_FOR_SAVE_RESULT");
                try {
                    String url = Utility.getJSONString(_jsonObject, "website");
                    String promoCode = Utility.getJSONString(_jsonObject, "promo_code");
                    // for debugging url = "www.mcdvoice.com";
                    // for debugging promoCode = "39301-3432-234234 223423- 242342 23423";
                    String companyName = "";

                    if (url.indexOf("mcd") >= 0) url = "www.mcdvoice.com?";
                    if (url.toUpperCase().indexOf("POPE") > 0) {
                        companyName = "Popeyes";
                        url = "www.tellpopeyes.com?";
                    }
                    if (url.toUpperCase().indexOf("DUNKIN") >= 0 || url.toUpperCase().indexOf("DUNK") >= 0)
                    {
                        companyName = "Dunkin Donuts";
                        url = "https://www.telldunkinbaskin.com/Index.aspx?LanguageID=US";
                    }
                    if (url.toUpperCase().indexOf("GIANT") >= 0)
                    {
                        companyName = "Giant Foods";
                        url = "https://www.talktogiantfoods.com";

                    }

                    String[] list = promoCode.split("[- ]+");
                    String promoCodeString = "";
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("simple text", "Hello, World!");

                    if (companyName.equals("Giant Foods"))
                    {

                    }
                    else
                    if (promoCode.length() > 0 && list.length > 0) {
                        Log.d("debugme", list.length + " - " + list);
                        if (list.length == 6)
                            promoCodeString = "CN1=" + list[0] + "&CN2=" + list[1] + "&CN3=" + list[2] + "&CN4=" + list[3] + "&CN5=" + list[4] + "&CN6=" + list[5];
                        if (list.length == 5)
                            promoCodeString = "CN1=" + list[0] + "&CN2=" + list[1] + "&CN3=" + list[2] + "&CN4=" + list[3] + "&CN5=" + list[4];
                        if (list.length == 4) // Dunkin Donuts
                            promoCodeString = "&CN1=" + list[0] + "&CN2=" + list[1] + "&CN3=" + list[2] + "&CN4=" + list[3];

                    }
                    if (url.length() > 0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((url.indexOf("http") < 0 ? "http:////" : "") + url + promoCodeString));
                        startActivity(browserIntent);
                    }
                    else
                    {
                        ((Activity)context).finish();
                    }
                }catch (Exception e)
                {
                    Log.e("debugme", "IN HERE", e);
                }
                ((Activity)context).finish();
            }
        }
    };
}
