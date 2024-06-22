package com.yt.backend;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mock.web.MockHttpServletRequest;
public class MockHttpRequest {
    public static HttpServletRequest createMockHttpRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost"); // Set the server name
        request.setServerPort(8080); // Set the server port
        request.setContextPath("/your-context-path"); // Set the context path
        return request;
    }
}
