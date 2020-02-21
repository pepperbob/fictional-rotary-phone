package de.byoc.gobbler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.byoc.events.EventStream;
import de.byoc.events.MutableEventStore;
import io.reactivex.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.transaction.Transactional;

@ApplicationScoped
public class Gobbler {

    @Inject
    EventStream eventStream;

    @Inject
    MutableEventStore eventStore;

    @Inject
    ObjectMapper objectMapper;

    @Transactional
    public void gobble(String aggregateIdentifier, GobbleRule gobbleRule) {
        eventStream.streamAggregate(aggregateIdentifier)
                .map(x -> gobbleRule.gobble(objectMapper, x))
                .flatMap(Maybe::toFlowable)
                .subscribe(eventStore::update);
    }

}
