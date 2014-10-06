package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.dataobj.Restriction
import com.rjturek.amp.govna.db.DependencyDao

import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Request

@Path("/restrictions")
@Produces(MediaType.APPLICATION_JSON)

class RestrictionService {

    @Context Request request

    DependencyDao dao = new DependencyDao()

    @GET
    @Path("groups")
    public String getDependencyGroups() {
        return dao.getGroupRestrictionsList()
        println "HHHHHHHHHEEEYYYYYY"
    }

    @GET
    @Path("group/{groupName}")
    public GroupRestrictions getGroup(@PathParam("groupName") String groupName) {
        return dao.getGroupRestrictions(groupName)
    }

    @POST
    @Path("group/*")
    @Consumes(MediaType.APPLICATION_JSON)
    public Object insertGroupPost(GroupRestrictions group) {
        if (! group) {
            println "Post Thing is null"
        }
        else {
            println group.dump()
        }
        return "hey Post"
    }

    @PUT
    @Path("group/*")
    @Consumes(MediaType.APPLICATION_JSON)
    public Object insertGroupPut(GroupRestrictions group) {
        if (! group) {
            println "Put Thing is null"
        }
        else {
            println group.dump()
        }
        return "hey Put"
    }
}


