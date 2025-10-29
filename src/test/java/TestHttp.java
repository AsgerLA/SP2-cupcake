import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

import app.Path;
import app.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import static org.junit.jupiter.api.Assertions.*;

public class TestHttp {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 7979;
    private static final String ROOT_URI = "http://"+HOST+":"+PORT;


    @BeforeAll
    public static void beforeAll()
    {
        TestDB.beforeAll();
        Server.start(HOST, PORT);
    }

    @AfterAll
    public static void afterAll()
    {
        Server.stop();
    }

    @BeforeEach
    public void beforeEach()
    {
        TestDB.setUp();
    }

    @Test
    public void GET_index()
            throws Exception
    {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        HttpRequest request;

        request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URI+Path.Web.INDEX))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void user()
            throws Exception
    {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        HttpRequest request;

        String form = "email=user&password=password";

        request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URI+Path.Web.BASKET))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URI+Path.Web.REGISTER))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URI+Path.Web.LOGIN))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Map<String, List<String>> map = response.headers().map();
        String setCookie = map.get("set-cookie").get(0);

        request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URI+Path.Web.BASKET))
                .header("Cookie", setCookie)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

}
