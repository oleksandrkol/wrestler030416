package common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesLoader {

    public Properties loadProperties () {
        Properties prop = new Properties();
        InputStream in = PropertiesLoader.class.getClassLoader().getResourceAsStream("api.properties");
        try {
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  prop;
    }
}
