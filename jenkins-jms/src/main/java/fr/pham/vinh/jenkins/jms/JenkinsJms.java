package fr.pham.vinh.jenkins.jms;

import fr.pham.vinh.jms.commons.JmsPush;
import fr.pham.vinh.jms.commons.dto.PortailRequest;
import fr.pham.vinh.jms.commons.dto.Server;
import fr.pham.vinh.jms.commons.dto.SquashRequest;

import java.util.List;

/**
 * Classe permettant la communication de Jenkins, du Portail QI et de Squash sur un ESB.
 * Created by Vinh PHAM on 11/03/2017.
 */
public class JenkinsJms extends JmsPush {

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
     * Create a Squash request.
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

}
