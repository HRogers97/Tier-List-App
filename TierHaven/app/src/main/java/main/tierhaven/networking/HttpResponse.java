package main.tierhaven.networking;

import java.util.List;
import java.util.Map;

public class HttpResponse {

    private int responseCode;
    private Map<String, List<String>> headers;
    private String responseBody;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "responseCode=" + responseCode +
                ", headers=" + headers +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}
