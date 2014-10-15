package com.rjturek.amp.govna.utility

import com.rjturek.amp.govna.dataobj.ValidationRequest
import com.rjturek.amp.govna.dataobj.ValidationResponse
import com.rjturek.amp.govna.db.DependencyDao

import java.util.logging.Logger

/**
 * Created by ckell on 10/6/14.
 */
class ValidationUtility {

     static Logger logger = Logger.getLogger("sharedLogger")

    DependencyDao dao = new DependencyDao()

    /**
     *
     */
    public Object checkDependencyRestrictions(ValidationRequest request, Map restrictionsMap){
        logger.fine("checkDependencyRestrictions()")


        request.dependencyCoordinates.each{ gav->
            logger.info(" Dependency GAV: ${gav}")
        }

    }

    /**
     * Entry point to compare the ValidationRequest against the Restrictions for the consumerGroup.
     *
     * @param ValidationRequest jsonRequest
     * @return ValidationResponse validationResponse
     */
    public Object checkConsumerGroupRestrictions( ValidationRequest jsonRequest){
        logger.fine( "checkConsumerGroupRestrictions()" )
        logger.info( "Consumer group: ${jsonRequest.consumerGroup}" )

        logger.info("Restrictions found for consumer group: " + dao.getGroupRestrictions(jsonRequest.consumerGroup))

        Map groupRestrictionsMap = dao.getAllGroupRestrictionsMap()

        ValidationResponse validationResponse = checkDependencyRestrictions(jsonRequest, groupRestrictionsMap)

        return validationResponse

    }
}