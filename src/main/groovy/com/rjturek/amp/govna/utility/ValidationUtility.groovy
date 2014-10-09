package com.rjturek.amp.govna.utility

import com.rjturek.amp.govna.dataobj.ValidationRequest

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by ckell on 10/6/14.
 */
class ValidationUtility {

    static Logger logger = Logger.getLogger("sharedLogger")
    def log(msg) {
        logger.log(Level.FINE, msg)
    }
    def log(msg, throwable) {
        logger.log(Level.FINE, msg, throwable)
    }


    public Object checkConsumerGroupRestrictions( ValidationRequest jsonRequest){
        log( "checkConsumerGroupRestrictions()" )
        log( "Consumer group: ${jsonRequest.consumerGroup}" )

        jsonRequest.dependencyCoordinates.each { gav ->

            log("Group Artifact Version: ${gav}")
        }

        return "{test:json}"

    }
}