// see http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/

package com.rjturek.amp.govna.db
import com.gmongo.GMongo
import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.utility.GenericStructUtil
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import java.util.logging.Level
import java.util.logging.Logger

class DependencyDao {

    static Logger logger = Logger.getLogger("sharedLogger")

    def log(msg) {
        logger.log(Level.FINE, msg)
    }

    GMongo mongo 
    def db
    def rstrColl

    // ctor
    def DependencyDao() {
        mongo = new GMongo("localhost", 27017)
        db = mongo.getDB("govna")
        rstrColl = db.getCollection("restrictions")
    }

    def Object getGroupRestrictionsList() {
        log("getGroupRestrictionsList")
        def groups = rstrColl.find()
        List groupList = []
        groups.each {
            groupList.add(it)
        }
        groupList
    }

    def Object getAllGroupRestrictionsMap(){
        log( "getAllGroupRestrictionsMap" )

        def groups = getGroupRestrictionsList()

        List<GroupRestrictions> groupRestrictions = GenericStructUtil.convertGroups(groups)

        Map groupMap = [:]

        groupRestrictions.each{
            if (! groupMap.put(it.groupName, it )){
                throw new Exception("GroupName exists in Map.  More than 1 groupName returned from DB.")
            }
        }

        groupMap.each{
            logger.info( " >>>> Key: ${it.key}" )
            logger.info( " >>>> Value: ${it.value}" )
        }
        groupMap

    }

    def Object getGroupRestrictions(groupName) {
        log("getGroupRestrictions")
        def groupCursor = rstrColl.find(groupName: groupName)
        if (!groupCursor.hasNext()) {
            return null
        }
        def group = groupCursor.next()

        // group is the unique key, there should never be more than one found.
        assert !groupCursor.hasNext()

        return group
    }

    boolean groupRestrictionsExist(groupName) {
        log("groupRestrictionsExist")
        int count = rstrColl.count(groupName: groupName)
        log("the count is: $count")
        if (count > 1) {
            throw new Exception(
                 "Invariant violated: groupName found in the restrictions collection more than once. groupName: $groupName")
        }
        return count
    }

// Probably not needed due to upsert capability - below
//    def Object insertGroupRestrictions(group) {
//        log("insertGroupRestrictions")
//        JsonBuilder jsonBuilder = new JsonBuilder(group)
//        def theJson = jsonBuilder.toPrettyString()
//        log(theJson)
//        rstrColl.insert(new JsonSlurper().parseText(theJson))
//    }

    def Object upsertGroupRestrictions(GroupRestrictions group)  {
        log("updateGroupRestrictions")
        JsonBuilder jsonBuilder = new JsonBuilder(group)
        def theJson = jsonBuilder.toPrettyString()
        log(theJson)
        def genericStructure = new JsonSlurper().parseText(theJson)
        log(genericStructure.toString())
        rstrColl.update([groupName: group.groupName], [$set: new JsonSlurper().parseText(theJson)], true)
    }
}
