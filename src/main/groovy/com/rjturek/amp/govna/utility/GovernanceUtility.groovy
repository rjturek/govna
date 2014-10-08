package com.rjturek.amp.govna.utility

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by ckell on 10/6/14.
 */
class GovernanceUtility {
    Logger logger = Logger.getLogger("sharedLogger")

    def log(msg) {
        logger.log(Level.FINE, msg)
    }

    public Object processGAVDependencies(String groupName, String artifactName, String version){
        log("processGAVDependencies()")

    }
}
