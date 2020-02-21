package de.byoc.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.reactivex.Flowable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class JsonNodeStream {

    @Inject
    EventStream es;

    @Inject
    ObjectMapper om;

    public Flowable<JsonNode> streamAggregate(String aggregateIdentifier) {
        return es.streamAggregate(aggregateIdentifier)
                .map(x -> ((ObjectNode) om.readTree(x.payload())).put("@class", x.payloadType()));
    }

}
