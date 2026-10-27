package com.inventory.control.web.system.ports.out;

public interface EventPublisherPort {
    void publish(String topic, String key, Object message);
}