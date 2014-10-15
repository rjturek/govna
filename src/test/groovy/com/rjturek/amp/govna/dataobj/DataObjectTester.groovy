package com.rjturek.amp.govna.dataobj

import com.rjturek.amp.govna.db.DependencyDao

import java.util.logging.Logger

/**
 * Created by ckell on 10/15/14.
 */
class DataObjectTester {
    static Logger logger = Logger.getLogger("DataObjectTester")

    /**
     * Create a Restriction object
     *
     * @param isDeprecated
     * @param message
     * @param permittedConsumers
     * @return Restriction
     */
    private static Object createRestriction(boolean isDeprecated, String  message, List<String> permittedConsumers){
        logger.info("createRestriction()")
        Restriction restriction =  new Restriction()
        restriction.isDeprecated = isDeprecated
        restriction.message = message
        restriction.permittedConsumers = permittedConsumers

        restriction
    }

    /**
     * create a GroupOnly GroupRestriction Object
     *
     * @param groupId
     * @param restriction
     * @return GroupRestriction
     */
    private static Object createGroupOnlyRestriction(String groupId, Restriction restriction){
        logger.info("createGroupOnlyRestriction()")

        GroupRestrictions groupRestrictions = new GroupRestrictions()

        groupRestrictions.groupName = groupId
        groupRestrictions.restriction = restriction

        groupRestrictions

    }



    static void main(String[] args) {

        /* what group and artifact are we restricting?*/
        String groupId = "ant"
        String artifactId = "ant-optional"


        /* if we just need to restriction one version, set the low and the high to the same version? */
        String artifactVersionLow  = "1.4.1"
        String artifactVersionHigh = "1.4.1"

        /* restriction variables */
        boolean isDeprecated = false
        String restrictionMessage = "This version has known vulnerabilities.  Please use the latest provided in Artifactory."
        List<String> permittedConsumers = ["com.trp.amp.app", "com.trp.sec.util"]


        DataObjectTester dataObjectTester = new DataObjectTester()

        logger.info("Creating Group only restriction")
        Restriction restriction = createRestriction(isDeprecated, restrictionMessage, permittedConsumers)
        GroupRestrictions groupRestrictions = dataObjectTester.createGroupOnlyRestriction(groupId, restriction)


        /* after we get the restrictions we need, let's drop it in mongo*/
        DependencyDao dependencyDao = new DependencyDao()
        dependencyDao.upsertGroupRestrictions(groupRestrictions)

        /* uncomment if you want to remove it too. */
       logger.info( "Removing: ${groupRestrictions.groupName}")
       dependencyDao.removeGroupRestrictions(groupRestrictions)
    }
}
