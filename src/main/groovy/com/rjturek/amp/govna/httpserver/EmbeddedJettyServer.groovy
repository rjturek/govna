package com.rjturek.amp.govna.httpserver

import org.eclipse.jetty.webapp.JettyWebXmlConfiguration

import java.security.ProtectionDomain
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.webapp.WebXmlConfiguration
import org.eclipse.jetty.webapp.WebInfConfiguration
import org.eclipse.jetty.webapp.MetaInfConfiguration
import org.eclipse.jetty.plus.webapp.EnvConfiguration
import org.eclipse.jetty.webapp.FragmentConfiguration
import org.eclipse.jetty.plus.webapp.PlusConfiguration
import org.eclipse.jetty.annotations.AnnotationConfiguration


public final class EmbeddedJettyServer{
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("port", "8080"))
        Server server = new Server(port)

        ProtectionDomain domain = EmbeddedJettyServer.class.getProtectionDomain()
        URL location = domain.getCodeSource().getLocation()

        // Web App Context using web.xml
        WebAppContext webAppContext = new WebAppContext()
        webAppContext.setDescriptor("WEB-INF/web.xml")
        def configurations = [
                AnnotationConfiguration,
                WebInfConfiguration,
                WebXmlConfiguration,
                MetaInfConfiguration,
                FragmentConfiguration,
                EnvConfiguration,
                PlusConfiguration,
                JettyWebXmlConfiguration]*.newInstance()
        webAppContext.configurations = configurations

        // Important! make sure Jetty scans all classes under ./classes looking for annotations.
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/classes/.*");

        webAppContext.setContextPath("/")
        webAppContext.setWar(location.toExternalForm())

        // (Optional) Set the directory the war will extract to.
        // If not set, java.io.tmpdir will be used, which can cause problems
        // if the temp directory gets cleaned periodically.
        // Your build scripts should remove this directory between deployments
        //webAppContext.setTempDirectory(new File("/var/tmp/webapp"));

        server.setHandler(webAppContext)
        server.start()
        server.join()
    }
}
