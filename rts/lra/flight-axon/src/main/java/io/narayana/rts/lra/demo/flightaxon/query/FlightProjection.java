package io.narayana.rts.lra.demo.flightaxon.query;

import io.narayana.rts.lra.demo.flightaxon.command.api.event.ChangeFlightStateEvent;
import io.narayana.rts.lra.demo.flightaxon.command.api.event.CreateFlightEvent;
import io.narayana.rts.lra.demo.flightaxon.model.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightProjection {

    private final EntityManager em;

    @EventHandler
    public void on(CreateFlightEvent evt){
        log.debug("projecting CreateFlightEvent {}", evt);
        em.persist(new Booking(evt.getId(), evt.getName(), evt.getType(), evt.getStatus(), evt.getDetails()));
        em.flush();
    }

    @EventHandler
    public void on(ChangeFlightStateEvent evt){
        log.debug("projecting ChangeFlightStateEvent {}", evt);
        em.find(Booking.class, evt.getId()).setStatus(evt.getStatus());
        em.flush();
    }

    @QueryHandler
    public Booking handle(BookingSummaryQuery qry){
        return em.find(Booking.class, qry.getId());
    }

    @QueryHandler
    public List<Booking> handle(AllBookingSummaryQuery qry){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
        Root<Booking> rootEntry = cq.from(Booking.class);
        CriteriaQuery<Booking> all = cq.select(rootEntry);
        TypedQuery<Booking> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }
}
