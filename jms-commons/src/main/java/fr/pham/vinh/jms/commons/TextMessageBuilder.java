package fr.pham.vinh.jms.commons;

import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Created by Vinh PHAM on 10/03/2017.
 */
public class TextMessageBuilder {

    private TextMessage textMessage = new ActiveMQTextMessage();

    public TextMessage build() {
        return textMessage;
    }

    public TextMessageBuilder setJMSCorrelationID(String correlationId) throws JMSException {
        textMessage.setJMSCorrelationID(correlationId);
        return this;
    }

    public TextMessageBuilder setRequest(String request) throws JMSException {
        textMessage.setText(request);
        return this;
    }
}
