package com.rjturek.amp.govna.service

import com.rjturek.amp.govna.dataobj.ValidationRequest
import com.rjturek.amp.govna.utility.ValidationUtility

import javax.ws.rs.Consumes
import javax.ws.rs.GET
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

    ValidationUtility vu = new ValidationUtility()

    @POST
    @Path( "consumerGroup" )
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateConsumerGroupDependencies( ValidationRequest jsonRequest ) {
        log( "Validating Consumer Group Dependencies" )

        return Response.ok(vu.checkConsumerGroupRestrictions(jsonRequest)).build()
    }

    @GET
    @Path("test")
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
