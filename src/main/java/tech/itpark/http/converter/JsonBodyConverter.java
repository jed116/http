package tech.itpark.http.converter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import tech.itpark.http.BodyConverter;
import tech.itpark.http.Request;

import java.nio.charset.StandardCharsets;

public class JsonBodyConverter implements BodyConverter {
    @Override
    public boolean canRead(Request request) {
        if(request == null ) {
            return false;
        }
        if(!request.getHeaders().containsKey("content-type")){
            return false;
        }
        if(!String.join(" , ", request.getHeaders().get("content-type")).contains("json")){
            return false;
        }
        return true;
    }

    @Override
    public boolean canWrite(Class<?> cls) {
        return true;
    }

    @Override
    public <T> T convert(Request request, Class<T> cls) {
        Gson gson = new Gson();
        String json = new String(request.getBody(), StandardCharsets.UTF_8);
        T result = null;
        try {
            result = gson.fromJson(json, cls);
        }catch (JsonSyntaxException e){
            return null;
        }
        return result;
    }
}
