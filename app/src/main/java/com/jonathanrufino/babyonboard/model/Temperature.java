package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Temperature {

    @SerializedName("id")
    private int id;
    @SerializedName("temperature")
    private String temperature;
    @SerializedName("datetime")
    private String datetime;

    public Temperature(int id, String temperature, String datetime) {
        this.id = id;
        this.temperature = temperature;
        this.datetime = datetime;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
