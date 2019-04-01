package com.exercise.continusebiometrics.feature.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exercise.continusebiometrics.BuildConfig;
import com.exercise.continusebiometrics.R;
import com.exercise.continusebiometrics.application.App;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements TextWatcher {

    @Nullable
    SettingsScreenViewModel viewModel;

    @Inject
    SettingsViewModelFactory viewModelFactory;

    @BindView(R.id.versionTextView)
    TextView versionTextView;

    @BindView(R.id.delayEditTextView)
    EditText delayEditTextView;

    @BindView(R.id.saveButton)
    Button saveButton;

    @BindView(R.id.resetButton)
    Button resetButton;

    @BindView(R.id.openSourcesTextView)
    TextView openSourcesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        App.getAppComponent(this).inject(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsScreenViewModel.class);
        setupToolBar();
        initViews();
        observableViewModel();
    }

    private void initViews() {
        delayEditTextView.addTextChangedListener(this);
        versionTextView.setText(BuildConfig.VERSION_NAME + "("  + BuildConfig.VERSION_CODE + ")");
        resetButton.setOnClickListener(v -> viewModel.resetSettings());
        saveButton.setOnClickListener(v -> viewModel.setMessageDelay(Integer.valueOf(delayEditTextView.getText().toString())));
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void observableViewModel() {
        viewModel.getDelayMessageTime().observe(this, delayTime -> delayEditTextView.setText(delayTime.toString()));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        saveButton.setEnabled(s.length() > 0);
    }
}
