package fr.pham.vinh.jms.commons.builder;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * TextMessage builder.
 * Created by Vinh PHAM on 10/03/2017.
 */
public class TextMessageBuilder {

    private TextMessage textMessage;

    /**
     * Default constructor.
     *
     * @param textMessage the text message to build
     */
    public TextMessageBuilder(TextMessage textMessage) {
        this.textMessage = textMessage;
    }

    /**
     * Build the text message.
     *
     * @return the text message
     */
    public TextMessage build() {
        return textMessage;
    }

    /**
     * Set the correlation id on the text message.
     *
     * @param correlationId the correlation id to set
     * @return the builder
     * @throws JMSException JMSException
     */
    public TextMessageBuilder setJMSCorrelationID(String correlationId) throws JMSException {
        textMessage.setJMSCorrelationID(correlationId);
        return this;
    }

    /**
     * Set the reply to on the text message.
     *
     * @param replyTo the reply to to set
     * @return the builder
     * @throws JMSException JMSException
     */
    public TextMessageBuilder setJMSReplyTo(Destination replyTo) throws JMSException {
        textMessage.setJMSReplyTo(replyTo);
        return this;
    }

    /**
     * Set the type on the text message.
     *
     * @param jmsType the type to set
     * @return the builder
     * @throws JMSException JMSException
     */
    public TextMessageBuilder setJMSType(String jmsType) throws JMSException {
        textMessage.setJMSType(jmsType);
        return this;
    }

    /**
     * Set the request on the text message
     *
     * @param request the request to set
     * @return the builder
     * @throws JMSException JMSException
     */
    public TextMessageBuilder setRequest(String request) throws JMSException {
        textMessage.setText(request);
        return this;
    }

}
