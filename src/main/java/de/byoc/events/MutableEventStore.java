package de.byoc.events;

public interface MutableEventStore {

    void update(RawEvent rawEvent);

}
