package com.techchallenge.infrastructure.api;

import com.techchallenge.core.exceptions.handler.ExceptionHandlerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class RedirectApiTest {
    MockMvc mockMvc;
    RedirectApi redirectApi;

    @BeforeEach
    void init(){
        redirectApi = new RedirectApi();
        mockMvc = MockMvcBuilders.standaloneSetup(redirectApi)
                .setControllerAdvice().build();
    }

    @Test
    void swagger() throws Exception {
       mockMvc.perform(get("/product"))
                .andExpect(redirectedUrl("swagger-ui.html"));
    }
}