package fr.pham.vinh.jms.commons;

/**
 * Created by Vinh PHAM on 10/03/2017.
 */
public class SelectorBuilder {

    private StringBuilder selector = new StringBuilder();

    public String build() {
        return selector.toString();
    }

    public SelectorBuilder addJMSCorrelationID(String jmsCorrelationId) {
        selector.append("JMSCorrelationID='").append(jmsCorrelationId).append("'");
        return this;
    }

}
