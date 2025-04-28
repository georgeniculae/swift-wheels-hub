package com.autohub.expense.consumer;

import com.autohub.dto.InvoiceReprocessRequest;
import com.autohub.expense.service.InvoiceReprocessingService;
import com.autohub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FailedInvoiceDlqMessageConsumer {

    private final InvoiceReprocessingService invoiceReprocessingService;

    @Bean
    public Consumer<Message<InvoiceReprocessRequest>> failedInvoiceDlqConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<InvoiceReprocessRequest> message) {
        invoiceReprocessingService.reprocessInvoice(message.getPayload());
        KafkaUtil.acknowledgeMessage(message.getHeaders());
        log.info("Failed invoice reprocessed successfully");
    }

}
