package com.ibm.iot.android.iotstarter.utils;
// A Java type that can be serialized to JSON
public class User {
    private String _id = null;
    private String _rev = null;
    private String name = null;
    private String organization = null;
    private String device_id = null;
    private String auth_token = null;
    private String search_key_words = null;

    public User() {
    }
    public String getUserName()
    {
        return name;
    }
    public void setUserName(String nameStr)
    {
        name = nameStr;
    }
    public String getOrganization()
    {
        return organization;
    }
    public void setDeviceID(String deviceIDStr)
    {
        device_id = deviceIDStr;
    }
    public String getAuthToken()
    {
        return auth_token;
    }
    public void setAuthToken(String authTokenStr)
    {
        auth_token = authTokenStr;
    }

    public String getSearchCriteria()
    {
        return search_key_words;
    }
    public void setSearchCriteria(String nameStr)
    {
        search_key_words = nameStr;
    }

    public String getSearchKeywords()
    {
        return search_key_words;
    }
    public void setSearchKeywords(String nameStr)
    {
        search_key_words = nameStr;
    }
    public String toString() {
        return "{ id: " + _id + ",\nrev: " + _rev + ",\nUser Name: " + _id + "\n" + auth_token + "\n" + organization +
                "\n" + search_key_words + "}";
    }
}
