package fr.pham.vinh.jenkins.jms;

import fr.pham.vinh.jms.commons.JmsPush;
import fr.pham.vinh.jms.commons.dto.PortailRequest;
import fr.pham.vinh.jms.commons.dto.Server;
import fr.pham.vinh.jms.commons.dto.SquashRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

/**
 * Classe permettant la communication de Jenkins, du Portail QI et de Squash sur un ESB.
 * Created by Vinh PHAM on 11/03/2017.
 */
public class JenkinsJms extends JmsPush {

    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsJms.class);

    private static final String CONNECTION_FACTORY_NAME = "connectionFactory";

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

    @Override
    protected ConnectionFactory getConnectionFactory() {
        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            return (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
        } catch (NamingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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
