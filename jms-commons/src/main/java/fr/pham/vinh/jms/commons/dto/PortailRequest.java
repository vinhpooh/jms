package fr.pham.vinh.jms.commons.dto;

/**
 * Portail Request DTO.
 * Created by Vinh PHAM on 15/03/2017.
 */
public class PortailRequest {

    private String product;

    private String version;

    private String environment;

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
}
