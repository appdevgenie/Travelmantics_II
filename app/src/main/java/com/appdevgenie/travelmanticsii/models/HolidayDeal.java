package com.appdevgenie.travelmanticsii.models;

public class HolidayDeal {

    private String id;
    private String city;
    private String resort;
    private String cost;

    public HolidayDeal(String city, String resort, String cost) {
        this.city = city;
        this.resort = resort;
        this.cost = cost;
    }

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
}
