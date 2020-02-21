package de.byoc.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateRoot;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@AggregateRoot
public class HelloWorldAggregate {

    @AggregateIdentifier
    private String id;

    private long pileOfCrap;

    public HelloWorldAggregate() {
    }

    @CommandHandler
    public HelloWorldAggregate(DoSayHelloWorld cmd) {
        apply(HelloWorldSaid.builder().helloId(cmd.helloId()).whatItSaid(cmd.theHelloToSay()).build());
    }

    @CommandHandler
    public void on(InsertCrap cmd) {
        apply(CrapInserted.builder().id(cmd.id()).whatsTheCrap(cmd.crapThereIs()).build());
    }

    @EventSourcingHandler
    void on(HelloWorldSaid event) {
        this.id = event.helloId();
    }

    @EventSourcingHandler
    void on(CrapInserted event) {
        pileOfCrap += event.whatsTheCrap().length();
    }
}
