package fr.pham.vinh.jms.commons.dto;

import java.util.List;

/**
 * Squash Request DTO.
 * Created by Vinh PHAM on 15/03/2017.
 */
public class SquashRequest {

    private String product;

    private String version;

    private String environment;

    private List<Server> servers;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
}
