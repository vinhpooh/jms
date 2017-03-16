package fr.pham.vinh.jms.commons.enumeration;

/**
 * JMSType allowed.
 * Created by Vinh PHAM on 11/03/2017.
 */
public enum JMSType {
    REQUEST("The request type"),
    RESPONSE("The response type");

    private String label;

    JMSType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
