package com.example.store.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TimedConfiguration.class)
class TimedConfigurationTest {

    @Autowired
    private TimedAspect timedAspect;

    @MockitoBean
    private MeterRegistry meterRegistry;

    @Test
    void timedAspect_beanShouldBeCreated() {
        assertThat(timedAspect).isNotNull();
    }
}
