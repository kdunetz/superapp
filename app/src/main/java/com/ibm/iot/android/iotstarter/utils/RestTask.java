package com.ibm.iot.android.iotstarter.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.Deflater;

/**
 * Created by kevindunetz on 4/30/18.
 */

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.iot.android.iotstarter.IoTStarterApplication;

/**
 * Android RestTask (REST) from the Android Recipes book.
 */
public class RestTask extends AsyncTask<HttpUriRequest, Void, String>
{
    private static final String TAG = "AARestTask";
    public static final String HTTP_RESPONSE = "httpResponse";

    private Context mContext;
    private HttpClient mClient;
    private String mAction;

    public RestTask(Context context, String action)
    {
        mContext = context;
        mAction = action;
        mClient = new DefaultHttpClient();
    }

    public RestTask(Context context, String action, HttpClient client)
    {
        mContext = context;
        mAction = action;
        mClient = client;
    }

    @Override
    protected String doInBackground(HttpUriRequest... params)
    {
        try
        {
            HttpUriRequest request = params[0];
            HttpResponse serverResponse = mClient.execute(request);
            BasicResponseHandler handler = new BasicResponseHandler();
            return handler.handleResponse(serverResponse);
        }
        catch (Exception e)
        {
            // TODO handle this properly
            e.printStackTrace();
            return "";
        }
    }

    /**
     * `onPostExecute` is run after `doInBackground`, and it's
     * run on the main/ui thread, so you it's safe to update ui
     * components from it. (this is the correct way to update ui
     * components.)
     */
    @Override
    protected void onPostExecute(String result)
    {
        try {
            Log.i("debugme", "RESULT = " + result);
            Intent intent = new Intent(mAction);
            if (result.length() < 10000)
                intent.putExtra(HTTP_RESPONSE, result);
            else
            {
                intent.putExtra(HTTP_RESPONSE, "GET_GLOBAL");
                Utility.setGlobalStr(result);
            }
            // broadcast the completion
            mContext.sendBroadcast(intent);

        } catch (Exception e) {
            Log.e("debugme", "blah", e);
            e.printStackTrace();
        }
    }

    public String compress(String input) {

        try {
            byte[] inputBytes = input.getBytes("UTF-8");

            // Compress the bytes
            byte[] output = new byte[1000000];
            Deflater compresser = new Deflater();
            compresser.setInput(inputBytes);
            compresser.finish();
            int compressedDataLength = compresser.deflate(output);
            compresser.end();
            return output.toString();
        }
        catch(Exception e)
        {

        }
        return input;
    }

}
