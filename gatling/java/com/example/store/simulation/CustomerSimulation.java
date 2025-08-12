package com.example.store.simulation;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class CustomerSimulation extends Simulation {

    // --- FEEDER ---
    // Use a CSV file to get dynamic customer names
    private final FeederBuilder.Batchable<String> customerFeeder =
            csv("data/customers.csv").random();

    // --- SCENARIO ---
    ScenarioBuilder createAndSearchCustomer = scenario("Create and Search Customer")
            .feed(customerFeeder) // Inject a random row from the CSV into the session
            .exec(http("Create Customer")
                    .post("/customer")
                    .body(StringBody("{ \"name\": \"#{customerName}\" }"))
                    .asJson()
                    .check(status().is(201)))
            .pause(Duration.ofSeconds(2))
            .exec(http("Find Customer By Name")
                    .get("/customer?name=#{customerName}")
                    .check(status().is(200))
                    .check(jsonPath("$[0].name").is("Alice")));

    // --- LOAD SETUP ---
    {
        setUp(createAndSearchCustomer.injectOpen(atOnceUsers(100))).protocols(Engine.httpProtocol);
    }
}
