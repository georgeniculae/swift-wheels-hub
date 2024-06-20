package com.swiftwheelshub.expense.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.service.EmailNotificationProducer;
import io.debezium.config.Configuration;
import io.debezium.data.Envelope;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.debezium.data.Envelope.Operation;

@Component
@Slf4j
public class DebeziumListener {

    private static final String UNDERSCORE = "_";
    private static final char UNDERSCORE_CHAR = '_';
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final EmailNotificationProducer emailNotificationProducer;
    private final InvoiceMapper invoiceMapper;
    private final ObjectMapper objectMapper;

    public DebeziumListener(Configuration userConnectorConfiguration,
                            EmailNotificationProducer emailNotificationProducer,
                            InvoiceMapper invoiceMapper,
                            ObjectMapper objectMapper) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(userConnectorConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        this.emailNotificationProducer = emailNotificationProducer;
        this.invoiceMapper = invoiceMapper;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void start() {
        executor.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() {
        if (Objects.nonNull(debeziumEngine)) {
            try {
                debeziumEngine.close();
            } catch (IOException e) {
                throw new SwiftWheelsHubException(e.getMessage());
            }
        }
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();

        log.info("Key = '{}', value = '{}'", sourceRecord.key(), sourceRecord.value());

        Struct sourceRecordChangeValue = (Struct) sourceRecord.value();

        if (ObjectUtils.isNotEmpty(sourceRecordChangeValue)) {
            Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(Envelope.FieldName.OPERATION));

            if (Operation.READ != operation) {
                Map<String, Object> payload = getPayload(operation, sourceRecordChangeValue);
                notifyCustomer(payload, operation);

                log.info("Processed payload: {} with operation: {}", payload, operation.name());
            }
        }
    }

    private Map<String, Object> getPayload(Operation operation, Struct sourceRecordChangeValue) {
        String record = Operation.DELETE == operation ? Envelope.FieldName.BEFORE : Envelope.FieldName.AFTER;
        Struct struct = (Struct) sourceRecordChangeValue.get(record);

        return struct.schema()
                .fields()
                .stream()
                .map(Field::name)
                .filter(fieldName -> ObjectUtils.isNotEmpty(struct.get(fieldName)))
                .collect(Collectors.toMap(this::getUpdatedFieldName, struct::get));
    }

    private String getUpdatedFieldName(String fieldName) {
        if (fieldName.contains(UNDERSCORE)) {
            return replaceUnderscoresWithUpperCases(fieldName);
        }

        return fieldName;
    }

    private String replaceUnderscoresWithUpperCases(String fieldName) {
        StringBuilder updatedFieldName = new StringBuilder();
        int index = 0;
        int fieldNameLength = fieldName.length() - 1;

        while (index < fieldNameLength) {
            char currentCharacter = fieldName.charAt(index);
            char nextCharacter = fieldName.charAt(index + 1);

            if (UNDERSCORE_CHAR == currentCharacter) {
                updatedFieldName.append(Character.toUpperCase(nextCharacter));
                index += 2;

                continue;
            }

            updatedFieldName.append(currentCharacter);
            index++;
        }

        updatedFieldName.append(fieldName.charAt(fieldNameLength));

        return updatedFieldName.toString();
    }

    private void notifyCustomer(Map<String, Object> payload, Operation operation) {
        Invoice invoice = objectMapper.convertValue(payload, Invoice.class);
        InvoiceResponse invoiceResponse = invoiceMapper.mapEntityToDto(invoice);

        if (Operation.UPDATE.equals(operation) && ObjectUtils.isNotEmpty(invoiceResponse.totalAmount())) {
            emailNotificationProducer.sendMessage(invoiceResponse);
        }
    }

}
