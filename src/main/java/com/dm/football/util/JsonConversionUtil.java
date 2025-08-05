package com.dm.football.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonConversionUtil {

    @Setter
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonConversionUtil() {
        // object initialization not allowed.
    }

    public static <T> T convertFromJsonSilently(String jsonString, TypeReference<T> typeReference) throws JsonParseException {
        try {
            return convertFromJson(jsonString, typeReference);
        } catch (IOException ex) {
            log.error("Error while converting json: {} to object due to:", jsonString, ex);
            throw new JsonParseException("Error converting JSON to object");
        }
    }

    public static <T> T convertFromJson(String jsonString, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(jsonString, typeReference);
    }
}