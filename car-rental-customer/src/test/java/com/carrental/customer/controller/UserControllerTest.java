package com.carrental.customer.controller;

import com.carrental.customer.service.KeycloakService;
import com.carrental.dto.CurrentUserDto;
import com.carrental.customer.service.CustomerService;
import com.carrental.customer.util.TestUtils;
import com.carrental.dto.AuthenticationResponse;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class UserControllerTest {

    private static final String PATH = "/customers";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private KeycloakService keycloakService;

    @Test
    void getCurrentUserTest_success() throws Exception {
        CurrentUserDto currentUserDto =
                TestUtils.getResourceAsJson("/data/CurrentUserDto.json", CurrentUserDto.class);

        when(customerService.getCurrentUser()).thenReturn(currentUserDto);

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

        AuthenticationResponse authenticationResponse =
                TestUtils.getResourceAsJson("/data/AuthenticationResponse.json", AuthenticationResponse.class);

        String content = TestUtils.writeValueAsString(registerRequest);

        when(customerService.registerCustomer(any(RegisterRequest.class)))
                .thenReturn(authenticationResponse);

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

        AuthenticationResponse authenticationResponse =
                TestUtils.getResourceAsJson("/data/AuthenticationResponse.json", AuthenticationResponse.class);

        String content = TestUtils.writeValueAsString(registerRequest);

        when(customerService.registerCustomer(any(RegisterRequest.class)))
                .thenReturn(authenticationResponse);

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

        AuthenticationResponse authenticationResponse =
                TestUtils.getResourceAsJson("/data/AuthenticationResponse.json", AuthenticationResponse.class);

        String content = TestUtils.writeValueAsString(registerRequest);

        when(customerService.registerCustomer(any(RegisterRequest.class)))
                .thenReturn(authenticationResponse);

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
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        String content = TestUtils.writeValueAsString(userDto);

        when(customerService.updateUser(anyLong(), any(userDto.getClass()))).thenReturn(userDto);

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
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        String content = TestUtils.writeValueAsString(userDto);

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
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        String content = TestUtils.writeValueAsString(userDto);

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
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(customerService.findUserByUsername(anyString())).thenReturn(userDto);

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
        when(customerService.countUsers()).thenReturn(1L);

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
