package com.exercise.continusebiometrics.application;

import android.app.Application;
import android.content.Context;

import com.exercise.continusebiometrics.R;
import com.exercise.continusebiometrics.util.di.component.AppComponent;
import com.exercise.continusebiometrics.util.di.component.DaggerAppComponent;
import com.exercise.continusebiometrics.util.di.module.AppModule;
import com.exercise.continusebiometrics.util.di.module.ContextModule;
import com.exercise.continusebiometrics.util.di.module.StorageModule;

import androidx.annotation.NonNull;

//

public class App extends Application {

	@NonNull
	private AppComponent appComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		appComponent = DaggerAppComponent.builder()
				.appModule(new AppModule(this))
				.contextModule(new ContextModule(getApplicationContext()))
				.storageModule(new StorageModule(getApplicationContext(), getString(R.string.db_name)))
				.build();
		appComponent.inject(this);
	}

	public static AppComponent getAppComponent(Context context) {
		return ((App) context.getApplicationContext()).appComponent;
	}

}