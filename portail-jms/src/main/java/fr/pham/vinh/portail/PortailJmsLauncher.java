package fr.pham.vinh.portail;

import fr.pham.vinh.portail.jms.PortailJms;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application permettant la communication du Portail QI sur un ESB.
 * Created by Vinh PHAM on 17/03/2017.
 */
public class PortailJmsLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortailJmsLauncher.class);

    private static final String TOPIC_PORTAIL_QI = "topic.portail.qi";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";

    public static void main(String args[]) throws Exception {

        ResourceConfig config = new ResourceConfig();
        config.packages("fr.pham.vinh.portail.resource");
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        Server server = new Server(8084);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        try (PortailJms portailJms = new PortailJms(TOPIC_PORTAIL_QI, USER, PASSWORD)) {
            portailJms.init();
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

}
