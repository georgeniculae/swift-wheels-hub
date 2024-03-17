package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.CarService;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.UpdateCarRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        MockHttpServletResponse response = mockMvc.perform(get(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findAllCarsTest_unauthorized() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findCarByIdTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarById(anyLong())).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findCarByIdTest_unauthorized() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findCarsByMakeTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarsByMake(anyString())).thenReturn(List.of(carResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/make/{make}", "Test")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findCarsByMakeTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarsByMake(anyString())).thenReturn(List.of(carResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/make/{make}", "Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findAvailableCarTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findAvailableCar(anyLong())).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{id}/availability", 1L)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findAvailableCarTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findAvailableCar(anyLong())).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{id}/availability", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void countCarsTest_success() throws Exception {
        when(carService.countCars()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findCarImageTest_success() throws Exception {
        when(carService.getCarImage(anyLong())).thenReturn(new byte[]{});

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void findCarImageTest_unauthorized() throws Exception {
        when(carService.getCarImage(anyLong())).thenReturn(new byte[]{});

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void countCarsTest_unauthorized() throws Exception {
        when(carService.countCars()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void addCarTest_success() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.saveCar(any(CarRequest.class))).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(post(PATH)
                        .flashAttr("carRequest", carRequest)
                        .param("make", carRequest.make())
                        .param("model", carRequest.model())
                        .param("bodyCategory", String.valueOf(carRequest.bodyCategory()))
                        .param("yearOfProduction", String.valueOf(carRequest.yearOfProduction()))
                        .param("color", carRequest.color())
                        .param("mileage", String.valueOf(carRequest.mileage()))
                        .param("carState", String.valueOf(carRequest.carState()))
                        .param("amount", String.valueOf(carRequest.amount()))
                        .param("originalBranchId", String.valueOf(carRequest.originalBranchId()))
                        .param("actualBranchId", String.valueOf(carRequest.actualBranchId()))
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void addCarTest_unauthorized() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockHttpServletResponse response = mockMvc.perform(post(PATH)
                        .flashAttr("carRequest", carRequest)
                        .param("make", carRequest.make())
                        .param("model", carRequest.model())
                        .param("bodyCategory", String.valueOf(carRequest.bodyCategory()))
                        .param("yearOfProduction", String.valueOf(carRequest.yearOfProduction()))
                        .param("color", carRequest.color())
                        .param("mileage", String.valueOf(carRequest.mileage()))
                        .param("carState", String.valueOf(carRequest.carState()))
                        .param("amount", String.valueOf(carRequest.amount()))
                        .param("originalBranchId", String.valueOf(carRequest.originalBranchId()))
                        .param("actualBranchId", String.valueOf(carRequest.actualBranchId()))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void addCarTest_forbidden() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockHttpServletResponse response = mockMvc.perform(post(PATH)
                        .flashAttr("carRequest", carRequest)
                        .param("make", carRequest.make())
                        .param("model", carRequest.model())
                        .param("bodyCategory", String.valueOf(carRequest.bodyCategory()))
                        .param("yearOfProduction", String.valueOf(carRequest.yearOfProduction()))
                        .param("color", carRequest.color())
                        .param("mileage", String.valueOf(carRequest.mileage()))
                        .param("carState", String.valueOf(carRequest.carState()))
                        .param("amount", String.valueOf(carRequest.amount()))
                        .param("originalBranchId", String.valueOf(carRequest.originalBranchId()))
                        .param("actualBranchId", String.valueOf(carRequest.actualBranchId())))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void uploadCarsTest_success() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "Cars.xlsx", MediaType.TEXT_PLAIN_VALUE, "Cars".getBytes());

        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);

        when(carService.uploadCars(any(MultipartFile.class))).thenReturn(carResponses);

        MockHttpServletResponse response = mockMvc.perform(multipart(HttpMethod.POST, PATH + "/upload")
                        .file(file)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void uploadCarsTest_unauthorized() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "Cars.xlsx", MediaType.TEXT_PLAIN_VALUE, "Cars".getBytes());

        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);

        when(carService.uploadCars(any(MultipartFile.class))).thenReturn(carResponses);

        mockMvc.perform(multipart(HttpMethod.POST, PATH + "/upload")
                        .file(file)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateCarTest_success() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.updateCar(anyLong(), any(CarRequest.class))).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .flashAttr("carRequest", carRequest)
                        .param("make", carRequest.make())
                        .param("model", carRequest.model())
                        .param("bodyCategory", String.valueOf(carRequest.bodyCategory()))
                        .param("yearOfProduction", String.valueOf(carRequest.yearOfProduction()))
                        .param("color", carRequest.color())
                        .param("mileage", String.valueOf(carRequest.mileage()))
                        .param("carState", String.valueOf(carRequest.carState()))
                        .param("amount", String.valueOf(carRequest.amount()))
                        .param("originalBranchId", String.valueOf(carRequest.originalBranchId()))
                        .param("actualBranchId", String.valueOf(carRequest.actualBranchId()))
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateCarTest_unauthorized() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .flashAttr("carRequest", carRequest)
                        .param("make", carRequest.make())
                        .param("model", carRequest.model())
                        .param("bodyCategory", String.valueOf(carRequest.bodyCategory()))
                        .param("yearOfProduction", String.valueOf(carRequest.yearOfProduction()))
                        .param("color", carRequest.color())
                        .param("mileage", String.valueOf(carRequest.mileage()))
                        .param("carState", String.valueOf(carRequest.carState()))
                        .param("amount", String.valueOf(carRequest.amount()))
                        .param("originalBranchId", String.valueOf(carRequest.originalBranchId()))
                        .param("actualBranchId", String.valueOf(carRequest.actualBranchId()))
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateCarTest_forbidden() throws Exception {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .flashAttr("carRequest", carRequest)
                        .param("make", carRequest.make())
                        .param("model", carRequest.model())
                        .param("bodyCategory", String.valueOf(carRequest.bodyCategory()))
                        .param("yearOfProduction", String.valueOf(carRequest.yearOfProduction()))
                        .param("color", carRequest.color())
                        .param("mileage", String.valueOf(carRequest.mileage()))
                        .param("carState", String.valueOf(carRequest.carState()))
                        .param("amount", String.valueOf(carRequest.amount()))
                        .param("originalBranchId", String.valueOf(carRequest.originalBranchId()))
                        .param("actualBranchId", String.valueOf(carRequest.actualBranchId()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateCarsStatusTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        UpdateCarRequest carRequest = UpdateCarRequest.builder()
                .carId(1L)
                .carState(CarState.NOT_AVAILABLE)
                .build();

        String content = TestUtils.writeValueAsString(List.of(carRequest));

        when(carService.updateCarsStatus(anyList())).thenReturn(List.of(carResponse));

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/update-statuses")
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
    @WithAnonymousUser
    void updateCarsStatusTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        UpdateCarRequest carRequest = UpdateCarRequest.builder()
                .carId(1L)
                .carState(CarState.NOT_AVAILABLE)
                .build();

        String content = TestUtils.writeValueAsString(List.of(carRequest));

        when(carService.updateCarsStatus(anyList())).thenReturn(List.of(carResponse));

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/update-statuses")
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
    void updateCarWhenBookingIsClosedTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId(1L)
                .carState(CarState.NOT_AVAILABLE)
                .build();

        String content = TestUtils.writeValueAsString(carUpdateDetails);

        when(carService.updateCarWhenBookingIsClosed(anyLong(), any(CarUpdateDetails.class))).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}/update-after-return", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateCarWhenBookingIsClosedTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId(1L)
                .carState(CarState.NOT_AVAILABLE)
                .build();

        String content = TestUtils.writeValueAsString(carUpdateDetails);

        when(carService.updateCarWhenBookingIsClosed(anyLong(), any(CarUpdateDetails.class))).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}/update-after-return", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateCarStatusTest_success() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.updateCarStatus(anyLong(), any(CarState.class))).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}/change-status?carState=NOT_AVAILABLE", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void updateCarStatusTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.updateCarStatus(anyLong(), any(CarState.class))).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}/change-status?carState=NOT_AVAILABLE", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void deleteCarByIdTest_success() throws Exception {
        doNothing().when(carService).deleteCarById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(delete(PATH + "/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    void deleteCarByIdTest_forbidden() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(delete(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

}
