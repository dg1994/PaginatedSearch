package com.example.dunzoassignment.di.main;

import com.example.dunzoassignment.networking.api.ApiService;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class MainModule {
    @MainScope
    @Provides
    static ApiService provideMainApi(Retrofit retrofit){
        return retrofit.create(ApiService.class);
    }
}
