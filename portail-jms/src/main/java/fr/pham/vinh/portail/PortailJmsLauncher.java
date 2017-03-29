package fr.pham.vinh.portail;

import fr.pham.vinh.portail.jms.PortailJms;
import fr.pham.vinh.portail.server.EmbeddedServer;
import org.apache.commons.lang3.StringUtils;
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

        try (PortailJms portailJms = new PortailJms(topic, user, password)) {
            // Initialize publiser and subscriber
            portailJms.init();

            // Start the embedded server
            EmbeddedServer embeddedServer = new EmbeddedServer(port);
            embeddedServer.start();
        }
    }

}
