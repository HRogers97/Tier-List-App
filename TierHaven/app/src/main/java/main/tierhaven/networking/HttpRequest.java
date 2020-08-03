package main.tierhaven.networking;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HttpRequest {

    public enum Method {
        GET, POST, PUT, PATCH, DELETE
    }

    private String url;
    private Method method;
    private String requestBody;
    private String contentType;

    public HttpRequest(String url, Method method) {
        this.url = url;
        this.method = method;
    }

    public void setRequestBody(String contentType, String requestBody) {
        if(method == Method.GET || method == Method.DELETE)
            throw new IllegalArgumentException("No request body for allowed for request method:" + method);
        this.contentType = contentType;
        this.requestBody = requestBody;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpResponse perform() throws IOException {

        // phase 1: configure the request
        HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
        conn.setRequestMethod(method.toString());

        // phase 2: Request has a body - stream to server
        if(requestBody != null) {
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);
            PrintWriter writer = new PrintWriter(conn.getOutputStream());
            writer.print(requestBody);
            writer.close();
        }

        // phase 3: Response - get comments from the server
        HttpResponse response = new HttpResponse();

        response.setResponseCode(conn.getResponseCode());

        Map<String, List<String>> headers = conn.getHeaderFields();
        response.setHeaders(headers);
        int contentLength = 100;
        if(headers.containsKey("Content-Length"))
            contentLength = Integer.parseInt(headers.get("Content-Length").get(0));

        try {
            StringBuilder builder = new StringBuilder(contentLength);
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                builder.append(scanner.nextLine() + '\n');
            }
            scanner.close();

            response.setResponseBody(builder.toString());
        }
        catch (FileNotFoundException e){

        }
        return response;
    }

}
