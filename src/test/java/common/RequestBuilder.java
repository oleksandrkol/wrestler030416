//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package common;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class RequestBuilder {
    private RestTemplate restTemplate;
    private MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap();
    private MultiValueMap<String, String> headersMap = new HttpHeaders();
    private Object objectBody;
    private List<HttpCookie> cookies;
    private UriComponentsBuilder uriComponentsBuilder;
    private HttpMethod method;
    private String fileName;
    private boolean uploadAndSend;

    public RequestBuilder() {
    }

    public RequestBuilder template(RestTemplate t) {
        this.restTemplate = t;
        return this;
    }

    public RequestBuilder get(URI val) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromUri(val);
        this.method = HttpMethod.GET;
        return this;
    }

    public RequestBuilder get(String val) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(val);
        this.method = HttpMethod.GET;
        return this;
    }

    public RequestBuilder queryParam(String name, String val) {
        Assert.isTrue(this.method == HttpMethod.GET, "Available  only for GET");
        this.uriComponentsBuilder.queryParam(name, new Object[]{val});
        return this;
    }

    public RequestBuilder post(URI val) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromUri(val);
        this.method = HttpMethod.POST;
        return this;
    }

    public RequestBuilder post(String val) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(val);
        this.method = HttpMethod.POST;
        return this;
    }

    public RequestBuilder put(URI val) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromUri(val);
        this.method = HttpMethod.PUT;
        return this;
    }

    public RequestBuilder put(String val) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(val);
        this.method = HttpMethod.PUT;
        return this;
    }

    public RequestBuilder delete(URI val) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromUri(val);
        this.method = HttpMethod.DELETE;
        return this;
    }

    public RequestBuilder delete(String val) {
        this.uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(val);
        this.method = HttpMethod.DELETE;
        return this;
    }

    public RequestBuilder header(String name, String... vals) {
        String[] arr$ = vals;
        int len$ = vals.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String val = arr$[i$];
            this.headersMap.add(name, val);
        }

        return this;
    }

    public RequestBuilder body(String val, String... vals) {
        if(this.bodyMap == null) {
            this.bodyMap = new LinkedMultiValueMap();
        }

        String[] arr$ = vals;
        int len$ = vals.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            this.bodyMap.add(val, s);
        }

        return this;
    }

    public RequestBuilder body(String val) {
        this.objectBody = val;
        return this;
    }

    public RequestBuilder objectBody(Object val) {
        this.objectBody = val;
        return this;
    }

    public RequestBuilder body(MultiValueMap<String, ?> val) {
        this.bodyMap = (MultiValueMap<String, Object>) val;
        return this;
    }

    public RequestBuilder cookies(List<HttpCookie> val) {
        this.cookies = val;
        return this;
    }

    public RequestBuilder cookie(String key, String val) {
        if(this.cookies == null) {
            this.cookies = new ArrayList();
        }

        this.cookies.add(new HttpCookie(key, val));
        return this;
    }

    public RequestBuilder contentType(MediaType... val) {
        MediaType[] arr$ = val;
        int len$ = val.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            MediaType mediaType = arr$[i$];
            this.header("Accept", new String[]{mediaType.toString()});
        }

        return this;
    }

    public RequestEntity<?> build() {
        BodyBuilder requestBuilder = RequestEntity.method(this.method, this.uriComponentsBuilder.build().toUri());
        Iterator res;
        if(this.cookies != null) {
            res = this.cookies.iterator();

            while(res.hasNext()) {
                HttpCookie entry = (HttpCookie)res.next();
                requestBuilder.header("Cookie", new String[]{entry.toString()});
            }
        }

        if(this.headersMap != null) {
            res = this.headersMap.entrySet().iterator();

            while(res.hasNext()) {
                Entry entry1 = (Entry)res.next();
                Iterator i$ = ((List)entry1.getValue()).iterator();

                while(i$.hasNext()) {
                    String s = (String)i$.next();
                    requestBuilder.header((String)entry1.getKey(), new String[]{s});
                }
            }
        }

        if(this.fileName != null) {
            requestBuilder.contentType(MediaType.MULTIPART_FORM_DATA);
            FileSystemResource res1 = new FileSystemResource(this.fileName);
            this.bodyMap.add("file", res1);
            this.bodyMap.add("fileName", res1.getFilename());
            if(this.uploadAndSend) {
                this.bodyMap.add("uploadAndSend", Boolean.valueOf(true));
            } else {
                this.bodyMap.add("uploadAndSend", Boolean.valueOf(false));
            }
        }

        RequestEntity entity;
        if(this.objectBody != null) {
            entity = requestBuilder.body(this.objectBody);
        } else {
            entity = requestBuilder.body(this.bodyMap);
        }

        return entity;
    }

    public RequestBuilder file(String val) {
        this.fileName = val;
        return this;
    }

    public RequestBuilder uploadAndSend(boolean val) {
        this.uploadAndSend = val;
        return this;
    }

    public <T> ResponseEntity<T> execute(Class<T> clazz) {
        Assert.notNull(this.restTemplate, "Template not set");
        ResponseEntity result = this.restTemplate.exchange(this.build(), clazz);
        return result;
    }

    public <T> ResponseEntity<T> failsafeExecute(Class<T> clazz) {
        Assert.notNull(this.restTemplate, "Template not set");
        ResponseEntity result = null;

        try {
            result = this.restTemplate.exchange(this.build(), clazz);
        } catch (RestClientException var5) {
            ResponseEntity stringResponseEntity = this.executeForString();
            Assert.state(false, "Unable to parse response with " + clazz.getClass().getName() + "\nCaused by:\n" + var5.getCause() + "\nStacktrace:\n" + var5.getStackTrace() + "\nURL:\n" + this.uriComponentsBuilder.build().toUriString() + "\nResponse body:\n" + (String)stringResponseEntity.getBody() + "\nResponse code:\n" + stringResponseEntity.getStatusCode());
        }

        return result;
    }

    public ResponseEntity<String> executeForString() {
        return this.execute(String.class);
    }
}
