package com.ibm.iot.android.iotstarter.utils;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;
import android.util.Log;


import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;

/**
 * Created by kevindunetz on 5/13/18.
 */


public class Utility extends Object {
    private final static String TAG = Utility.class.getName();
    private static String globalStr = "";
    private static ProgressDialog progressDialog = null;

    private Utility() {
        super();
    }

    public static boolean couponActive(String date, String days) {
        if (date == null || days == null) return false;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(date));
            c.add(Calendar.DATE, parseInt(days));
            Date couponExpirationDate = c.getTime();
            if ((new Date()).before(couponExpirationDate)) {
                Log.d("debugme123", "In couponActive TRUE:" + sdf.format(c.getTime()));
                return true;
            } else {
                Log.d("debugme123", "In couponActive FALSE:" + sdf.format(c.getTime()));

                return false;
            }
        } catch (Exception e) {
            Log.e("debugme123", "Date Parser failed", e);
            e.printStackTrace();
        }
        return false;
    }

    public static String getCurrentDateTime() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Log.d("debugme", "GETTING DATE - " + (new Date()).toString());
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    public static String encodeTobase64(Bitmap image) {
        Log.d("debugme", "in encodeToBase");
        try {
            Bitmap immagex = image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

            Log.e("LOOK", imageEncoded);
            return imageEncoded;
        } catch (Exception e) {
            Log.e("debugme", "encodeTo64Base", e);
            e.printStackTrace();
        }
        return "";
    }

    public static Bitmap decodeFile(File f) {
        BitmapFactory.Options o, o2;
        FileInputStream fis;

        Bitmap b = null;
        try {
            // Decode image size
            o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int IMAGE_MAX_SIZE = 1000;
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2,
                        (int) Math.round(Math.log(IMAGE_MAX_SIZE
                                / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }

            // Decode with inSampleSize
            o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }


    public static void setGlobalStr(String str) {
        globalStr = str;
    }

    public static String getGlobalStr() {
        return globalStr;
    }

    public static void setProgressDialog(ProgressDialog pd) {
        progressDialog = pd;

    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public static void callRESTAPI(Context context, String url, String type, String callBack, String json) {
        // the request
        try {
            if (type.equals("get")) {
                HttpGet httpGet = new HttpGet(new URI(url));

                RestTask task = new RestTask(context, callBack);
                task.execute(httpGet);
            } else {
                HttpPost httpPost = new HttpPost(new URI(url));

                //String json = "{\"id\":1,\"name\":\"John\"}";
                StringEntity entity = new StringEntity(json);
                httpPost.setEntity(entity);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                RestTask task = new RestTask(context, callBack);
                task.execute(httpPost);
            }
            //progress = ProgressDialog.show(this, "Getting Data ...", "Waiting For Results...", true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }


    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return -1;
        } catch (Exception e) {
            // p_LU.log(" - ");
            return -1;
        }
    }

    public static void testCompress() {
        try {
            // Encode a String into bytes
            String inputString = "Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar Tu hi bata Pehla nasha Pehla khumaar Udta hi firoon in hawaon mein kahin Ya main jhool jaoon in ghataon mein kahin Udta hi firoon in hawaon mein kahin Ya main jhool jaoon in ghataon mein kahin Ek kar doon aasmaan zameen Kaho yaaron kya karoon kya nahin Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar Tu hi bata Pehla nasha Pehla khumaar Usne baat ki kuchh aise dhang se Sapne de gaya vo hazaaron range ke Usne baat ki kuchh aise dhang se Sapne de gaya vo hazaaron range ke Reh jaoon jaise main haar ke Aur choome vo mujhe pyaar se Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar";
            byte[] input = inputString.getBytes("UTF-8");

            // Compress the bytes
            byte[] output1 = new byte[input.length];
            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            int compressedDataLength = compresser.deflate(output1);
            compresser.end();

            String str = Base64.encodeToString(output1, 0);


            Log.d("debugme", "Deflated String:" + str);


            byte[] output2 = Base64.decode(str, 0);


            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(output2);
            byte[] result = str.getBytes();
            int resultLength = decompresser.inflate(result);
            decompresser.end();

            // Decode the bytes into a String
            String outputString = new String(result, 0, resultLength, "UTF-8");
            Log.d("debugme", "Deflated String:" + outputString);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DataFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String compress(String inputString) {
        try {
            // Encode a String into bytes
            //String inputString = "Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar Tu hi bata Pehla nasha Pehla khumaar Udta hi firoon in hawaon mein kahin Ya main jhool jaoon in ghataon mein kahin Udta hi firoon in hawaon mein kahin Ya main jhool jaoon in ghataon mein kahin Ek kar doon aasmaan zameen Kaho yaaron kya karoon kya nahin Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar Tu hi bata Pehla nasha Pehla khumaar Usne baat ki kuchh aise dhang se Sapne de gaya vo hazaaron range ke Usne baat ki kuchh aise dhang se Sapne de gaya vo hazaaron range ke Reh jaoon jaise main haar ke Aur choome vo mujhe pyaar se Pehla nasha Pehla khumaar Naya pyaar hai naya intezaar Kar loon main kya apna haal Aye dil-e-bekaraar Mere dil-e-bekaraar";
            byte[] input = inputString.getBytes("UTF-8");

            // Compress the bytes
            byte[] output1 = new byte[input.length];
            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            int compressedDataLength = compresser.deflate(output1);
            compresser.end();

            String str = Base64.encodeToString(output1, 0);


            Log.d("debugme", "Deflated String:" + str);
            return str;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "NULL";
    }

    public static String decompress(String str) {
        byte[] output2 = Base64.decode(str, 0);

        try {
            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(output2);
            byte[] result = str.getBytes();
            int resultLength = decompresser.inflate(result);
            decompresser.end();

            // Decode the bytes into a String
            String outputString = new String(result, 0, resultLength, "UTF-8");
            Log.d("debugme", "Deflated String:" + outputString);
            return outputString;
        } catch (UnsupportedEncodingException e)

        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DataFormatException e)

        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "NULL";
    }

    static public JSONObject parseOutReceipt(String value) {
        try {
            JSONObject json = new JSONObject();

            try {
                if (value.toUpperCase().indexOf("FREE") > 0) {
                    int freePosition = value.toUpperCase().indexOf("FREE");
                    String freeString = value.substring(0, freePosition);
                    int lastPosition = freeString.lastIndexOf("\\n");
                    if (lastPosition < 0) lastPosition = 0;
                    freeString = value.substring(freePosition);

                    int nextPosition = freeString.indexOf("\\n");

                    if (nextPosition < 0) nextPosition = value.length() - 1;
                    String offer = "";
                    if (lastPosition >= 0 && nextPosition >= 0)
                        offer = value.substring(lastPosition, nextPosition);

                    Log.d("debugme", "Offer = " + offer);
                    json.put("offer", offer);
                }
            } catch (Exception e) {
                Log.e("debugme", "offer doesn't work", e);
                e.printStackTrace();
            }
            if (value.indexOf("www") > 0) {
                String website = value.substring(value.indexOf("www"));
                website = website.substring(0, website.indexOf(" "));
                Log.d("debugme", "Website = " + website);
                json.put("website", website);
            }

            //Pattern p = Pattern.compile("//w+\\.//w+\\.(com|org|gov)");
            //Pattern p = Pattern.compile("(?=^.{1,253}$)(^(((?!-)[a-zA-Z0-9-]{1,63}(?<!-))|((?!-)[a-zA-Z0-9-]{1,63}(?<!-)\\.)+[a-zA-Z]{2,63})$)");
            Pattern p = Pattern.compile("((ftp|http|https):\\/\\/)?(www.)?(?!.*(ftp|http|https|www.))[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+((\\/)[\\w#]+)*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?");

            Matcher m = p.matcher(value);

            if (m.find()) {
                String url = m.group(0);
                String companyName = "";

                //Log.d("debugme", "Found Code = " + m.group(0));
                if (url.toUpperCase().indexOf("POPE") > 0) {
                    url = "www.tellpopeyes.com";
                    companyName = "Popeyes";
                }
                if (url.toUpperCase().indexOf("DUNKIN") >= 0 || url.toUpperCase().indexOf("DUNK") >= 0) {
                    companyName = "Dunkin Donuts";
                    url = "www.telldunkinbaskin.com";
                }
                if (url.toUpperCase().indexOf("GIANT") >= 0) {
                    companyName = "Giant Foods";
                    url = "https://www.talktogiantfoods.com";

                }
                json.put("website", url);
                json.put("company_name", companyName);

                Log.d("debugme", m.toString());
            }

            p = Pattern.compile("(\\d+[- ])+\\d+");
            m = p.matcher(value);

            if (m.find()) {
                //Log.d("debugme", "Found Code = " + m.group(0));
                json.put("promo_code", m.group(0));

                Log.d("debugme", m.toString());
            }
            p = Pattern.compile("(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d");
            m = p.matcher(value);
            if (m.find()) {
                //Log.d("debugme", "Found Date = " + m.group(0));
                json.put("date", m.group(0).replace("///", ""));
                Log.d("debugme", m.toString().replace("///", ""));
            }
            p = Pattern.compile("within (\\d+) days");
            m = p.matcher(value);
            if (m.find()) {
                //Log.d("debugme", "Found Date = " + m.group(0));
                json.put("num_days", m.group(1));
                Log.d("debugme", "num days = " + m.group(1));
            }
            p = Pattern.compile("(next|NEXT) (\\d+) (days|DAYS)");
            m = p.matcher(value);
            if (m.find()) {
                //Log.d("debugme", "Found Date = " + m.group(0));
                json.put("num_days", m.group(2));
                Log.d("debugme", "num days = " + m.group(2));
            }

            p = Pattern.compile("Expires (\\d+) days");
            m = p.matcher(value);
            if (m.find()) {
                //Log.d("debugme", "Found Date = " + m.group(0));
                json.put("coupon_expiration_days", m.group(1));
                Log.d("debugme", "coupon expiration days = " + m.group(1));
            }

            if (value.toUpperCase().indexOf("GIANT") >= 0) {
                json.put("company_name", "Giant Foods");
                json.put("website", "http://www.talktogiantfoods.com");

            }
            if (value.toUpperCase().indexOf("POPEYE") >= 0) {
                json.put("company_name", "Popeyes");
                json.put("website", "http://www.tellpopeyes.com");

            }
            if (value.toUpperCase().indexOf("MCD") >= 0) {
                json.put("company_name", "McDonald's");

            }
            if (value.toUpperCase().indexOf("WHOPPER") >= 0 || value.toUpperCase().indexOf("mybk") >= 0) {
                json.put("company_name", "Burger King");
                json.put("website", "http://www.mybkexperience.com");

            }

            Log.d("debugme", "JSON = " + json.toString());
            return json;
        } catch (Exception e) {
            Log.e("debugme", "Problem parsing scanned content", e);
            e.printStackTrace();
        }
        return null;
    }

    /* getString returns a null so wrapping the method so I don't have to worry about exception */
    public static String getJSONString(JSONObject json, String name) {
        try {
            return json.getString(name);
        } catch (Exception e) {
            return "";
        }

    }
    public static String getJSONString(JSONObject json, String name, String defaultValue) {
        try {
            return json.getString(name);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    public static String toSHA1(byte[] convertme) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return Base64.encodeToString(md.digest(convertme), 0);
        } catch (Exception e) {
            Log.e("debugme", "Couldn't hash password", e);
        }
        return "";
    }

    public static void loadLocalBusinesses(Context context, final IoTStarterApplication app, Location location) {
        final GoogleMap googleMap = app.getGoogleMap();

        app.localBusinesses = new JSONArray();

        Vector companies = app.getCouponCompanies();
        //Vector companies = new Vector();
        //companies.add("McDonald's");
        //companies.add("Baskin-Robbins");
        //companies.add("Chick Fillet");
        Log.d("debugme", "Getting localBusinesses receiptCompanyList = " + companies);
        for (int z = 0; z < companies.size(); z++) {
            String company = (String) companies.elementAt(z);
            company.replaceAll(" ", "%20");
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=" + app.localBusinessSearchRadius + "&keyword=" + company + "&key=" +
                    "AIzaSyDCDhTlu3ZBZ3BvtgUbQfS1DqHdPWCWzzk";
            Log.d("debugme", "Getting localBusinesses Request -  " + url);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("debugme", "Getting localBusinesses Response -" + response.toString());
                            try {
                                JSONArray array = response.getJSONArray("results");
                                for (int x = 0; x < array.length(); x++) {
                                    JSONObject obj = array.getJSONObject(x);
                                    String name = obj.getString("name");
                                    String id = obj.getString("id");
                                    Log.d("debugme", "Getting localBusinesses name = " + name);
                                    double latitude = (Double) obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                    double longitude = (Double) obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                    Log.d("debugme", "Getting localBusinesses location =  " + latitude + "," + longitude);
                                    JSONObject business = new JSONObject();
                                    business.put("name", name);
                                    business.put("latitude", latitude);
                                    business.put("longitude", longitude);
                                    business.put("id", id);
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    if (googleMap != null) {
                                        BitmapDescriptor newBitmap = null;

                                        if (Utility.companyNameMatch(app, name)) {
                                                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.dollarsign_29x29);
                                            Marker now = googleMap.addMarker(new MarkerOptions().icon(newBitmap).position(latLng).title(name));
                                        }
                                        else {
                                            Marker now = googleMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                        }
                                    }
                                    app.localBusinesses.put(business);
                                    Log.d("debugme", "Getting localBusinesses Length = " + app.localBusinesses.length());
                                }
                            } catch (Exception e) {
                                Log.e("debugme", "Problem getting localBusinesses Google API", e);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.d("debugme", "Problem getting localBusinesses - " + error.getMessage());

                        }
                    });
            RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        }
    }
    public static void saveUser(final Context context, final IoTStarterApplication app) {

        String url1 = "";
        try {
            url1 = "https://new-node-red-demo-kad.mybluemix.net/save?object_name=object_one";
        }
        catch (Exception e) {
            Log.e("debugme", "Couldn't find ID in App User Record...returning without doing anything", e);
            return;
        }
        String url2 = "";
        try {
            url2 = "https://new-node-red-demo-kad.mybluemix.net/getobject?object_name=object_one&id=" + app.appUser.getString("_id");
        }
        catch (Exception e) {
            Log.e("debugme", "Couldn't find ID in App User Record...returning without doing anything", e);
            return;
        }

        Log.d("debugme", "Getting AppUser Request -  " + url1);
        JsonObjectRequest jsonObjectRequest = null;
try {
    final JsonObjectRequest secondRequest = new JsonObjectRequest

            (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    Log.d("debugme", "Refresh AppUser Response -" + response.toString());
                    try {
                        app.setAppUser(response);
                    } catch (Exception e) {
                        Log.e("debugme", "Refresh AppUser Record", e);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Log.d("debugme", "Refresh AppUser Record - " + error.getMessage());

                }
            });


    jsonObjectRequest = new JsonObjectRequest
            (Request.Method.POST, url1, new JSONObject(app.appUser.toString()), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    Log.d("debugme", "Refresh AppUser Response First One -" + response.toString());
                    try {
                        //app.appUser = response;

                        RequestQueueSingleton.getInstance(context).addToRequestQueue(secondRequest);

                    } catch (Exception e) {
                        Log.e("debugme", "Refresh AppUser Record", e);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Log.d("debugme", "Refresh AppUser Record - " + error.getMessage());

                }
            });
} catch (Exception e)
{
    Log.e("debugme", "problem saving", e);
}

        RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        // 2nd call

    }
    public static boolean companyNameMatch(final IoTStarterApplication app, String name) {
        for (int i = 0; i < app.getCouponCompanies().size(); i++) {
            if (name.toUpperCase().replaceAll("'", "").replaceAll("-", " ").indexOf(app.getCouponCompanies().elementAt(i).toString().toUpperCase()) >= 0) {
                return true;
            }
            if (app.getCouponCompanies().elementAt(i).toString().toUpperCase().replaceAll("'", "").replaceAll("-", " ").indexOf(name.toUpperCase()) >= 0) {
                return true;
            }
        }
        return false;
    }
    public static boolean companyNameMatch(String name1, String name2) {
          name1 = name1.toUpperCase().replaceAll("'", "").replaceAll("-", " ");
          name2 = name2.toUpperCase().replaceAll("'", "").replaceAll("-", " ");
            if (name1.indexOf(name2) >= 0) {
                return true;
            }
            if (name2.indexOf(name1) >= 0) {
                return true;
            }
        return false;
    }

    public static void getPeopleInArea(Context context, final IoTStarterApplication app, Location location)
    {
        final GoogleMap googleMap = app.getGoogleMap();

        String url = "https://new-node-red-demo-kad.mybluemix.net/peopleInArea";

            Log.d("debugme","Getting people in area - Request "+url);

    JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    //mTextView.setText("Response: " + response.toString());
                    Log.d("debugme", "Getting people in area - Response: " + response.toString());
                    try {


                        for (int x = 0; x < response.length(); x++) {
                            Log.d("debugme", "showing people");
                            JSONObject jsonObject = response.getJSONObject(x);
                            //Marker now = app.getMapMarker(app.getCurrentUser());
                            String user = jsonObject.get("username").toString();
                            Log.d("debugme", "showing people : " + user);
                            BitmapDescriptor newBitmap = BitmapDescriptorFactory.fromResource(R.drawable.person_48x48);

                            if (user.equals("kevindunetz@gmail.com")) {
                                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.kevindunetz);
                            } else if (user.equalsIgnoreCase("ryandunetz@gmail.com")) {
                                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ryandunetz);
                            } else if (user.equalsIgnoreCase("andrewdunetz@gmail.com")) {
                                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.andrewdunetz);
                            } else if (user.equalsIgnoreCase("rosadunetz@gmail.com")) {
                                newBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.rosadunetz);
                            }

                            if (jsonObject.isNull("latitude") || jsonObject.isNull("longitude"))
                                continue;
                            // Getting latitude of the current location
                            double latitude = Double.parseDouble(jsonObject.get("latitude").toString());

                            // Getting longitude of the current location
                            double longitude = Double.parseDouble(jsonObject.get("longitude").toString());

                            String userName = jsonObject.get("username").toString();

                            if (userName.equals(app.getCurrentUser()))
                                continue; // don't show for yourself

                            LatLng latLng = new LatLng(latitude, longitude);
                            Marker now = googleMap.addMarker(new MarkerOptions().icon(newBitmap).position(latLng).title(userName));
                            //app.addMapMarker(app.getCurrentUser(), now);

                        }

                    } catch (Exception e) {
                        Log.e("debugme", "Problem getting people in area", e);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Log.d("debugme", "Error getting people in area response - " + error.getMessage());


                }
            });
            RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
}

}
