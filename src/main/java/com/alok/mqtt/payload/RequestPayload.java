package com.alok.mqtt.payload;

import lombok.*;
import org.springframework.http.HttpMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPayload  {

    private HttpMethod httpMethod;
    private String uri;
    private String body;
    private String correlationId;

    @Override
    public String toString() {
        return "{" +
                "\"httpMethod\":\"" + httpMethod + "\"" +
                ", \"uri\":\"" + uri + "\"" +
                ", \"correlationId\":\"" + correlationId + "\"" +
                ", \"body\":\"" + body  + "\"" +
                "}";
    }

}
