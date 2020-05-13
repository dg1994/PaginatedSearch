package com.example.dunzoassignment.di.main;

import androidx.lifecycle.ViewModel;

import com.example.dunzoassignment.di.ViewModelKey;
import com.example.dunzoassignment.viewmodel.PhotoViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PhotoViewModel.class)
    public abstract ViewModel bindProfileViewModel(PhotoViewModel viewModel);
}
