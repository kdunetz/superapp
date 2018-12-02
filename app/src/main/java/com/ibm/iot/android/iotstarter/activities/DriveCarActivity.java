package com.ibm.iot.android.iotstarter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.utils.Constants;
import com.ibm.iot.android.iotstarter.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DriveCarActivity extends Activity {
    private final static String TAG = DriveCarActivity.class.getName();
    protected IoTStarterApplication app;
    private static final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_car);

        app = (IoTStarterApplication) getApplication();

        try {

            final EditText firstName = (EditText) findViewById(R.id.first_name);
            final EditText lastName = (EditText) findViewById(R.id.last_name);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("first_name", firstName.getText());
            jsonObject.put("last_name", lastName.getText());
            app.appUser = jsonObject;


            //searchKeywords.setText(app.appUser.getString("search_key_words"));
            ImageButton voiceResponse = (ImageButton) this.findViewById((R.id.get_voice_command));

            voiceResponse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVoiceInput();
                }
            });

            Button button = (Button) findViewById(R.id.drive_forward);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        app.mPubsubPublisher.start(Constants.DRIVE_FORWARD, 300);

                        if (false)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriveCarActivity.this);
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
            button = (Button) findViewById(R.id.drive_backward);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        app.mPubsubPublisher.start(Constants.DRIVE_BACKWARD, -300);

                        if (false)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriveCarActivity.this);
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

            button = (Button) findViewById(R.id.gripper_open);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        app.mPubsubPublisher.start(Constants.GRIPPER_OPEN, -1);

                        if (false)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriveCarActivity.this);
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

            button = (Button) findViewById(R.id.gripper_close);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        app.mPubsubPublisher.start(Constants.GRIPPER_CLOSE, -1);

                        if (false)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriveCarActivity.this);
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
            button = (Button) findViewById(R.id.turn_left);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        app.mPubsubPublisher.start(Constants.TURN_LEFT, -45);

                        if (false)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriveCarActivity.this);
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
            button = (Button) findViewById(R.id.turn_right);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        app.mPubsubPublisher.start(Constants.TURN_RIGHT, 45);

                        if (false)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriveCarActivity.this);
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
            button = (Button) findViewById(R.id.speed);
            button.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        // SAVE THING TO DATABASE
                        //app.mPubsubPublisher.sendMessage(Constants.DRIVE_FORWARD, 11);
                        app.mPubsubPublisher.start(Constants.SPEED, 45);

                        if (false)
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriveCarActivity.this);
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
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a) {

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to   startResolutionForResult().
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("debugme", "SPEECH RESULT = " + result.get(0));
                    String text = result.get(0);

                    Toast.makeText(this, text, Toast.LENGTH_LONG);

                    app.engine.speak(text, TextToSpeech.QUEUE_ADD, null, null);


                    if (true) {
                        if (text.equalsIgnoreCase("help")) {
                            app.engine.speak("You can say send text to PERSON and then the actual text message", TextToSpeech.QUEUE_ADD, null, null);

                        }
                        else if (text.equalsIgnoreCase("drive forward"))
                            app.mPubsubPublisher.start(Constants.DRIVE_FORWARD, 300);
                        else if (text.equalsIgnoreCase("drive backward"))
                            app.mPubsubPublisher.start(Constants.DRIVE_BACKWARD, -300);
                        else if (text.equalsIgnoreCase("open gripper"))
                            app.mPubsubPublisher.start(Constants.GRIPPER_OPEN, -1);
                        else if (text.equalsIgnoreCase("close gripper"))
                            app.mPubsubPublisher.start(Constants.GRIPPER_CLOSE, -1);
                        else if (text.equalsIgnoreCase("turn right"))
                            app.mPubsubPublisher.start(Constants.TURN_RIGHT, 45);
                        else if (text.equalsIgnoreCase("turn left"))
                            app.mPubsubPublisher.start(Constants.TURN_LEFT, -45);

                        else
                            app.engine.speak("Unrecognized command", TextToSpeech.QUEUE_ADD, null, null);

                    }

                }
                break;
        }
    }

}
