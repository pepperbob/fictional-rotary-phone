package de.byoc.gobbler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.byoc.domain.DoSayHelloWorld;
import de.byoc.domain.HelloWorldSaid;
import de.byoc.domain.InsertCrap;
import de.byoc.events.EventStream;
import de.byoc.events.JsonNodeStream;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class HelloWorldAggregateTest {

    @Inject
    CommandGateway cmd;

    @Inject
    Gobbler gobbler;

    @Inject
    EventStream eventStream;

    @Inject
    JsonNodeStream jsonStream;

    @Test
    void name() {
        cmd.sendAndWait(DoSayHelloWorld.builder().helloId("one-two").build());
        for(int i = 0; i < 123; i++) {
            cmd.sendAndWait(InsertCrap.builder().id("one-two").crapThereIs(UUID.randomUUID().toString()).build());
        }

        given()
                .accept(ContentType.JSON)
                .when().get("/hello/one-two").then()
                .log().all()
                .statusCode(200)
                .body("whatItSaid", Matchers.containsString("World!"));

        var rule = new GobbleRule(
                GobbleRule.GobbleFn.gobbel(HelloWorldSaid.class, o -> o.put("whatItSaid", "xxxxxx")),
                GobbleRule.GobbleFn.gobbel(String.class, o -> o.put("unrelated", "xxxxxx")),
                GobbleRule.GobbleFn.gobbel(HelloWorldSaid.class, o -> o.put("whatItSaid", "aaaaa")),
                GobbleRule.GobbleFn.gobbel(t -> t.id().startsWith("5"), o -> {
                    System.out.println("Putting awww in " + o);
                    return o.put("test", "awww!1");
                }));

        gobbler.gobble("one-two", rule);

        given()
                .accept(ContentType.JSON)
                .when().get("/hello/one-two").then()
                .log().all()
                .statusCode(200)
                .body("whatItSaid", Matchers.containsString("xxxxxx"));
    }

    @Test
    void test2() {
        var start = Instant.now();
        jsonStream.streamAggregate("one-two").forEach(x -> System.out.println(x));
        var dur = Duration.between(start, Instant.now()).toMillis();
        System.out.println("Took me ms " + dur);
    }


}