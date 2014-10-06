// see http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/

package com.rjturek.amp.govna.db
import com.gmongo.GMongo

import java.util.logging.Level
import java.util.logging.Logger

class DependencyDao {

    Logger logger = Logger.getLogger("sharedLogger")

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
        assert (! count == 1) {
            return count
        }
    }

    def Object insertGroupRestrictions(group) {
        log("insertGroupRestrictionsExist")
//        rstrColl.insert(group)
    }

    def Object updateGroupRestrictions(group)  {
        log("insertGroupRestrictionsExist")
        //rstrColl.update(group)
    }
}
