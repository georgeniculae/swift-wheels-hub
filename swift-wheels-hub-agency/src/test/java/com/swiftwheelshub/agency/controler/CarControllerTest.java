package com.swiftwheelshub.agency.controler;

import com.swiftwheelshub.agency.controller.CarController;
import com.swiftwheelshub.agency.service.CarService;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CarController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class CarControllerTest {

    private static final String PATH = "/cars";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    void findAllCarsTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findAllCars()).thenReturn(List.of(carResponse));

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
    void findAllCarsTest_unauthorized() throws Exception {
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
    void findCarByIdTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarById(anyLong())).thenReturn(carResponse);

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
    void findCarByIdTest_unauthorized() throws Exception {
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
    void findCarsByMakeTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarsByMake(anyString())).thenReturn(List.of(carResponse));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/make/{make}", "Test")
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
    void findCarsByMakeTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarsByMake(anyString())).thenReturn(List.of(carResponse));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/make/{make}", "Test")
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
    void countCarsTest_success() throws Exception {
        when(carService.countCars()).thenReturn(1L);

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
    void countCarsTest_unauthorized() throws Exception {
        when(carService.countCars()).thenReturn(1L);

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
    void addCarTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        String valueAsString = TestUtils.writeValueAsString(carResponse);

        when(carService.saveCar(any(CarRequest.class))).thenReturn(carResponse);

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
    void addCarTest_unauthorized() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        String valueAsString = TestUtils.writeValueAsString(carRequest);

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
    void addCarTest_forbidden() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        String valueAsString = TestUtils.writeValueAsString(carRequest);

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
    void addAllCarsTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);
        String valueAsString = TestUtils.writeValueAsString(carResponses);

        when(carService.saveAllCars(anyList())).thenReturn(carResponses);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/add")
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
    void addAllCarsTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);
        String valueAsString = TestUtils.writeValueAsString(carResponses);

        when(carService.saveAllCars(anyList())).thenReturn(carResponses);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/add")
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
    void addAllCarsTest_forbidden() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);
        String valueAsString = TestUtils.writeValueAsString(carResponses);

        when(carService.saveAllCars(anyList())).thenReturn(carResponses);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/add")
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
    void updateCarTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        String valueAsString = TestUtils.writeValueAsString(carResponse);

        when(carService.updateCar(anyLong(), any(CarRequest.class))).thenReturn(carResponse);

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
    void updateCarTest_unauthorized() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        String valueAsString = TestUtils.writeValueAsString(carRequest);

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
    void updateCarTest_forbidden() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        String valueAsString = TestUtils.writeValueAsString(carRequest);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN"))
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
    void deleteCarByIdTest_success() throws Exception {
        doNothing().when(carService).deleteCarById(anyLong());

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
    void deleteCarByIdTest_forbidden() throws Exception {
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
