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


    // Return null if no group restriction applies to the dependency.
    def ValidationResponseElement validateDependency( String consumerGroupName, String dependencyArtifactId,
                                                      String dependencyVersion, GroupRestrictions groupRestrictions) {
        logger.fine( "ValidationUtility.buildValidationResponseElement()" )
        logger.info( "consumer group name: ${consumerGroupName}" )

        int restrictionCount = 0

        ValidationResponseElement validationResponseElement = null

        // Group "Restrictions" come in 3 flavors:
        //      Deprecation - will not fail a build
        //      Prohibition - trumps a Deprecation.  Will fail the build
        //      Prohibition Exemption - trumps Deprecations and Prohibitions - (a.k.a. get out of jail free :-)
        // Loop over all Group Restrictions.
        // If we get to the end of the list without finding anything which applies,
        //    this method will return null.
        // If we find a Prohibition Exemption, we immediately return null.
        // Otherwise we'll return a Deprecation or Restriction validationResponseElement.
        for (r in groupRestrictions.restrictions) {
            restrictionCount++
            logger.info("=============================================================")
            logger.info("Restriction:     ${restrictionCount} of ${groupRestrictions.restrictions.size()}")
            logger.info("type:            ${r.type}")
            logger.info("artifactId:      ${r.artifactId}")
            logger.info("versionLow:      ${r.versionLow}")
            logger.info("versionHist:     ${r.versionHigh}")
            logger.info("message:         ${r.message}")
            logger.info("exemptConsumers: ${r.exemptConsumers}")
            logger.info("=============================================================")

            if (r.artifactId != null && r.artifactId.trim() != "") {
                if (r.artifactId != dependencyArtifactId) {
                    logger.info("artifactId is present null and does not match dependency - moving on")
                    continue  // move on to the next restriction, this one does not apply
                }
            }

            if ((r.versionLow != null && r.versionLow.trim() != "")
                    ||
                (r.versionHigh != null && r.versionHigh.trim() != "")) {
                // If the low version is null/empty set it to 0.
                // If the high version is null/empty set it to java MAX_VALUE CONSTANT - RJT 10/22/14
                if (r.versionLow == null || r.versionLow.trim() == "") {
                    logger.fine("low version is null/empty. setting it to 0.0.0")
                    r.versionLow = "0.0.0"
                }
                if (r.versionHigh == null || r.versionHigh.trim() == "") {
                    logger.fine("high version is null/empty. setting it to Integer.MAX_VALUE")
                    r.versionHigh = Integer.MAX_VALUE + "." + Integer.MAX_VALUE + "." + Integer.MAX_VALUE
                }
                if (!checkVersionBoundaries(dependencyVersion, r.versionLow, r.versionHigh)) {
                    logger.info("version ranges are not both null/empty, dependency is not in range - moving on")
                    continue // move on the the next restriction, this one does not apply
                }
            }

            // Still here?   This restriction element applies to the dependency.

            // If this is a Prohibition Exemption, the dependency is allowed.  Hallelujah!  Leave the method.
            if ((r.exemptConsumers != null) && (consumerGroupName in r.exemptConsumers)) {
                assert r.type == Restriction.TYPE_PROHIBITED
                logger.fine("Prohibition Exemption found - this dependency is OK")
                return null;
            }

            // If no prior validationResponseElement existed OR
            // if this restriction is a Prohibition and the prior restriction was a Deprecation
            // the validationResponseElement will be based on this one.
            if (validationResponseElement == null
                    ||
                (r.type == Restriction.TYPE_PROHIBITED &&
                 validationResponseElement.type == Restriction.TYPE_DEPRECATED))
            {
                validationResponseElement = new ValidationResponseElement()
                validationResponseElement.dependency = "$consumerGroupName:$dependencyArtifactId:$dependencyVersion"
                validationResponseElement.type = r.type
                validationResponseElement.message = r.message
            }
        } // end loop over GroupRestrictions restriction elements

        if (validationResponseElement == null) {
            logger.fine("Returning null validationResponseElement - this dependency is OK.")
        }
        else {
            logger.fine("Returning validationResponseElement type=$validationResponseElement.type")
        }

        return validationResponseElement  // will be null if no restrictions apply
    }

    def ValidationResponse analyzeDependenciesForRestrictions (
                                    ValidationRequest request, Map<String, GroupRestrictions> groupRestrictionsMap)
    {
        logger.fine("ValidationUtility.analyzeDependenciesForRestrictions(request, groupRestrictionsMap)")

        ValidationResponse validationResponse = new ValidationResponse()
        validationResponse.failBuild = false

        List<ValidationResponseElement> validationResponseElementList = Lists.newArrayList()  //Cool use of guava, bro.
        validationResponse.validationResponseElements = validationResponseElementList

        // Loop through the consumer's dependencies. If matching group restrictions exist, pass this to the validate algorithm.
        request.dependencyCoordinates.each{ gav->
            logger.info( "Inspecting dependency Group Artifact and Version: ${gav}" )

            def ( dependencyGroupName, dependencyArtifactId, dependencyVersion ) = gav.split( ":" )[0..2]

            // check the cache Map to see if the dependency group name is a key: meaning there are restrictions applied to the group name.
            if( groupRestrictionsMap.containsKey( dependencyGroupName ) ) {
                logger.info( "Found a consumer dependency that contains restrictions: ${dependencyGroupName}" )
                logger.info( "Inspecting restriction.................." )

                GroupRestrictions groupRestrictions = groupRestrictionsMap[dependencyGroupName]

                ValidationResponseElement validationResponseElement =
                            validateDependency(request.consumerGroup, dependencyArtifactId, dependencyVersion ,groupRestrictions )

                if (validationResponseElement != null) {
                    validationResponseElementList.add(validationResponseElement)
                    if (validationResponseElement.type == Restriction.TYPE_PROHIBITED) {
                        validationResponse.failBuild = true
                    }
                }

            }else{
                logger.info( "No Group Restrictions found for: ${dependencyGroupName} ")
            }
        }

        return validationResponse
    }

    /**
     * Entry point to compare the ValidationRequest against the Restrictions for the dependencies of the consumerGroup.
     * The groupRestrictionMap is passed in when doing a trial validation. When passed in null, the map will be constructed the database.
     *
     * @param ValidationRequest jsonRequest
     * @param Map<String, GroupRestrictions> groupRestrictionsMap
     * @return ValidationResponse validationResponse
     */
    def ValidationResponse checkConsumerGroupRestrictions(
                ValidationRequest validationRequest, Map<String, GroupRestrictions> groupRestrictionsMap ) {

        logger.fine( "ValidationUtility.checkConsumerGroupRestrictions()" )

        if (groupRestrictionsMap == null) {
            groupRestrictionsMap = dao.getAllGroupRestrictionsMap()
        }

        return analyzeDependenciesForRestrictions(validationRequest, groupRestrictionsMap )
    }
}