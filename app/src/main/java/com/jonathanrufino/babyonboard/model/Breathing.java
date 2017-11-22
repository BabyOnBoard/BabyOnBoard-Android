package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Breathing {

    @SerializedName("id")
    private int id;
    @SerializedName("is_breathing")
    private boolean isBreathing;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    public String time;

    public Breathing(int id, boolean isBreathing, String date, String time) {
        this.id = id;
        this.isBreathing = isBreathing;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBreathing() {
        return isBreathing;
    }

    public void setBreathing(boolean breathing) {
        isBreathing = breathing;
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
