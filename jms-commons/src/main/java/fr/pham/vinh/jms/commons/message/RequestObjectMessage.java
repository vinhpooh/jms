package fr.pham.vinh.jms.commons.message;

import java.io.Serializable;

/**
 * Created by Vinh PHAM on 08/03/2017.
 */
public class RequestObjectMessage implements Serializable {

    private String application;

    private String environment;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
