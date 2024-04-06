package chatweb.controller;

import chatweb.configuration.WebConfiguration;
import chatweb.entity.User;
import chatweb.helper.UserAuthHelper;
import chatweb.interceptor.ServiceAuthInterceptor;
import chatweb.interceptor.UserAuthInterceptor;
import chatweb.model.Color;
import chatweb.model.event.TestEvent;
import chatweb.repository.RoomRepository;
import chatweb.service.EventsService;
import chatweb.utils.SseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        EventsController.class,
        EventsService.class,
        WebConfiguration.class,
        ServiceAuthInterceptor.class,
        UserAuthInterceptor.class,
        UserAuthHelper.class
})
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventsControllerTest {

    private ExecutorService executorService;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ServiceAuthInterceptor serviceAuthInterceptor;
    @MockBean
    private UserAuthHelper userAuthHelper;
    @MockBean
    private RoomRepository roomRepository;

    @BeforeAll
    public void setup() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @AfterAll
    public void cleanup() {
        executorService.shutdown();
    }

    @Test
    public void streamEventsReceiveTestEvent() throws Exception {
        User user = new User(1, "TestUser", "test@example.org", null, Color.BLUE);
        when(userAuthHelper.auth(any())).thenReturn(user);
        when(serviceAuthInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        Future<ResultActions> eventsFuture = executorService.submit(() ->
                mvc.perform(get("/api/events/stream").accept(MediaType.TEXT_EVENT_STREAM))
        );
        TestEvent testEvent = new TestEvent(Instant.parse("2024-03-28T12:20:30.123Z"), "Кириллица");
        mvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEvent))
        ).andExpect(status().isOk());

        DocumentContext receivedTestEvent = SseUtils.parseResponse(
                        eventsFuture.get().andReturn().getResponse().getContentAsString()
                )
                .stream()
                .filter(json -> json.read("$.type").equals(".TestEvent"))
                .findFirst()
                .orElseThrow();

        assertEquals("2024-03-28T12:20:30.123Z", receivedTestEvent.read("$.date"));
        assertEquals("Кириллица", receivedTestEvent.read("$.cyrillicText"));
    }

}
