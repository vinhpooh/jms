package fr.pham.vinh.portail.jms.commons.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

/**
 * The embedded server to serve welcome page and REST API.
 * Created by Vinh PHAM on 29/03/2017.
 */
public class EmbeddedServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedServer.class);

    private final static String[] SCAN_PACKAGES = new String[]{EmbeddedServer.class.getPackage().getName() + ".resource"};

    private Server server;
    private int port;

    /**
     * Default constructor.
     *
     * @param port the server's port
     */
    public EmbeddedServer(int port) {
        this.port = port;
        this.init();
    }

    /**
     * Initialize the embedded server.
     */
    private void init() {
        // Server (Jetty) is the main class for the Jetty HTTP Servlet server. It aggregates connectors and requests.
        // The server itself is a handler and a ThreadPool.
        this.server = new Server(port);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.setResourceBase(EmbeddedServer.class.getResource("/webroot").toExternalForm());
        context.setWelcomeFiles(new String[]{"index.html"});

        // Add Jersey servlet at "/portail-jms/*"
        ServletHolder portailJmsHolder = new ServletHolder("portail-jms", createJerseyServlet());
        context.addServlet(portailJmsHolder, "/portail-jms/*");

        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // Must be named "default", must be on path mapping "/" and it is important that this is last.
        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed", "false");
        context.addServlet(holderPwd, "/");
    }

    /**
     * Get the Jersey servlet container.
     *
     * @return the jersey servlet container
     */
    private Servlet createJerseyServlet() {
        // ResourceConfig (Jersey) inherit from Application and gives a new configuration without custom properties.
        // The next line takes a vararg of packages as arguments to search for components / resources.
        ResourceConfig config = new ResourceConfig();
        config.packages(SCAN_PACKAGES);

        // The ServletContainer (Jersey) is a Servlet / Filter for deploying root resource classes.
        return new ServletContainer(config);
    }

    /**
     * Start the embedded server.
     *
     * @throws Exception exception
     */
    public void start() throws Exception {
        try {
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
