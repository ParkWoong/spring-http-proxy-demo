package io.github.parkwoong.demo.spec.util;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class HttpUtils {

    public static URI getForwardUri(HttpServletRequest request, String forwardHost) {

        if (request == null || forwardHost == null) {

            log.warn("invalid parameter: request={}, forwardHost={}", request, forwardHost);
            return null;
        }

        URI uri = null;
        try {

            uri =
                    UriComponentsBuilder.fromUri(new URI(forwardHost))
                            .path(request.getRequestURI())
                            .query(request.getQueryString())
                            .build(true)
                            .toUri();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return uri;
    }

    public static HttpHeaders getHeaders(HttpServletRequest request) {

        if (request == null) {

            log.warn("invalid parameter: request={}", request);
            return null;
        }

        final HttpHeaders headers = new HttpHeaders();
        final Enumeration<String> headerNames = request.getHeaderNames();

        String headerName = null;

        while (headerNames.hasMoreElements())
            headers.set((headerName = headerNames.nextElement()), request.getHeader(headerName));

        return headers;
    }

    public static String getAccessToken(HttpHeaders headers) {

        if (headers == null) {

            log.warn("invalid parameter: headers=null");
            return null;
        }

        String token = null;

        if (headers.containsKey("Authorization")) {

            final List<String> list = headers.get("Authorization");
            try {

                for (String str : list)
                    if (str.contains("Bearer")) {

                        token = str.replaceAll("Bearer ", "");
                        break;
                    }

            } catch (Exception e) {

                log.warn("invalid Authorization={}", list);
            }
        }

        return token;
    }
}
