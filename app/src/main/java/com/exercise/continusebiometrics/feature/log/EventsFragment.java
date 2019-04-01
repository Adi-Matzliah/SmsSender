package com.exercise.continusebiometrics.feature.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exercise.continusebiometrics.R;
import com.exercise.continusebiometrics.application.App;
import com.exercise.continusebiometrics.data.Event;
import com.exercise.continusebiometrics.feature.main.MainScreenViewModel;
import com.exercise.continusebiometrics.feature.main.MainViewModelFactory;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.exercise.continusebiometrics.data.EventDao.Order.ASC;
import static com.exercise.continusebiometrics.data.EventDao.Order.DESC;

public class EventsFragment extends Fragment implements ItemClickListener{

    @Nullable
    MainScreenViewModel viewModel;

    @Inject
    MainViewModelFactory viewModelFactory;

    @BindView(R.id.nameHeaderView)
    TextView nameHeader;

    @BindView(R.id.dateHeaderView)
    TextView dateHeader;

    @BindView(R.id.statusHeaderView)
    TextView statusHeader;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Nullable
    private Unbinder unbinder;

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainScreenViewModel.class);
        observableViewModel();
        initViews();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        App.getAppComponent(getContext()).inject(this);
        return view;
    }

    private void initViews() {
        initRecyclerView();
        nameHeader.setOnClickListener(v -> viewModel.loadEventsByFilters("name", ASC));
        dateHeader.setOnClickListener(v -> viewModel.loadEventsByFilters("date", DESC));
        statusHeader.setOnClickListener(v -> viewModel.loadEventsByFilters("status", DESC));
    }

    private void initRecyclerView() {
        EventsRecyclerViewAdapter adapter = new EventsRecyclerViewAdapter(viewModel, this, this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observableViewModel() {
        //viewModel.getIsParsingPhoneSuccess().observe(this, sucess -> Toast.makeText(getContext(), getString(sucess ? R.string.phone_parse_success_message_text : R.string.phone_parse_error_message_text), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(Event item, int position) { }

    @Override
    public void onLongItemClick(Event item, int position) { }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
