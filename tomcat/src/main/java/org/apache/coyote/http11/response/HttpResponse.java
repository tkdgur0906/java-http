package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaderNames;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private String version;
    private HttpStatus httpStatus;
    private Map<String, Object> headers;
    private String body;

    public HttpResponse() {
        headers = new HashMap<>();
    }

    public void setResponseFromRequest(HttpRequest request) throws URISyntaxException, IOException {
        String responseBody = new String(request.toHttpResponseBody());
        version = request.getVersion();
        headers.put(HttpHeaderNames.CONTENT_TYPE, request.getContentType());
        headers.put(HttpHeaderNames.CONTENT_LENGTH, responseBody.getBytes().length);
        body = responseBody;
    }

    public void addHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addHeader(String key, Object value) {
        headers.put(key, value);
    }

    public void send(OutputStream outputStream) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(version).append(" ").append(httpStatus.getCode())
                    .append(" ").append(httpStatus.getMessage()).append(" \r\n");
            for (Entry<String, Object> entry : headers.entrySet()) {
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
                sb.append(" \r\n");
            }
            sb.append("\r\n");
            sb.append(body);
            outputStream.write(sb.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHttpStatus() {
        return httpStatus.getCode();
    }
}
