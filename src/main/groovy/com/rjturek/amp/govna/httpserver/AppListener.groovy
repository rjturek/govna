package com.rjturek.amp.govna.httpserver

import com.rjturek.amp.govna.utility.CustomLogFormatter

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger;

public class AppListener implements ServletContextListener
{
    // The logger named "sharedLogger will have a ConsoleHandler added to it.
    // So for now, for simplicity, every class should getLogger("sharedLogger")
    static Logger logger = Logger.getLogger("sharedLogger")
    static {
        logger.setLevel(Level.ALL)
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler()
        consoleHandler.setLevel(Level.ALL)
        consoleHandler.setFormatter(new CustomLogFormatter())
        logger.addHandler(consoleHandler)
    }

    public AppListener() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)
    {
        // Dump the classloader
        ClassLoader cl = AppListener.class.getClassLoader();
        ClassLoader pcl = cl.getParent();

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("AppListener parent classloader: " + pcl.toString());

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("AppListener classloader: " + cl.toString());

        logger.fine(".......... Checking the Logger")
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)
    {
    }
}
