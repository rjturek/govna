package com.rjturek.amp.govna.httpserver

import com.rjturek.amp.govna.service.RestrictionService
import com.rjturek.amp.govna.service.ValidationService
import org.glassfish.jersey.jackson.JacksonFeature

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig

import org.glassfish.grizzly.http.server.HttpServer

class GovnaGrizzlyServer {

    // The logger named "sharedLogger will have a ConsoleHandler added to it.
    // So for now, for simplicity, every class should getLogger("sharedLogger")
    static Logger logger = Logger.getLogger("sharedLogger")
    static {
        logger.setLevel(Level.ALL)
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
    }

    static def URI uri = URI.create("http://localhost:8080/api");

    static void main(String[] args) {

        println "Starting Grizzly......"
        printClassPath(GovnaGrizzlyServer.classLoader)

        def resourceConfig = new ResourceConfig(
             RestrictionService.class,
             ValidationService.class,
             JacksonFeature.class
        )

        resourceConfig.addProperties(["com.sun.jersey.api.json.POJOMappingFeature": "true"])

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)

        System.out.println(String.format("Application started.%nHit enter to stop it..."))

        logger.log(Level.INFO, '..... Every class should use the logger called "sharedLogger" for now .....')

        System.in.read()
        server.shutdownNow()
    }

    static def printClassPath(classLoader) {
        println "CLASSLOADER: $classLoader"
        classLoader.getURLs().each {url->
            println "- ${url.toString()}"
        }
        if (classLoader.parent) {
            printClassPath(classLoader.parent)
        }
    }
}


