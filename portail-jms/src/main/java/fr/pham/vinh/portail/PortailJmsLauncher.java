package fr.pham.vinh.portail;

import fr.pham.vinh.portail.jms.PortailJms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application permettant la communication du Portail QI sur un ESB.
 * Created by Vinh PHAM on 17/03/2017.
 */
public class PortailJmsLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortailJmsLauncher.class);

    private static final String TOPIC_PORTAIL_QI = "topic.portail.qi";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";

    public static void main(String args[]) {
        try (PortailJms portail = new PortailJms(TOPIC_PORTAIL_QI, USER, PASSWORD)) {
            Thread.sleep(120 * 60 * 1000);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
