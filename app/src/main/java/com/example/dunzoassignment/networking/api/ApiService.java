package com.example.dunzoassignment.networking.api;

import com.example.dunzoassignment.networking.model.ApiResponse;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/services/rest")
    public Flowable<ApiResponse> getPhotos(@Query("method") String method, @Query("api_key") String api_key,
                                           @Query("text") String text, @Query("format") String format,
                                           @Query("nojsoncallback") Integer nojsoncallback, @Query("per_page") Integer per_page,
                                           @Query("page") Integer page);
}
