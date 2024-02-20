package com.swiftwheelshub.customer.controller;

import com.swiftwheelshub.customer.service.CustomerService;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserInfo;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        UserInfo userInfo = TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        when(customerService.getCurrentUser(any(HttpServletRequest.class))).thenReturn(userInfo);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/current")
                        .contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void getCurrentUserTest_unauthorized() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/current")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

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

        MockHttpServletResponse response = mockMvc.perform(post(PATH + "/register")
                        .contextPath(PATH)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

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

        MockHttpServletResponse response = mockMvc.perform(post(PATH + "/register")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

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

        MockHttpServletResponse response = mockMvc.perform(post(PATH + "/register")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateUserTest_success() throws Exception {
        UserInfo userInfo = TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        String content = TestUtils.writeValueAsString(userInfo);

        when(customerService.updateUser(anyString(), any(UserUpdateRequest.class))).thenReturn(userInfo);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateUserTest_forbidden() throws Exception {
        UserInfo userInfo = TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        String content = TestUtils.writeValueAsString(userInfo);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateUserTest_unauthorized() throws Exception {
        UserInfo userInfo = TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        String content = TestUtils.writeValueAsString(userInfo);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findUserByUsernameTest_success() throws Exception {
        UserInfo userInfo = TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        when(customerService.findUserByUsername(anyString())).thenReturn(userInfo);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{username}", "admin")
                        .contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findUserByUsernameTest_unauthorized() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{username}", "admin")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void countUsersTest_success() throws Exception {
        when(customerService.countUsers()).thenReturn(1);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void countUsersTest_unauthorized() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void deleteUserTest_success() throws Exception {
        doNothing().when(customerService).deleteUserById(anyString());

        MockHttpServletResponse response = mockMvc.perform(delete(PATH + "/{id}", "1")
                        .contextPath(PATH)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void logoutTest_success() throws Exception {
        doNothing().when(customerService).signOut(any(HttpServletRequest.class));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/sign-out")
                        .contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void logoutTest_unauthorized() throws Exception {
        doNothing().when(customerService).signOut(any(HttpServletRequest.class));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/sign-out")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

}
