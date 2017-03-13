package fr.pham.vinh.jms.commons.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Handle message consumer.
 * Created by Vinh PHAM on 10/03/2017.
 */
public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    /**
     * Consume a message on the session and destination that match the selector.
     *
     * @param session     the session of the consumer
     * @param destination the topic of the consumer
     * @param selector    the message filters
     * @param timeout     the timeout
     * @return the payload
     * @throws JMSException JMSException
     */
    public static String consume(Session session, Destination destination, String selector, int timeout) throws JMSException {
        String response;

        // Create a MessageConsumer
        MessageConsumer messageConsumer = session.createConsumer(destination, selector);

        // Wait for a message
        Message message = messageConsumer.receive(timeout);

        // Close the consumer
        messageConsumer.close();

        if (message == null) {
            LOGGER.error("Timeout while waiting message response [selector : {}/ timeout : {} ms]", selector, timeout);
            throw new RuntimeException("Timeout while waiting message response.");
        } else if (message instanceof TextMessage) {
            response = ((TextMessage) message).getText();
        } else {
            LOGGER.error("Unsupported message type");
            throw new RuntimeException("Unsupported message type");
        }

        return response;
    }

}
