package com.example.store.simulation;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class OrderSimulation extends Simulation {

    // --- SCENARIO DEFINITION ---
    ScenarioBuilder placeOrderScenario = scenario("User Places an Order")
            // 1. Get a list of all available products
            .exec(http("Get All Products")
                    .get("/products")
                    .check(status().is(200))
                    // Find a random product and save its ID to the session
                    .check(jsonPath("$..id").findRandom().saveAs("productId")))
            .pause(Duration.ofMillis(500), Duration.ofSeconds(2)) // Simulate user browsing
            // 2. Feed a random quantity for the order
            // 3. Create the order using the saved product ID
            .exec(http("Create Order")
                    .post("/order")
                    // For this simulation, we assume customer with ID 1 always exists.
                    // In a real-world test, this could also come from a feeder.
                    .body(
                            StringBody(
                                    """
                                          {
                                            "customerId": 1,
                                            "productIds": [
                                             #{productId}
                                            ]
                                          }
                                          """))
                    .asJson()
                    .check(status().is(201)));

    // --- LOAD SETUP ---
    {
        setUp(placeOrderScenario.injectOpen(rampUsers(200).during(Duration.ofSeconds(20))))
                .protocols(Engine.httpProtocol);
    }
}
