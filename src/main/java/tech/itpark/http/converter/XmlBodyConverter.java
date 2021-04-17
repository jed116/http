package tech.itpark.http.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.nio.charset.StandardCharsets;

public class XmlBodyConverter implements BodyConverter {
    @Override
    public boolean isConverted(String contentType){
        return contentType.toLowerCase().contains("xml");
    }

    @Override
    public <T> T convert(byte[] body, Class<T> cls) {
        T result = null;
        try {
            result = new XmlMapper().readValue(new String(body, StandardCharsets.UTF_8), cls);
        } catch (JsonMappingException  e) {
            return null;
        } catch (JsonProcessingException e) {
            return null;
        }
        return result;
    }

    @Override
    public <T> String unconvert(T object){
        String result = null;
        try {
            result = new XmlMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            result = null;
        }
        return result;
    }
}
