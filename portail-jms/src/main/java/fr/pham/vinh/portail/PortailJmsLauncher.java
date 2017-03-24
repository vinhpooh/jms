package fr.pham.vinh.portail;

import fr.pham.vinh.portail.jms.PortailJms;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application permettant la communication du Portail QI sur un ESB.
 * Created by Vinh PHAM on 17/03/2017.
 */
public class PortailJmsLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortailJmsLauncher.class);

    private final static String[] SCAN_PACKAGES = new String[]{"fr.pham.vinh.portail.resource"};
    private final static String DEFAULT_CONTEXT_PATH = "/portail-jms/*";
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_TOPIC = "topic.portail.qi";
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    private static String contextPath;
    private static int port;
    private static String topic;
    private static String user;
    private static String password;

    static {
        Properties properties = new Properties();
        try (InputStream in = PortailJmsLauncher.class.getClassLoader().getResourceAsStream("properties.properties")) {
            properties.load(in);
            contextPath = properties.getProperty("context.path", DEFAULT_CONTEXT_PATH);
            port = Integer.valueOf(properties.getProperty("port", DEFAULT_PORT));
            topic = properties.getProperty("topic", DEFAULT_TOPIC);
            user = properties.getProperty("user", DEFAULT_USER);
            password = properties.getProperty("password", DEFAULT_PASSWORD);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            LOGGER.warn("Cannot parse port \"{}\", using default port \"{}\".", properties.getProperty("port"), DEFAULT_PORT);
            port = Integer.valueOf(DEFAULT_PORT);
        }
    }

    public static void main(String args[]) throws Exception {
        // ResourceConfig (Jersey) inherit from Application and gives a new configuration without custom properties.
        // The next line takes a vararg of packages as arguments to search for components / resources.
        ResourceConfig config = new ResourceConfig();
        config.packages(SCAN_PACKAGES);

        // The ServletContainer (Jersey) is a Servlet / Filter for deploying root resource classes.
        ServletContainer servletContainer = new ServletContainer(config);

        // ServletHolder (Jetty) is a Servlet instance and context holder.
        // It holds name, parameters and some states of javax.servlet.Servlet instance.
        ServletHolder servlet = new ServletHolder(servletContainer);

        // Server (Jetty) is the main class for the Jetty HTTP Servlet server. It aggregates connectors and requests.
        // The server itself is a handler and a ThreadPool.
        Server server = new Server(port);

        // ServletContextHandler (Jetty) is a specialization of ContextHandler with support for standard servlets
        ServletContextHandler context = new ServletContextHandler(server, contextPath);
        context.addServlet(servlet, "/*");

        try (PortailJms portailJms = new PortailJms(topic, user, password)) {
            // Initialize publiser and subscriber
            portailJms.init();
            // Start things up!
            server.start();
            // By using the server.join() the server thread will join with the current thread.
            server.join();
        } finally {
            server.destroy();
        }
    }

}
