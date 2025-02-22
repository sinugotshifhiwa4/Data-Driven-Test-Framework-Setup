package com.data.driven.config.jackson;

import com.data.driven.utils.ErrorHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public final class JsonDataConfig {

    // Thread-safe ObjectMapper instance
    private static final ObjectMapper objectMapper = createObjectMapper();

    private JsonDataConfig() {
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Serializes the given object to a JSON string.
     *
     * @param value The object to serialize
     * @return The JSON string representation of the object
     */
    public static String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            ErrorHandler.logError(error, "serialize", "Failed to serialize object to JSON");
            throw new RuntimeException("Failed to serialize object to JSON", error);
        }
    }

    /**
     * Serializes the given object to a pretty-printed JSON string.
     *
     * @param value The object to serialize
     * @return The pretty-printed JSON string
     */
    public static String serializePretty(Object value) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException error) {
            ErrorHandler.logError(error, "serializePretty", "Failed to serialize object to pretty-printed JSON");
            throw new RuntimeException("Failed to serialize object to pretty-printed JSON", error);
        }
    }

    /**
     * Deserializes the given JSON string to the given class type.
     *
     * @param json  The JSON string to deserialize
     * @param clazz The class type to deserialize to
     * @return The deserialized object of the given class type
     */
    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException error) {
            ErrorHandler.logError(error, "deserialize", "Failed to deserialize JSON to object");
            throw new RuntimeException("Failed to deserialize JSON to object", error);
        }
    }

    /**
     * Deserializes the given JSON file to the given class type.
     *
     * @param file  The JSON file to deserialize
     * @param clazz The class type to deserialize to
     * @return The deserialized object of the given class type
     * @throws IOException If the file cannot be read or deserialization fails
     */
    public static <T> T deserialize(File file, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(file, clazz);
        } catch (JsonProcessingException error) {
            ErrorHandler.logError(error, "deserialize", "Failed to deserialize JSON file to object");
            throw new RuntimeException("Failed to deserialize JSON file to object", error);
        }
    }
}