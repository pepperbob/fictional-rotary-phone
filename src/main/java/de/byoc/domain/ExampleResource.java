package de.byoc.domain;

import de.byoc.events.EventStream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class ExampleResource {

    @Inject
    EventStream eventStore;

    @GET
    @Path("/{helloId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello(@PathParam("helloId") String helloId) {
        return eventStore.streamAggregate(helloId)
                .firstElement()
                .map(x -> x.payload())
                .map(x -> Response.ok(x).build())
                .blockingGet();
    }

}