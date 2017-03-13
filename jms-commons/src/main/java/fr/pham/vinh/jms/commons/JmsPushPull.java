package fr.pham.vinh.jms.commons;

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
public abstract class JmsPushPull {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsPushPull.class);

    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final String DEFAULT_TOPIC_NAME = "ci_portail_qi";

    private ConnectionFactory connectionFactory;
    private Destination defaultTopic;

    private String topic;
    private int timeout;
    private String user;
    private String password;

    private static final Boolean NON_TRANSACTED = false;

    /**
     * Constructor with specific topie to use.
     *
     * @param topic    the topic to use
     * @param timeout  the timeout in ms to use
     * @param user     the user to use
     * @param password the password to use
     */
    public JmsPushPull(String topic, int timeout, String user, String password) {
        try {
            // JNDI lookup of JMS connection factory and JMS destination
            Context context = new InitialContext();
            this.connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            this.defaultTopic = (Destination) context.lookup(DEFAULT_TOPIC_NAME);
        } catch (NamingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        this.topic = topic;
        this.timeout = timeout;
        this.user = user;
        this.password = password;
    }

    /**
     * Default constructor.
     *
     * @param timeout  the timeout in ms to use
     * @param user     the user to use
     * @param password the password to use
     */
    public JmsPushPull(int timeout, String user, String password) {
        this(null, timeout, user, password);
    }

    /**
     * Send a message and wait the response.
     *
     * @param request the request to send
     * @return the response to the request
     */
    public String run(String request) {
        Connection connection = null;

        try {
            // Create a connection
            connection = connectionFactory.createConnection(user, password);
            connection.start();

            // Create a session and destination
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Destination destination = topic != null ? session.createTopic(topic) : defaultTopic;

            // Create message
            String correlationId = UUID.randomUUID().toString();
            TextMessage message = new TextMessageBuilder(session.createTextMessage())
                    .setJMSCorrelationID(correlationId)
                    .setJMSReplyTo(destination)
                    .setJMSType(JMSType.REQUEST.name())
                    .setText(request)
                    .build();

            // Send message
            LOGGER.debug("Send message : {}", message);
            try (Publisher publisher = new Publisher(session)) {
                publisher.send(destination, message);
            }

            // Create selector
            String selector = new SelectorBuilder()
                    .jmsCorrelationID(correlationId)
                    .and().jmsType(JMSType.RESPONSE.name())
                    .build();

            // Consume message
            LOGGER.debug("Wait message with selector : {}", selector);
            String response = new Consumer(session, destination).consume(selector, timeout);

            // Clean up
            session.close();

            return response;
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
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
