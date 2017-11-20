package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Temperature {

    @SerializedName("id")
    private int id;
    @SerializedName("temperature")
    private String temperature;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    public String time;

    public Temperature(int id, String temperature, String date, String time) {
        this.id = id;
        this.temperature = temperature;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
