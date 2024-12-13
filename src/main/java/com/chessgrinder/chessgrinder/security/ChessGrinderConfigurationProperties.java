package com.chessgrinder.chessgrinder.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties("chessgrinder")
public class ChessGrinderConfigurationProperties {

    private Map<String, String> feature;

    /**
     * Returns a new map containing only keys where the corresponding {key}.export entry is "true".
     * @return a filtered map
     */
    public Map<String, String> getExportProperties() {
        return feature.entrySet().stream()
                .filter(entry -> "true".equals(feature.get(entry.getKey() + ".export")))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
