package co.edu.escuelaing.httpserver;

import java.net.URI;

/**
 * A simple REST service handler that generates HTTP responses for GET and POST
 * requests in JSON format. This class is used by {@link HttpServer} to handle
 * REST endpoints under paths such as /app or/app/hello.
 *
 * Author: laura.rsanchez
 */
public class RestService {

    /**
     * Generates a JSON response for a POST request. Status code: 201 Created.
     *
     * @param requesturi the request URI containing the query string.
     * @return a full HTTP response string with JSON content.
     */
    String responsePOST(URI requesturi) {
        String response = "HTTP/1.1 201 OK\n\r"
                + "content-type: application/json\n\r"
                + "\n\r";

        String name = requesturi.getQuery().split("=")[1];
        response = response + "{\"name\":\"" + name + "\", \"message\":\"You made a POST request\"}";

        return response;
    }

    /**
     * Generates a JSON response for a GET request. Status code: 200 OK.
     *
     * @param requesturi the request URI containing the query string.
     * @return a full HTTP response string with JSON content.
     */
    String responseGET(URI requesturi) {
        String response = "HTTP/1.1 200 OK\n\r"
                + "content-type: application/json\n\r"
                + "\n\r";

        String name = requesturi.getQuery().split("=")[1];
        response = response + "{\"name\":\"" + name + "\", \"message\":\"You made a GET request\"}";

        return response;
    }
}