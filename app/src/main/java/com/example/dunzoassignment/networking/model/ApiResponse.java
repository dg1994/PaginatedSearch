package com.example.dunzoassignment.networking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("photos")
    @Expose
    private PhotoResponse photoResponse;
    @SerializedName("stat")
    @Expose
    private String stat;

    public PhotoResponse getPhotoResponse() {
        return photoResponse;
    }

    public void setPhotoResponse(PhotoResponse photoResponse) {
        this.photoResponse = photoResponse;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}
