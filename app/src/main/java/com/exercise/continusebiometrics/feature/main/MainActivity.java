package com.exercise.continusebiometrics.feature.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.exercise.continusebiometrics.R;
import com.exercise.continusebiometrics.application.App;
import com.exercise.continusebiometrics.feature.file.FileImportService;
import com.exercise.continusebiometrics.feature.log.EventsFragment;
import com.exercise.continusebiometrics.feature.settings.SettingsScreenViewModel;
import com.exercise.continusebiometrics.feature.settings.SettingsViewModelFactory;
import com.exercise.continusebiometrics.feature.sms.SmsFragment;
import com.exercise.continusebiometrics.feature.sms.SmsSenderService;
import com.exercise.continusebiometrics.feature.viewpager.ViewPagerAdapter;
import com.exercise.continusebiometrics.util.Config;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener,
        ServiceConnection,
        IEventListener {

    @Nullable
    SettingsScreenViewModel viewModel;

    @Inject
    SettingsViewModelFactory viewModelFactory;

    @BindView(R.id.viewpager) ViewPager viewPager;

    @Nullable
    ViewPagerAdapter adapter;

    @Nullable
    FileImportService.LocalBinder fileBinder;
    @Nullable
    SmsSenderService.LocalBinder smsBinder;

    @BindView(R.id.collapse_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabs) TabLayout tabLayout;

    public static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        App.getAppComponent(this).inject(this);
        initViews();
        bindServices();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsScreenViewModel.class);
        observableViewModel();
    }

    private void initViews() {
        setupCollapsingToolbar();
        setupViewPager();
    }

    private void observableViewModel() {
    }

    private void bindServices() {
        // Bind to LocalService
        Intent fileIntent = new Intent(this, FileImportService.class);
        bindService(fileIntent, this, Context.BIND_AUTO_CREATE);
        Intent smsIntent = new Intent(this, SmsSenderService.class);
        bindService(smsIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SmsFragment().newInstance(), getString(R.string.sms_tab_text));
        adapter.addFrag(new EventsFragment().newInstance(), getString(R.string.events_tab_text));

        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupCollapsingToolbar() {
        collapsingToolbarLayout.setTitleEnabled(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        // We've bound to LocalService, cast the IBinder and get LocalService instance
        if (iBinder instanceof FileImportService.LocalBinder) {
            fileBinder = (FileImportService.LocalBinder) iBinder;
        } else if (iBinder instanceof SmsSenderService.LocalBinder) {
            smsBinder = (SmsSenderService.LocalBinder) iBinder;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) { }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Config.REQUEST_SMS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    smsBinder.getService().sendSmsToContactList();
                } else {
                    Toast.makeText(this, getString(R.string.grant_sms_permission_text), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case READ_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.getData() != null) {
                            fileBinder.getService().importExcelFile(data.getData());
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void sendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, Config.REQUEST_SMS_PERMISSION);
                Toast.makeText(this, getString(R.string.grant_sms_permission_text), Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, Config.REQUEST_SMS_PERMISSION);
            }
        } else {
            smsBinder.getService().sendSmsToContactList();
        }
    }
}

