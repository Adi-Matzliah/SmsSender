package com.exercise.continusebiometrics.util.di.component;

import com.exercise.continusebiometrics.application.App;
import com.exercise.continusebiometrics.feature.file.FileImportService;
import com.exercise.continusebiometrics.feature.log.EventsFragment;
import com.exercise.continusebiometrics.feature.main.MainActivity;
import com.exercise.continusebiometrics.feature.settings.SettingsActivity;
import com.exercise.continusebiometrics.feature.sms.SmsFragment;
import com.exercise.continusebiometrics.feature.sms.SmsSenderService;
import com.exercise.continusebiometrics.util.di.module.AppModule;
import com.exercise.continusebiometrics.util.di.module.ContextModule;
import com.exercise.continusebiometrics.util.di.module.StorageModule;
import com.exercise.continusebiometrics.util.di.scope.AppScope;

import dagger.Component;

@AppScope
@Component(modules = {AppModule.class, StorageModule.class, ContextModule.class})
public interface AppComponent {
    void inject(App app);

    void inject(MainActivity activity);

    void inject(SmsFragment fragment);

    void inject(EventsFragment fragment);

    void inject(SettingsActivity activity);

    void inject(FileImportService service);

    void inject(SmsSenderService service);
}
