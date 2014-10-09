package com.rjturek.amp.govna.jettyserver

import com.fasterxml.jackson.core.JsonGenerator
import com.rjturek.amp.govna.filter.CorsResponseFilter
import com.rjturek.amp.govna.service.RestrictionService
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger;

class GovnaJettyServer {

    // The logger named "sharedLogger will have a ConsoleHandler added to it.
    // So for now, for simplicity, every class should getLogger("sharedLogger")
    static Logger logger = Logger.getLogger("sharedLogger")
    static {
        logger.setLevel(Level.ALL)
        ConsoleHandler consoleHandler = new ConsoleHandler()
        consoleHandler.setLevel(Level.ALL)
        logger.addHandler(consoleHandler)
    }

    private int serverPort = 8080

    Server server = null

    public GovnaJettyServer() throws Exception {

        server = configureServer();
        server.start();
        server.join();
    }

    private killServer() {
        logger.log(Level.FINE, "shut down server")
        server.stop();
    }

    private Server configureServer() {

        ResourceConfig resourceConfig = new ResourceConfig()

        resourceConfig.packages("com.rjturek.amp.govna.service")

        // Register components
        resourceConfig.register(CorsResponseFilter.class) // Response Filter to set cross-origin header
        resourceConfig.register(JacksonFeature.class)  // For JSON rendering, but posts say only for older Jackson

        //resourceConfig.property(JsonGenerator.PRETTY_PRINTING, true);
        resourceConfig.property("com.sun.jersey.api.json.POJOMappingFeature", "true");

        ServletContainer servletContainer = new ServletContainer(resourceConfig)

        ServletHolder sh = new ServletHolder(servletContainer)

        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS)
        servletContext.setContextPath("/api")  // All services are under /api
        servletContext.addServlet(sh, "/*")

        // Web App Context using web.xml
        WebAppContext webAppContext = new WebAppContext()
        webAppContext.setDescriptor("src/main/webapp/WEB-INF/web.xml")
        webAppContext.setResourceBase("src/main/webapp")
        webAppContext.setContextPath("/")
        webAppContext.setParentLoaderPriority(true)

        Server server = new Server(serverPort)

        ContextHandlerCollection contexts = new ContextHandlerCollection()
        def handlers = [servletContext, webAppContext] as Handler[]
        contexts.setHandlers(handlers)

        server.setHandler(contexts);

        return server;
    }

    public static void main(String[] args) throws Exception {

 //       printClassPath GovnaJettyServer.classLoader

        GovnaJettyServer jettyServerClass = new GovnaJettyServer();
        System.in.read()
        logger.log(Level.FINE, "Stopping server")
        jettyServerClass.killServer();
    }

    // Just for debugging
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