package view.client;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

import javax.ws.rs.ext.Provider;

/**
 * Created by emilstepanian on 27/11/2016.
 * Adds a response filter to every response being send from the server.
 * The filter adds headers to the responses that specifies the CORS configurations of this server
 */
@Provider
public class CORSResponseFilter
        implements com.sun.jersey.spi.container.ContainerResponseFilter {

    public ContainerResponse filter(ContainerRequest requestContainer, ContainerResponse responseContainer) {

        responseContainer.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        responseContainer.getHttpHeaders().add("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
        responseContainer.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContainer.getHttpHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        return responseContainer;
    }




}