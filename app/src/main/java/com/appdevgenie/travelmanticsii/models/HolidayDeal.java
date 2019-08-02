package com.appdevgenie.travelmanticsii.models;

import android.os.Parcel;
import android.os.Parcelable;

public class HolidayDeal implements Parcelable {

    private String id;
    private String city;
    private String resort;
    private String cost;
    private String imageUrl;
    private String imageName;

    public HolidayDeal() {
    }

    public HolidayDeal(String city, String resort, String cost, String imageUrl, String imageName) {
        this.city = city;
        this.resort = resort;
        this.cost = cost;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }

    protected HolidayDeal(Parcel in) {
        id = in.readString();
        city = in.readString();
        resort = in.readString();
        cost = in.readString();
        imageUrl = in.readString();
        imageName = in.readString();
    }

    public static final Creator<HolidayDeal> CREATOR = new Creator<HolidayDeal>() {
        @Override
        public HolidayDeal createFromParcel(Parcel in) {
            return new HolidayDeal(in);
        }

        @Override
        public HolidayDeal[] newArray(int size) {
            return new HolidayDeal[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getResort() {
        return resort;
    }

    public void setResort(String resort) {
        this.resort = resort;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(city);
        parcel.writeString(resort);
        parcel.writeString(cost);
    }
}
