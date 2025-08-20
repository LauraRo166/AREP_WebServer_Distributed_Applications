package co.edu.escuelaing.httpserver;

import java.net.*;
import java.io.*;
import java.nio.file.*;

/**
 * A simple HTTP server implementation that listens on port 35000 and can: Serve
 * static files (HTML, CSS, JS, images, etc.) from a predefined directory.
 * Handle basic REST endpoints using {@link RestService} for GET and POST
 * requests. Return 404 responses for resources that are not found.
 *
 * @author laura.rsanchez
 *
 */
public class HttpServer {

    /**
     * Root directory where static resources (HTML, CSS, JS, images, etc.) are
     * located.
     */
    private static final String WEB_ROOT = "src/main/java/co/edu/escuelaing/httpserver/resources";

    /**
     * Entry point of the HTTP server. Starts listening on port 35000 and
     * handles incoming requests in an infinite loop.
     *
     * @param args command-line arguments (not used).
     * @throws IOException if an I/O error occurs when opening the socket or
     * handling requests.
     * @throws URISyntaxException if a malformed URI is found in the request.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            return;
        }

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("New connection...");
                handleClientRequest(clientSocket);
            } catch (IOException e) {
                System.err.println("Accept failed.");
            }
        }
    }

    /**
     * Handles a client request by reading the HTTP request, extracting the
     * method and URI, and delegating the response to
     * {@link #handleMethod(String, URI, PrintWriter, Socket)}.
     *
     * @param clientSocket the connected client socket.
     * @throws URISyntaxException if the request URI is malformed.
     * @throws IOException if an I/O error occurs while reading the request or
     * writing the response.
     */
    private static void handleClientRequest(Socket clientSocket) throws URISyntaxException, IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        boolean isFirstLine = true;
        URI requestUri = null;
        String method = null;

        while ((inputLine = in.readLine()) != null) {
            if (isFirstLine) {
                String[] request = inputLine.split(" ");
                method = request[0];
                requestUri = new URI(request[1]);
                System.out.println("Method: " + method + " / Path: " + requestUri.getPath());
                isFirstLine = false;
            }
            if (!in.ready()) {
                break;
            }
        }

        if (requestUri != null) {
            handleMethod(method, requestUri, out, clientSocket);
        }

        out.close();
        in.close();
    }

    /**
     * Routes a request based on its HTTP method (GET or POST) and path.
     *
     * @param method HTTP method (e.g., GET, POST).
     * @param requestUri the requested resource URI.
     * @param out writer for sending text-based responses.
     * @param clientSocket socket used for binary responses (e.g., images).
     * @throws IOException if an I/O error occurs while handling the request.
     */
    private static void handleMethod(String method, URI requestUri, PrintWriter out, Socket clientSocket) throws IOException {
        String requestPath = requestUri.getPath();

        if ("GET".equals(method)) {
            String contentType = getContentType(requestPath);

            if (requestPath.startsWith("/app")) {
                String outputLine = new RestService().responseGET(requestUri);
                out.println(outputLine);

            } else if (contentType.startsWith("text") || contentType.equals("application/javascript")) {
                String outputLine = readTextFile(requestPath, contentType);
                out.println(outputLine);

            } else if (contentType.startsWith("image")) {
                BufferedOutputStream bodyOut = new BufferedOutputStream(clientSocket.getOutputStream());
                requestImg(requestPath, contentType, bodyOut, out);

            } else {
                serveStaticFile(requestUri, out, clientSocket);
            }

        } else if ("POST".equals(method)) {
            if (requestPath.startsWith("/app/hello")) {
                String outputLine = new RestService().responsePOST(requestUri);
                out.println(outputLine);
            }
        } else {
            serveStaticFile(requestUri, out, clientSocket);
        }
    }

    /**
     * Serves a static file (HTML, CSS, JS, etc.) from the {@link #WEB_ROOT}
     * directory.
     *
     * @param requestUri requested resource URI.
     * @param out writer for sending headers and text responses.
     * @param clientSocket client socket for binary data.
     * @throws IOException if an error occurs while reading the file or writing
     * the response.
     */
    private static void serveStaticFile(URI requestUri, PrintWriter out, Socket clientSocket) throws IOException {
        String path = requestUri.getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }
        File file = new File(WEB_ROOT + path);
        if (file.exists() && !file.isDirectory()) {
            String contentType = getContentType(path);
            byte[] fileData = Files.readAllBytes(file.toPath());

            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Content-Type: " + contentType + "\r\n");
            out.print("Content-Length: " + fileData.length + "\r\n");
            out.print("\r\n");
            out.flush();

            clientSocket.getOutputStream().write(fileData, 0, fileData.length);
            clientSocket.getOutputStream().flush();
        } else {
            out.println(notFound());
        }
    }

    /**
     * Serves an image file by writing binary data to the client.
     *
     * @param filePath path of the requested image.
     * @param contentType MIME type of the image.
     * @param bodyOut binary output stream for sending image bytes.
     * @param out writer for sending headers.
     * @throws IOException if an error occurs while reading the file or writing
     * the response.
     */
    private static void requestImg(String filePath, String contentType, BufferedOutputStream bodyOut, PrintWriter out) throws IOException {
        File file = new File(WEB_ROOT + filePath);
        if (file.exists()) {
            byte[] fileData = readFileData(file);

            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Content-Type: " + contentType + "\r\n");
            out.print("Content-Length: " + fileData.length + "\r\n");
            out.print("\r\n");
            out.flush();

            bodyOut.write(fileData, 0, fileData.length);
            bodyOut.flush();
        } else {
            out.println(notFound());
        }
    }

    /**
     * Reads and serves a text-based file (HTML, CSS, JS).
     *
     * @param filePath path of the file relative to {@link #WEB_ROOT}.
     * @param contentType MIME type of the file.
     * @return HTTP response string including headers and file content, or a 404
     * response if not found.
     * @throws IOException if an error occurs while reading the file.
     */
    private static String readTextFile(String filePath, String contentType) throws IOException {
        File file = new File(WEB_ROOT + filePath);
        if (file.exists()) {
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Content-Type: ").append(contentType).append("\r\n\r\n");
            response.append(Files.readString(file.toPath()));
            return response.toString();
        } else {
            return notFound();
        }
    }

    /**
     * Reads a file and returns its contents as a byte array.
     *
     * @param file the file to read.
     * @return byte array containing the file's data.
     * @throws IOException if an error occurs while reading the file.
     */
    private static byte[] readFileData(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Determines the MIME type of a file based on its extension.
     *
     * @param path file path or URI string.
     * @return MIME type string.
     */
    private static String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        if (path.endsWith(".png")) {
            return "image/png";
        }
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (path.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }

    /**
     * Generates a 404 Not Found HTTP response. If a custom 404.html file exists
     * in {@link #WEB_ROOT}, it will be returned as the response body.
     *
     * @return HTTP 404 response string.
     */
    public static String notFound() {
        File file = new File(WEB_ROOT + "/404.html");
        if (file.exists()) {
            try {
                String body = Files.readString(file.toPath());
                return "HTTP/1.1 404 Not Found\r\n"
                        + "Content-Type: text/html\r\n"
                        + "Content-Length: " + body.getBytes().length + "\r\n"
                        + "\r\n"
                        + body;
            } catch (IOException e) {
                return "HTTP/1.1 404 Not Found\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "404 - Not Found";
            }
        } else {
            return "HTTP/1.1 404 Not Found\r\n"
                    + "Content-Type: text/plain\r\n"
                    + "\r\n"
                    + "404 - Not Found";
        }
    }
}