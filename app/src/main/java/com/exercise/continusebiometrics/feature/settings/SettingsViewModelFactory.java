package com.exercise.continusebiometrics.feature.settings;

import android.content.SharedPreferences;

import com.exercise.continusebiometrics.util.di.scope.AppScope;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

@AppScope
public class SettingsViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    private final SharedPreferences sharedPref;

    @Inject
    public SettingsViewModelFactory(@NonNull SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SettingsScreenViewModel.class)) {
            return (T) new SettingsScreenViewModel(sharedPref);
        } else {
            throw new IllegalArgumentException("unknown view model class " + modelClass);
        }
    }
}