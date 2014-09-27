package com.rjturek.amp.govna.service

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("/artifact")
class Artifact {

    @GET
    public String hello() {
        return "wah"
    }

    @GET
    @Path("")
    public String getProviders() {
        return "here are some providers"
    }

    @GET
    @Path("group:{groupId}")
    public String getGroup(@PathParam("groupId") String id) {
        return "here is goupd $id stuff - provider"
    }
}
