package com.exercise.continusebiometrics.data;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.Flowable;

public class StorageRepository {

    @NonNull
    private final ContactDao contactDao;

    @NonNull
    private final EventDao eventDao;

    @Inject
    StorageRepository(@NonNull ContactDao contactDao, @NonNull EventDao eventDao) {
        this.contactDao = contactDao;
        this.eventDao = eventDao;
    }

    public Flowable<List<Contact>> loadAllContacts(){
        return contactDao.getAll();
    }

    public void saveOrUpdateContacts(Contact... contacts){
        contactDao.insertOrUpdateAll(contacts);
    }

    public void deleteAllContacts() {
        contactDao.deleteAll();
    }

    public Flowable<List<Event>> loadAllEvents(String orderBy, String order){
        return eventDao.getAllOrderBy(orderBy/*, order*/);
    }

    public void saveOrUpdateEvents(Event... events){
        eventDao.insertOrUpdateAll(events);
    }

    public void deleteAllEvents() {
        eventDao.deleteAll();
    }

}
