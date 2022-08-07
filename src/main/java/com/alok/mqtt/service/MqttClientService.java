package com.alok.mqtt.service;

import com.alok.mqtt.config.Properties;
import com.alok.mqtt.listener.MqttCallbackListener;
import com.alok.mqtt.utils.CertUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;

@Slf4j
@Data
public class MqttClientService {

    private IMqttClient mqttClient;
    private MqttCallbackListener mqttCallbackListener;

    private Properties iotProperties;

    public void connect() throws MqttException {
        MqttConnectOptions mqttConnectOptions = mqttClientConnectOptions();
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

    private MqttConnectOptions mqttClientConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(iotProperties.getMqtt().getCleanState());
        mqttConnectOptions.setAutomaticReconnect(iotProperties.getMqtt().getAutoReconnect());
        mqttConnectOptions.setConnectionTimeout(iotProperties.getMqtt().getConnectionTimeout());
        mqttConnectOptions.setKeepAliveInterval(iotProperties.getMqtt().getKeepAlive());
        mqttConnectOptions.setWill(iotProperties.getMqtt().getStatusTopic(), offlineMessage(), 1, false);

        mqttConnectOptions.setSSLProperties(sslClientProperties());

        return mqttConnectOptions;
    }

    private java.util.Properties sslClientProperties() {

        //valid properties are {"com.ibm.ssl.protocol", "com.ibm.ssl.contextProvider", "com.ibm.ssl.keyStore", "com.ibm.ssl.keyStorePassword", "com.ibm.ssl.keyStoreType", "com.ibm.ssl.keyStoreProvider", "com.ibm.ssl.keyManager", "com.ibm.ssl.trustStore", "com.ibm.ssl.trustStorePassword", "com.ibm.ssl.trustStoreType", "com.ibm.ssl.trustStoreProvider", "com.ibm.ssl.trustManager", "com.ibm.ssl.enabledCipherSuites", "com.ibm.ssl.clientAuthentication"};
        java.util.Properties properties = new java.util.Properties();
        properties.setProperty("com.ibm.ssl.keyStoreType", iotProperties.getSecure().getKeystoreType());
        properties.setProperty("com.ibm.ssl.keyStore", CertUtils.getClientKeyStore(iotProperties.getSecure().getKeystoreFile()));
        properties.setProperty("com.ibm.ssl.keyStorePassword", iotProperties.getSecure().getKeystorePassword());
        properties.setProperty("com.ibm.ssl.trustStore", CertUtils.getClientTrustStore(iotProperties.getSecure().getTruststoreFile()));
        properties.setProperty("com.ibm.ssl.trustStorePassword", iotProperties.getSecure().getTruststorePassword());
        return properties;
    }

    private byte[] offlineMessage() {

        return (iotProperties.getMqtt().getClientId() + "-OFFLINE").getBytes(StandardCharsets.UTF_8);
    }

}
