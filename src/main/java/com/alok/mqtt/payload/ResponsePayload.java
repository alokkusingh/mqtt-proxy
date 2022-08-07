package com.alok.mqtt.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.net.URLEncoder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePayload {

    private Integer code;
    private String body;
    private HttpHeaders httpHeaders;
    private String correlationId;

    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"correlationId\":\"" + correlationId + "\"" +
                ", \"body\":\"" + URLEncoder.encode(body)  + "\"" +
                ", \"httpHeaders\":" + httpHeaders +
                "}";
    }
}
