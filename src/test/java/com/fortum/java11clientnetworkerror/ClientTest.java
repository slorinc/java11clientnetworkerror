package com.fortum.java11clientnetworkerror;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.SSLParameters;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClientTest {

    private MockWebServer server = new MockWebServer();

    @Test
    public void clientTestHappyPath() {
        server.enqueue(new MockResponse());

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(1000))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .proxy(ProxySelector.getDefault())
                .version(HttpClient.Version.HTTP_2)
                .sslParameters(new SSLParameters())
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://127.0.0.1:%d/foo", server.getPort())))
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            Assertions.assertThat(response.statusCode()).isEqualTo(200);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void clientTestWithNetworkError() {
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(1000))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .proxy(ProxySelector.getDefault())
                .version(HttpClient.Version.HTTP_2)
                .sslParameters(new SSLParameters())
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://127.0.0.1:%d/foo", server.getPort())))
                .GET()
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception e) {
            Assert.fail();
        }

    }
}
