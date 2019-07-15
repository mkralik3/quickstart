package io.narayana.rts.lra.demo.flightaxon.command.api.command;

import io.narayana.rts.lra.demo.flightaxon.model.Booking;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CreateFlightCmd {

    @TargetAggregateIdentifier
    private String id;
    private String name;
    private Booking.BookingStatus status = Booking.BookingStatus.PROVISIONAL;
    private String type;
    private Booking[] details = null;
}
