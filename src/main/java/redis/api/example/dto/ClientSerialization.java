package redis.api.example.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ClientSerialization {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object inputObj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(inputObj);
    }

    public static <T> Object deserialize(String jsonString, Client client) throws IOException {
        return objectMapper.readValue(jsonString, client.getClass() );
    }
}
