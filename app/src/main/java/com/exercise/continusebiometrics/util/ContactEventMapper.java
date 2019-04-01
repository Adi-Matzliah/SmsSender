package com.exercise.continusebiometrics.util;

import com.exercise.continusebiometrics.data.Contact;
import com.exercise.continusebiometrics.data.Event;

public class ContactEventMapper {

    public static Event convertContactToEvent(Contact contact, @Event.Status String status, @Event.Type String type) {
        String firstName = contact.getFirstName();
        String lastName = contact.getLastName();
        String name = (firstName == null || lastName == null)
                ? contact.getPhone()
                : contact.getFirstName() + " " + contact.getLastName();
        return new Event(name, status, type);
    }
}
