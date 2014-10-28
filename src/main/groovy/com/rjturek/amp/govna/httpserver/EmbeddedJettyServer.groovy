package com.rjturek.amp.govna.httpserver

import java.security.ProtectionDomain
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.servlet.ServletContextHandler

public final class EmbeddedJettyServer{
    public static void main(String[] args) throws Exception
    {
        int port = Integer.parseInt(System.getProperty("port", "8080"));
        Server server = new Server(port);

        ProtectionDomain domain = EmbeddedJettyServer.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation()

        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS)
        servletContext.setContextPath("/api")

        // Web App Context using web.xml
        WebAppContext webAppContext = new WebAppContext()
        webAppContext.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml")
        webAppContext.setResourceBase("/")
        webAppContext.setContextPath("/")
        webAppContext.setParentLoaderPriority(true)
        webAppContext.setServer(server);
        webAppContext.setWar(location.toExternalForm());

        // (Optional) Set the directory the war will extract to.
        // If not set, java.io.tmpdir will be used, which can cause problems
        // if the temp directory gets cleaned periodically.
        // Your build scripts should remove this directory between deployments
        webAppContext.setTempDirectory(new File("/var/tmp/webapp"));

        server.setHandler(webAppContext);
        server.start();
        server.join();
    }
}
