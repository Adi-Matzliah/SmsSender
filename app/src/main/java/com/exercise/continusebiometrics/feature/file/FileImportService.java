package com.exercise.continusebiometrics.feature.file;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.exercise.continusebiometrics.application.App;
import com.exercise.continusebiometrics.data.Contact;
import com.exercise.continusebiometrics.data.Event;
import com.exercise.continusebiometrics.data.StorageRepository;
import com.exercise.continusebiometrics.util.TextUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.exercise.continusebiometrics.data.Event.Status.SUCCESS;
import static com.exercise.continusebiometrics.data.Event.Type.CONTACT_SAVED;

public class FileImportService extends Service {
    @Inject
    StorageRepository storageRepo;

    @NonNull
    private final CompositeDisposable disposable = new CompositeDisposable();

    @NonNull
    private final Binder binder = new FileImportService.LocalBinder();

    @Nullable
    private Map<Integer, String> headers;
    @Nullable
    private List<Contact> contacts;

    @NonNull
    private int corruptedRows;

    private static final String PHONE_HEADER = "נייד";
    private static final String TELEPHONE_HEADER = "טלפון";
    private static final String FIRST_NAME_HEADER = "שם פרטי";
    private static final String LAST_NAME_HEADER = "שם משפחה";

    private static final String TAG = FileImportService.class.getSimpleName();

    public FileImportService() {
        headers = new HashMap<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getAppComponent(this).inject(this);
    }

    private int getNumOfColumns(HSSFRow row) {
        int numOfHeaders = 0;
        Iterator<Cell> cellIter = row.cellIterator();
        while (cellIter.hasNext()) {
            HSSFCell myCell = (HSSFCell) cellIter.next();
            if (!myCell.toString().isEmpty())
                numOfHeaders++;
        }
        return numOfHeaders > 0 ? numOfHeaders : 1;
    }


    public <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void iterateOverHeaders(HSSFRow row) {
        Iterator<Cell> cellIter = row.cellIterator();
        while (cellIter.hasNext()) {
            HSSFCell myCell = (HSSFCell) cellIter.next();
            switch (myCell.toString()) {
                case PHONE_HEADER:
                    if (headers.containsValue(TELEPHONE_HEADER)) {
                        headers.remove(getKeyByValue(headers, TELEPHONE_HEADER));
                    }
                    headers.put(myCell.getColumnIndex(), myCell.toString());
                    break;
                case TELEPHONE_HEADER:
                    if (!headers.containsKey(PHONE_HEADER)) {
                        headers.put(myCell.getColumnIndex(), myCell.toString());
                    }
                    break;
                case FIRST_NAME_HEADER:
                case LAST_NAME_HEADER:
                    headers.put(myCell.getColumnIndex(), myCell.toString());
                    break;
                default:
                    break;
            }
            if (myCell != null) {
                Log.e(TAG, "Row_Index :" + myCell.getRowIndex() + " Col_Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
            }
        }
    }
    public void importExcelFile(Uri uri) {
        disposable.add(Single.fromCallable(() -> parseExcelFileFromAssets(uri))
                .flatMap(contacts -> {
                    storageRepo.deleteAllContacts();
                    storageRepo.saveOrUpdateContacts(contacts.toArray(new Contact[contacts.size()]));
                    return Single.just(contacts);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Contact>>() {
                    @Override
                    public void onSuccess(List<Contact> contacts) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error", e.toString());
                    }
                }));
    }

    public List<Contact> parseExcelFileFromAssets(Uri uri) {
        contacts = new ArrayList<>();
        headers.clear();
        corruptedRows = 0;
        try {
/*            InputStream myInput;
            // initialize asset manager
            AssetManager assetManager = getAssets();
            //  open excel sheet
            myInput = assetManager.open(path);*/

            //File file = new File(path);
            InputStream myInput = getContentResolver().openInputStream(uri);

            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            // start from row number 1
            rowIter.next();
            HSSFRow firstRow = (HSSFRow)rowIter.next();
            int numOfColumns = getNumOfColumns(firstRow);
            if (numOfColumns > 1) {
                iterateOverHeaders(firstRow);
            } else {
                headers.put(0, PHONE_HEADER);
            }
            while (rowIter.hasNext()) {
                boolean isValidRow = false;
                HSSFRow myRow = (HSSFRow) rowIter.next();
                //Iterator<Cell> cellIter = myRow.cellIterator();
                Contact contact = new Contact();
                for (int cellIndx = 0; cellIndx < numOfColumns; cellIndx++) {
                    HSSFCell myCell = myRow.getCell(cellIndx);
                    if (myCell != null && headers.containsKey(myCell.getColumnIndex()) || numOfColumns == 1) {
                        switch (headers.get(myCell.getColumnIndex())) {
                            case PHONE_HEADER:
                            case TELEPHONE_HEADER:
                                if (TextUtils.isPhoneNumberValid(myCell.toString())) {
                                    String validPhoneNum = TextUtils.convertToValidPhoneNumber(myCell.toString());
                                    contact.setPhone(validPhoneNum);
                                    isValidRow = true;
                                } else {
                                    corruptedRows++;
                                }
                                break;
                            case FIRST_NAME_HEADER:
                                contact.setFirstName(myCell.toString());
                                break;
                            case LAST_NAME_HEADER:
                                contact.setLastName(myCell.toString());
                                break;
                            default:
                                break;
                        }
                    }
                    if (myCell != null) {
                        Log.e(TAG, "Row_Index :" + myCell.getRowIndex() + " Col_Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                }
                if (isValidRow) {
                    contacts.add(contact);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error "+ e.toString());
        }
        onImportFinished();
        return contacts;
    }

    private void onImportFinished() {
        disposable.add(Single.fromCallable(()-> {
            Event event = new Event( contacts.size() + "/" + (corruptedRows + contacts.size() + " Contacts"), SUCCESS, CONTACT_SAVED);
            storageRepo.saveOrUpdateEvents(event);
            return event;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(event -> Log.e(TAG, "File import ends")));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    public class LocalBinder extends Binder {
        public FileImportService getService() {
            return FileImportService.this;
        }
    }
}
