package com.example.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Configuration class to enable Micrometer's TimedAspect for method timing. This aspect will automatically time methods
 * annotated with @Timed.
 */
@Configuration
public class TimedConfiguration {

    /**
     * Creates a TimedAspect bean that will use the provided MeterRegistry.
     *
     * @param registry the MeterRegistry to be used by the TimedAspect
     * @return a new instance of TimedAspect
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
