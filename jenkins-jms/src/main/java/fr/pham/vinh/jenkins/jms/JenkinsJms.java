package fr.pham.vinh.jenkins.jms;

import com.google.gson.Gson;
import fr.pham.vinh.jms.commons.JmsPush;
import fr.pham.vinh.jms.commons.dto.*;
import fr.pham.vinh.jms.commons.enumeration.StatusCodeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Application permettant la communication de Jenkins, du Portail QI et de Squash sur un ESB.
 * Created by Vinh PHAM on 11/03/2017.
 */
public class JenkinsJms extends JmsPush {
    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsJms.class);

    private static final String TOPIC_PORTAIL_QI = "topic.portail.qi";
    private static final String TOPIC_SQUASH = "topic.squash";
    private static final int TIMEOUT = 10 * 1000; // timeout in milli
    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";

    /**
     * Default constructor.
     *
     * @param topic    the topic tu use
     * @param timeout  the timeout in ms to use
     * @param user     the user to use
     * @param password the password to use
     */
    public JenkinsJms(String topic, int timeout, String user, String password) {
        super(topic, timeout, user, password);
    }

    /**
     * Create a Portail request.
     *
     * @param product     the product
     * @param version     the version
     * @param environment the environment
     * @return th request
     */
    public PortailRequest createPortailRequest(String product, String version, String environment) {
        PortailRequest portailRequest = new PortailRequest();
        portailRequest.setProduct(product);
        portailRequest.setVersion(version);
        portailRequest.setEnvironment(environment);
        return portailRequest;
    }

    /**
     * Create a SQUASH request
     *
     * @param product     the product
     * @param version     the version
     * @param environment the environment
     * @param servers     the servers' list
     * @return the request
     */
    public SquashRequest createSquashRequest(String product, String version, String environment, List<Server> servers) {
        SquashRequest squashRequest = new SquashRequest();
        squashRequest.setProduct(product);
        squashRequest.setVersion(version);
        squashRequest.setEnvironment(environment);
        squashRequest.setServers(servers);
        return squashRequest;
    }

    public static void main(String args[]) {
        // Get VM arguments
        String product = System.getProperty("rsi.product");
        String version = System.getProperty("rsi.version");
        String environment = System.getProperty("rsi.environment");

        // Verify VM arguments
        if (product == null || version == null || environment == null) {
            throw new RuntimeException("Missing VM arguments..");
        }

        // Init parser
        Gson gson = new Gson();

        // Execute a push pull on PortailQI
        JenkinsJms jenkins = new JenkinsJms(TOPIC_PORTAIL_QI, TIMEOUT, USER, PASSWORD);

        PortailRequest portailRequest = jenkins.createPortailRequest(product, version, environment);
        String jsonPortailRequest = gson.toJson(portailRequest);

        String jsonPortailResponse = jenkins.process((jsonPortailRequest));
        PortailResponse portailResponse = gson.fromJson(jsonPortailResponse, PortailResponse.class);

        // Verify PortailQI response
        if (portailResponse == null) {
            throw new RuntimeException("No Portail response.");
        } else if (!StatusCodeEnum.OK.name().equals(portailResponse.getStatus())) {
            throw new RuntimeException("Portail response status is KO.");
        } else if (CollectionUtils.isEmpty(portailResponse.getServers())) {
            LOGGER.info("No servers found for the request : {}", jsonPortailRequest);
            System.exit(0);
        }

        // TODO : generate inventory

        // TODO : push pull on SQUASH
        // Execute a push pull on SQUASH
        jenkins = new JenkinsJms(TOPIC_SQUASH, TIMEOUT, USER, PASSWORD);

        SquashRequest squashRequest = jenkins.createSquashRequest(product, version, environment, portailResponse.getServers());
        String jsonSquashRequest = gson.toJson(squashRequest);

        String jsonSquashResponse = jenkins.process(jsonSquashRequest);
        SquashResponse squashResponse = gson.fromJson(jsonSquashResponse, SquashResponse.class);

        LOGGER.debug("JSON response : {}", jsonSquashResponse);
        LOGGER.debug("OBJ response  : {}", squashResponse);
    }

}
