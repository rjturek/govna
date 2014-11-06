package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.dataobj.TrialValidationRequest
import com.rjturek.amp.govna.dataobj.ValidationRequest
import com.rjturek.amp.govna.utility.ValidationUtility

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Request
import javax.ws.rs.core.Response
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by ckell on 10/6/14.
 */

@Path("/validation")
@Consumes(MediaType.APPLICATION_JSON)

class ValidationService {

    static Logger logger = Logger.getLogger("sharedLogger")
    def log(msg) {
        logger.log(Level.FINE, msg)
    }
    def log(msg, throwable) {
        logger.log(Level.FINE, msg, throwable)
    }
    @Context Request request

    ValidationUtility validationUtility = new ValidationUtility()

    /**
     * REST api url to POST json structure to tell the story of which dependencies have restrictions
     * for a particular consumerGroup. (the group in gav - group:archive:version)
     *
     * json structure:
     * { "consumerGroup" : "com.trp.wdt.app",
     *   "dependencyCoordinates":["log4j:log4j:1.2.17", "com.trp.amp.afutil:AMPafutilUTIL:1.10", "org.jdom:jdom:1.1.1"]
     * }
     *
     * @param jsonRequest
     * @return ValidationResponse
     */
    @POST
    @Path( "buildValidation" )
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buildValidation( ValidationRequest request ) {
        logger.info("Received POST request to validate the consumer group dependencies for the build of: ${request.consumerGroup}")

        try {
            return Response.ok(validationUtility.checkConsumerGroupRestrictions(request, null)).build()

        } catch (Exception e) {
            log("Exception in validateConsumerGroupDependencies()", e.printStackTrace())
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.message).build()
        }
    }

    @POST
    @Path( "trialValidation" )
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response trialValidation( TrialValidationRequest request ) {
        logger.info("Received POST request to trial validate the consumer group dependencies for : " +
                "${request.consumerGroup} using trial group : ${request.groupRestrictions.groupName}")

        try {
            Map<String, GroupRestrictions> groupRestrictionsMap = new HashMap<String, GroupRestrictions>()
            groupRestrictionsMap.put(request.groupRestrictions.groupName, request.groupRestrictions)

            return Response.ok(validationUtility.checkConsumerGroupRestrictions(request, groupRestrictionsMap)).build()

        } catch (Exception e) {
            log("Exception in validateConsumerGroupDependencies()", e.printStackTrace())
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.message).build()
        }
    }
}
