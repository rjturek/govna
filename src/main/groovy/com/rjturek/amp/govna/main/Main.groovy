
//@Grab(group='com.gmongo', module='gmongo', version='1.0')

package com.rjturek.amp.govna.main

import com.rjturek.amp.govna.db.MongoData

class Main {

    static void main(args) {
        println "Here we go now!"
        def dao = new MongoData()
        def mne = dao.getMne("nna") 
        println mne   
    }
}