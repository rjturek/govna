// see http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/

package com.rjturek.amp.govna.db

import com.gmongo.GMongo

class DependencyDao {

    GMongo mongo 
    def db

    // ctor
    def DependencyDao() {
        mongo = new GMongo("localhost", 27017)
        db = mongo.getDB("govna")
    }

    def Object getGroups() {
        def groups = db.groups.find()
        List groupList = []
        groups.each {
            groupList.add(it)
        }
        groupList
    }

    def Object getGroup(groupName) {
        def groupCursor = db.groups.find(_id: groupName)
        if (!groupCursor.hasNext()) {
            return null
        }
        def group = groupCursor.next()

        // group is the unique key, there should never be more than one found.
        assert !groupCursor.hasNext()

        return group
    }

    def Object insertGroup(group) {
        db.groups.insert(group)
    }
}
