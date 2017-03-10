package fr.pham.vinh.jms.commons.sample;

import fr.pham.vinh.jms.commons.message.RequestObjectMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by Vinh PHAM on 08/03/2017.
 */
public class SimpleProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleProducer.class);
    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final String DESTINATION_NAME = "ci_squash";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";
    private static final Boolean NON_TRANSACTED = false;

    public static void main(String args[]) {
        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            // Create a Connection
            Connection connection = connectionFactory.createConnection(USER, PASSWORD);
            connection.start();

            // Create a Session
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
//            String text = "Hello world! From: " + Thread.currentThread().getName();
//            TextMessage message = session.createTextMessage(text);

            RequestObjectMessage message = new RequestObjectMessage();
            message.setApplication("DALI");
            message.setEnvironment("PIC");

            ObjectMessage objectMessage = new ActiveMQObjectMessage();
            objectMessage.setObject(message);

            // Tell the producer to send the message
            producer.send(objectMessage);

            // Clean up
            session.close();
            connection.close();
        } catch (NamingException | JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
