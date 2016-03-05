package tests.api;

import common.FileReader;
import common.JsonParser;
import common.RequestBuilder;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class WrestlerTests {

    private RestTemplate restTemplate = new RestTemplate();
    private FileReader fileReader = new FileReader();
    private JsonParser jsonParser = new JsonParser();
    private long wrestlerId;
    private String cookies;

    @Before
    public void login () {
        String URL = "http://streamtv.net.ua/base/php/login.php";
        String body = fileReader.fileToString("login.json");

        ResponseEntity<String> response = new RequestBuilder()
                .template(restTemplate)
                .post(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .execute(String.class);

        cookies = response.getHeaders().getFirst("Set-Cookie");
    }

    @Test
    public void createWrestlerTest () throws ParseException {
        //given
        String URL = "http://streamtv.net.ua/base/php/wrestler/create.php";
        String body = fileReader.fileToString("create.json");

        //when
        ResponseEntity<String> response = new RequestBuilder()
                .template(restTemplate)
                .post(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.COOKIE, cookies)
                .body(body)
                .execute(String.class);

        //than
        String responseBody = response.getBody();
        assertEquals("Incorrect response status code", HttpStatus.OK, response.getStatusCode());
        assertTrue("Unexpected result: " + responseBody, responseBody.contains("\"result\":true"));

        wrestlerId = jsonParser.getId(responseBody);
    }

    @Test
    public void deleteWrestlerTest () throws ParseException {
        //given
        createWrestlerTest();

        //when
        String URL = "http://streamtv.net.ua/base/php/wrestler/delete.php?id=" + wrestlerId;
        ResponseEntity<String> response = new RequestBuilder()
                .template(restTemplate)
                .get(URL)
                .header(HttpHeaders.COOKIE, cookies)
                .execute(String.class);

        //than
        String responseBody = response.getBody();
        assertEquals("Incorrect response status code", HttpStatus.OK, response.getStatusCode());
        assertTrue("Unexpected result: " + responseBody, responseBody.contains("\"result\":true"));
    }

    @Test
    public void readWrestlerTest () throws ParseException {
        //given
        createWrestlerTest();

        //when
        String URL = "http://streamtv.net.ua/base/php/wrestler/read.php?id=" + wrestlerId;
        ResponseEntity<String> response = new RequestBuilder()
                .template(restTemplate)
                .get(URL)
                .header(HttpHeaders.COOKIE, cookies)
                .execute(String.class);

        //than
        String template = fileReader.fileToString("read.template.json");
        String expectedBody = template.replaceAll("ID_PLACEHOLDER", String.valueOf(wrestlerId));

        assertEquals("Incorrect response status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Incorrect response body", expectedBody, response.getBody());
    }

    @Test
    public void updateWrestlerTest () throws ParseException {
        //given
        createWrestlerTest();

        //when
        String template = fileReader.fileToString("update.template.json");
        String body = template.replaceAll("ID_PLACEHOLDER", String.valueOf(wrestlerId));

        String URL = "http://streamtv.net.ua/base/php/wrestler/update.php";
        ResponseEntity<String> response = new RequestBuilder()
                .template(restTemplate)
                .post(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.COOKIE, cookies)
                .body(body)
                .execute(String.class);

        //than
        String responseBody = response.getBody();
        assertEquals("Incorrect response status code", HttpStatus.OK, response.getStatusCode());
        assertTrue("Unexpected result: " + responseBody, responseBody.contains("\"result\":true"));
    }
}