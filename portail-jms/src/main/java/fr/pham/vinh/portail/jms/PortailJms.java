package fr.pham.vinh.portail.jms;

import com.google.gson.Gson;
import fr.pham.vinh.jms.commons.JmsPull;
import fr.pham.vinh.jms.commons.dto.PortailRequest;
import fr.pham.vinh.jms.commons.dto.PortailResponse;
import fr.pham.vinh.jms.commons.dto.Server;
import fr.pham.vinh.jms.commons.enumeration.StatusCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Classe permettant la communication du Portail QI sur un ESB.
 * Created by Vinh PHAM on 13/03/2017.
 */
public class PortailJms extends JmsPull {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortailJms.class);

    /**
     * Default constructor.
     *
     * @param topic    the topic to use
     * @param user     the user to use
     * @param password the password to use
     */
    public PortailJms(String topic, String user, String password) {
        super(topic, user, password);
    }

    @Override
    protected String processRequest(String request) {
        LOGGER.debug("request {}", request);

        Gson gson = new Gson();
        PortailRequest portailRequest = gson.fromJson(request, PortailRequest.class);

        // TODO : traiter la requête

        Server server1 = new Server();
        server1.setName("ORA11");
        server1.setHostname("XXXXX");

        Server server2 = new Server();
        server1.setName("JBOSS7");
        server1.setHostname("XXXXX");

        Server server3 = new Server();
        server1.setName("JBOSS7");
        server1.setHostname("XXXXX");

        Server server4 = new Server();
        server1.setName("APACHE");
        server1.setHostname("XXXXXX");

        PortailResponse portailResponse = new PortailResponse();
        portailResponse.setStatus(StatusCodeEnum.OK.name());
        portailResponse.setServers(Arrays.asList(server1, server2, server3, server4));

        return gson.toJson(portailResponse);
    }

}
