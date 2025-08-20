package co.edu.escuelaing.httpserver;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpServerTest {

    private Thread serverThread;

    @BeforeAll
    public void startServer() {
        serverThread = new Thread(() -> {
            try {
                HttpServer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldReturnHtmlFile() throws Exception {
        URL url = new URL("http://localhost:35000/index.html");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        connection.disconnect();
    }

    @Test
    public void shouldReturnRestGet() throws Exception {
        URL url = new URL("http://localhost:35000/app/hello?name=Laura");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        connection.disconnect();
    }

    @Test
    public void shouldReturnRestPost() throws Exception {
        URL url = new URL("http://localhost:35000/app/hellopost?name=Laura");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        int responseCode = connection.getResponseCode();
        assertEquals(201, responseCode);

        connection.disconnect();
    }

    @AfterAll
    public void stopServer() throws Exception {
        serverThread.interrupt();
    }
}