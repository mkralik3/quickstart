package io.narayana.rts.lra.demo.flightaxon.command.api.event;

import io.narayana.rts.lra.demo.flightaxon.model.Booking;
import lombok.Value;

@Value
public class ChangeFlightStateEvent {
    private String id;
    private Booking.BookingStatus status;
}
