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

    private Session session;
    private Destination destination;

    /**
     * Default constructor.
     *
     * @param session     the session of the producer
     * @param destination the topic of the producer
     */
    public Producer(Session session, Destination destination) {
        this.session = session;
        this.destination = destination;
    }

    /**
     * Send a message.
     *
     * @param message the message to send
     * @throws JMSException JMSException
     */
    public void send(Message message) throws JMSException {
        // Create a MessageProducer
        MessageProducer messageProducer = session.createProducer(destination);
        // TODO : utile en mode topic ?
        // messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Tell the producer to send the message
        messageProducer.send(message);

        // Close the producer
        messageProducer.close();
    }

}
