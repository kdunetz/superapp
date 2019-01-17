package com.ibm.iot.android.iotstarter.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kevindunetz on 1/16/19.
 */

/**
 * Android GetImageTask
 */
public class GetImageTask extends AsyncTask<String, Void, Bitmap>
{
    private static final String TAG = "ImageTask";

    private Context mContext;
    private String mAction;
    private IoTStarterApplication mApp;

    public GetImageTask(Context context, IoTStarterApplication app, String action)
    {
        mContext = context;
        mApp = app;
        mAction = action;
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        try
        {
           String username = params[0];
            Log.d("debugme", "IN HERE - " + username);

            try {
                String urlString = "https://storage.googleapis.com/superapp-pictures/" +  username.replace("@", "%40") + ".png";
                Log.d("debugmebitmap", "IN HERE - " + urlString);
                URL url = new URL(urlString);
                Log.d("debugme", "IN HERE - " + username);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "POOPOO", e);
                return null;
            }
        }
        catch (Exception e)
        {
            // TODO handle this properly
            e.printStackTrace();
            return null;
        }
    }

    /**
     * `onPostExecute` is run after `doInBackground`, and it's
     * run on the main/ui thread, so you it's safe to update ui
     * components from it. (this is the correct way to update ui
     * components.)
     */
    @Override
    protected void onPostExecute(Bitmap result)
    {
        try {
            Log.d("debugme", " IN HERE onPostExecute");
            mApp.bitmap = result;

        } catch (Exception e) {
            Log.e("debugme", "blah", e);
            e.printStackTrace();
        }
    }

}
