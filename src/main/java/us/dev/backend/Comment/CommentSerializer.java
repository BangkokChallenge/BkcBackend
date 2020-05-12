package us.dev.backend.Comment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class CommentSerializer extends JsonSerializer<Comment> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public void serialize(Comment comment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", comment.getId());
        jsonGenerator.writeStringField("content", comment.getContent());
        jsonGenerator.writeObjectField("createdAt", DATE_FORMAT.format(comment.getCreatedAt()));
        jsonGenerator.writeObjectField("modifiedAt", DATE_FORMAT.format(comment.getModifiedAt()));
        jsonGenerator.writeNumberField("postId", comment.getPost().getId());
        jsonGenerator.writeStringField("accountId", comment.getAccount().getId());
        jsonGenerator.writeStringField("profile_photo", comment.getAccount().getProfile_photo());
        jsonGenerator.writeStringField("nickname", comment.getAccount().getNickname());
        jsonGenerator.writeEndObject();
    }
}
