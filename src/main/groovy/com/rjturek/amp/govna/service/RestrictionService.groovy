package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.db.DependencyDao

import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Request
import javax.ws.rs.core.Response
import java.util.logging.Level
import java.util.logging.Logger

@Path("/restrictions")
@Produces(MediaType.APPLICATION_JSON)

class RestrictionService {

    static Logger logger = Logger.getLogger("sharedLogger")
    def log(msg) {
        logger.log(Level.FINE, msg)
    }
    def log(msg, throwable) {
        logger.log(Level.FINE, msg, throwable)
    }
    @Context Request request

    DependencyDao dao = new DependencyDao()

    @GET
    @Path("groups")
    public Object getGroups() {
        log('GET groups')
        try {
            return dao.getGroupRestrictionsList()
        }
        catch (Exception e) {
            log("Exception in getGroups()", e)
        }
    }

    @GET
    @Path("group/{groupName}")
    public Object getGroup(@PathParam("groupName") String groupName) {
        log("GET group/${groupName}")
        try {
            return dao.getGroupRestrictions(groupName)
        }
        catch (Exception e) {
            log("Exception in getGroup()", e)
        }
    }

    @PUT
    @Path("group/{groupName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Object insertOrUpdateGroup(@PathParam("groupName") String groupName, GroupRestrictions group) {
        log("PUT group... groupName: $groupName")
        try {
            if (!group) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: The JSON request object is empty").build();
            }
            if (groupName != group.groupName) {
                return Response.status(Response.Status.BAD_REQUEST).entity(
                        "Error: The groupName in the URI does not match 'groupName' in the JSON request object").build();
            }
            dao.upsertGroupRestrictions(group)
        }
        catch (Exception e) {
            log("Exception in insertOrUpdateGroup()", e)
        }
    }
}


