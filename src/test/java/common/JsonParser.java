package common;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonParser {

    public long getId(String jsonString) throws ParseException {
        JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
        long id = (long) json.get("id");
        return id;
    }
}
