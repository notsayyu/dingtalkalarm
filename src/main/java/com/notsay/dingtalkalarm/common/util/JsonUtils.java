package com.notsay.dingtalkalarm.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author notsay
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static ObjectWriter PRETTY_WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String prettyPrint(Object o) {
        if (o == null) {
            log.warn("prettyPrint input is null!!!");
            return "";
        }
        try {
            if (o instanceof String) {
                o = parseObject((String) o);
            }
            return PRETTY_WRITER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("prettyPrint exception:{}", e.getMessage(), e);
            throw new JsonException(e.getMessage(), e);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return MAPPER;
    }

    public static String toJSONString(Object o) {
        if (o == null) {
            return "";
        }
        try {
            return MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage(), e);
        }
    }

    public static JsonNode parseObject(String json) {
        if (json == null || "".equals(json)) {
            throw new IllegalArgumentException("input can not be blank");
        }
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            log.error("json转化异常，入参={}", json);
            throw new JsonException(e.getMessage(), e);
        }
    }

    public static ArrayNode parseArray(String jsonArray) {
        if (jsonArray == null || "".equals(jsonArray)) {
            throw new IllegalArgumentException("input can not be blank");
        }
        try {
            return MAPPER.readValue(jsonArray, ArrayNode.class);
        } catch (IOException e) {
            log.error("json转化异常，入参={}", jsonArray);
            throw new JsonException(e.getMessage(), e);
        }
    }

    public static <T> T parseObject(String json, Class<T> tClass) {
        if (json == null || "".equals(json)) {
            throw new RuntimeException( "jsonString can not be blank");
        }
        try {
            return MAPPER.readValue(json, tClass);
        } catch (IOException e) {
            log.error("json转化异常，入参={}，目标对象={}", json, tClass.getName());
            throw new JsonException(e.getMessage(), e);
        }
    }

    public static <E> List<E> parseArray(String jsonArray, Class<E> eClass) {
        if (jsonArray == null || "".equals(jsonArray)) {
            return Collections.emptyList();
        }
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, eClass);
        try {
            return MAPPER.readValue(jsonArray, javaType);
        } catch (IOException e) {
            log.error("json转化异常，入参={}，目标对象={}", jsonArray, eClass.getName());
            throw new JsonException(e.getMessage(), e);
        }
    }

    public static <T> T parseGeneric(String json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("转化泛型失败,原文为:{}", json);
            throw new JsonException(e.getMessage(), e);
        }
    }

    @Getter
    public static final class JsonException extends RuntimeException {

        private String message;

        public JsonException(String message, Throwable throwable) {
            super(message, throwable);
            this.message = message;
        }

    }

}
