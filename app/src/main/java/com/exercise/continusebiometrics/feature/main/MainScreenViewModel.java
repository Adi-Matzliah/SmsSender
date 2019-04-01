package com.exercise.continusebiometrics.feature.main;

import android.content.SharedPreferences;
import android.util.Log;

import com.exercise.continusebiometrics.data.Contact;
import com.exercise.continusebiometrics.data.Event;
import com.exercise.continusebiometrics.data.EventDao;
import com.exercise.continusebiometrics.data.StorageRepository;
import com.exercise.continusebiometrics.util.Config;
import com.exercise.continusebiometrics.util.TextUtils;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.exercise.continusebiometrics.data.Event.Status.FAILURE;
import static com.exercise.continusebiometrics.data.Event.Status.SUCCESS;
import static com.exercise.continusebiometrics.data.Event.Type.CONTACT_SAVED;
import static com.exercise.continusebiometrics.data.EventDao.Order.DESC;

public class MainScreenViewModel extends ViewModel {

    @NonNull
    private final SharedPreferences sharedPref;

    @NonNull
    private final StorageRepository storageRepository;

    @Nullable
    private final MutableLiveData<List<Contact>> validPhoneList = new MutableLiveData<>();

    @Nullable
    private final MutableLiveData<List<Event>> eventList = new MutableLiveData<>();

    @Nullable
    private final MutableLiveData<String> message = new MutableLiveData<>();

    @Nullable
    private final MutableLiveData<String> phoneListText = new MutableLiveData<>();

    @Nullable
    private final MutableLiveData<Boolean> isParsingPhoneSuccess = new MutableLiveData<>();

    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();

    private static final String TAG = MainScreenViewModel.class.getSimpleName();

    @Inject
    public MainScreenViewModel(@NonNull StorageRepository storageRepository, @NonNull SharedPreferences sharedPref) {
        this.storageRepository = storageRepository;
        this.sharedPref = sharedPref;
        initializeData();
    }

    private void initializeData() {
        phoneListText.setValue(sharedPref.getString(Config.PHONE_LIST_KEY, ""));
        String msg = sharedPref.getString(Config.MESSAGE_KEY, "");
        message.setValue(msg);
        disposable.add(storageRepository.loadAllContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(validPhoneList::setValue)
        );

        loadEventsByFilters("date", DESC);
    }

    public void loadEventsByFilters(String orderByField, @EventDao.Order String order) {
        disposable.add(storageRepository.loadAllEvents(orderByField, order)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> {
                    eventList.setValue(events);
                    Log.e(TAG, String.valueOf(events.get(0).getId()));
                        },
                        error -> Log.e(TAG, "Error occurred while trying to fetch events from DB!")
                )
        );
    }

    public void parsePhoneNumbers(String contactStr) {
        String[] phones = contactStr.split(";");

        disposable.add(Observable.fromArray(phones)
                .filter(TextUtils::isPhoneNumberValid)
                .map(Contact::new)
                .toList()
                .flatMap(this::saveContacts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Contact>>() {
                    @Override
                    public void onSuccess(List<Contact> contacts) {
                        Event event = new Event( contacts.size() + "/" + phones.length + " Contacts", SUCCESS, CONTACT_SAVED);
                        addNewEvent(event);
                        isParsingPhoneSuccess.setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Event event = new Event( 0 + "/" + phones.length + " Contacts", FAILURE, CONTACT_SAVED);
                        addNewEvent(event);
                        isParsingPhoneSuccess.setValue(false);
                    }
                })
        );
    }

    private void addNewEvent(Event event) {
        disposable.add(Single.fromCallable(()-> {
            storageRepository.saveOrUpdateEvents(event);
            return event;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(eventLog -> Log.e(TAG, "Saved event log was added")));
    }

    public Single<List<Contact>> saveContacts(List<Contact> contacts) {
        storageRepository.deleteAllContacts();
        storageRepository.saveOrUpdateContacts(contacts.toArray(new Contact[contacts.size()]));
        validPhoneList.postValue(contacts);
        return Single.just(contacts);
    }

    public void saveMessage(String msg) {
        message.setValue(msg);
        sharedPref.edit().putString(Config.MESSAGE_KEY, msg).apply();
    }

    public void savePhoneTextList(String phoneList) {
        phoneListText.setValue(phoneList);
        sharedPref.edit().putString(Config.PHONE_LIST_KEY, phoneList).apply();
    }

    public LiveData<String> getMessage() {
        return message;

    }

    @Nullable
    public LiveData<String> getPhoneListText() {
        return phoneListText;
    }

    @Nullable
    public MutableLiveData<Boolean> getIsParsingPhoneSuccess() {
        return isParsingPhoneSuccess;
    }

    @Nullable
    public LiveData<List<Event>> getEvents() {
        return eventList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
