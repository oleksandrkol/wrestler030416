package common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class FileReader {

    public String fileToString (String file) {
        Charset charset = StandardCharsets.UTF_8;
        try {
            URL resource = getClass().getClassLoader().getResource(file);
            assert resource != null;
            Path path = Paths.get(resource.toURI());
            return new String(Files.readAllBytes(path), charset);
        }
        catch (IOException | URISyntaxException e){
            throw new IllegalStateException("IOException | URISyntaxException occurs while file to used", e);
        }
    }
}
