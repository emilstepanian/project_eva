package view.client;

/**
 * Created by emilstepanian on 27/11/2016.
 */

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

import javax.ws.rs.ext.Provider;

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