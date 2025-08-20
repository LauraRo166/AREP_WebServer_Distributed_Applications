# Non-Concurrent Web Server

This project is a implementation of an HTTP server in **Java**.  
It demonstrates how to handle HTTP requests (GET/POST), serve static resources, and expose a simple REST service without relying on external frameworks.

## 📦 Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/LauraRo166/AREP_WebServer_Distributed_Applications.git
   cd AREP_WebServer_Distributed_Applications
   ```
   
2. Make sure you have Java 17+ and Maven installed:

   ```bash
   java -version
   mvn -version
   ```
3. Build the project:

   ```bash
   mvn clean package
   ```
4. Should look something like this:
    
{Imagen de build}

## ▶️ How to Run

1. Start the server by running:

   ```bash
   mvn exec:java -Dexec.mainClass="co.edu.escuelaing.httpserver.HttpServer"
   ```

2. The server will listen on port 35000, you can open your browser and navigate to:
   ```
   http://localhost:35000/
   ```
   
3. You can try the index file with:
   ```
   http://localhost:35000/index.html
   ```
   And you can try other resources stored in the resources folder, for example:
   ```
   perrito.jpg
   ```
   With:
   ```
   http://localhost:35000/perrito.jpg
   ```

## 🏗️ Architecture

{Imagen de arquitectura}

The architecture of this prototype follows a client-server model where the main components are:

**1. Client**
- The client (usually a web browser) initiates an HTTP request (GET or POST).
- This request can be directed either to a REST endpoint or to request a static file.

**2. Back-End (Server)**
The back-end is composed of two main modules:

**a. HttpServer**
- The entry point of the server.
- Listens for incoming client connections using a ServerSocket.
- Receives HTTP requests and is responsible for routing them to the appropriate service.

**b. RestServices**
- Manages dynamic responses for REST requests.
- Processes queries from the URI, builds JSON-formatted HTTP responses, and returns them to the client.
- Supports GET and POST requests with appropriate status codes (200 OK for GET, 201 Created for POST).

**3. Request Flow**
- The client sends a request to the server.
- The HttpServer receives the request.
- If the request corresponds to a REST endpoint, it is routed to the RestServices module.
- RestServices generates a JSON response and sends it back through the HttpServer to the client.

## ✅ Evaluation (Tests)
Unit tests were created using JUnit to validate the server’s functionality:

**- RestServiceTest**

- Verifies that responseGET() returns a 200 OK with the correct JSON message.
- Verifies that responsePOST() returns a 201 Created with the correct JSON message.

**- HttpServerTest**

- Tests that the server can serve static files (e.g., index.html).
- Tests that a GET request to /app/hello?name=Laura returns 200 OK.
- Tests that a POST request to /app/hello?name=Laura returns 201 Created.
- Tests that a non-existent file returns a 404 Not Found.

To run the tests:

   ```bash
   mvn test
   ```

Should look something like this:


## 👩‍💻 Author

Laura Daniela Rodríguez