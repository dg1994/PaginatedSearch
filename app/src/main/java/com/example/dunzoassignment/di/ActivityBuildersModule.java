package com.example.dunzoassignment.di;

import com.example.dunzoassignment.MainActivity;
import com.example.dunzoassignment.di.main.MainModule;
import com.example.dunzoassignment.di.main.MainScope;
import com.example.dunzoassignment.di.main.MainViewModelsModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {
    @MainScope
    @ContributesAndroidInjector(
            modules = {MainViewModelsModule.class, MainModule.class}
    )
    abstract MainActivity contributeMainActivity();
}
