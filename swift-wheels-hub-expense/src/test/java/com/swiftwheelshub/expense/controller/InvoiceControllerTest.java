package com.swiftwheelshub.expense.controller;

import com.swiftwheelshub.expense.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest(classes = InvoiceController.class)
@AutoConfigureMockMvc
@EnableWebMvc
public class InvoiceControllerTest {

    private static final String PATH = "/invoices";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

}
