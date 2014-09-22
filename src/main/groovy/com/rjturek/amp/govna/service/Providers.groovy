package com.rjturek.amp.govna.service

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("/*")
class Providers {

    @GET
    public String hello() {
        return "wah"
    }

    @GET
    @Path("/provider")
    public String getProviders() {
        return "here are some providers"
    }

    @GET
    @Path("/providers/group/{groupId}")
    public String getGroup(@PathParam("group") String id) {
        return "here is goupd $id stuff - provider"
    }
}
