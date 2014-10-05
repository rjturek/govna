package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.dataobj.Restriction
import com.rjturek.amp.govna.db.DependencyDao

import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Request

@Path("/restrictions")
class RestrictionService {

    @Context Request request

    DependencyDao dao = new DependencyDao()

    @GET
    @Path("groups")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDependencyGroups() {
        return dao.getGroupRestrictionsList()
    }

    @GET
    @Path("group/{groupName}")
    @Produces(MediaType.APPLICATION_JSON)
    public GroupRestrictions getGroup(@PathParam("groupName") String groupName) {
        return dao.getGroupRestrictions(groupName)
    }

    @PUT
    @Path("group/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object insertGroup(GroupRestrictions group) {
        if (! group) {
            println "Thing is null"
        }
        else {
            println group.dump()
        }
    }
}


