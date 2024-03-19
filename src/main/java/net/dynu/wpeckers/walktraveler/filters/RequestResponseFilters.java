package net.dynu.wpeckers.walktraveler.filters;

import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Component
@Order(1)
public class RequestResponseFilters implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseFilters.class);

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        CustomContentCachingRequestWrapper request1 = new CustomContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper response1 = new ContentCachingResponseWrapper(res);
        long startTime = System.currentTimeMillis();
        logRequest(request1);
        try {
            chain.doFilter(request1, response1);
            logResponse(request1, response1, startTime);
            response1.copyBodyToResponse();
        } catch (Exception ex) {
            if (ex.getMessage().contains("Session not found with")) {
                response1.sendError(401, "Session ID not received in header!");
                log.warn("Session timeout:  {}", ex.getMessage());
            } else {
                log.error("Unexpected error occurred : " + ex.getMessage(), ex);
            }
        }
    }

    private void logRequest(CustomContentCachingRequestWrapper request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod() + " " + request.getRequestURI());
        String queryString = request.getQueryString();
        if (queryString != null) {
            sb.append(" " + queryString);
        }
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                Collections.list(request.getHeaders(headerName)).forEach(headerValue ->
                        sb.append(" " + headerName + "=" + headerValue)));
        log.info("REQUEST  INFO {}", sb.toString());
        request.getParameterMap(); // This must be called to get cache working for request
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, request.getContentType(), request.getCharacterEncoding(), "REQUEST  DATA");
        }
    }

    private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper response, long startTime) {
        int status = response.getStatus();
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod() + " " + request.getRequestURI() + " ");
        sb.append(status + " " + HttpStatus.valueOf(status).getReasonPhrase());
        sb.append(" (total time " + (System.currentTimeMillis()-startTime) + "ms)");
        response.getHeaderNames().forEach(headerName ->
                response.getHeaders(headerName).forEach(headerValue ->
                        sb.append(headerName + "=" + headerValue + ",")));
        log.info("RESPONSE INFO {}", sb.toString());
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, response.getContentType(), response.getCharacterEncoding(), "RESPONSE DATA");
        }
    }

    private void logContent(byte[] content, String contentType, String contentEncoding, String prefix) {
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        if (visible) {
            try {
                String contentString = new String(content, contentEncoding);
                Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> {
                    log.info("{} {}", prefix, line);
                });
            } catch (UnsupportedEncodingException e) {
                log.info("{} [{} bytes content]", prefix, content.length);
            }
        } else {
            log.info("{} [{} bytes content]", prefix, content.length);
        }
    }

}
