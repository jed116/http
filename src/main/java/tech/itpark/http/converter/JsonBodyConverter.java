package tech.itpark.http.converter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.nio.charset.StandardCharsets;

public class JsonBodyConverter implements BodyConverter {
    @Override
    public boolean isConverted(String contentType){
        return contentType.toLowerCase().contains("json");
    }

    @Override
    public <T> T convert(byte[] body, Class<T> cls) {
        T result = null;
        try {
            result = new Gson().fromJson(new String(body, StandardCharsets.UTF_8), cls);
        }catch (JsonSyntaxException e){
            return null;
        }
        return result;
    }

    @Override
    public <T> String unconvert(T object) {
        String result = null;
        try {
            result = new Gson().toJson(object);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }
}
