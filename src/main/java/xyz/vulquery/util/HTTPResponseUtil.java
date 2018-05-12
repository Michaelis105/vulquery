package xyz.vulquery.util;

import com.google.gson.Gson;

public class HTTPResponseUtil {

    public static String createJSONMessage(String message) {
        HTTPResponse response = new HTTPResponse();
        Gson gson = new Gson();
        response.setMessage(message);
        if (StringUtils.isBlank(message)) {
            response.setMessage("Internal Server Error: Message is null or blank.");
        }

        return gson.toJson(response);
    }

}
