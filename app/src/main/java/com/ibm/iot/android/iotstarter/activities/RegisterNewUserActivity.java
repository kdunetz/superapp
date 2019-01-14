package com.ibm.iot.android.iotstarter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.RequestQueueSingleton;
import com.ibm.iot.android.iotstarter.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegisterNewUserActivity extends Activity {
    private final static String TAG = RegisterNewUserActivity.class.getName();
    protected IoTStarterApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        app = (IoTStarterApplication) getApplication();

        try {

            final EditText firstName = (EditText) findViewById(R.id.first_name);
            final EditText lastName = (EditText) findViewById(R.id.last_name);
            final EditText username = (EditText) findViewById(R.id.username_email);
            final EditText password1 = (EditText) findViewById(R.id.password1);
            final EditText password2 = (EditText) findViewById(R.id.password2);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("first_name", firstName.getText());
            jsonObject.put("last_name", lastName.getText());
            jsonObject.put("username", username.getText());
            jsonObject.put("password", password1.getText());
            app.appUser = jsonObject;

            //searchKeywords.setText(app.appUser.getString("search_key_words"));

            Button button = (Button) findViewById(R.id.register_user);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(final View v) {

                    String url = "https://superapp-apis.appspot.com/api/superapp_users?filter[where][username]=" + username.getText();
                    final JsonArrayRequest secondRequest = new JsonArrayRequest

                            (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray response) {

                                    Log.d("debugme", "Refresh AppUser Response -" + response.toString());
                                    try {
                                        if (response.length() > 0) {
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterNewUserActivity.this);
                                            dialog.setMessage("Username already exists.");
                                            AlertDialog alert = dialog.create();
                                            alert.show();
                                        } else if (firstName.getText().length() == 0 || lastName.getText().length() == 0 || username.getText().length() == 0 || password1.getText().length() == 0) {
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterNewUserActivity.this);
                                            dialog.setMessage("Please enter values in all fields");
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
                                        } else if (username.getText().toString().indexOf("@") < 0 || username.getText().toString().indexOf(".") < 0) {
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterNewUserActivity.this);
                                            dialog.setMessage("Please enter valid email address for username");
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
                                        } else if (!password1.getText().toString().equals(password2.getText().toString())) {
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterNewUserActivity.this);
                                            dialog.setMessage("Both passwords need to be the same. ");
                                            Log.d("debugme", password1 + " " + password2);
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
                                        } else {
                                            // TODO Auto-generated method stub
                                            try {
                                                // SAVE THING TO DATABASE
                                                Utility.createUser(v.getContext(), app);
                                                Intent mainIntent = new Intent(RegisterNewUserActivity.this, MainActivity.class);
                                                Bundle b = new Bundle();
                                                String email = app.appUser.getString("username");
                                                Log.d("debugme", email);
                                                b.putString("email", email);
                                                Log.d("debugme", email);
                                                mainIntent.putExtras(b);

                                                RegisterNewUserActivity.this.startActivity(mainIntent, b);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Log.e(TAG, "KAD CRASH", e);
                                            }
                                            //System.out.println("You have inserted the document");
                                        }

                                    } catch (Exception e) {
                                        Log.e("debugme", "Refresh AppUser Record (Exception)", e);
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    Log.d("debugme", "Refresh AppUser Record (onErrorResponse) - " + error.getMessage());

                                }
                            });
                    try {
                        //app.appUser = response;

                        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(secondRequest);

                    } catch (Exception e) {
                        Log.e("debugme", "Refresh AppUser Record", e);
                        e.printStackTrace();
                    }


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



    }

}
