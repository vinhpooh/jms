package fr.pham.vinh.jms.commons;

import fr.pham.vinh.jms.commons.builder.TextMessageBuilder;
import fr.pham.vinh.jms.commons.consumer.Subscriber;
import fr.pham.vinh.jms.commons.producer.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Pull a request and push the response on Java Message Service.
 * Created by Vinh PHAM on 09/03/2017.
 */
public class JmsPullPush implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsPullPush.class);

    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final String DESTINATION_NAME = "ci_portail_qi";
    private static final String USER_NAME = "java.naming.security.principal";
    private static final String PASSWORD_NAME = "java.naming.security.credentials";

    private static final Boolean NON_TRANSACTED = false;

    private Session session;
    private Publisher publisher;

    /**
     * Initialize a subscriber and a publisher.
     */
    public void start() {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            // Create a Connection
            String user = (String) context.getEnvironment().get(USER_NAME);
            String password = (String) context.getEnvironment().get(PASSWORD_NAME);
            connection = connectionFactory.createConnection(user, password);
            connection.start();

            // Create a Session
            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);

            // Create the publisher
            LOGGER.debug("create the publisher");
            publisher = new Publisher(session);

            // Create the subscriber
            LOGGER.debug("create the subscriber");
            Subscriber subscriber = new Subscriber(session, destination);
            subscriber.setMessageListener(this);

            // TODO : ajouter une boucle d'attente

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
    }

    @Override
    public void onMessage(Message message) {
        try {
            // Sleep 1 seconde before responding
            // If the client publisher send the request and the response is sent before the client consumer get initialized,
            // the response is lost (topic mode)
            Thread.sleep(1000);

            TextMessage responseMessage;
            LOGGER.debug("Request message  : {} ", message);

            if (message instanceof TextMessage) {
                String request = ((TextMessage) message).getText();
                String response = processRequest(request);

                responseMessage = new TextMessageBuilder(session.createTextMessage())
                        // Set the correlation ID from the received message to be the correlation id of the response message
                        // this lets the client identify which message this is a response to if it has more than
                        // one outstanding message to the server
                        .setJMSCorrelationID(message.getJMSCorrelationID())
                        // Set the jms type
                        .setJMSType(JMSType.RESPONSE.name())
                        // Set the text
                        .setText(response)
                        .build();

                // Send the response to the Destination specified by the JMSReplyTo field of the received message,
                // this is presumably a temporary queue created by the client
                publisher.send(message.getJMSReplyTo(), responseMessage);
            } else {
                LOGGER.error("Unsupported message type");
                throw new RuntimeException("Unsupported message type");
            }

            LOGGER.debug("Response message : {} ", responseMessage);
        } catch (JMSException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Process the request.
     *
     * @param request the request to process
     * @return the response of the request
     */
    private String processRequest(String request) {
        // TODO : répondre à la requête
        return "{" +
                "type:\"response\"," +
                "request:\"" + request + "\"," +
                "status:\"OK\"" +
                "}";
    }

}
