package com.swiftwheelshub.emailnotification.service;

import com.github.mustachejava.MustacheFactory;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private SendGrid sendGrid;

    @Mock
    private MustacheFactory mustacheFactory;

}
