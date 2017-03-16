package fr.pham.vinh.jms.commons.dto;

import java.util.List;

/**
 * Portail Response DTO.
 * Created by Vinh PHAM on 15/03/2017.
 */
public class PortailResponse {

    private String status;

    private List<Server> servers;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
}
