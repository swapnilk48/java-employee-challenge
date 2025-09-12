package com.reliaquest.api.config;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WebClientConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "urls.employee_server_base_url=http://localhost:8081"
})
public class WebClientConfigTest {

    public static MockWebServer mockBackEnd;

    @Autowired
    private WebClient employeeWebClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(8081);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void employeeWebClientBeanIsCreated() {
        assertNotNull(employeeWebClient);
    }

    @Test
    void employeeWebClient_usesCorrectBaseUrl() throws Exception {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Test Response")
                .addHeader("Content-Type", "text/plain"));

        String responseBody = employeeWebClient.get()
                .uri("/employees")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("/employees", recordedRequest.getPath());
        assertEquals("Test Response", responseBody);
    }
}
