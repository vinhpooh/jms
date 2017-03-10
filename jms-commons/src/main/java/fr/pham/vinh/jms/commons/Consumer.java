package fr.pham.vinh.jms.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Handle message consumer.
 * Created by Vinh PHAM on 10/03/2017.
 */
public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private Session session;
    private Destination destination;
    private String selector;

    /**
     * Default constructor.
     *
     * @param session     the session of the consumer
     * @param destination the topic of the consumer
     * @param selector    the message filters
     */
    public Consumer(Session session, Destination destination, String selector) {
        this.session = session;
        this.destination = destination;
        this.selector = selector;
    }

    /**
     * Create a consumer whitouth selector.
     *
     * @param session     the session of the consumer
     * @param destination the topic of the consumer
     */
    public Consumer(Session session, Destination destination) {
        this(session, destination, null);
    }

    /**
     * Consume a message that match the selector.
     *
     * @param timeout the timeout
     * @return the payload
     * @throws JMSException JMSException
     */
    public String consume(int timeout) throws JMSException {
        String response;

        // Create a MessageConsumer
        MessageConsumer messageConsumer = session.createConsumer(destination, selector);

        // Wait for a message
        Message message = messageConsumer.receive(timeout);

        if (message == null) {
            LOGGER.error("Timeout while waiting message response [selector : {}/ timeout : {} ms]", messageConsumer.getMessageSelector(), timeout);
            throw new RuntimeException("Timeout while waiting message response.");
        } else if (message instanceof TextMessage) {
            response = ((TextMessage) message).getText();
        } else {
            LOGGER.error("Unsupported message type");
            throw new RuntimeException("Unsupported message type");
        }

        // Close the consumer
        messageConsumer.close();

        return response;
    }

}
