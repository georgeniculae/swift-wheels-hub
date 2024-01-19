package com.swiftwheelshub.agency.controler;

import com.swiftwheelshub.agency.controller.BranchController;
import com.swiftwheelshub.agency.service.BranchService;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.BranchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BranchController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class BranchControllerTest {

    private static final String PATH = "/branches";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BranchService branchService;

    @Test
    void findAllBranchesTest_success() throws Exception {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        when(branchService.findAllBranches()).thenReturn(List.of(branchDto));

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
    void findAllBranchesTest_unauthorized() throws Exception {
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
    void findBranchByIdTest_success() throws Exception {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        when(branchService.findBranchById(anyLong())).thenReturn(branchDto);

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
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findBranchByIdTest_successWithMockUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser()
    void findBranchByIdTest_unauthorized() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals("Unauthorized", response.getErrorMessage());
        assertEquals(401, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void countBranchesTest_success() throws Exception {
        when(branchService.countBranches()).thenReturn(1L);

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
    void countBranchesTest_unauthorized() throws Exception {
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
    void addBranchTest_success() throws Exception {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        String content = TestUtils.writeValueAsString(branchDto);

        when(branchService.saveBranch(any(BranchDto.class))).thenReturn(branchDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
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
    void addBranchTest_unauthorized() throws Exception {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        String content = TestUtils.writeValueAsString(branchDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
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
    void addBranchTest_forbidden() throws Exception {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        String content = TestUtils.writeValueAsString(branchDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
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
    void updateBranchTest_success() throws Exception {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        String content = TestUtils.writeValueAsString(branchDto);

        when(branchService.updateBranch(anyLong(), any(BranchDto.class))).thenReturn(branchDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
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
    void updateBranchTest_forbidden() throws Exception {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        String content = TestUtils.writeValueAsString(branchDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
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
    void deleteBranchByIdTest_success() throws Exception {
        doNothing().when(branchService).deleteBranchById(anyLong());

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
    void deleteBranchByIdTest_forbidden() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(403, response.getStatus());
        assertNotNull(response.getContentAsString());
    }

}
