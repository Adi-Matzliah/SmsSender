package com.exercise.continusebiometrics.util.di.module;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import com.exercise.continusebiometrics.util.di.scope.AppScope;

@Module
public class ContextModule {
    public final Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @AppScope
    Context provideContext() {
        return context;
    }
}
