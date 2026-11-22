package io.github.parkwoong.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;
class ProxyControllerTest {
 @Test void forwardsBytesQueryHeadersAndErrorStatus() throws Exception {
  var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
  server.createContext("/sample", exchange -> {
   byte[] response = (exchange.getRequestMethod()+" "+exchange.getRequestURI()+" "+new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
   exchange.getResponseHeaders().add("Set-Cookie", "a=1"); exchange.getResponseHeaders().add("Set-Cookie", "b=2");
   exchange.sendResponseHeaders(422, response.length); exchange.getResponseBody().write(response); exchange.close();
  });
  server.start();
  try {
   var proxy = new ProxyController(new RestTemplateBuilder(), URI.create("http://127.0.0.1:"+server.getAddress().getPort()));
   var request = new MockHttpServletRequest("POST", "/sample"); request.setQueryString("q=a%20b");
   var response = proxy.forward(HttpMethod.POST, request, "예제".getBytes(StandardCharsets.UTF_8));
   assertEquals(422, response.getStatusCode().value());
   assertEquals("POST /sample?q=a%20b 예제", new String(response.getBody(), StandardCharsets.UTF_8));
   assertEquals(2, response.getHeaders().get("Set-Cookie").size());
  } finally { server.stop(0); }
 }
 @Test void removesHopHeadersAndPreservesRepeatedHeaders() {
  var input = new HttpHeaders(); input.add("Connection", "X-Internal"); input.add("X-Internal", "secret"); input.add("Host", "wrong"); input.add("X-Trace", "one"); input.add("X-Trace", "two");
  var output = ProxyController.endToEnd(input);
  assertFalse(output.containsKey("X-Internal")); assertFalse(output.containsKey("Host")); assertEquals(2, output.get("X-Trace").size());
 }
}
