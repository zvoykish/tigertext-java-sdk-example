package com.tigertext.example;

import com.tigertext.sdk.*;
import com.tigertext.sdk.authorization.ApiCredentials;
import com.tigertext.sdk.entities.Message;
import com.tigertext.sdk.entities.User;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static com.tigertext.sdk.TigerTextConfiguration.getApiKey;
import static com.tigertext.sdk.TigerTextConfiguration.getApiSecret;

/**
 * Created by Zvika on 1/27/15.
 */
public class TigerTextSdkExample {
    private static Logger log = Logger.getLogger(TigerTextSdkExample.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        // Build a new SDK using the API key/secret in tigertext.properties
        ApiCredentials credentials = new ApiCredentials(getApiKey(), getApiSecret());
        TigerTextSdk sdk = TigerTextSdkBuilder.build(credentials);
        EventSdk eventSdk = sdk.events();
        log.info("Connecting to TigerText SDK...");
        eventSdk.connect((event, handlerSdk) -> {
                if ("tigertext:iq:message".equals(event.getType())) {
                log.info("Received message event (" + event.getId() + "): " + event.getPayload());
            } else {
                log.info("Ignoring event (" + event.getId() + "): " + event.getType() + " with payload: " + event.getPayload());
            }
        });

        // Example of fetching a user based on identifier
        UserSdk userSdk = sdk.users();
        User user = userSdk.get("recipient-identifier");
        log.info("Here's the user: " + user);

        // Example of sending out a simple text message to a recipient
        MessageSdk messageSdk = sdk.messages();
        String messageId = messageSdk.send("Message body", "another-user-identifier");
        log.info("New message ID: " + messageId);

        Message message = messageSdk.get(messageId);
        log.info("New message properties: " + message);

        log.info("Waiting 30 seconds before disconnecting...");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Disconnecting from TigerText SDK...");
        eventSdk.disconnect();
    }
}
