package com.ibm.iot.android.iotstarter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity2 extends Activity {
    private final static String TAG = RegisterActivity2.class.getName();
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
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        // SAVE THING TO DATABASE
                        Utility.saveUser(v.getContext(), app);
                        if (false)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity2.this);
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "KAD CRASH", e);
                    }
                    //System.out.println("You have inserted the document");
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
