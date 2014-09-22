package com.rjturek.amp.govna.jettyserver

import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent

class AppListener implements ServletContextListener{

    void contextInitialized(ServletContextEvent sce) {
        println "Initialized ${sce.dump()}"
        println AppListener.classLoader.dump()
    }

    void contextDestroyed(ServletContextEvent sce) {
        println "destroyed ${sce.dump()}"
    }
}
