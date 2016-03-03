package common;


import domain.Status;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonToObject {

    public Status getStatus (String jsonString) throws ParseException {
        JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
        Status status = new Status();
        status.setResult((Boolean) json.get("result"));
        status.setId((Long) json.get("id"));
        return status;
    }
}
