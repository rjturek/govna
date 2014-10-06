package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.dataobj.Restriction
import com.rjturek.amp.govna.db.DependencyDao

import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Request
import java.util.logging.Level
import java.util.logging.Logger

@Path("/restrictions")
@Produces(MediaType.APPLICATION_JSON)

class RestrictionService {

    static Logger logger = Logger.getLogger("sharedLogger")

    @Context Request request

    DependencyDao dao = new DependencyDao()

    @GET
    @Path("groups")
    public String getDependencyGroups() {
        logger.log(Level.FINE, 'GET groups')
        return dao.getGroupRestrictionsList()
    }

    @GET
    @Path("group/{groupName}")
    public GroupRestrictions getGroup(@PathParam("groupName") String groupName) {
        logger.log(Level.FINE, "GET group/${groupName}")
        return dao.getGroupRestrictions(groupName)
    }

    @POST
    @Path("groupx")
    @Consumes(MediaType.APPLICATION_JSON)
    public Object insertGroupPost(GroupRestrictions group) {
        logger.log(Level.FINE, 'POST group')
        if (! group) {
            logger.log(Level.FINE, "It's null")
        }
        else {
            logger.log(Level.FINE, group.dump())
        }
    }

    @PUT
    @Path("groupy")
    @Consumes(MediaType.APPLICATION_JSON)
    public Object insertGroupPut(GroupRestrictions group) {
        logger.log(Level.FINE, 'PUT group')
        if (! group) {
            logger.log(Level.FINE, "It's null")
        }
        else {
            logger.log(Level.FINE, group.dump())
        }
    }
}


