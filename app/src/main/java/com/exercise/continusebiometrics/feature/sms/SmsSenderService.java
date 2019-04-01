package com.exercise.continusebiometrics.feature.sms;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.exercise.continusebiometrics.application.App;
import com.exercise.continusebiometrics.data.Contact;
import com.exercise.continusebiometrics.data.Event;
import com.exercise.continusebiometrics.data.StorageRepository;
import com.exercise.continusebiometrics.util.Config;
import com.exercise.continusebiometrics.util.ContactEventMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.exercise.continusebiometrics.data.Event.Status.FAILURE;
import static com.exercise.continusebiometrics.data.Event.Status.SUCCESS;
import static com.exercise.continusebiometrics.data.Event.Type.SMS;

public class SmsSenderService extends Service {

    private static final String TAG = SmsSenderService.class.getSimpleName();

    @Inject
    StorageRepository storageRepo;

    @Inject
    SharedPreferences sharedPreferences;

    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();

    @NonNull
    private Binder binder = new SmsSenderService.LocalBinder();

    @Nullable
    SmsManager smsManager;

    public SmsSenderService() {
        smsManager = SmsManager.getDefault();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getAppComponent(this).inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    public void sendSmsToContactList() {
        disposable.add(storageRepo.loadAllContacts()
                .toObservable()
                .flatMapIterable(list->list)
                .zipWith(Observable.interval(sharedPreferences.getInt(Config.DELAY_MESSAGE_SEC_KEY, Config.DELAY_MESSAGE_SEC), TimeUnit.SECONDS), (item, delay) -> item)
                .map(contact -> sendSmsToContact(contact, sharedPreferences.getString(Config.MESSAGE_KEY, "")))
                .toList()
                .observeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Contact>>() {
                    @Override
                    public void onSuccess(List<Contact> contacts) {
                        Log.e(TAG, "SMS_SENT has been sent!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,  e.toString());
                    }
                }));
    }

    synchronized public Contact sendSmsToContact(Contact contact, String message) {
        disposable.add(Single.fromCallable(()-> {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contact.getPhone(), null, message, null, null);
                    return contact;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Contact>() {
                    @Override
                    public void onSuccess(Contact contact) {
                        Event event = ContactEventMapper.convertContactToEvent(contact, SUCCESS, SMS);
                        onSmsSent(event);
                        Log.e(TAG, "Phone: " + contact.getPhone() + " message sent!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Event event = ContactEventMapper.convertContactToEvent(contact, FAILURE, SMS);
                        onSmsSent(event);
                        Log.e(TAG, e.getMessage());
                    }
                })
        );
        return contact;
    }

    private void onSmsSent(Event event) {
        disposable.add(Single.fromCallable(()-> {
            storageRepo.saveOrUpdateEvents(event);
            return event;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(smsEvent -> Log.e(TAG, "Sms Event saved!")));
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    public class LocalBinder extends Binder {
        public SmsSenderService getService() {
            return SmsSenderService.this;
        }
    }
}
