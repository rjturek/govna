package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.DependencyGroup
import com.rjturek.amp.govna.dataobj.Restriction
import com.rjturek.amp.govna.db.DependencyDao
import groovy.json.JsonBuilder

import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/dependencies")
class DependencyService {

    DependencyDao dao = new DependencyDao()

    @GET
    @Path("groups")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDependencyGroups() {
        return dao.getGroups()
    }

    @GET
    @Path("group/{groupName}")
    @Produces(MediaType.APPLICATION_JSON)
    public DependencyGroup getGroup(@PathParam("groupName") String groupName) {
        return groupName
    }

    @PUT
    @Path("group/{groupName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object insertGroup(@PathParam("groupName") String groupName) {

        DependencyGroup group = new DependencyGroup()
        group.groupName = groupName
        Restriction restriction = new Restriction()
        restriction.isDeprecated = false
        group.restriction = restriction

        return group
    }
}