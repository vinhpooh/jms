package fr.pham.vinh.jms.commons;

/**
 * Selector builder.
 * Created by Vinh PHAM on 10/03/2017.
 */
public class SelectorBuilder {

    private StringBuilder selector = new StringBuilder();

    /**
     * Build the selector.
     *
     * @return the selector
     */
    public String build() {
        return selector.toString();
    }

    /**
     * Add and clause.
     *
     * @return the builder
     */
    public SelectorBuilder and() {
        this.selector.append(" and ");
        return this;
    }

    /**
     * Add or clause.
     *
     * @return the builder
     */
    public SelectorBuilder or() {
        this.selector.append(" or ");
        return this;
    }

    /**
     * Add JMSCorrelationID.
     *
     * @param jmsCorrelationId the correlation id to add.
     * @return the builder
     */
    public SelectorBuilder jmsCorrelationID(String jmsCorrelationId) {
        selector.append("JMSCorrelationID='").append(jmsCorrelationId).append("'");
        return this;
    }

}
