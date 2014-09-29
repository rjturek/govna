package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.Group

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/consumer")
class Consumer {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "hello stizzle gronk"
    }

    @GET
    @Path("group")
    @Produces(MediaType.APPLICATION_JSON)
    public Map getConsumers() {
        return ["message": "here are some consumers"]
    }

    @GET
    @Path("group/{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Group getGroup(@PathParam("groupId") String id) {
        Group group = new Group()
        group.groupName = id
        return group
    }
}
