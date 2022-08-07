package com.alok.mqtt.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.http.HttpHeaders;

import java.net.URLEncoder;
import java.util.Base64;

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
                ", \"body\":\"" + Base64.getEncoder().encodeToString(body.getBytes())  + "\"" +
                ", \"httpHeaders\":" + httpHeaders +
                "}";
    }
}
