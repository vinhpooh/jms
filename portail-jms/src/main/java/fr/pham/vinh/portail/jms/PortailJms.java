package fr.pham.vinh.portail.jms;

import fr.pham.vinh.jms.commons.JmsPullPush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vinh PHAM on 13/03/2017.
 */
public class PortailJms extends JmsPullPush {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortailJms.class);

    /**
     * Default constructor.
     *
     * @param user     the user to use
     * @param password the password to use
     */
    public PortailJms(String user, String password) {
        super(user, password);
    }

    @Override
    protected String processRequest(String request) {
        // TODO : répondre à la requête
        return "{" +
                "type:\"response\"," +
                "request:\"" + request + "\"," +
                "status:\"OK\"" +
                "}";
    }

    public static void main(String args[]) {
        try (PortailJms portail = new PortailJms("admin", "admin123")) {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
