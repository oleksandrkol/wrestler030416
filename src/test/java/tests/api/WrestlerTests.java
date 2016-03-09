package tests.api;

import common.FileReader;
import common.JsonParser;
import common.PropertiesLoader;
import common.RequestBuilder;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class WrestlerTests {

    private Properties properties = new PropertiesLoader().loadProperties();
    private String LOGIN_URL = properties.getProperty("LOGIN_URL");
    private String CREATE_URL = properties.getProperty("CREATE_URL");
    private String DELETE_URL = properties.getProperty("DELETE_URL");
    private String READ_URL = properties.getProperty("READ_URL");
    private String UPDATE_URL = properties.getProperty("UPDATE_URL");


    private RestTemplate restTemplate = new RestTemplate();
    private FileReader fileReader = new FileReader();
    private JsonParser jsonParser = new JsonParser();
    private long wrestlerId;
    private String cookies;

    @Before
    public void login () {
        String body = fileReader.fileToString("login.json");

        ResponseEntity<String> response = new RequestBuilder()
                .template(restTemplate)
                .post(LOGIN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .execute(String.class);

        cookies = response.getHeaders().getFirst("Set-Cookie");
    }

    @Test
    public void createWrestlerTest () throws ParseException {
        //given
        String body = fileReader.fileToString("create.json");

        //when
        ResponseEntity<String> response = new RequestBuilder()
                .template(restTemplate)
                .post(CREATE_URL)
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
        String URL = DELETE_URL + wrestlerId;
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
        String URL = READ_URL + wrestlerId;
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

        ResponseEntity<String> response = new RequestBuilder()
                .template(restTemplate)
                .post(UPDATE_URL)
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