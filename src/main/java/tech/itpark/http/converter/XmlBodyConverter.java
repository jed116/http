package tech.itpark.http.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import tech.itpark.http.BodyConverter;
import tech.itpark.http.Request;

import java.nio.charset.StandardCharsets;

public class XmlBodyConverter implements BodyConverter {
    @Override
    public boolean canRead(Request request) {
        if(request == null ) {
            return false;
        }
        if(!request.getHeaders().containsKey("content-type")){
            return false;
        }
        if(!String.join(" , ", request.getHeaders().get("content-type")).contains("xml")){
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
        XmlMapper xmlMapper = new XmlMapper();
        T result = null;
        String str = new String(request.getBody(), StandardCharsets.UTF_8);
        try {
            result = xmlMapper.readValue(str, cls);
        } catch (JsonMappingException  e) {
            return null;
        } catch (JsonProcessingException e) {
            return null;
        }
        return result;
    }
}
