package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.EmployeeService;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EmployeeController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class EmployeeControllerTest {

    private static final String PATH = "/employees";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void findAllEmployeesTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeService.findAllEmployees()).thenReturn(List.of(employeeResponse));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findAllEmployeesTest_forbidden() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findEmployeeByIdTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeService.findEmployeeById(anyLong())).thenReturn(employeeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findEmployeeByIdTest_unauthorized() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findEmployeesByBranchIdTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeService.findEmployeesByBranchId(anyLong())).thenReturn(List.of(employeeResponse));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/branch/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findEmployeesByBranchIdTest_unauthorized() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/branch/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void countEmployeesTest_success() throws Exception {
        when(employeeService.countEmployees()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void countEmployeesTest_unauthorized() throws Exception {
        when(employeeService.countEmployees()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void addEmployeeTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        String valueAsString = TestUtils.writeValueAsString(employeeResponse);

        when(employeeService.saveEmployee(any(EmployeeRequest.class))).thenReturn(employeeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void addEmployeeTest_unauthorized() throws Exception {
        EmployeeRequest employeeRequest = TestUtils.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);
        String valueAsString = TestUtils.writeValueAsString(employeeRequest);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void addEmployeeTest_forbidden() throws Exception {
        EmployeeRequest employeeRequest = TestUtils.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);
        String valueAsString = TestUtils.writeValueAsString(employeeRequest);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateEmployeeTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        String valueAsString = TestUtils.writeValueAsString(employeeResponse);

        when(employeeService.updateEmployee(anyLong(), any(EmployeeRequest.class))).thenReturn(employeeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateEmployeeTest_unauthorized() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        String valueAsString = TestUtils.writeValueAsString(employeeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateEmployeeTest_forbidden() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        String valueAsString = TestUtils.writeValueAsString(employeeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void deleteEmployeeByIdTest_success() throws Exception {
        doNothing().when(employeeService).deleteEmployeeById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void deleteEmployeeByIdTest_forbidden() throws Exception {
        doNothing().when(employeeService).deleteEmployeeById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

}
