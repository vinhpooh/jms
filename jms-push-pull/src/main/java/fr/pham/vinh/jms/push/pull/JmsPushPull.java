package fr.pham.vinh.jms.push.pull;

import fr.pham.vinh.jms.commons.JMSType;
import fr.pham.vinh.jms.commons.builder.SelectorBuilder;
import fr.pham.vinh.jms.commons.builder.TextMessageBuilder;
import fr.pham.vinh.jms.commons.consumer.Consumer;
import fr.pham.vinh.jms.commons.producer.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.UUID;

/**
 * Push a request and pull the response on Java Message Service.
 * Created by Vinh PHAM on 09/03/2017.
 */
public class JmsPushPull {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsPushPull.class);

    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final String DESTINATION_NAME = "ci_portail_qi";
    private static final String USER_NAME = "java.naming.security.principal";
    private static final String PASSWORD_NAME = "java.naming.security.credentials";

    private static final Boolean NON_TRANSACTED = false;
    private static final int TIMEOUT = 10 * 1000;

    /**
     * Send a message and wait the response.
     *
     * @param request the request to send
     * @return the response to the request
     */
    public String start(String request) {
        String response = null;
        Connection connection = null;

        try {
            // JNDI lookup of JMS connection factory and JMS destination
            Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            // Create a connection
            String user = (String) context.getEnvironment().get(USER_NAME);
            String password = (String) context.getEnvironment().get(PASSWORD_NAME);
            connection = connectionFactory.createConnection(user, password);
            connection.start();

            // Create a session
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);

            // Create message
            String correlationId = UUID.randomUUID().toString();
            TextMessage message = new TextMessageBuilder(session.createTextMessage())
                    .setJMSCorrelationID(correlationId)
                    .setJMSReplyTo(destination)
                    .setJMSType(JMSType.REQUEST.name())
                    .setRequest(request)
                    .build();

            // Send message
            LOGGER.debug("send message");
            new Publisher(session).send(destination, message).close();

            // Create selector
            String selector = new SelectorBuilder()
                    .jmsCorrelationID(correlationId)
                    .and().jmsType(JMSType.RESPONSE.name())
                    .build();

            // Consume message
            LOGGER.debug("wait message with selector : {}", selector);
            response = new Consumer(session, destination).consume(selector, TIMEOUT);

            // Clean up
            session.close();
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

        return response;
    }

}
