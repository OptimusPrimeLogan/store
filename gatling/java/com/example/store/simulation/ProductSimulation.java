package com.example.store.simulation;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class ProductSimulation extends Simulation {

    // --- SCENARIOS ---

    // Scenario 1: A user browses the list of all products
    ScenarioBuilder browseAllProducts = scenario("Browse All Products")
            .exec(http("Get All Products").get("/products").check(status().is(200)));

    // Scenario 2: A user creates a new product and then views it
    ScenarioBuilder createAndFindProduct = scenario("Create and Find Product")
            .exec(http("Create Product")
                    .post("/products")
                    .body(StringBody("{ \"description\": \"A shiny new gadget\" }"))
                    .asJson()
                    .check(status().is(201))
                    .check(jsonPath("$.id").saveAs("productId")))
            .pause(Duration.ofSeconds(1), Duration.ofSeconds(3)) // Simulate user think time
            .exec(http("Get Product By ID")
                    .get("/products/#{productId}") // Use the saved ID with Gatling EL
                    .check(status().is(200)));

    // --- LOAD SETUP ---
    {
        setUp(
                        browseAllProducts.injectOpen(rampUsers(200).during(Duration.ofSeconds(20))),
                        createAndFindProduct.injectOpen(rampUsers(200).during(Duration.ofSeconds(20))))
                .protocols(Engine.httpProtocol);
    }
}
