package com.autohub.expense;

import com.autohub.lib.annotation.AutoHubMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubMicroservice
public class AutoHubExpenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoHubExpenseApplication.class, args);
    }

}
