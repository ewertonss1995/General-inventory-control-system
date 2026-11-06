package com.inventory.control.web.system.adapters.out.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherAdapterTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private MeterRegistry meterRegistry;

    @InjectMocks
    private KafkaEventPublisherAdapter kafkaEventPublisherAdapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        kafkaEventPublisherAdapter = new KafkaEventPublisherAdapter(kafkaTemplate, meterRegistry);
    }

    @Test
    void shouldPublishEventSuccessfully() {
        String topic = "test-topic";
        String key = "test-key";
        Object message = "test-message";

        kafkaEventPublisherAdapter.publish(topic, key, message);

        verify(kafkaTemplate).send(topic, key, message);
        assertEquals(1.0, meterRegistry.counter("kafka.producer.publish.success", "layer", "adapter_out", "target", "kafka", "topic", topic).count());
    }

    @Test
    void shouldThrowExceptionAndRecordFailureWhenKafkaFails() {
        String topic = "test-topic";
        String key = "test-key";
        Object message = "test-message";

        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(anyString(), anyString(), any());

        assertThrows(RuntimeException.class, () -> kafkaEventPublisherAdapter.publish(topic, key, message));

        verify(kafkaTemplate).send(topic, key, message);
        assertEquals(1.0, meterRegistry.counter("kafka.producer.publish.failures", "layer", "adapter_out", "target", "kafka", "topic", topic, "error_type", "RuntimeException").count());
    }
}