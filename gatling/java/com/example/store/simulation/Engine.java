package com.example.store.simulation;

import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;

public class Engine {

    public static final HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Gatling/PerformanceTest");
}
