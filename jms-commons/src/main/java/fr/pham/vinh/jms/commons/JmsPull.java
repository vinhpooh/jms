package fr.pham.vinh.jms.commons;

import fr.pham.vinh.jms.commons.builder.SelectorBuilder;
import fr.pham.vinh.jms.commons.builder.TextMessageBuilder;
import fr.pham.vinh.jms.commons.enumeration.JMSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Closeable;

/**
 * Pull a request and push the response on Java Message Service.
 * Created by Vinh PHAM on 09/03/2017.
 */
public abstract class JmsPull implements Closeable, MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsPull.class);

    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final String DEFAULT_TOPIC_NAME = "default.topic";
    private static final Boolean NON_TRANSACTED = false;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination defaultTopic;
    private MessageProducer publisher;
    private MessageConsumer subscriber;

    private String topic;
    private String user;
    private String password;


    /**
     * Constructor with a specific topic to use.
     *
     * @param topic    the topic to use
     * @param user     the user to use
     * @param password the password to use
     */
    public JmsPull(String topic, String user, String password) {
        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            this.connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            this.defaultTopic = (Destination) context.lookup(DEFAULT_TOPIC_NAME);
        } catch (NamingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        this.topic = topic;
        this.user = user;
        this.password = password;
        this.init();
    }

    /**
     * Default constructor.
     *
     * @param user     the user to use
     * @param password the password to use
     */
    public JmsPull(String user, String password) {
        this(null, user, password);
    }

    /**
     * Initialize a subscriber and a publisher.
     */
    private void init() {
        try {
            // Create a Connection
            connection = connectionFactory.createConnection(user, password);
            connection.start();

            // Create a session and destination
            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Destination destination = topic != null ? session.createTopic(topic) : defaultTopic;

            // Create the publisher
            LOGGER.debug("Create the publisher");
            publisher = session.createProducer(null);

            // Create the subscriber
            LOGGER.debug("Create subscriber on topic : {}", destination);
            String selector = new SelectorBuilder()
                    .jmsType(JMSType.REQUEST.name())
                    .build();
            subscriber = session.createConsumer(destination, selector);
            subscriber.setMessageListener(this);
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        // Cleanup code
        // In general, you should always close producers, consumers, sessions, and connections in reverse order of creation.
        // For this simple example, a JMS connection.close will clean up all other resources.
        try {
            if (subscriber != null) {
                subscriber.close();
            }
            if (publisher != null) {
                publisher.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            Message responseMessage;
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
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Process the request.
     *
     * @param request the request to process
     * @return the response of the request
     */
    protected abstract String processRequest(String request);

}
