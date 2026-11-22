package io.github.parkwoong.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;

import io.github.parkwoong.demo.controller.ProxyController;
import io.github.parkwoong.demo.properties.GatewayProperties;
import io.github.parkwoong.demo.spec.util.HttpUtils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

class ProxyControllerTest {
    @Test
    void extractedForwardingPreservesOriginalResponseContract() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/sample",
                exchange -> {
                    byte[] output =
                            (exchange.getRequestMethod()
                                            + " "
                                            + exchange.getRequestURI()
                                            + " "
                                            + new String(
                                                    exchange.getRequestBody().readAllBytes(),
                                                    StandardCharsets.UTF_8))
                                    .getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/plain;charset=UTF-8");
                    exchange.sendResponseHeaders(422, output.length);
                    exchange.getResponseBody().write(output);
                    exchange.close();
                });
        server.start();
        try {
            var controller = new ProxyController();
            var settings = new GatewayProperties();
            settings.setOrigin("http://127.0.0.1:" + server.getAddress().getPort());
            ReflectionTestUtils.setField(controller, "gatewayProperties", settings);
            var request = new MockHttpServletRequest("POST", "/sample");
            request.setQueryString("q=a%20b");
            request.addHeader("Content-Type", "text/plain;charset=UTF-8");
            var result = controller.toss(HttpMethod.POST, request, "sample");
            assertEquals(422, result.getStatusCodeValue());
            assertEquals("POST /sample?q=a%20b sample", result.getBody());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void originalHttpHelpers() {
        var request = new MockHttpServletRequest("GET", "/sample");
        request.addHeader("Authorization", "Bearer sample-token");
        assertEquals("sample-token", HttpUtils.getAccessToken(HttpUtils.getHeaders(request)));
        assertNull(HttpUtils.getForwardUri(null, "http://localhost:9090"));
    }
}
