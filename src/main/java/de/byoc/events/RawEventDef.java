package de.byoc.events;

import de.byoc.util.DataConstructorClass;

@DataConstructorClass
public interface RawEventDef {

    Long index();

    String id();

    String payloadType();

    byte[] payload();

}
