package com.ibm.iot.android.iotstarter.utils;
import java.util.Date;
// A Java type that can be serialized to JSON
public class Deal {
    public final int PLAY_BOTH_SIDES = 0;
    public final int PLAY_ON_FRONT_SIDE = 1;
    public final int PLAY_ON_BACK_SIDE = 2;

    private String _id = null;
    private String _rev = null;
    private boolean isExample;
    private String latitude = null;
    private String longitude = null;
    private String location = null;
    private String _type = null;
    private String deal = null;
    private boolean lastSpoke = false;
    private int playSides = PLAY_BOTH_SIDES;
    private String num_days = null;
    private String coupon_expiration_days = null;
    private String creation_date = Utility.getCurrentDateTime();
    private String username = "";
    private String company_name = "";

    public Deal(boolean isExample) {
        this.isExample = isExample; // KAD have no idea why this is here ...please remove
        lastSpoke = false;
    }
    public String getID()
    {
        return _id;
    }

    public String getType()
    {
        return _type;
    }
    public void setType(String inputType)
    {
        _type = inputType;
    }

    public String getUserName()
    {
        return username;
    }
    public void setUserName(String userName)
    {
        username = userName;
    }

    public String getCompanyName()
    {
        return company_name;
    }
    public void setCompanyName(String companyName) {
        company_name = companyName;
    }

    public double getLatitude()
    {
        if (latitude != null && latitude.length() > 0)
            return Double.parseDouble(latitude);
        else
            return -1;
    }
    public void setLatitude(String latitudeStr)
    {
        latitude = latitudeStr;
    }

    public double getLongitude()
    {
        if (longitude != null && longitude.length() > 0)
            return Double.parseDouble(longitude);
        else
            return -1;
    }
    public void setLongitude(String longitudeStr)
    {
        longitude = longitudeStr;
    }

    public String getDeal()
    {
        return deal;
    }
    public void setDeal(String dealStr)
    {
        deal = dealStr;
    }

    public boolean getLastSpoke()
    {
        return lastSpoke;
    }
    public void setLastSpoke(boolean value)
    {
        lastSpoke = value;
    }

    public void setPlaySides(int ps)
    {
        playSides = ps;
    }
    public int getPlaySides()
    {
        return playSides;
    }
    public boolean playOnBackSide()
    {
        if (playSides == PLAY_ON_BACK_SIDE || playSides == PLAY_BOTH_SIDES)
            return true;

        return false;
    }
    public boolean playOnFrontSide()
    {
        if (playSides == PLAY_ON_FRONT_SIDE || playSides == PLAY_BOTH_SIDES)
            return true;

        return false;
    }
    public String getCreationDate()
    {
        return creation_date;
    }
    public String getCouponExpirationDays()
    {
        if (coupon_expiration_days == null || coupon_expiration_days.length() == 0)
            return "30";
        else
            return coupon_expiration_days;
    }

    public void setCouponExpirationDays(String couponExpirationDays)
    {
        coupon_expiration_days = couponExpirationDays;
    }

    public String toString() {
        return "{ id: " + _id + ",\nrev: " + _rev + ",\nlast_spoke: " + lastSpoke + "\ndeal: " + deal + "\n" + latitude + "\n" + longitude + "\n" + num_days + "\n" + coupon_expiration_days + "}";
    }
}
