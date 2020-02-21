package de.byoc.domain;

import de.byoc.util.DataClass;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@DataClass
public interface InsertCrapDef {

    @TargetAggregateIdentifier
    String id();

    String crapThereIs();

}
