package com.example.dunzoassignment.di;

import androidx.lifecycle.ViewModelProvider;

import com.example.dunzoassignment.viewmodel.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelFactory);

}