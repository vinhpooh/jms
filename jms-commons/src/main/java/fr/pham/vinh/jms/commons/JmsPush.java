package fr.pham.vinh.jms.commons;

import fr.pham.vinh.jms.commons.builder.SelectorBuilder;
import fr.pham.vinh.jms.commons.builder.TextMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Push a request and pull the response on Java Message Service.
 * Created by Vinh PHAM on 09/03/2017.
 */
public abstract class JmsPush {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsPush.class);

    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final String DEFAULT_TOPIC_NAME = "default.topic";

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
    public JmsPush(String topic, int timeout, String user, String password) {
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
    public JmsPush(int timeout, String user, String password) {
        this(null, timeout, user, password);
    }

    /**
     * Send a message and wait the response.
     *
     * @param request the request to send
     * @return the response to the request
     */
    public String process(String request) {
        // Create connection
        try (Connection connection = connectionFactory.createConnection(user, password)) {
            connection.start();

            // Create session and destination
            try (Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE)) {
                Destination destination = topic != null ? session.createTopic(topic) : defaultTopic;

                // Create correlationId
                String correlationId = UUID.randomUUID().toString();

                // Create message
                TextMessage message = new TextMessageBuilder(session.createTextMessage())
                        .setJMSCorrelationID(correlationId)
                        .setJMSReplyTo(destination)
                        .setJMSType(JMSType.REQUEST.name())
                        .setText(request)
                        .build();

                // Create selector
                String selector = new SelectorBuilder()
                        .jmsCorrelationID(correlationId)
                        .and().jmsType(JMSType.RESPONSE.name())
                        .build();

                // Asynchroneously wait the response
                CompletableFuture<String> consumed = asyncConsumer(session, destination, selector);

                // Asynchroneously send the request
                CompletableFuture<Void> produced = asyncProducer(session, destination, message);

                // Wait the request's response
                return produced.thenCombine(consumed, (t, u) -> u).join();
            }
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Asynchroneously wait a message.
     *
     * @param session     the session to create the consumer
     * @param destination the destination to listen
     * @param selector    the message filter
     * @return futur response
     */
    private CompletableFuture<String> asyncConsumer(Session session, Destination destination, String selector) {
        return CompletableFuture
                .supplyAsync(() -> {
                    try (MessageConsumer consumer = session.createConsumer(destination, selector)) {
                        // Wait for a message
                        LOGGER.debug("Wait message on topic {} with selector : {}", destination, selector);
                        Message message = consumer.receive(timeout);

                        // Handle message
                        if (message == null) {
                            LOGGER.error("Timeout while waiting message response [selector : {}/ timeout : {} ms]", selector, timeout);
                            throw new RuntimeException("Timeout while waiting message response.");
                        } else if (message instanceof TextMessage) {
                            return ((TextMessage) message).getText();
                        } else {
                            LOGGER.error("Unsupported message type");
                            throw new RuntimeException("Unsupported message type");
                        }
                    } catch (JMSException e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Asynchroneously send a message.
     *
     * @param session     the session to create the producer
     * @param destination the destination to send
     * @param message     the message to send
     * @return Void
     */
    private CompletableFuture<Void> asyncProducer(Session session, Destination destination, Message message) {
        return CompletableFuture
                .supplyAsync(() -> {
                    try (MessageProducer producer = session.createProducer(null)) {
                        // FIXME : setDeliveryDelay not working
                        Thread.sleep(1000);

                        // Send message
                        LOGGER.debug("Send message on topic {} : {}", destination, message);
                        producer.send(destination, message);
                        return null;
                    } catch (JMSException | InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                });
    }

}
