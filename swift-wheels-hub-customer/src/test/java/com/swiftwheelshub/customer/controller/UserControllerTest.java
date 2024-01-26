package com.swiftwheelshub.customer.controller;

import com.swiftwheelshub.customer.service.CustomerService;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDetails;
import com.swiftwheelshub.dto.UserUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class UserControllerTest {

    private static final String PATH = "/customers";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    void getCurrentUserTest_success() throws Exception {
        UserDetails userDetails = TestUtils.getResourceAsJson("/data/UserDetails.json", UserDetails.class);

        when(customerService.getCurrentUser(any(HttpServletRequest.class))).thenReturn(userDetails);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/current").contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void getCurrentUserTest_unauthorized() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/current").contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void registerUserTest_success() throws Exception {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtils.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        String content = TestUtils.writeValueAsString(registerRequest);

        when(customerService.registerCustomer(any(RegisterRequest.class))).thenReturn(registrationResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/register").contextPath(PATH)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void registerUserTest_forbidden() throws Exception {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtils.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        String content = TestUtils.writeValueAsString(registerRequest);

        when(customerService.registerCustomer(any(RegisterRequest.class)))
                .thenReturn(registrationResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/register").contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(403, response.getStatus());
        assertEquals("Forbidden", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void registerUserTest_unauthorized() throws Exception {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtils.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        String content = TestUtils.writeValueAsString(registerRequest);

        when(customerService.registerCustomer(any(RegisterRequest.class))).thenReturn(registrationResponse);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/register").contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateUserTest_success() throws Exception {
        UserDetails userDetails = TestUtils.getResourceAsJson("/data/UserDetails.json", UserDetails.class);

        String content = TestUtils.writeValueAsString(userDetails);

        when(customerService.updateUser(anyString(), any(UserUpdateRequest.class))).thenReturn(userDetails);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L).contextPath(PATH)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateUserTest_forbidden() throws Exception {
        UserDetails userDetails = TestUtils.getResourceAsJson("/data/UserDetails.json", UserDetails.class);

        String content = TestUtils.writeValueAsString(userDetails);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L).contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(403, response.getStatus());
        assertEquals("Forbidden", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateUserTest_unauthorized() throws Exception {
        UserDetails userDetails = TestUtils.getResourceAsJson("/data/UserDetails.json", UserDetails.class);

        String content = TestUtils.writeValueAsString(userDetails);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L).contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void findUserByUsernameTest_success() throws Exception {
        UserDetails userDetails = TestUtils.getResourceAsJson("/data/UserDetails.json", UserDetails.class);

        when(customerService.findUserByUsername(anyString())).thenReturn(userDetails);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{username}", "admin").contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void findUserByUsernameTest_unauthorized() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{username}", "admin").contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void countUsersTest_success() throws Exception {
        when(customerService.countUsers()).thenReturn(1);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count").contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void countUsersTest_unauthorized() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count").contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

}
