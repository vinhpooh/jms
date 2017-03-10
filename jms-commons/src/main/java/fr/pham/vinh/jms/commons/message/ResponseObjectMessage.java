package fr.pham.vinh.jms.commons.message;

import java.io.Serializable;

/**
 * Created by Vinh PHAM on 08/03/2017.
 */
public class ResponseObjectMessage implements Serializable {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
