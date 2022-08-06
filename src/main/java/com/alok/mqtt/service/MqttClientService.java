package com.alok.mqtt.service;

import com.alok.mqtt.config.Properties;
import com.alok.mqtt.listener.MqttCallbackListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

@Slf4j
@Data
public class MqttClientService {

    private IMqttClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;
    private MqttCallbackListener mqttCallbackListener;

    private Properties iotProperties;

    public void connect() throws MqttException {
        mqttClient = mqttClient();
        mqttCallbackListener.setMqttClientService(this);
        boolean connected = false;
        int retryCount = 0;
        int maxCount = iotProperties.getMqtt().getConnectionRetry();
        String clientId = iotProperties.getMqtt().getClientId();
        while (!connected && retryCount < maxCount) {
            try {
                log.warn("[{}] Connecting to MQTT broker - attempt: {}", clientId, retryCount + 1);
                log.debug(mqttConnectOptions.toString());
                mqttClient.connect(mqttConnectOptions);
                log.warn("[{}] Connected: {}", clientId, mqttClient.isConnected());
                mqttClient.subscribe(new String[] {iotProperties.getMqtt().getSubscribeTopic()}, new int[] {iotProperties.getMqtt().getSubscribeQos()});
                log.warn("[{}] Subscribed: {}", clientId, iotProperties.getMqtt().getSubscribeTopic());

                connected = true;
            } catch (MqttException e) {
                e.printStackTrace();
                ++retryCount;
                if (retryCount < maxCount) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }

    public void disConnect() {
        try {
            String clientId = iotProperties.getMqtt().getClientId();
            log.warn("[{}] Disconnecting, connected: {}", clientId, mqttClient.isConnected());
            if (mqttClient.isConnected())
                mqttClient.disconnect();
            log.warn("[{}] Disconnected", clientId);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(final MqttMessage message) throws MqttException {
        mqttClient.publish( iotProperties.getMqtt().getPublishTopic(), message );
    }

    private IMqttClient mqttClient() throws MqttException {


        String publisherId = iotProperties.getMqtt().getClientId();
        String iotClientUrl = String.format("ssl://%s:%s", iotProperties.getMqtt().getHost(), iotProperties.getMqtt().getPort());

        MqttClient mqttClient = new MqttClient(iotClientUrl, publisherId);
        mqttClient.setCallback(mqttCallbackListener);


        return mqttClient;
    }
}
