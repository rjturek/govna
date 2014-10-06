//package com.rjturek.amp.govna.jettyserver
//
//import com.fasterxml.jackson.core.JsonGenerator
//import com.rjturek.amp.govna.filter.CorsResponseFilter
//import com.rjturek.amp.govna.service.RestrictionService
//import org.eclipse.jetty.server.Handler
//import org.eclipse.jetty.server.Server
//import org.eclipse.jetty.server.handler.ContextHandlerCollection;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder
//import org.eclipse.jetty.webapp.WebAppContext;
//import org.glassfish.jersey.jackson.JacksonFeature;
//import org.glassfish.jersey.server.ResourceConfig;
//import org.glassfish.jersey.servlet.ServletContainer;
//
//class GovnaJettyServer {
//
//    private static final int DEFAULT_PORT = 8080;
//
//    private int serverPort;
//
//    public GovnaJettyServer(int serverPort) throws Exception {
//        this.serverPort = serverPort;
//
//        Server server = configureServer();
//        server.start();
//        server.join();
//    }
//
//    private Server configureServer() {
//
//        ResourceConfig resourceConfig = new ResourceConfig()
//
//        // Tell where components to be scanned live.  DependencyGroup class will yield the package containing all the
//        // service classes
//        resourceConfig.packages(RestrictionService.class.getPackage().getName())
////        resourceConfig.packages(RestrictionService.class.getPackage().getName(), "com.fasterxml.jackson.jaxrs.base")
//
//        // Register components
//        resourceConfig.register(CorsResponseFilter.class) // Response Filter to set cross-origin header
//        resourceConfig.register(JacksonFeature.class)  // For JSON rendering, but posts say only for older Jackson
//
//        //resourceConfig.property(JsonGenerator.PRETTY_PRINTING, true);
//        resourceConfig.property("com.sun.jersey.api.json.POJOMappingFeature", "true");
//
//        ServletContainer servletContainer = new ServletContainer(resourceConfig)
//
//        ServletHolder sh = new ServletHolder(servletContainer)
//
//        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS)
//        servletContext.setContextPath("/api")  // All services are under /api
//        servletContext.addServlet(sh, "/*")
//
//        // Web App Context using web.xml
//        WebAppContext webAppContext = new WebAppContext()
//        webAppContext.setDescriptor("src/main/webapp/WEB-INF/web.xml")
//        webAppContext.setResourceBase("src/main/webapp")
//        webAppContext.setContextPath("/")
//        webAppContext.setParentLoaderPriority(true)
//
//        Server server = new Server(serverPort)
//
//        ContextHandlerCollection contexts = new ContextHandlerCollection()
//        def handlers = [servletContext, webAppContext] as Handler[]
//        contexts.setHandlers(handlers)
//
//        server.setHandler(contexts);
//
//        return server;
//    }
//
//    public static void main(String[] args) throws Exception {
//
//        printClassPath GovnaJettyServer.classLoader
//
//        int serverPort = DEFAULT_PORT
//
//        if(args.length >= 1) {
//            try {
//                serverPort = Integer.parseInt(args[0])
//            } catch (NumberFormatException e) {
//                e.printStackTrace()
//            }
//        }
//
//        new GovnaJettyServer(serverPort);
//    }
//
//    static def printClassPath(classLoader) {
//        println "CLASSLOADER: $classLoader"
//        classLoader.getURLs().each {url->
//            println "- ${url.toString()}"
//        }
//        if (classLoader.parent) {
//            printClassPath(classLoader.parent)
//        }
//    }
//}