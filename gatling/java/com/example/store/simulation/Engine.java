package com.example.store.simulation;

import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;

public class Engine {

    // Localhost URL for the API endpoint, also when port forwarding is used
    private static final String baseEndpointUrl = "http://localhost:8080";

    // Optional - microk8s URL for the API endpoint, it may change based on NodePort assigned to the service
    // private static final String baseEndpointUrl = "http://10.152.183.169:8080";

    public static final HttpProtocolBuilder httpProtocol = http.baseUrl(baseEndpointUrl)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Gatling/PerformanceTest");
}
