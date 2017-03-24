package fr.pham.vinh.jenkins;

import com.google.gson.Gson;
import fr.pham.vinh.jenkins.jms.JenkinsJms;
import fr.pham.vinh.jms.commons.dto.PortailRequest;
import fr.pham.vinh.jms.commons.dto.PortailResponse;
import fr.pham.vinh.jms.commons.dto.SquashRequest;
import fr.pham.vinh.jms.commons.dto.SquashResponse;
import fr.pham.vinh.jms.commons.enumeration.StatusCodeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application permettant la communication de Jenkins, du Portail QI et de Squash sur un ESB.
 * Created by Vinh PHAM on 17/03/2017.
 */
public class JenkinsLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsLauncher.class);

    private static final String DEFAULT_TOPIC_PORTAIL_QI = "topic.portail.qi";
    private static final String DEFAULT_TOPIC_SQUASH = "topic.squash";
    private static final String DEFAULT_TIMEOUT = "10000"; // timeout in milli
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    private static String topicPortailQI;
    private static String topicSquash;
    private static int timeout;
    private static String user;
    private static String password;

    static {
        Properties properties = new Properties();
        try (InputStream in = JenkinsLauncher.class.getClassLoader().getResourceAsStream("properties.properties")) {
            properties.load(in);
            topicPortailQI = properties.getProperty("topic.portail.qi", DEFAULT_TOPIC_PORTAIL_QI);
            topicSquash = properties.getProperty("topic.squash", DEFAULT_TOPIC_SQUASH);
            timeout = Integer.valueOf(properties.getProperty("timeout", DEFAULT_TIMEOUT));
            user = properties.getProperty("user", DEFAULT_USER);
            password = properties.getProperty("password", DEFAULT_PASSWORD);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            LOGGER.warn("Cannot parse port \"{}\", using default port \"{}\".", properties.getProperty("port"), DEFAULT_TIMEOUT);
            timeout = Integer.valueOf(DEFAULT_TIMEOUT);
        }
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
        JenkinsJms jenkins = new JenkinsJms(topicPortailQI, timeout, user, password);

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
        jenkins = new JenkinsJms(topicSquash, timeout, user, password);

        SquashRequest squashRequest = jenkins.createSquashRequest(product, version, environment, portailResponse.getServers());
        String jsonSquashRequest = gson.toJson(squashRequest);

        String jsonSquashResponse = jenkins.process(jsonSquashRequest);
        SquashResponse squashResponse = gson.fromJson(jsonSquashResponse, SquashResponse.class);

        LOGGER.debug("JSON response : {}", jsonSquashResponse);
        LOGGER.debug("OBJ response  : {}", squashResponse);
    }

}
