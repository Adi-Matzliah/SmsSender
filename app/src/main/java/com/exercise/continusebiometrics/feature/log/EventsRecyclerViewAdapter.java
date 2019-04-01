package com.exercise.continusebiometrics.feature.log;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exercise.continusebiometrics.R;
import com.exercise.continusebiometrics.data.Event;
import com.exercise.continusebiometrics.feature.main.MainScreenViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.exercise.continusebiometrics.data.Event.Status.FAILURE;
import static com.exercise.continusebiometrics.data.Event.Status.SUCCESS;
import static com.exercise.continusebiometrics.data.Event.Type.CONTACT_SAVED;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.TaxiViewHolder>{

    private final ItemClickListener listener;
    private final List<Event> data = new ArrayList<>();

    public EventsRecyclerViewAdapter(MainScreenViewModel viewModel, LifecycleOwner lifecycleOwner, ItemClickListener clickListener) {
        this.listener = clickListener;
        viewModel.getEvents().observe(lifecycleOwner, this::updateItems);
        setHasStableIds(true);
    }

    private void updateItems(List<Event> events) {
        data.clear();
        if (events != null) {
            data.addAll(events);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public TaxiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_event_item, parent, false);
        return new TaxiViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxiViewHolder holder, int position) {
        Event event = data.get(position);
        View view = holder.itemView;
        switch (event.getStatus()) {
            case SUCCESS:
                view.setBackgroundColor(Color.GREEN);
                break;
            case FAILURE:
                view.setBackgroundColor(Color.RED);
                break;
        }

        holder.bind(new WeakReference(holder.itemView.getContext()), data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    static final class TaxiViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iconType)
        ImageView iconType;

        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.date)
        TextView date;

        @BindView(R.id.status)
        TextView status;

        private Event event;

        TaxiViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if(event != null) {
                    listener.onItemClick(event, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if(event != null) {
                    listener.onLongItemClick(event, getAdapterPosition());
                }
                return true;
            });

        }

        void bind(WeakReference<Context> context, Event event) {
            this.event = event;
            iconType.setImageDrawable(event.getType().equals(CONTACT_SAVED) ? context.get().getDrawable(R.drawable.ic_baseline_attach_file_24px) : context.get().getDrawable(R.drawable.ic_baseline_sms_24px));
            if (event.getType().equals(CONTACT_SAVED)) {
                name.setBackgroundColor(Color.parseColor("#54d4ff"));
            }
            name.setText(event.getName());
            date.setText(event.getDate());
            status.setText(event.getStatus());
        }
    }
}
