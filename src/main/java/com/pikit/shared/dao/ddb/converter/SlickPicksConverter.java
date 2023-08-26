package com.pikit.shared.dao.ddb.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikit.shared.exceptions.InternalErrorException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SlickPicksConverter {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String toJSONString(T object) throws InternalErrorException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Exception thrown mapping object into string {}", object, e);
            throw new InternalErrorException("Unable to transform object into JSON string");
        }
    }

    public static <T> T createFromJSONString(String s, Class<T> clazz) throws InternalErrorException {
        try {
            return objectMapper.readValue(s, clazz);
        } catch (Exception e) {
            log.error("Exception thrown transforming JSON string into object", e);
            throw new InternalErrorException("Unable to transform JSON strings to object");
        }
    }
}
