package com.alok.mqtt.processor;

import com.alok.mqtt.payload.RequestPayload;
import com.alok.mqtt.payload.ResponsePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestProcessor {
    private ObjectMapper objectMapper;

    public RequestPayload parseMqttRequest(byte[] payload) {
        RequestPayload requestPayload = null;
        try {
            requestPayload = objectMapper.readValue(payload, RequestPayload.class);
        } catch (Exception rte) {
            rte.printStackTrace();
            log.error("Failed to parse MQTT Request, error: {}", rte.getMessage());
        }

        return requestPayload;
    }

    public ResponseEntity<String> processRequest(RequestPayload requestPayload) {
        log.error("Doing nothing - consider overriding this method");
        return null;
    }

    public ResponsePayload prepareMqttResponse(RequestPayload requestPayload, ResponseEntity<String> httpResponse) {
        requestPayload = Optional.ofNullable(requestPayload).orElse(new RequestPayload());
        return ResponsePayload.builder()
                .code(httpResponse.getStatusCodeValue())
                .body(httpResponse.getBody())
                .correlationId(requestPayload.getCorrelationId())
                .build();
    }
}
