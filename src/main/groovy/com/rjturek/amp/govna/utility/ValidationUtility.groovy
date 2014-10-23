package com.rjturek.amp.govna.utility

import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.dataobj.Restriction
import com.rjturek.amp.govna.dataobj.ValidationRequest
import com.rjturek.amp.govna.dataobj.ValidationResponse
import com.rjturek.amp.govna.db.DependencyDao
import com.rjturek.amp.govna.dataobj.ValidationResponseElement
import jersey.repackaged.com.google.common.collect.Lists

import java.util.logging.Logger
import org.apache.maven.artifact.versioning.DefaultArtifactVersion

/**
 * Created by ckell on 10/6/14.
 */
class ValidationUtility {

    static Logger logger = Logger.getLogger("sharedLogger")
    DependencyDao dao = new DependencyDao()

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

        if (version.compareTo(minVersion) >= 0 && version.compareTo(maxVersion) <= 0) {
            logger.info("Version: ${dependencyVersion} falls between the version range: ${versionLow} - ${versionHigh} ")
            inBoundary = true
        }

        return inBoundary
    }


    def ValidationResponseElement buildValidationResponseElement( String consumerGroupName, String dependencyArtifactId, String dependencyVersion, GroupRestrictions groupRestrictions) {
        logger.fine( "ValidationUtility.buildValidationResponseElement()" )
        logger.info( "consumer group name: ${consumerGroupName}" )

        ValidationResponseElement validationResponseElement = new ValidationResponseElement()

        groupRestrictions.each{ gr ->
            logger.info( "Group Restriction Group Name: ${gr.groupName}" )

            int restrictionCount = 0

            gr.restrictions.each { r ->
                restrictionCount++
                logger.info("=============================================================")
                logger.info("Restriction:     ${restrictionCount} of ${gr.restrictions.size()}")
                logger.info("type:            ${r.type}")
                logger.info("artifactId:      ${r.artifactId}")
                logger.info("versionLow:      ${r.versionLow}")
                logger.info("versionHist:     ${r.versionHigh}")
                logger.info("message:         ${r.message}")
                logger.info("exemptConsumers: ${r.exemptConsumers}")
                logger.info("=============================================================")

                /* it has been said, if the low version is null, it is 0, and if the high version is null, it is java MAX_VALUE CONSTANT - RJT 10/22/14 */
                if (r.versionLow == null){
                    logger.fine ( "low version is null. setting it to 0.0.0")
                    r.versionLow = "0.0.0"
                }

                if (r.versionHigh == null){
                    logger.fine("high version is null. setting it to Integer.MAX_VALUE")
                    r.versionHigh = Integer.MAX_VALUE + "." + Integer.MAX_VALUE + "." + Integer.MAX_VALUE
                }

                /*
                 *  built the validationResponseElement
                 */
                if (r.artifactId != null && dependencyArtifactId == r.artifactId){
                    logger.info("The consumer dependency artifactId matches the artifactId within the restriction........")

                    /* are we an exempt Consumer? */
                    if ( (r.exemptConsumers != null) && (consumerGroupName in r.exemptConsumers) ) {
                        logger.info("The consumer is found to be exempt......")

                        /* are we exempt for this version? */
                        if ( r.versionLow != null && r.versionHigh != null ){

                            boolean inBoundary = checkVersionBoundaries(dependencyVersion, r.versionLow, r.versionHigh )
                            if (inBoundary) {
                                logger.info("Dependency Version found within boundary.")

                            } else {
                                logger.info("Dependency Version found outside of boundary.")
                                validationResponseElement.type = r.type
                                validationResponseElement.dependency = "${gr.groupName}:${r.artifactId}:${dependencyVersion}"
                                validationResponseElement.message = r.message

                                return validationResponseElement
                            }
                        }
                    }else {
                        logger.info("The consumer is NOT found to be exempt......")
                    }
                }else{
                    logger.info("The consumer dependency artifactId DID NOT match the artifactId within the restriction........")

                }
            }
        }

        return validationResponseElement
    }

    /**
     *
     */
    def List<ValidationResponseElement> analyzeDependenciesForRestrictions( ValidationRequest request ){
        logger.fine( "ValidationUtility.analyzeDependenciesForRestrictions()" )

        /* a poor mans cache of all the restrictions */
        Map<String, GroupRestrictions> groupRestrictionsMap = dao.getAllGroupRestrictionsMap()

        /* generate a List holder for what we will return. */
        List<ValidationResponseElement> validationResponseElementList = Lists.newArrayList()

        /* loop through the consumers dependencies and see if any of them have a top level group name restriction
         * TODO: move to separate method.
         */
        request.dependencyCoordinates.each{ gav->
            logger.info( "Inspecting dependency Group Artifact and Version: ${gav}" )

            def ( dependencyGroupName, dependencyArtifactId, dependencyVersion ) = gav.split( ":" )[0..2]

            /* check the cache Map to see if the dependency group name is a key: meaning there are restrictions applied to the group name.
             * TODO: Look to implement the Groovy Truth here? meaning groupRestrictionsMap.getKey(dependencyGroupName) instead of contains key.
             * */
            if( groupRestrictionsMap.containsKey( dependencyGroupName ) ){
                logger.info( "Found a consumer dependency that contains restrictions: ${dependencyGroupName}" )
                logger.info( "Inspecting restriction.................." )

                GroupRestrictions groupRestrictions = groupRestrictionsMap[dependencyGroupName]

                ValidationResponseElement validationResponseElement = buildValidationResponseElement(request.consumerGroup, dependencyArtifactId, dependencyVersion ,groupRestrictions )

                validationResponseElementList.add(validationResponseElement)

            }else{
                logger.info( "No Group Restrictions found for: ${dependencyGroupName} ")
                /* I am thinking there isn't a need to add an empty ValidationResponseElement to the list */
            }
        }

        return validationResponseElementList
    }

    /**
     * Entry point to compare the ValidationRequest against the Restrictions for the dependencies of the consumerGroup.
     *
     * @param ValidationRequest jsonRequest
     * @return ValidationResponse validationResponse
     */
    def ValidationResponse checkConsumerGroupRestrictions( ValidationRequest jsonRequest ){
        logger.fine( "ValidationUtility.checkConsumerGroupRestrictions()" )

        /* looking for a list of ValidationResponseElement(s).  One foreach consumer dependency GAV */
        List<ValidationResponseElement> validationResponseElementsList = analyzeDependenciesForRestrictions( jsonRequest )

        /* loop over the list and see if we have anything interesting to return to the caller */
        ValidationResponse validationResponse = new ValidationResponse()
        validationResponseElementsList.each{ vre ->
            if (vre.type != null && vre.type == Restriction.TYPE_PROHIBITED){
                logger.info("Found a Prohibited type validation response element..  Setting Build to fail. ")
                validationResponse.failBuild = true
            }
        }

        validationResponse.validationResponseElements = validationResponseElementsList

        return validationResponse

    }
}