package com.taskmanager.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class SFObjectMapper extends ObjectMapper {
    public SFObjectMapper() {
        super();
        this.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
