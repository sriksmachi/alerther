package com.example.alerther.alerther;

import java.util.Date;

/**
 * Created by chitti on 21-02-2016.
 */
public class IncidentItem {
    public  IncidentItem(){}
    @com.google.gson.annotations.SerializedName("id")
    public   String mid;
    @com.google.gson.annotations.SerializedName("description")
    public String mDescription;
    @com.google.gson.annotations.SerializedName("reporteddatetime")
    public Date mReportedTime;
    @com.google.gson.annotations.SerializedName("createddatetime")
    public Date mCreatedDate;
    @com.google.gson.annotations.SerializedName("tips")
    public String mTips;
    @com.google.gson.annotations.SerializedName("username")
    public String mUserName;
    @com.google.gson.annotations.SerializedName("longitude")
    public double mLongitude;
    @com.google.gson.annotations.SerializedName("latitude")
    public double mLatitude;
}
