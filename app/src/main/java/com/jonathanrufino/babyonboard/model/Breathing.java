package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Breathing {

    @SerializedName("id")
    private int id;
    @SerializedName("status")
    private String status;
    @SerializedName("datetime")
    private String datetime;

    public Breathing(int id, String status, String datetime) {
        this.id = id;
        this.status = status;
        this.datetime = datetime;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
