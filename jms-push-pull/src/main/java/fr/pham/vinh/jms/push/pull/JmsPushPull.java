package fr.pham.vinh.jms.push.pull;

import fr.pham.vinh.jms.commons.Consumer;
import fr.pham.vinh.jms.commons.Producer;
import fr.pham.vinh.jms.commons.SelectorBuilder;
import fr.pham.vinh.jms.commons.TextMessageBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vinh PHAM on 09/03/2017.
 */
public class JmsPushPull {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsPushPull.class);

    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final List<String> TRUSTED_PACKAGES = Collections.singletonList("fr.pham.vinh.jms.commons.message");

    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";

    private static final Boolean NON_TRANSACTED = false;

    private static final String DESTINATION_NAME = "ci_portail_qi";
    private static final int TIMEOUT = 10 * 1000;

    public static void main(String args[]) {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            // TODO : adhérence avec activemq ?
            ((ActiveMQConnectionFactory) connectionFactory).setTrustedPackages(TRUSTED_PACKAGES);

            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            // Create a Connection
            connection = connectionFactory.createConnection(USER, PASSWORD);
            connection.start();

            // Create a Session
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);

            // Create message
            String request = "";
            String correlationId = UUID.randomUUID().toString();
            TextMessage message = new TextMessageBuilder()
                    .setJMSCorrelationID(correlationId)
                    .setRequest(request)
                    .build();

            // Send message
            LOGGER.debug("send message");
            new Producer(session, destination).send(message);

            // Create selector
            String selector = new SelectorBuilder()
                    .addJMSCorrelationID(correlationId)
                    .build();

            // Consume message
            LOGGER.debug("wait message with selector : {}", selector);
            String response = new Consumer(session, destination, selector).consume(TIMEOUT);
            System.out.println(response);

            // Clean up
            session.close();
            connection.close();
        } catch (NamingException | JMSException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            // Cleanup code
            // In general, you should always close producers, consumers, sessions, and connections in reverse order of creation.
            // For this simple example, a JMS connection.close will clean up all other resources.
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

}
