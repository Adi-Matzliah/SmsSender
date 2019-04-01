package com.exercise.continusebiometrics.feature.main;

import android.content.SharedPreferences;

import com.exercise.continusebiometrics.data.StorageRepository;
import com.exercise.continusebiometrics.util.di.scope.AppScope;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

@AppScope
public class MainViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    private final StorageRepository storageRepository;

    @NonNull
    private final SharedPreferences sharedPref;

    @Inject
    public MainViewModelFactory(@NonNull StorageRepository storageRepository, @NonNull SharedPreferences sharedPref) {
        this.storageRepository = storageRepository;
        this.sharedPref = sharedPref;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainScreenViewModel.class)) {
            return (T) new MainScreenViewModel(storageRepository, sharedPref);
        } else {
            throw new IllegalArgumentException("unknown view model class " + modelClass);
        }
    }
}