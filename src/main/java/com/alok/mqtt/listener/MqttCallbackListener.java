package com.alok.mqtt.listener;

import com.alok.mqtt.payload.RequestPayload;
import com.alok.mqtt.payload.ResponsePayload;
import com.alok.mqtt.processor.RequestProcessor;
import com.alok.mqtt.service.MqttClientService;
import lombok.Builder;
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

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("Connection Lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.info("Message arrived - {} - {}", topic, mqttMessage);

        RequestPayload requestPayload = requestProcessor.parseMqttRequest(mqttMessage.getPayload());

        ResponseEntity<String> httpResponse = requestProcessor.processRequest(requestPayload);
        httpResponse = Optional.ofNullable(httpResponse).orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        requestPayload = Optional.ofNullable(requestPayload).orElse(new RequestPayload());

        // 2. Send response to MQTT broker
        ResponsePayload responsePayload =  requestProcessor.prepareMqttResponse(requestPayload, httpResponse);

        MqttMessage mqttResponseMessage = new MqttMessage();
        mqttResponseMessage.setQos(1);
        // AWS IoT Core doesn't support retained=true
        mqttResponseMessage.setRetained(false);
        mqttResponseMessage.setPayload(responsePayload.toString().getBytes(StandardCharsets.UTF_8));
        mqttClientService.publish(mqttResponseMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        log.info("Delivery completed - {}", iMqttDeliveryToken.getMessageId());

    }
}
