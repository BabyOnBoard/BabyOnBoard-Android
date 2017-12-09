package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Noise {

    @SerializedName("id")
    private int id;
    @SerializedName("is_crying")
    private boolean isCrying;
    @SerializedName("datetime")
    private String datetime;

    public Noise(int id, boolean isCrying, String datetime) {
        this.id = id;
        this.isCrying = isCrying;
        this.datetime = datetime;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
