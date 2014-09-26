package com.rjturek.amp.govna.service

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/api")
class Consumers {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "hello"
    }

    @GET
    @Path("/consumer")
    @Produces(MediaType.APPLICATION_JSON)
    public String getConsumers() {
        return "here are some consumers"
    }

    @GET
    @Path("/consumer/group/{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getGroup(@PathParam("groupId") String id) {
        return "here is group $id stuff"
    }
}
