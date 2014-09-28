// see http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/

package com.rjturek.amp.govna.db

import com.gmongo.GMongo

class MongoData {

    GMongo mongo 
    def db

    // ctor
    def MongoData() {
        mongo = new GMongo("localhost", 27017)
        db = mongo.getDB("govna")
    }

    def Object getGroups() {
        def groups = db.groups.find()
        List groupArr = []
        groups.each {
            groupArr.add(it)
        }
        groupArr
    }

    def Object getGroup(groupId) {
        def groupCursor = db.groups.find(_id: groupId)
        if (!groupCursor.hasNext()) {
            return null
        }
        def group = groupCursor.next()

        // group is the unique key, there should never be more than one found.
        assert !groupCursor.hasNext()

        return group
    }
}
