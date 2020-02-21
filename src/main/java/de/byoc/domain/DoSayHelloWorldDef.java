package de.byoc.domain;

import de.byoc.util.DataClass;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@DataClass
public interface DoSayHelloWorldDef {

    @TargetAggregateIdentifier
    String helloId();

    default String theHelloToSay() {
        return "Hello, World!";
    }

}
