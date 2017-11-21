package com.jonathanrufino.babyonboard.networking;

import com.jonathanrufino.babyonboard.model.Heartbeats;
import com.jonathanrufino.babyonboard.model.Temperature;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET("/api/v1/heartbeats/")
    Call<Heartbeats> getHeartbeats();

    @GET("/api/v1/temperature/")
    Call<Temperature> getTemperature();
}
