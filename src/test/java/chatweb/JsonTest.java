//package chatweb;
//
//import chatweb.entity.Message;
//import chatweb.model.api.NewMessage;
//import chatweb.model.api.SendMessageRequest;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.util.Date;
//import java.util.Map;
//import java.util.UUID;
//
//public class JsonTest {
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    public void sendMessageRequestFromJson() throws JsonProcessingException {
//        SendMessageRequest sendMessageRequest = objectMapper.readValue("{\"message\":\"hi\"}", SendMessageRequest.class);
//        Assertions.assertEquals("hi", sendMessageRequest.getMessage());
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void newMessageToJson() throws JsonProcessingException {
//        NewMessage newMessage = new NewMessage(new Message(UUID.randomUUID().toString(), "hi", "name", null, new Date(Long.parseLong("1685282664601"))));
//        String json = objectMapper.writeValueAsString(newMessage);
//        Map<String, ?> parsed = objectMapper.readValue(json, Map.class);
//        Assertions.assertEquals("NEW_MESSAGE", parsed.get("type"));
//        Assertions.assertEquals(newMessage.getDate().getTime(), parsed.get("date"));
//        Assertions.assertEquals("name", ((Map<String, ?>) parsed.get("message")).get("username"));
//        Assertions.assertEquals("hi", ((Map<String, ?>) parsed.get("message")).get("message"));
//    }
//}
