package com.tryouts.courierapplication.items;


public class OrderReceived {

    private String mFrom;
    private String mTo;
    private String mDistance;
    private String mIsExpress;
    private String mIsSuperExpress;
    private String mIsCarExpress;
    private String mFromPlaceLat;
    private String mFromPlaceLng;
    private String mToPlaceLat;
    private String mToPlaceLng;
    private String mIsItDone;
    private String mWhoDelivers;
    private String mDate;
    private String mCustomersUid;
    private String mPhoneNumber;
    private String mUserTimeStamp;
    private String mWhoDeliversName;
    private long mTimeStamp;
    private String mUserToken;
    private String mType;


    public OrderReceived() {
        this.mIsExpress = "no";
        this.mIsSuperExpress = "no";
        this.mIsCarExpress = "no";
        this.mIsItDone = "no";
        this.mWhoDelivers = "";
        this.mWhoDeliversName = "";
        this.mUserToken = "";
        this.mType = "new";
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getIsExpress() {
        return mIsExpress;
    }

    public void setIsExpress(String isExpress) {
        mIsExpress = isExpress;
    }

    public String getIsSuperExpress() {
        return mIsSuperExpress;
    }

    public void setIsSuperExpress(String isSuperExpress) {
        mIsSuperExpress = isSuperExpress;
    }

    public String getIsCarExpress() {
        return mIsCarExpress;
    }

    public void setIsCarExpress(String isCarExpress) {
        mIsCarExpress = isCarExpress;
    }

    public String getIsItDone() {
        return mIsItDone;
    }

    public void setIsItDone(String isItDone) {
        mIsItDone = isItDone;
    }

    public String getWhoDelivers() {
        return mWhoDelivers;
    }

    public void setWhoDelivers(String whoDelivers) {
        mWhoDelivers = whoDelivers;
    }

    public String getWhoDeliversName() {
        return mWhoDeliversName;
    }

    public void setWhoDeliversName(String whoDeliversName) {
        mWhoDeliversName = whoDeliversName;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getFromPlaceLat() {
        return mFromPlaceLat;
    }

    public void setFromPlaceLat(String fromPlaceLat) {
        mFromPlaceLat = fromPlaceLat;
    }

    public String getFromPlaceLng() {
        return mFromPlaceLng;
    }

    public void setFromPlaceLng(String fromPlaceLng) {
        mFromPlaceLng = fromPlaceLng;
    }

    public String getToPlaceLat() {
        return mToPlaceLat;
    }

    public void setToPlaceLat(String toPlaceLat) {
        mToPlaceLat = toPlaceLat;
    }

    public String getToPlaceLng() {
        return mToPlaceLng;
    }

    public void setToPlaceLng(String toPlaceLng) {
        mToPlaceLng = toPlaceLng;
    }

    public String getCustomersUid() {
        return mCustomersUid;
    }

    public void setCustomersUid(String customersUid) {
        mCustomersUid = customersUid;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getUserTimeStamp() {
        return mUserTimeStamp;
    }

    public void setUserTimeStamp(String userTimeStamp) {
        mUserTimeStamp = userTimeStamp;
    }

    public String getUserToken() {
        return mUserToken;
    }

    public void setUserToken(String userToken) {
        mUserToken = userToken;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }
}
