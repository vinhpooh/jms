package fr.pham.vinh.jms.pull.push;

import fr.pham.vinh.jms.commons.JMSType;
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
     * Wait messages and send reponses.
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

            // Clean up
            //session.close();
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
            TextMessage responseMessage = session.createTextMessage();

            if (message instanceof TextMessage) {
                String request = ((TextMessage) message).getText();
                String response = processRequest(request);

                responseMessage.setText(response);
            } else {
                LOGGER.error("Unsupported message type");
                throw new RuntimeException("Unsupported message type");
            }

            // Set the correlation ID from the received message to be the correlation id of the response message
            // this lets the client identify which message this is a response to if it has more than
            // one outstanding message to the server
            responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());

            // Set the jms type
            responseMessage.setJMSType(JMSType.RESPONSE.name());

            // Send the response to the Destination specified by the JMSReplyTo field of the received message,
            // this is presumably a temporary queue created by the client
            this.publisher.send(message.getJMSReplyTo(), responseMessage);
        } catch (JMSException e) {
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
