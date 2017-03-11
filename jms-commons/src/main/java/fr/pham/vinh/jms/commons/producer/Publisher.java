package fr.pham.vinh.jms.commons.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Simulate a publisher.
 * Created by Vinh PHAM on 10/03/2017.
 */
public class Publisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    /**
     * The simulated publisher.
     */
    private MessageProducer messageProducer;

    /**
     * Default constructor.
     *
     * @param session the session to use to create the publisher
     * @throws JMSException JMSException
     */
    public Publisher(Session session) throws JMSException {
        this.messageProducer = session.createProducer(null);
        // TODO : utile en mode topic ?
        // messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    /**
     * Publish a message on a topic.
     *
     * @param destination the topic to publish
     * @param message     the message to publish
     * @return self
     * @throws JMSException JMSException
     */
    public Publisher send(Destination destination, Message message) throws JMSException {
        messageProducer.send(destination, message);
        return this;
    }

    /**
     * Close the publisher.
     *
     * @throws JMSException JMSException
     */
    public void close() throws JMSException {
        messageProducer.close();
    }
}
