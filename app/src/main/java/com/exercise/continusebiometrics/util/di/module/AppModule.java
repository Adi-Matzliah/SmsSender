package com.exercise.continusebiometrics.util.di.module;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import com.exercise.continusebiometrics.util.di.scope.AppScope;

@Module
public class AppModule {
    Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @AppScope
    Application provideApplication() {
        return application;
    }
}
