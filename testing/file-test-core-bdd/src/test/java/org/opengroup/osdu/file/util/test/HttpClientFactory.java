package org.opengroup.osdu.file.util.test;

public class HttpClientFactory {
    private static HttpClient httpClient = null;

    public static HttpClient getInstance() {
        if (httpClient == null) {
            httpClient = new RestAssuredClient();
        }
        return httpClient;
    }
}
