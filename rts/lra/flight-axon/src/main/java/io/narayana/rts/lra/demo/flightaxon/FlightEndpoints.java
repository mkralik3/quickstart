package io.narayana.rts.lra.demo.flightaxon;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.narayana.rts.lra.demo.flightaxon.command.api.command.CancelFlightCmd;
import io.narayana.rts.lra.demo.flightaxon.command.api.command.CompensateFlightCmd;
import io.narayana.rts.lra.demo.flightaxon.command.api.command.CompleteFlightCmd;
import io.narayana.rts.lra.demo.flightaxon.command.api.command.CreateFlightCmd;
import io.narayana.rts.lra.demo.flightaxon.model.Booking;
import io.narayana.rts.lra.demo.flightaxon.query.AllBookingSummaryQuery;
import io.narayana.rts.lra.demo.flightaxon.query.BookingSummaryQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;

@RequestScoped
@Path("/")
@Slf4j
public class FlightEndpoints {
    @Inject
    private CommandGateway cmdGateway;

    @Inject
    private QueryGateway queryGateway;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LRA(value = LRA.Type.NESTED, end = false)
    public Booking bookFlight(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId,
                              @QueryParam("flightNumber") @DefaultValue("") String flightNumber) throws InterruptedException {
        cmdGateway.sendAndWait(new CreateFlightCmd(lraId, flightNumber, "Flight"));
        Thread.sleep(500);
        return getBookingFromQueryBus(lraId);
    }

    @PUT
    @Path("complete")
    @Produces(MediaType.APPLICATION_JSON)
    @Complete
    public Response completeWork(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId) throws NotFoundException, InterruptedException, JsonProcessingException {
        cmdGateway.sendAndWait(new CompleteFlightCmd(lraId));
        Thread.sleep(500);
        return Response.ok(getBookingFromQueryBus(lraId).toJson()).build();
    }

    @PUT
    @Path("compensate")
    @Produces(MediaType.APPLICATION_JSON)
    @Compensate
    public Response compensateWork(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId) throws NotFoundException, InterruptedException, JsonProcessingException {
        cmdGateway.sendAndWait(new CompensateFlightCmd(lraId));
        Thread.sleep(500);
        return Response.ok(getBookingFromQueryBus(lraId).toJson()).build();
    }

    @DELETE
    @Path("{bookingId}")
    @Produces(MediaType.APPLICATION_JSON)
    @LRA(LRA.Type.NOT_SUPPORTED)
    public Booking cancelFlight(@PathParam("bookingId") String bookingId) throws InterruptedException {
        cmdGateway.sendAndWait(new CancelFlightCmd(bookingId));
        Thread.sleep(500);
        return getBookingFromQueryBus(bookingId);
    }

    @GET
    @Path("{bookingId}")
    @Produces(MediaType.APPLICATION_JSON)
    @LRA(LRA.Type.NOT_SUPPORTED)
    public Booking getBooking(@PathParam("bookingId") String bookingId) {
        return getBookingFromQueryBus(bookingId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Booking> getAll() {
        return queryGateway.query(new AllBookingSummaryQuery(), ResponseTypes.multipleInstancesOf(Booking.class)).join();
    }

    private Booking getBookingFromQueryBus(String lraId) {
        Booking join = queryGateway.query(new BookingSummaryQuery(lraId),
                ResponseTypes.instanceOf(Booking.class))
                .join();
        log.debug("returned class is {}", join);
        return join;
    }
}
