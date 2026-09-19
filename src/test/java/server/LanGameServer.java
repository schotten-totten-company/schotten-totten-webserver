package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.management.RuntimeErrorException;

import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class LanGameServer {

    private final Map<String, Game> gameMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpServer server;

    public LanGameServer(int port) {
        try {
			this.server = HttpServer.create(new InetSocketAddress(port), 0);
			this.server.createContext("/", new MainHandler());
			// Utilisation d'un pool de threads par défaut pour gérer plusieurs requêtes en parallèle
			this.server.setExecutor(Executors.newCachedThreadPool());
		} catch (IOException e) {
			throw new RuntimeErrorException(new Error(e), e.getMessage());
		}
    }

    public void start() {
        this.server.start();
    }

    public void stop(int delayInSeconds) {
        this.server.stop(delayInSeconds);
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object o) throws IOException {
        try {
            final String jsonObject = objectMapper.writeValueAsString(o);
            final byte[] responseBytes = jsonObject.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (final OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } catch (final JsonProcessingException e) {
            sendTextResponse(exchange, 500, e.getMessage());
        }
    }

    private void sendTextResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        final byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (final OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private Map<String, String> parseQueryParams(URI uri) {
        final Map<String, String> queryParams = new HashMap<>();
        final String query = uri.getQuery();
        if (query != null && !query.isEmpty()) {
            for (String param : query.split("&")) {
                final String[] entry = param.split("=");
                if (entry.length > 1) {
                    queryParams.put(entry[0], entry[1]);
                } else if (entry.length == 1) {
                    queryParams.put(entry[0], "");
                }
            }
        }
        return queryParams;
    }

    private class MainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            final String method = exchange.getRequestMethod();
            final String path = exchange.getRequestURI().getPath();
            final Map<String, String> params = parseQueryParams(exchange.getRequestURI());

            if ("GET".equalsIgnoreCase(method)) {
                switch (path) {
                    case "/ping":
                        final String message = new Date().toString() + " - it is time to SCHOTTEN !!!!";
                        sendJsonResponse(exchange, 200, message);
                        break;

                    case "/createGame":
                        final String gamenameToCreate = params.get("gamename");
                        if (gamenameToCreate == null || gameMap.containsKey(gamenameToCreate)) {
                            sendJsonResponse(exchange, 200, Boolean.FALSE);
                        } else {
                            try {
                                final Game game = new Game("Player 1", "Player 2");
                                gameMap.put(gamenameToCreate, game);
                                sendJsonResponse(exchange, 200, Boolean.TRUE);
                            } catch (final GameCreationException e) {
                                sendJsonResponse(exchange, 200, Boolean.FALSE);
                            }
                        }
                        break;

                    case "/getGame":
                        sendJsonResponse(exchange, 200, gameMap.get(params.get("gamename")));
                        break;

                    case "/listGames":
                        sendJsonResponse(exchange, 200, new ArrayList<>(gameMap.keySet()));
                        break;

                    case "/deleteGame":
                        sendJsonResponse(exchange, 200, gameMap.remove(params.get("gamename")) != null);
                        break;

                    case "/getPlayingPlayer":
                        Game game = gameMap.get(params.get("gamename"));
                        sendJsonResponse(exchange, 200, game != null ? game.getPlayingPlayer() : null);
                        break;

                    default:
                        sendTextResponse(exchange, 405, path + " not supported.");
                        break;
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                switch (path) {
                    case "/updateGame":
                        try (InputStream is = exchange.getRequestBody()) {
                            final String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                            final String gamename = params.get("gamename");
                            final Game game = objectMapper.readValue(body, Game.class);

                            if (gamename == null || !gameMap.containsKey(gamename)) {
                                sendJsonResponse(exchange, 200, Boolean.FALSE);
                            } else {
                                gameMap.put(gamename, game);
                                sendJsonResponse(exchange, 200, Boolean.TRUE);
                            }
                        } catch (final Exception e) {
                            sendTextResponse(exchange, 500, e.getMessage());
                        }
                        break;

                    default:
                        sendTextResponse(exchange, 405, path + " not supported.");
                        break;
                }
            } else {
                sendTextResponse(exchange, 405, method + " not supported.");
            }
        }
    }
}