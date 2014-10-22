package com.rjturek.amp.govna.dataobj

import com.rjturek.amp.govna.db.DependencyDao
import jersey.repackaged.com.google.common.collect.Lists

import java.util.logging.Logger

/**
 * Created by ckell on 10/15/14.
 */
class DataObjectTester {
    static Logger logger = Logger.getLogger( "DataObjectTester" )

    DependencyDao dependencyDao = new DependencyDao()

    /**
     * Test to verify we can call the dao for getGroupRestrictionList() and get valid results.
     *
     * @return
     */
    private boolean testDaoGetGroupRestrictionsList() {
        logger.info( "DataObjectTester.testDaoGetGroupRestrictionsList()" )

        List<GroupRestrictions> groupRestrictionsList = dependencyDao.getGroupRestrictionsList()
        boolean status

        if (groupRestrictionsList) {
            groupRestrictionsList.each { group ->
                logger.info( "Found Group Name: ${group.groupName}" )
            }
            status = true
        }else {
            logger.warning( "No Group Restrictions Found." )
            status = false
        }

        return status
    }

    private boolean testDaoGetAllGroupRestrictionsMap(){
        logger.info( "DataObjectTester.testDaoGetGroupRestrictionsMap()" )

        Map<String, GroupRestrictions> groupRestrictionsMap = dependencyDao.getAllGroupRestrictionsMap()
        boolean status

        if( groupRestrictionsMap ) {
            groupRestrictionsMap.each { group ->
                logger.info( "Found Group Restriction for: ${group.key}" )
            }
            status = true
        } else {
            logger.warning("No Group Restrictions found in the Map")
            status = false
        }
    }


    static void main(String[] args) {
        logger.info("Starting Tests.......")

        DataObjectTester dataObjectTester = new DataObjectTester()

        boolean listStatus = dataObjectTester.testDaoGetGroupRestrictionsList()
        logger.info("test getGroupRestrictionList results: ${listStatus}")

        boolean mapStatus = dataObjectTester.testDaoGetAllGroupRestrictionsMap()
        logger.info("test getAllGroupRestrictionsMap results: ${mapStatus}")


        logger.info( "Complete Tests........." )
        System.exit(0)

    }
}
