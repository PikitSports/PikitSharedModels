package com.pikit.shared.dynamodb;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;


public class LocalDynamoDB {
    private DynamoDBProxyServer server;
    private int port;

    public void start() {
        System.setProperty("sqlite4java.library.path", "native-libs");
        port = getFreePort();
        String portString = Integer.toString(port);

        try {
            server = createServer(portString);
            server.start();
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    public DynamoDbClient createClient() {
        String endpoint = String.format("http://localhost:%d", port);
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy-key", "dummy-secret")))
                .build();
    }

    private void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    private DynamoDBProxyServer createServer(String portString) throws Exception {
        return ServerRunner.createServerFromCommandLineArgs(
                new String[]{
                        "-inMemory",
                        "-port", portString
                });
    }

    private int getFreePort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            int port  = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException e) {
            throw propagate(e);
        }
    }

    private static RuntimeException propagate(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException(e);
    }
}
