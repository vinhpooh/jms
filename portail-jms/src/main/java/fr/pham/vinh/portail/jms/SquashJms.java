package fr.pham.vinh.portail.jms;

import com.google.gson.Gson;
import fr.pham.vinh.jms.commons.JmsPull;
import fr.pham.vinh.jms.commons.dto.SquashRequest;
import fr.pham.vinh.jms.commons.dto.SquashResponse;
import fr.pham.vinh.jms.commons.enumeration.StatusCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application de test permettant de simuler la commuication avec Squash sur un ESB.
 * Created by Vinh PHAM on 13/03/2017.
 */
public class SquashJms extends JmsPull {

    private static final Logger LOGGER = LoggerFactory.getLogger(SquashJms.class);

    private static final String TOPIC_SQUASH = "topic.squash";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";

    /**
     * Default constructor.
     *
     * @param topic    the topic tu use
     * @param user     the user to use
     * @param password the password to use
     */
    public SquashJms(String topic, String user, String password) {
        super(topic, user, password);
    }

    @Override
    protected String processRequest(String request) {
        LOGGER.debug("request {}", request);

        Gson gson = new Gson();
        SquashRequest squashRequest = gson.fromJson(request, SquashRequest.class);

        SquashResponse squashResponse = new SquashResponse();
        squashResponse.setStatus(StatusCodeEnum.OK.name());
        squashResponse.setComment("un commentaire");

        return gson.toJson(squashResponse);
    }

    public static void main(String args[]) {
        try (SquashJms squash = new SquashJms(TOPIC_SQUASH, USER, PASSWORD)) {
            Thread.sleep(120 * 60 * 1000);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
