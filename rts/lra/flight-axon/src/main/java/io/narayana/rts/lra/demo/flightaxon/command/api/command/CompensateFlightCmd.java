package io.narayana.rts.lra.demo.flightaxon.command.api.command;

import io.narayana.rts.lra.demo.flightaxon.model.Booking;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CompensateFlightCmd {
    @TargetAggregateIdentifier
    private String id;
    private Booking.BookingStatus status = Booking.BookingStatus.CANCELLED;
}
