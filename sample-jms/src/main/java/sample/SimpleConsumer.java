package sample;

import fr.pham.vinh.jms.commons.message.RequestObjectMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vinh PHAM on 08/03/2017.
 */
public class SimpleConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleProducer.class);
    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";
    private static final List<String> TRUSTED_PACKAGES = Collections.singletonList("fr.pham.vinh.jms.commons.message");
    private static final String DESTINATION_NAME = "ci_squash";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";
    private static final Boolean NON_TRANSACTED = false;

    public static void main(String[] args) {
        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            // TODO : adhérence avec activemq ?
            ((ActiveMQConnectionFactory) connectionFactory).setTrustedPackages(TRUSTED_PACKAGES);

            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            // Create a Connection
            Connection connection = connectionFactory.createConnection(USER, PASSWORD);
            connection.start();

            // Create a Session
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // Wait for a message
            Message message = consumer.receive(10000);

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("1 Received: " + text);
            } else if (message instanceof ObjectMessage) {
                Object obj = ((ObjectMessage) message).getObject();
                if (obj instanceof RequestObjectMessage) {
                    RequestObjectMessage customMessage = (RequestObjectMessage) obj;
                    System.out.println("2 Received: " + customMessage.getApplication() + "/" + customMessage.getEnvironment());
                } else {
                    System.out.println("3 Received: " + message);
                }
            } else {
                System.out.println("4 Received: " + message);
            }

            // Clean up
            consumer.close();
            session.close();
            connection.close();
        } catch (NamingException | JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
