package co.edu.escuelaing.httpserver;

import org.junit.jupiter.api.Test;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestServiceTest {

    private final RestService restService = new RestService();

    @Test
    public void testResponseGET() throws Exception {
        URI uri = new URI("http://localhost:35000/app?name=Laura");
        String response = restService.responseGET(uri);

        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("\"name\":\"Laura\""));
        assertTrue(response.contains("\"message\":\"You made a GET request\""));
    }

    @Test
    public void testResponsePOST() throws Exception {
        URI uri = new URI("http://localhost:35000/app/hello?name=Laura");
        String response = restService.responsePOST(uri);

        assertTrue(response.contains("HTTP/1.1 201 OK"));
        assertTrue(response.contains("\"name\":\"Laura\""));
        assertTrue(response.contains("\"message\":\"You made a POST request\""));
    }
}
