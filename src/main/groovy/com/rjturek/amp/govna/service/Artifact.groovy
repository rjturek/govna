package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.db.MongoData

import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.Path

@Path("/artifact")
class Artifact {

    def dao = new MongoData()

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "wah"
    }

    @GET
    @Path("groups")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getGroups() {
        def groups = dao.getGroups()
        return groups
    }

    @GET
    @Path("group:{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getGroup(@PathParam("groupId") String id) {
        def group = dao.getGroup(id)
        return group
    }
}
