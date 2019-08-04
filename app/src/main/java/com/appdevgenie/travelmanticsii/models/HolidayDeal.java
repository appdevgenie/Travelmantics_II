package com.appdevgenie.travelmanticsii.models;

import android.os.Parcel;
import android.os.Parcelable;

public class HolidayDeal implements Parcelable {

    private String id;
    private String city;
    private String resort;
    private float cost;
    private String imageUrl;
    private String imageName;
    private String rating;

    public HolidayDeal() {
    }

    public HolidayDeal(String city, String resort, float cost, String imageUrl, String imageName, String rating) {
        this.city = city;
        this.resort = resort;
        this.cost = cost;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.rating = rating;
    }


    protected HolidayDeal(Parcel in) {
        id = in.readString();
        city = in.readString();
        resort = in.readString();
        cost = in.readFloat();
        imageUrl = in.readString();
        imageName = in.readString();
        rating = in.readString();
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

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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
        parcel.writeFloat(cost);
        parcel.writeString(imageUrl);
        parcel.writeString(imageName);
        parcel.writeString(rating);
    }


}
