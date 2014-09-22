package com.rjturek.amp.govna.service

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("/*")
class Consumers {

    @GET
    public String hello() {
        return "hello"
    }

    @GET
    @Path("/consumer")
    public String getConsumers() {
        return "here are some consumers"
    }

    @GET
    @Path("/consumer/group/{groupId}")
    public String getGroup(@PathParam("group") String id) {
        return "here is goupd $id stuff"
    }
}
