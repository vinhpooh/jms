package fr.pham.vinh.jms.commons.enumeration;

/**
 * Status code used.
 * Created by Vinh PHAM on 15/03/2017.
 */
public enum StatusCodeEnum {

    OK("OK"),
    KO("KO");

    private String label;

    StatusCodeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
