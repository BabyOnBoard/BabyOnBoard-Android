package com.jonathanrufino.babyonboard.networking;

import com.jonathanrufino.babyonboard.model.BabyCrib;
import com.jonathanrufino.babyonboard.model.Breathing;
import com.jonathanrufino.babyonboard.model.Heartbeats;
import com.jonathanrufino.babyonboard.model.Noise;
import com.jonathanrufino.babyonboard.model.Temperature;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {

    @GET("/api/v1/heartbeats/")
    Call<Heartbeats> getHeartbeats();

    @GET("/api/v1/temperature/")
    Call<Temperature> getTemperature();

    @GET("/api/v1/breathing/")
    Call<Breathing> getBreathing();

    @GET("/api/v1/noise/")
    Call<Noise> getNoise();

    @GET("/api/v1/movement/")
    Call<BabyCrib> getMovement();

    @POST("/api/v1/movement/")
    Call<BabyCrib> setMovement(@Body BabyCrib babyCrib);
}
