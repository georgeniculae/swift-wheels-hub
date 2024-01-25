package com.swiftwheelshub.agency.controler;

import com.swiftwheelshub.agency.controller.RentalOfficeController;
import com.swiftwheelshub.agency.service.RentalOfficeService;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.RentalOfficeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RentalOfficeController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class RentalOfficeControllerTest {

    private static final String PATH = "/rental-offices";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalOfficeService rentalOfficeService;

    @Test
    void findAllRentalOfficesTest_success() throws Exception {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeService.findAllRentalOffices()).thenReturn(List.of(rentalOfficeDto));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH)
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
    void findAllRentalOfficesTest_unauthorized() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH)
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
    void findRentalOfficeByIdTest_success() throws Exception {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeService.findRentalOfficeById(anyLong())).thenReturn(rentalOfficeDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
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
    void findRentalOfficeByIdTest_unauthorized() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
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
    void countRentalOfficesTest_success() throws Exception {
        when(rentalOfficeService.countRentalOffices()).thenReturn(1L);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
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
    void countRentalOfficesTest_unauthorized() throws Exception {
        when(rentalOfficeService.countRentalOffices()).thenReturn(1L);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
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
    void addRentalOfficeTest_success() throws Exception {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        String valueAsString = TestUtils.writeValueAsString(rentalOfficeDto);

        when(rentalOfficeService.saveRentalOffice(any(RentalOfficeDto.class))).thenReturn(rentalOfficeDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void addRentalOfficeTest_unauthorized() throws Exception {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        String valueAsString = TestUtils.writeValueAsString(rentalOfficeDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void addRentalOfficeTest_forbidden() throws Exception {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        String valueAsString = TestUtils.writeValueAsString(rentalOfficeDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isForbidden())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(403, response.getStatus());
        assertEquals("Forbidden", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateRentalOfficeTest_success() throws Exception {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        String valueAsString = TestUtils.writeValueAsString(rentalOfficeDto);

        when(rentalOfficeService.saveRentalOffice(any(RentalOfficeDto.class))).thenReturn(rentalOfficeDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateRentalOfficeTest_unauthorized() throws Exception {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        String valueAsString = TestUtils.writeValueAsString(rentalOfficeDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateRentalOfficeTest_forbidden() throws Exception {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        String valueAsString = TestUtils.writeValueAsString(rentalOfficeDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isForbidden())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(403, response.getStatus());
        assertEquals("Forbidden", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void deleteRentalOfficeByIdTest_success() throws Exception {
        doNothing().when(rentalOfficeService).deleteRentalOfficeById(anyLong());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(204, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void deleteRentalOfficeByIdTest_forbidden() throws Exception {
        doNothing().when(rentalOfficeService).deleteRentalOfficeById(anyLong());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(403, response.getStatus());
        assertEquals("Forbidden", response.getErrorMessage());
        assertNotNull(response.getContentAsString());
    }

}
