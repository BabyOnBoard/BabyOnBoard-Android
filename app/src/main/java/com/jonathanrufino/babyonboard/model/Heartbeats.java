package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Heartbeats {

    @SerializedName("id")
    private int id;
    @SerializedName("beats")
    private int beats;
    @SerializedName("datetime")
    private String datetime;

    public Heartbeats(int id, int beats, String datetime) {
        this.id = id;
        this.beats = beats;
        this.datetime = datetime;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
