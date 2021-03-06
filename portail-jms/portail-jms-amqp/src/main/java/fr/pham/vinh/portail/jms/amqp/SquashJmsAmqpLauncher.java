package fr.pham.vinh.portail.jms.amqp;

import com.google.gson.Gson;
import fr.pham.vinh.jms.commons.JmsPull;
import fr.pham.vinh.jms.commons.dto.SquashRequest;
import fr.pham.vinh.jms.commons.dto.SquashResponse;
import fr.pham.vinh.jms.commons.enumeration.StatusCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application de test permettant de simuler la commuication avec Squash sur un ESB.
 * Created by Vinh PHAM on 13/03/2017.
 */
public class SquashJmsAmqpLauncher extends JmsPull {

    private static final Logger LOGGER = LoggerFactory.getLogger(SquashJmsAmqpLauncher.class);

    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final String DEFAULT_TOPIC = "topic.squash";

    /**
     * Default constructor.
     *
     * @param topic    the topic tu use
     * @param user     the user to use
     * @param password the password to use
     */
    private SquashJmsAmqpLauncher(String topic, String user, String password) {
        super(topic, user, password);
    }

    @Override
    protected ConnectionFactory getConnectionFactory() {
        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            return (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
        } catch (NamingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String processRequest(String request) {
        LOGGER.debug("request {}", request);

        Gson gson = new Gson();
        SquashRequest squashRequest = gson.fromJson(request, SquashRequest.class);

        SquashResponse squashResponse = new SquashResponse();
        squashResponse.setStatus(StatusCodeEnum.OK.name());
        squashResponse.setComment("un commentaire");

        return gson.toJson(squashResponse);
    }

    private static String topic;
    private static String user;
    private static String password;

    static {
        Properties properties = new Properties();
        try (InputStream in = PortailJmsAmqpLauncher.class.getClassLoader().getResourceAsStream("properties.properties")) {
            properties.load(in);
            topic = DEFAULT_TOPIC;
            user = properties.getProperty("user");
            password = properties.getProperty("password");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]) {
        try (SquashJmsAmqpLauncher squash = new SquashJmsAmqpLauncher(topic, user, password)) {
            // Initialize publiser and subscriber
            squash.init();

            Thread.sleep(120 * 60 * 1000);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
