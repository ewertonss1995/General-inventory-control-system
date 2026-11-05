package com.inventory.control.web.system.adapters.out.kafka;

import com.inventory.control.web.system.ports.out.EventPublisherPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisherAdapter.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MeterRegistry meterRegistry;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate, MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void publish(String topic, String key, Object message) {
        Timer.builder("kafka.producer.publish.time")
                .description("Tempo de execução para publicação de eventos no Kafka")
                .tag("layer", "adapter_out")
                .tag("target", "kafka")
                .tag("topic", topic)
                .register(meterRegistry)
                .record(() -> {
                    try {
                        log.debug("Publicando evento no tópico Kafka: {} com chave: {}", topic, key);
                        kafkaTemplate.send(topic, key, message);
                        
                        meterRegistry.counter("kafka.producer.publish.success",
                                "layer", "adapter_out",
                                "target", "kafka",
                                "topic", topic).increment();
                                
                        log.debug("Evento publicado com sucesso no tópico: {}", topic);
                    } catch (Exception ex) {
                        meterRegistry.counter("kafka.producer.publish.failures",
                                "layer", "adapter_out",
                                "target", "kafka",
                                "topic", topic,
                                "error_type", ex.getClass().getSimpleName()).increment();
                                
                        log.error("Erro ao publicar evento no tópico Kafka: {}", topic, ex);
                        throw ex;
                    }
                });
    }
}