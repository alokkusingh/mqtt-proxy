package com.alok.mqtt.listener;

import com.alok.mqtt.payload.RequestPayload;
import com.alok.mqtt.payload.ResponsePayload;
import com.alok.mqtt.processor.RequestProcessor;
import com.alok.mqtt.service.MqttClientService;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Builder
@Setter
public class MqttCallbackListener implements MqttCallback {

    private MqttClientService mqttClientService;
    private RequestProcessor requestProcessor;

    public MqttCallbackListener() {
    }
    public MqttCallbackListener(MqttClientService mqttClientService, RequestProcessor requestProcessor) {
        this();
        this.mqttClientService = mqttClientService;
        this.requestProcessor = requestProcessor;
    }


    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("Connection Lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.info("Message arrived - {} - {}", topic, mqttMessage);
        log.error("Doing nothing - consider overriding this method");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        log.info("Delivery completed - {}", iMqttDeliveryToken.getMessageId());

    }

    protected MqttClientService getMqttClientService() {
        return mqttClientService;
    }
}
