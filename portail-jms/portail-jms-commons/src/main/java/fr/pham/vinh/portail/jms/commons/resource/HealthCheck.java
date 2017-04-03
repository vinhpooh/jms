package fr.pham.vinh.portail.jms.commons.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Properties;

/**
 * HealthCheck resource.
 */
@Path("/healthcheck")
public class HealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheck.class);

    /**
     * Get the application version.
     *
     * @return the application version
     */
    private String getVersion() {
        String version = null;

        // Try to load from maven properties first
        try {
            String path = "/META-INF/maven/fr.pham.vinh/portail-jms/pom.properties";
            Properties p = new Properties();
            InputStream is = this.getClass().getResourceAsStream(path);

            if (is != null) {
                LOGGER.debug("Get version from {}", path);
                p.load(is);
                version = p.getProperty("version", "");
            }
        } catch (Exception e) {
            // ignore
        }

        // Fallback using Java API
        if (version == null) {
            Package aPackage = this.getClass().getPackage();
            if (aPackage != null) {
                LOGGER.debug("Get version from package implementation version..");
                version = aPackage.getImplementationVersion();
                if (version == null) {
                    LOGGER.debug("Get version from package specification version..");
                    version = aPackage.getSpecificationVersion();
                }
            }
        }

        if (version == null) {
            // We could not compute the version
            version = "unknown";
        }

        return version;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthcheck() {
        return Response.ok("Version : " + this.getVersion()).build();
    }
}