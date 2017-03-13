package fr.pham.vinh.jms.commons.consumer;

import fr.pham.vinh.jms.commons.JMSType;
import fr.pham.vinh.jms.commons.builder.SelectorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Closeable;

/**
 * Simulate a subscriber.
 * Created by Vinh PHAM on 10/03/2017.
 */
public class Subscriber implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);

    /**
     * The simulated subscriber.
     */
    private MessageConsumer subscriber;

    /**
     * Default constructor.
     *
     * @param session     the session to use to create the subscriber
     * @param destination the topic to subscribe
     * @throws JMSException JMSException
     */
    public Subscriber(Session session, Destination destination) throws JMSException {
        String selector = new SelectorBuilder()
                .jmsType(JMSType.REQUEST.name())
                .build();
        subscriber = session.createConsumer(destination, selector);
    }

    /**
     * Set the message listener of the subscriber.
     *
     * @param listener the listener
     * @throws JMSException JMSException
     */
    public void setMessageListener(MessageListener listener) throws JMSException {
        subscriber.setMessageListener(listener);
    }

    /**
     * Close the subscriber.
     */
    @Override
    public void close() {
        try {
            subscriber.close();
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
