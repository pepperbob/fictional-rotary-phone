package de.byoc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.byoc.domain.HelloWorldAggregate;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.json.JacksonSerializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

@ApplicationScoped
public class AxonConfig {

    @Inject
    EntityManager em;

    @Inject
    UserTransaction utx;

    Configuration config;

    public void starteAxon(@Observes StartupEvent evt, Configurer config) {
        this.config = config.start();
    }

    public void stoppeAxon(@Observes ShutdownEvent evt) {
        config.shutdown();
    }

    @Produces
    public Configurer erstelleConfigurer(EventStorageEngine eventStore) {
        return DefaultConfigurer.defaultConfiguration()
                .configureAggregate(HelloWorldAggregate.class)
                .configureEmbeddedEventStore(c -> eventStore);
    }

    @Produces
    public CommandGateway commandGateway() {
        return config.commandGateway();
    }

    @Produces
    public EventStorageEngine eventStore(ObjectMapper objectMapper) {
        return JpaEventStorageEngine.builder()
                .entityManagerProvider(() -> em)
                .transactionManager(new UtxTransactionManager(utx))
                .eventSerializer(JacksonSerializer.builder()
                        .objectMapper(objectMapper)
                        .build())
                .build();
    }

}
