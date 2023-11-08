package com.carrental.expense.controller;

import com.carrental.expense.service.InvoiceService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ExtendWith(SpringExtension.class)
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
