package com.alok.mqtt.processor;

import com.alok.mqtt.payload.ResponsePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseProcessor {
    private ObjectMapper objectMapper;

    public ResponsePayload parseMqttRequest(byte[] payload) {
        ResponsePayload responsePayload = null;
        try {
            responsePayload = objectMapper.readValue(payload, ResponsePayload.class);
        } catch (Exception rte) {
            rte.printStackTrace();
            log.error("Failed to parse MQTT Request, error: {}", rte.getMessage());
        }

        return responsePayload;
    }

    public String processResponse(ResponsePayload responsePayload) {
        log.error("Doing nothing - consider overriding this method");
        return null;
    }

}
