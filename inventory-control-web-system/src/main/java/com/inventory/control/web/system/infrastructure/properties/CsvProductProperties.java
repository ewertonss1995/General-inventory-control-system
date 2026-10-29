package com.inventory.control.web.system.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "csv.product")
public record CsvProductProperties(
        Set<String> requiredHeaders
) {}