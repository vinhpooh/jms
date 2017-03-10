package fr.pham.vinh.jms.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Handle message producer.
 * Created by Vinh PHAM on 10/03/2017.
 */
public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    private MessageProducer messageProducer;

    /**
     * Default constructor.
     *
     * @param session     the session of the producer
     * @param destination the topic of the producer
     */
    public Producer(Session session, Destination destination) {
        try {
            // Create a MessageProducer
            messageProducer = session.createProducer(destination);
            // TODO : utile en mode topic ?
            // messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Send a text message.
     *
     * @param message the message to send
     * @throws JMSException JMSException
     */
    public void send(TextMessage message) throws JMSException {
        // Tell the producer to send the message
        messageProducer.send(message);
    }
}
