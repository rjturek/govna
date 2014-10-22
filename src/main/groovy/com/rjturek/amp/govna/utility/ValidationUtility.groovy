package com.rjturek.amp.govna.utility

import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.dataobj.ValidationRequest
import com.rjturek.amp.govna.dataobj.ValidationResponse
import com.rjturek.amp.govna.db.DependencyDao
import com.rjturek.amp.govna.dataobj.ValidationResponseElement

import java.util.logging.Logger
import org.apache.maven.artifact.versioning.DefaultArtifactVersion

/**
 * Created by ckell on 10/6/14.
 */
class ValidationUtility {

    static Logger logger = Logger.getLogger("sharedLogger")
    DependencyDao dao = new DependencyDao()

    def ValidationResponse buildValidationResponse(List<ValidationResponseElement> validationResponseElementList){
        logger.fine( "ValidationUtility.buildValidationResponse()" )
    }


    //TODO: convert any version to semantic version scheme. in other words, get rid of the weird text in versions.
    def boolean checkVersionBoundaries(String dependencyVersion, String versionLow, String versionHigh){
        logger.fine( "ValidationUtility.checkVersionBoundaries()" )
        logger.info( "determining if ${dependencyVersion} falls between ${versionLow} and ${versionHigh}")

        boolean inBoundary = false

        DefaultArtifactVersion minVersion = new DefaultArtifactVersion(versionLow)
        DefaultArtifactVersion maxVersion = new DefaultArtifactVersion(versionHigh)

        DefaultArtifactVersion version = new DefaultArtifactVersion(dependencyVersion);

        logger.fine("version compareTo minVersion: ${version.compareTo(minVersion)}")
        logger.fine("version compareTo maxVersion: ${version.compareTo(maxVersion)}")

        if (version.compareTo(minVersion) == 1 && version.compareTo(maxVersion) == -1) {
            logger.info("Version: ${dependencyVersion} falls between the version range: ${versionLow} - ${versionHigh} ")
            inBoundary = true
        }

        return inBoundary
    }


    def ValidationResponseElement buildValidationResponseElement( String consumerGroupName, String dependencyArtifactName, String dependencyVersion, GroupRestrictions groupRestrictions) {
        logger.fine( "ValidationUtility.buildValidationResponseElement()" )
        logger.info( "consumer group name: ${consumerGroupName}" )

        groupRestrictions.each{ gr ->
            logger.info( "Group Restriction Group Name: ${gr.groupName}" )

            int restrictionCount = 0
            gr.restrictions.each { r ->
                restrictionCount++
                logger.info("=============================================================")
                logger.info( "Restriction:    ${restrictionCount}")
                logger.info("isDeprecated:    ${r.isDeprecated}")
                logger.info("artifactId:      ${r.artifactId}")
                logger.info("versionLow:      ${r.versionLow}")
                logger.info("versionHish:     ${r.versionHigh}")
                logger.info("message:         ${r.message}")
                logger.info("exemptConsumers: ${r.exemptConsumers}")

                /* what is the partial story for this row? */
                if ( (r.exemptConsumers != null) && (consumerGroupName in r.exemptConsumers) ) {
                    logger.info("Consumer Group Name is found within the exempt consumers list..........")

                }

                if (dependencyArtifactName == r.artifactId){
                    logger.info("Match found on Artifact Name..........")

                }

                if ( r.versionLow!=null && r.versionHigh != null ){

                    boolean inBoundary = checkVersionBoundaries(dependencyVersion, r.versionLow, r.versionHigh )
                    if (inBoundary) {
                        logger.info("Dependency Version found within boundary.")
                    } else {
                        logger.info("Dependency Version found outside of boundary.")
                    }

                }
            }
            logger.info("=============================================================")
        }
    }

    /**
     *
     */
    def ValidationResponse analyzeDependenciesForRestrictions( ValidationRequest request ){
        logger.fine( "ValidationUtility.analyzeDependenciesForRestrictions()" )

        /* a poor mans cache of all the restrictions */
        Map<String, GroupRestrictions> groupRestrictionsMap = dao.getAllGroupRestrictionsMap()

        /* loop through the consumers dependencies and see if any of them have a top level group name restriction */
        request.dependencyCoordinates.each{ gav->
            logger.info( "Inspecting dependency Group Artifact and Version: ${gav}" )

            def ( dependencyGroupName, dependencyArtifactName, dependencyVersion ) = gav.split( ":" )

            /* check the cache Map to see if the dependency group name is a key: meaning there are restrictions applied to the group name. */
            if( groupRestrictionsMap.containsKey( dependencyGroupName ) ){
                logger.info( "Found a consumer dependency Group Name that contains Restrictions: ${dependencyGroupName}" )

                GroupRestrictions groupRestrictions = groupRestrictionsMap[dependencyGroupName]

                ValidationResponseElement validationResponseElement = buildValidationResponseElement( request.consumerGroup, dependencyArtifactName, dependencyVersion ,groupRestrictions )

            }else{
                logger.info( "No Group Restrictions found for: ${dependencyGroupName} ")
            }
        }

        validationResponse.failBuild = failBuild

        return validationResponse
    }

    /**
     * Entry point to compare the ValidationRequest against the Restrictions for the dependencies of the consumerGroup.
     *
     * @param ValidationRequest jsonRequest
     * @return ValidationResponse validationResponse
     */
    def ValidationResponse checkConsumerGroupRestrictions( ValidationRequest jsonRequest ){
        logger.fine( "ValidationUtility.checkConsumerGroupRestrictions()" )

        ValidationResponse validationResponse = analyzeDependenciesForRestrictions( jsonRequest )

        return validationResponse

    }
}