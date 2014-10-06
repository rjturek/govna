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

    @PUT
    @Path("group/{groupName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Object insertOrUpdateGroup(@PathParam("groupName") String groupName, GroupRestrictions group) {
        logger.log(Level.FINE, "PUT group... groupName: $groupName")

        if (!group) {

        }
        if (dao.groupRestrictionsExist(groupName)) {
            dao.update(group)
        }
        else {
            dao.insertGroupRestrictions(group)
        }
    }
}


