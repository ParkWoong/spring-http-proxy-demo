package io.github.parkwoong.demo.controller;

import io.github.parkwoong.demo.properties.GatewayProperties;
import io.github.parkwoong.demo.spec.util.HttpUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ProxyController {

    @Autowired private GatewayProperties gatewayProperties;

    @RequestMapping("/**")
    public ResponseEntity<String> toss(
            HttpMethod method,
            HttpServletRequest request,
            @RequestBody(required = false) String body)
            throws URISyntaxException, IOException {

        // 1. headers setting
        final HttpHeaders headers = HttpUtils.getHeaders(request);

        String forwardHost = gatewayProperties.getOrigin();

        final URI forwardUri = HttpUtils.getForwardUri(request, forwardHost);

        ResponseEntity<String> response = null;

        try {

            final ResponseEntity<String> result =
                    new RestTemplate()
                            .exchange(
                                    forwardUri,
                                    method,
                                    new HttpEntity<>(body, headers),
                                    String.class);

            response =
                    ResponseEntity.status(result.getStatusCode())
                            .contentType(result.getHeaders().getContentType())
                            .body(result.getBody());

        } catch (HttpStatusCodeException e) {

            response =
                    ResponseEntity.status(e.getRawStatusCode())
                            .contentType(e.getResponseHeaders().getContentType())
                            .body(e.getResponseBodyAsString());

        } catch (Exception e) {

            response = ResponseEntity.status(500).body("Proxy failed");
            e.printStackTrace();
        }
        return response;
    }
}
