package fr.pham.vinh.portail;

import fr.pham.vinh.portail.jms.PortailJms;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
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
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_TOPIC = "topic.portail.qi";
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    private static String topic;
    private static String user;
    private static String password;

    static {
        Properties properties = new Properties();
        try (InputStream in = PortailJmsLauncher.class.getClassLoader().getResourceAsStream("properties.properties")) {
            properties.load(in);
            topic = properties.getProperty("topic", DEFAULT_TOPIC);
            user = properties.getProperty("user", DEFAULT_USER);
            password = properties.getProperty("password", DEFAULT_PASSWORD);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]) throws Exception {
        // Get port from args
        int port = args.length != 0 && StringUtils.isNotEmpty(args[0]) ? Integer.valueOf(args[0]) : DEFAULT_PORT;

        // ResourceConfig (Jersey) inherit from Application and gives a new configuration without custom properties.
        // The next line takes a vararg of packages as arguments to search for components / resources.
        ResourceConfig config = new ResourceConfig();
        config.packages(SCAN_PACKAGES);

        // The ServletContainer (Jersey) is a Servlet / Filter for deploying root resource classes.
        ServletContainer servletContainer = new ServletContainer(config);

        // Server (Jetty) is the main class for the Jetty HTTP Servlet server. It aggregates connectors and requests.
        // The server itself is a handler and a ThreadPool.
        Server server = new Server(port);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.setResourceBase(PortailJmsLauncher.class.getResource("/webroot").toExternalForm());
        context.setWelcomeFiles(new String[]{"index.html"});

        // Add Jersey servlet at "/portail-jms/*"
        ServletHolder portailJmsHolder = new ServletHolder("portail-jms", servletContainer);
        context.addServlet(portailJmsHolder, "/portail-jms/*");

        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // Must be named "default", must be on path mapping "/" and it is important that this is last.
        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed", "false");
        context.addServlet(holderPwd, "/");

        try (PortailJms portailJms = new PortailJms(topic, user, password)) {
            // Initialize publiser and subscriber
            portailJms.init();
            // Start things up!
            server.start();
            LOGGER.info("Jetty server is reachable on port {}", port);
            // By using the server.join() the server thread will join with the current thread.
            server.join();
        } finally {
            server.destroy();
        }
    }

}
