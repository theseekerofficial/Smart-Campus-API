package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final String BASE_URI = "http://localhost:8080/";

    public static void main(String[] args) throws Exception {
        final ResourceConfig config = ResourceConfig.forApplicationClass(ApplicationConfig.class);

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI), config, false
        );

        server.start();
        LOGGER.info("Smart Campus API started at " + BASE_URI + "api/v1");
        LOGGER.info("Press CTRL+C to stop...");

        Thread.currentThread().join();
    }
}