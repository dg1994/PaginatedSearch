package com.example.dunzoassignment.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.dunzoassignment.networking.Resource;
import com.example.dunzoassignment.networking.api.ApiService;
import com.example.dunzoassignment.networking.model.ApiResponse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PhotoViewModel extends ViewModel {

    private ApiService apiService;
    private MediatorLiveData<Resource<ApiResponse>> apiResponse;

    @Inject
    public PhotoViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<ApiResponse>> getPhotosObserver() {
        return apiResponse;
    }

    public LiveData<Resource<ApiResponse>> getPhotos(String query, int page, int perPage) {
        apiResponse = new MediatorLiveData<>();
        apiResponse.setValue(Resource.loading((ApiResponse) null));

        final LiveData<Resource<ApiResponse>> source = LiveDataReactiveStreams.fromPublisher(

                apiService.getPhotos("flickr.photos.search", "062a6c0c49e4de1d78497d13a7dbb360",
                        query, "json", 1, perPage, page)
                        .onErrorReturn(throwable -> {
                            ApiResponse apiResponse = new ApiResponse();
                            apiResponse.setPhotoResponse(null);
                            apiResponse.setStat("Error");
                            return apiResponse;
                        })
                        .map(response -> {
                            if (response == null || "Error".equals(response.getStat())) {
                                return Resource.error("Error while fetching", (ApiResponse) null);
                            }
                            return Resource.success(response);
                        })
                        .subscribeOn(Schedulers.io())
        );

        apiResponse.addSource(source, listResource -> {
            apiResponse.setValue(listResource);
            apiResponse.removeSource(source);
        });
        return apiResponse;
    }
}
