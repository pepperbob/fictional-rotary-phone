package de.byoc.events;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class JpaEventStream implements EventStream, MutableEventStore {

    private EntityManager em;

    private int batchSize = 5;

    @Inject
    public JpaEventStream(EntityManager em) {
        this.em = em;
    }

    @Override
    public Flowable<RawEvent> streamAggregate(String aggregateIdentifier) {
        return doStream(x ->
                em.createQuery("SELECT new de.byoc.events.RawEvent(e.globalIndex, e.eventIdentifier, e.payloadType, e.payload) FROM DomainEventEntry e WHERE e.aggregateIdentifier = :aggregateIdentifier Order By e.globalIndex", RawEvent.class)
                        .setFirstResult(x * batchSize)
                        .setMaxResults(batchSize)
                        .setParameter("aggregateIdentifier", aggregateIdentifier));
    }

    private <T> Flowable<T> doStream(Function<? super Integer, TypedQuery<T>> queryFn) {
        return Flowable.range(0, Integer.MAX_VALUE)
                .map(queryFn)
                .map(TypedQuery::getResultList)
                .takeUntil((Predicate<? super List<T>>) rs -> rs.size() < batchSize)
                .flatMap(Flowable::fromIterable);
    }

    @Override
    public void update(RawEvent event) {
        em.createQuery("UPDATE DomainEventEntry e SET e.payload = :payload WHERE e.eventIdentifier = :id")
                .setParameter("id", event.id())
                .setParameter("payload", event.payload())
                .executeUpdate();
    }

}
