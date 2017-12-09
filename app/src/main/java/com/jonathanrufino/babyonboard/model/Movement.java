package com.jonathanrufino.babyonboard.model;

import com.google.gson.annotations.SerializedName;

public class Movement {

    @SerializedName("is_moving")
    private boolean isMoving;
    @SerializedName("remaining_time")
    private int remainingTime;
    @SerializedName("movement")
    private String movement;

    public Movement(boolean isMoving, int remainingTime, String movement) {
        this.isMoving = isMoving;
        this.remainingTime = remainingTime;
        this.movement = movement;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }
}
