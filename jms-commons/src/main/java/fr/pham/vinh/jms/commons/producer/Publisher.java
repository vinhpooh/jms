package fr.pham.vinh.jms.commons.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Closeable;

/**
 * Simulate a publisher.
 * Created by Vinh PHAM on 10/03/2017.
 */
public class Publisher implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    /**
     * The simulated publisher.
     */
    private MessageProducer publisher;

    /**
     * Default constructor.
     *
     * @param session the session to use to create the publisher
     * @throws JMSException JMSException
     */
    public Publisher(Session session) throws JMSException {
        this.publisher = session.createProducer(null);
        // TODO : utile en mode topic ?
        // messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    /**
     * Publish a message on a topic.
     *
     * @param destination the topic to publish
     * @param message     the message to publish
     * @throws JMSException JMSException
     */
    public void send(Destination destination, Message message) throws JMSException {
        publisher.send(destination, message);
    }

    /**
     * Close the publisher.
     */
    @Override
    public void close() {
        try {
            publisher.close();
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
