package io.narayana.rts.lra.demo.flightaxon.command;

import io.narayana.rts.lra.demo.flightaxon.command.api.command.CancelFlightCmd;
import io.narayana.rts.lra.demo.flightaxon.command.api.command.CompensateFlightCmd;
import io.narayana.rts.lra.demo.flightaxon.command.api.command.CompleteFlightCmd;
import io.narayana.rts.lra.demo.flightaxon.command.api.command.CreateFlightCmd;
import io.narayana.rts.lra.demo.flightaxon.command.api.event.ChangeFlightStateEvent;
import io.narayana.rts.lra.demo.flightaxon.command.api.event.CreateFlightEvent;
import io.narayana.rts.lra.demo.flightaxon.model.Booking;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
@Slf4j
public class Flight {

    @AggregateIdentifier
    private String id;
    private String name;
    private Booking.BookingStatus status;
    private String type;
    private Booking[] details;

    @CommandHandler
    public Flight(CreateFlightCmd cmd){
        log.debug("handling {}", cmd);
        apply(new CreateFlightEvent(cmd.getId(), cmd.getName(), cmd.getType()));
    }

    @CommandHandler
    public void handle(CompleteFlightCmd cmd){
        log.debug("handling {}", cmd);
        //do some validation
        apply(new ChangeFlightStateEvent(cmd.getId(), cmd.getStatus()));
    }

    @CommandHandler
    public void handle(CompensateFlightCmd cmd){
        log.debug("handling {}", cmd);
        //do some validation
        apply(new ChangeFlightStateEvent(cmd.getId(), cmd.getStatus()));
    }

    @CommandHandler
    public void handle(CancelFlightCmd cmd){
        log.debug("handling {}", cmd);
        //do some validation
        apply(new ChangeFlightStateEvent(cmd.getId(), cmd.getStatus()));
    }

    @EventSourcingHandler
    public void on(CreateFlightEvent evt){
        log.debug("applying {}", evt);
        id = evt.getId();
        name = evt.getName();
        status = evt.getStatus();
        type = evt.getType();
        details = evt.getDetails();
    }

    @EventSourcingHandler
    public void on(ChangeFlightStateEvent evt){
        log.debug("applying {}", evt);
        status = evt.getStatus();
    }
}
