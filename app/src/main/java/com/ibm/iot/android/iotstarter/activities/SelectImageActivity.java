package com.ibm.iot.android.iotstarter.activities;

/**
 * Created by kevindunetz on 5/13/18.
 */

        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.drawable.BitmapDrawable;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.util.Base64;
        import android.util.Log;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.ibm.iot.android.iotstarter.R;
        import com.ibm.iot.android.iotstarter.utils.RestTask;
        import com.ibm.iot.android.iotstarter.utils.Utility;

        import org.apache.commons.io.output.ByteArrayOutputStream;
        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.util.zip.Inflater;


public class SelectImageActivity extends Activity {
    private static final int SELECTED_PIC = 1;
    private static final String ACTION_FOR_INTENT_CALLBACK = "DYNAMIC_FORM";

    private final static String TAG = FindNeighborsActivity.class.getName();

    ImageView imageView;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_image);

        imageView = (ImageView) findViewById(R.id.imageView);
        /*
        Button b = (Button)findViewById((R.id.rotate_image_ninety));

        b.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {

                    imageView.setRotation(90F);
                    Log.d(TAG, "KAD saved");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "KAD CRASH");
                }
                //System.out.println("You have inserted the document");
            }
        });
        */
    }

    public void btnClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECTED_PIC);
    }

    public void scanImage(View v) {
        String json = "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"image\": {\n" +
                "        \"source\": {\n" +
                "          \"gcsImageUri\": \"gs://awesome-lotus-201318.appspot.com/20180513_180558.jpg\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"features\": [\n" +
                "        {\n" +
                "          \"type\": \"TEXT_DETECTION\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        //Utility.callRESTAPI(v.getContext(), "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyBESW49t48CGrcITU7-V34PeQkWAoOeBYM", "post", ACTION_FOR_INTENT_CALLBACK, json);
        Utility.callRESTAPI(v.getContext(), "https://new-node-red-demo-kad.mybluemix.net/OCRImage", "get", ACTION_FOR_INTENT_CALLBACK, json);

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("debugme", "IN HERE SelectImageActivity BroadcastReceiver");
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            if (progress != null) {
                progress.dismiss();
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
                Log.d("debugme", response);

            String callBack = intent.getAction();

            if (callBack.equals(ACTION_FOR_INTENT_CALLBACK)) {
                Log.d(TAG, "GOT ACTION_FOR_INTENT_CALLBACK");

                //ourTextView.setText(response);
                try {
                    //JSONObject jsonObject = new JSONObject(response);
                    String value = response.substring(response.indexOf("description") + 14);
                    value = value.substring(0, value.indexOf("\","));
                    String testString = "BUY ONE GET ONE FREE QUARTE? POUNDER\nW/CHEESE OR EG MCAUF IN\nGo to www.mcdvoice.cum with ri 7 days\nand tell us aou your isit\nValidation Code:_\nExpires 30 days after rece pt date\nValid at participatig US M onald's\nSurvey C de\n06345-03990-42118 12517- 10009-4\nMcDonald's Rest uranit lE345\n5651 BURK: CENTRE PY\nBURKE, VA 22015\nTEL# 703 210 3652\nKS# 3\nSide\n0/21/20 E 12:51 PM\nOrder 99\nFREE Large Fries with any puchase\n";
                    Utility.parseOutReceipt(value);
                            //Log.d("debugme", jsonArray.toString());
                    TextView tv = (TextView) findViewById(R.id.decoded_text);
                    //tv.setText(jsonArray.toString());
                    value = value.replace("\\n", System.getProperty("line.separator"));
                    tv.setText(value);
                } catch (Exception e) {
                    Log.e(TAG, "GOT ACTION_FOR_INTENT_CALLBACK", e);

                }
            }
        }
    };

        @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK));

    }

            @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECTED_PIC:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        Uri uri = data.getData();
                        Log.d("debugme", "in here - " + uri);

                        String[] projection = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(projection[0]);
                        String filepath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.d("debugme", columnIndex + " - " + filepath);

                        bitmap = Utility.decodeFile(new File(filepath));
                        if (bitmap == null) Log.d("debugme", "bitmap is null");
                        //bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                        imageView.setImageBitmap(bitmap);
                    }
                    catch (Exception e)
                    {
                        Log.e("debugme", "asdkjfa", e);
                        e.printStackTrace();
                    }
                    //imageView.setImageURI(uri);
                    String base64String = Utility.encodeTobase64(bitmap);
                    String json = "{\n" +
                            "  \"requests\": [\n" +
                            "    {\n" +
                            "      \"image\": {\n" +
                            "        \"source\": {\n" +
                            "          \"gcsImageUri\": \"gs://awesome-lotus-201318.appspot.com/20180513_180558.jpg\"\n" +
                            "        }\n" +
                            "      },\n" +
                            "      \"features\": [\n" +
                            "        {\n" +
                            "          \"type\": \"TEXT_DETECTION\"\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}";
                    json = "{\n" +
                            "  \"requests\": [\n" +
                            "    {\n" +
                            "      \"image\": {\n" +
                            "        \"content\": \"" + base64String + "\"\n" +
                            "      },\n" +
                            "      \"features\": [\n" +
                            "        {\n" +
                            "          \"type\": \"TEXT_DETECTION\"\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}";
                    Log.d("debugme", json);
                    Utility.callRESTAPI(imageView.getContext(), "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyBESW49t48CGrcITU7-V34PeQkWAoOeBYM", "post", ACTION_FOR_INTENT_CALLBACK, json);
                    //Utility.callRESTAPI(imageView.getContext(), "https://new-node-red-demo-kad.mybluemix.net/OCRImage", "post", ACTION_FOR_INTENT_CALLBACK, json);
                    progress = ProgressDialog.show(this, "Getting Data ...", "Waiting For Results...", true);

                    //imageView.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }
    }


}
