package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.db.MongoData

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/artifact")
class Artifact {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "wah"
    }

    @GET
    @Path("groups")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getGroups() {
        def dao = new MongoData()
        def groups = dao.getGroups()
        println groups.dump()
        return null
    }

    @GET
    @Path("group:{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getGroup(@PathParam("groupId") String id) {
        return "here is goupd $id stuff - provider"
    }
}
