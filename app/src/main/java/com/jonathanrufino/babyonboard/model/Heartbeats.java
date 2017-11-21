package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Heartbeats {

    @SerializedName("id")
    private int id;
    @SerializedName("beats")
    private int beats;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    public String time;

    public Heartbeats(int id, int beats, String date, String time) {
        this.id = id;
        this.beats = beats;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBeats() {
        return beats;
    }

    public void setBeats(int beats) {
        this.beats = beats;
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
