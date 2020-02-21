package de.byoc.events;

import io.reactivex.Flowable;

public interface EventStream {

    Flowable<RawEvent> streamAggregate(String aggregateIdentifier);

}
