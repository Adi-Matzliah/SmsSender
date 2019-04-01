package com.exercise.continusebiometrics.feature.sms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exercise.continusebiometrics.R;
import com.exercise.continusebiometrics.application.App;
import com.exercise.continusebiometrics.feature.main.IEventListener;
import com.exercise.continusebiometrics.feature.main.MainScreenViewModel;
import com.exercise.continusebiometrics.feature.main.MainViewModelFactory;
import com.exercise.continusebiometrics.feature.settings.SettingsActivity;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.exercise.continusebiometrics.feature.main.MainActivity.READ_REQUEST_CODE;

public class SmsFragment extends Fragment{

    @Nullable
    MainScreenViewModel viewModel;

    @Inject
    MainViewModelFactory viewModelFactory;

    @Nullable
    private Unbinder unbinder;

    @BindView(R.id.contactsEditText)
    EditText contactsEditText;

    @BindView(R.id.saveButton)
    Button saveButton;

    @BindView(R.id.sendButton)
    Button sendButton;

    @BindView(R.id.importButton)
    Button importButton;

    @BindView(R.id.messageEditText)
    EditText msgEditText;

    @BindView(R.id.settingsButton)
    Button settingsButton;

    @Nullable
    private IEventListener eventListener;

    public static SmsFragment newInstance() {
        return new SmsFragment();
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof IEventListener) {
            eventListener = (IEventListener)context;
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sms_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        App.getAppComponent(getContext()).inject(this);
        initViews();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainScreenViewModel.class);
        observableViewModel();
    }

    private void initViews() {
        contactsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!s.toString().isEmpty());

            }
        });

        msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                sendButton.setEnabled(!s.toString().isEmpty());
            }
        });

        importButton.setOnClickListener(v -> openFileExplorer());
        saveButton.setOnClickListener(v -> viewModel.parsePhoneNumbers(contactsEditText.getText().toString()));

        sendButton.setOnClickListener(v -> {
            viewModel.saveMessage(msgEditText.getText().toString());
            eventListener.sendSMS();
        });
        settingsButton.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
    }

    private void observableViewModel() {
        viewModel.getIsParsingPhoneSuccess().observe(this, success -> Toast.makeText(getContext(), getString(success ? R.string.phone_parse_success_message_text : R.string.phone_parse_error_message_text), Toast.LENGTH_SHORT).show());
        //viewModel.getIsSmsReadyToBeSent().observe(this, ready -> saveButton.setEnabled(ready));
        viewModel.getMessage().observe(this, message -> {
            msgEditText.setText(message);
            sendButton.setEnabled(!message.isEmpty());
        });
        viewModel.getPhoneListText().observe(this, phoneList -> {
            contactsEditText.setText(phoneList);
            sendButton.setEnabled(!phoneList.isEmpty());
        });
    }

    private void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
/*        String[] mimeTypes = {"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; //.xls & .xlsx"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);*/
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (getActivity() != null) {
            getActivity().startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_file_exporter_title)), READ_REQUEST_CODE);
        }

    }

    @Override
    public void onDestroyView() {
        viewModel.savePhoneTextList(contactsEditText.getText().toString());
        viewModel.saveMessage(msgEditText.getText().toString());
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
