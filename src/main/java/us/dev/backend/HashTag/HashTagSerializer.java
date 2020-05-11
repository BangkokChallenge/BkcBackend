package us.dev.backend.HashTag;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import us.dev.backend.Comment.Comment;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class HashTagSerializer extends JsonSerializer<HashTag> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public void serialize(HashTag hashTag, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", hashTag.getId());
        jsonGenerator.writeStringField("content", hashTag.getContent());
        jsonGenerator.writeObjectField("createdAt", DATE_FORMAT.format(hashTag.getCreatedAt()));
        jsonGenerator.writeObjectField("modifiedAt", DATE_FORMAT.format(hashTag.getModifiedAt()));
        jsonGenerator.writeEndObject();
    }
}
