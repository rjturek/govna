package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.ValidationRequest
import com.rjturek.amp.govna.utility.GovernanceUtility

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
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

@Path("/governance")
@Consumes(MediaType.APPLICATION_JSON)

class GovernanceService {

    static Logger logger = Logger.getLogger("sharedLogger")
    def log(msg) {
        logger.log(Level.FINE, msg)
    }
    def log(msg, throwable) {
        logger.log(Level.FINE, msg, throwable)
    }
    @Context Request request

    GovernanceUtility gu = new GovernanceUtility()

    @POST
    @Path( "validate" )
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateConsumerGroupDependencies( ValidationRequest jsonRequest ) {
        log( "Validating Consumer Group Dependencies" )
        log( " Consumer group:    ${jsonRequest.consumerGroup}")
        log("  Dependency Coords: ${jsonRequest.dependencyCoordinates}")

        String message = " {foo: bar}"
        return Response.ok(message).build()
    }

    @GET
    @Path("hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Object sayHello() {
        log("GET hello")
        try {
            return "{hello : world}"
        }
        catch (Exception e) {
            log("Exception in hello world", e)
        }
    }
}
