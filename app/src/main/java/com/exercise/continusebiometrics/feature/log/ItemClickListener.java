package com.exercise.continusebiometrics.feature.log;

import com.exercise.continusebiometrics.data.Event;

public interface ItemClickListener {
    void onItemClick(Event item, int position);
    void onLongItemClick(Event item, int position);
}
