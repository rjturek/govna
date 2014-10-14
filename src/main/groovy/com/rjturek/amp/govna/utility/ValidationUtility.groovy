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
     * Entry point to compare the ValidationRequest against the Restrictions for the consumerGroup.
     *
     * @param ValidationRequest jsonRequest
     * @return ValidationResponse validationResponse
     */
    public Object checkConsumerGroupRestrictions( ValidationRequest jsonRequest){
        logger.fine( "checkConsumerGroupRestrictions()" )
        logger.info( "Consumer group: ${jsonRequest.consumerGroup}" )

        logger.info("Restrictions found for consumer group: " + dao.getGroupRestrictions(jsonRequest.consumerGroup))

        Map groupMap = dao.getAllGroupRestrictionsMap()

        ValidationResponse response = new ValidationResponse()

        logger.fine( "*************************************" )
        for ( k in groupMap) {
            logger.fine( "Key: ${k.key} " )
            logger.fine( "Value: ${k.value}" )
        }



        jsonRequest.dependencyCoordinates.each { gav ->

            logger.info("Group Artifact Version: ${gav}")
        }

        return "${dao.getGroupRestrictions(jsonRequest.consumerGroup)}"

    }
}