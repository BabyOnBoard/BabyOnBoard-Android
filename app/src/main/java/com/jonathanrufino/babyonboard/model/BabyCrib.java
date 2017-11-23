package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class BabyCrib {

    @SerializedName("id")
    private int id;
    @SerializedName("status")
    private String status;
    @SerializedName("duration")
    private int duration;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    public String time;

    public BabyCrib(int id, String status, int duration, String date, String time) {
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.date = date;
        this.time = time;
    }

    public BabyCrib(String status, int duration) {
        this.status = status;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
