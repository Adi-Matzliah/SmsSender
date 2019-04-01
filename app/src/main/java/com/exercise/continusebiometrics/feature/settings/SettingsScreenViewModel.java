package com.exercise.continusebiometrics.feature.settings;

import android.content.SharedPreferences;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.exercise.continusebiometrics.util.Config.DELAY_MESSAGE_SEC;
import static com.exercise.continusebiometrics.util.Config.DELAY_MESSAGE_SEC_KEY;

public class SettingsScreenViewModel extends ViewModel {
    @NonNull
    private final SharedPreferences sharedPref;

    @Nullable
    private final MutableLiveData<Integer> messageDelay = new MutableLiveData<>();

    @Inject
    public SettingsScreenViewModel(@NonNull SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        initializeData();
    }

    public void initializeData() {
        messageDelay.setValue(sharedPref.getInt(DELAY_MESSAGE_SEC_KEY, DELAY_MESSAGE_SEC));
    }

    public void resetSettings() {
        sharedPref.edit().clear().apply();
        initializeData();
    }

    public void setMessageDelay(int delay) {
        messageDelay.setValue(delay);
        sharedPref.edit().putInt(DELAY_MESSAGE_SEC_KEY, delay).apply();
    }

    @Nullable
    public LiveData<Integer> getDelayMessageTime() {
        return messageDelay;
    }
}
