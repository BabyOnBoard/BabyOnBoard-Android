package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Noise {

    @SerializedName("id")
    private int id;
    @SerializedName("is_crying")
    private boolean isCrying;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    public String time;

    public Noise(int id, boolean isCrying, String date, String time) {
        this.id = id;
        this.isCrying = isCrying;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCrying() {
        return isCrying;
    }

    public void setCrying(boolean crying) {
        isCrying = crying;
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
