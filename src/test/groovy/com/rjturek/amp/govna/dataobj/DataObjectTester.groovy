package com.rjturek.amp.govna.dataobj

import com.rjturek.amp.govna.db.DependencyDao
import jersey.repackaged.com.google.common.collect.Lists

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
        restriction.exemptConsumers = permittedConsumers

        restriction
    }

    /**
     * create a GroupOnly GroupRestriction Object
     *
     * @param groupId
     * @param restriction
     * @return GroupRestriction
     */
    private static Object createGroupOnlyRestriction(String groupName, Restriction restriction){
        logger.info("createGroupOnlyRestriction()")

        GroupRestrictions groupRestrictions = new GroupRestrictions()

        groupRestrictions.groupName = groupName
        groupRestrictions.restriction = restriction

        groupRestrictions

    }

    /**
     * create a json object that contains restrictions on a group and an artifact.
     *   the G and A of GAV
     *
     * @param groupId
     * @param artifactId
     * @param restriction
     * @return GroupRestriction
     *
     * TODO: make multiple artifact restrictions.  (AKA list)
     */
    private static Object createGroupAndArtifactRestriction(String groupName, String artifactId, Restriction restriction) {
        logger.info("createGroupAndArtifactRestriction()")

        GroupRestrictions groupRestrictions = new GroupRestrictions()
        groupRestrictions.groupName = groupName

        ArtifactRestriction artifactRestriction = new ArtifactRestriction()
        artifactRestriction.artifactId = artifactId
        artifactRestriction.restriction = restriction

        /* add the artifactRestriction to a List */
        List<ArtifactRestriction> artifactRestrictionList = Lists.newArrayList(artifactRestriction)

        /* add the artifact restriction list to the Group Restriction */
        groupRestrictions.artifactRestrictions = artifactRestrictionList

        groupRestrictions

    }

    /**
     *   create a restriction for a version rnge at the group level.
     *
     * @param groupName
     * @param versionLow
     * @param versionHigh
     * @param restriction
     * @return
     */
    private static Object createGroupVersionRestriction( String groupName, String versionLow, String versionHigh, Restriction restriction ){
        logger.info("createGroupVersionRestriction")

        GroupRestrictions groupRestrictions = new GroupRestrictions()
        groupRestrictions.groupName = groupName

        VersionRestriction versionRestriction = new VersionRestriction()
        versionRestriction.versionLow = versionLow
        versionRestriction.versionHigh = versionHigh
        versionRestriction.restriction = restriction

        List<VersionRestriction> versionRestrictionList = Lists.newArrayList(versionRestriction)

        groupRestrictions.versionRestrictions = versionRestrictionList

        groupRestrictions
    }

    private static Object createGroupArtifactVersionRestriction(String groupName, String artifactId, String versionLow, String versionHigh, Restriction restriction){
        logger.info("createGroupArtifactVersionRestriction")

    }

    static void main(String[] args) {

        /* what group and artifact are we restricting?*/
//        String groupName = "ant"
//        String artifactId = "ant-optional"
        String groupName = "org.apache.solr"
        String artifactId = "solr-clustering"


        /* if we just need to restriction one version, set the low and the high to the same version? */
//        String artifactVersionLow  = "1.4.1"
//        String artifactVersionHigh = "1.4.1"
        String versionLow  = "3.6.2"
        String versionHigh  = "4.3.0"

        /* restriction variables */
//        boolean isDeprecated = false
//        String restrictionMessage = "This version has known vulnerabilities.  Please use the latest provided in Artifactory."
//        List<String> exemptConsumers = ["com.trp.amp.app", "com.trp.sec.util"]
          boolean isDeprecated = false
          String restrictionMessage = "These versions have known WebSphere cluster compatibility issues.  Please use the latest provided in Artifactory."
          List<String> permittedConsumers = ["com.trp.tst.app"]


        DataObjectTester dataObjectTester = new DataObjectTester()

        /* create a restriction to use */
        Restriction restriction = createRestriction(isDeprecated, restrictionMessage, permittedConsumers)


        //logger.info("Creating Group only restriction")
        //GroupRestrictions groupRestrictions = dataObjectTester.createGroupOnlyRestriction(groupName, restriction)

//        logger.info("Creating Group and Artifact restriction")
//        GroupRestrictions groupRestrictions = createGroupAndArtifactRestriction(groupName, artifactId, restriction)

        logger.info("Creating Group and Version restriction")
        GroupRestrictions groupRestrictions = createGroupVersionRestriction(groupName, versionLow, versionHigh, restriction)

        /* after we get the restrictions we need, let's drop it in mongo*/
        DependencyDao dependencyDao = new DependencyDao()
        dependencyDao.upsertGroupRestrictions(groupRestrictions)

        /* uncomment if you want to remove it too. */
        //logger.info( "Removing: ${groupRestrictions.groupName}")
        //dependencyDao.removeGroupRestrictions(groupRestrictions)
    }
}
