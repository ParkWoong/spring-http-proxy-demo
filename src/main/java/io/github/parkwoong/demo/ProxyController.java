package io.github.parkwoong.demo;

import java.net.URI;
import java.util.*;
import java.time.Duration;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;
@RestController
public class ProxyController {
 private final RestTemplate client;
 private final URI upstream;
 public ProxyController(RestTemplateBuilder builder, @Value("${proxy.upstream:http://localhost:9090}") URI upstream) {
  if (!("http".equals(upstream.getScheme()) || "https".equals(upstream.getScheme())) || upstream.getHost() == null || upstream.getUserInfo() != null || upstream.getQuery() != null || upstream.getFragment() != null)
   throw new IllegalArgumentException("Invalid upstream");
  this.upstream = upstream;
  var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory() {
   @Override protected void prepareConnection(java.net.HttpURLConnection connection, String method) throws java.io.IOException {
    super.prepareConnection(connection, method); connection.setInstanceFollowRedirects(false);
   }
  };
  factory.setConnectTimeout(Duration.ofSeconds(3)); factory.setReadTimeout(Duration.ofSeconds(5));
  this.client = builder.requestFactory(() -> factory).build();
 }
 // shortcut: bodies are buffered in memory; use streaming and size limits before handling large traffic.
 @RequestMapping(value="/**", method={RequestMethod.GET, RequestMethod.HEAD, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
 public ResponseEntity<byte[]> forward(HttpMethod method, HttpServletRequest request, @RequestBody(required=false) byte[] body) {
  var headers = new HttpHeaders();
  Collections.list(request.getHeaderNames()).forEach(name -> headers.put(name, Collections.list(request.getHeaders(name))));
  var target = UriComponentsBuilder.fromUri(upstream).path(request.getRequestURI()).query(request.getQueryString()).build(true).toUri();
  try {
   var response = client.exchange(target, method, new HttpEntity<>(body, endToEnd(headers)), byte[].class);
   return new ResponseEntity<>(response.getBody(), endToEnd(response.getHeaders()), response.getStatusCode());
  } catch (HttpStatusCodeException failure) {
   return new ResponseEntity<>(failure.getResponseBodyAsByteArray(), endToEnd(failure.getResponseHeaders()), failure.getStatusCode());
  } catch (ResourceAccessException failure) {
   return ResponseEntity.status(502).build();
  }
 }
 static HttpHeaders endToEnd(HttpHeaders source) {
  var result = new HttpHeaders();
  if (source == null) return result;
  var excluded = new HashSet<>(Set.of("connection", "keep-alive", "proxy-authenticate", "proxy-authorization", "te", "trailer", "transfer-encoding", "upgrade", "host", "content-length"));
  source.getOrEmpty("Connection").forEach(value -> Arrays.stream(value.split(",")).forEach(name -> excluded.add(name.trim().toLowerCase(Locale.ROOT))));
  source.forEach((name, values) -> { if (!excluded.contains(name.toLowerCase(Locale.ROOT))) result.put(name, new ArrayList<>(values)); });
  return result;
 }
}
