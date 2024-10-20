package com.swiftwheelshub.booking.eventhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.service.BookingProducerService;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
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
import org.apache.commons.text.CaseUtils;
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
    private static final String BOOKING_PROCESS_STATE_PREFIX = "IN_";
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final BookingProducerService bookingProducerService;
    private final ObjectMapper objectMapper;
    private final BookingMapper bookingMapper;

    public DebeziumListener(Configuration userConnectorConfiguration,
                            BookingProducerService bookingProducerService,
                            ObjectMapper objectMapper,
                            BookingMapper bookingMapper) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(userConnectorConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        this.bookingProducerService = bookingProducerService;
        this.objectMapper = objectMapper;
        this.bookingMapper = bookingMapper;
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
                handleBookingSending(payload, operation);

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
                .collect(Collectors.toMap(this::getCamelCaseFieldName, struct::get));
    }

    private void handleBookingSending(Map<String, Object> payload, Operation operation) {
        Booking booking = objectMapper.convertValue(payload, Booking.class);

        if (booking.getBookingProcessStatus().name().startsWith(BOOKING_PROCESS_STATE_PREFIX)) {
            BookingResponse bookingResponse = bookingMapper.mapEntityToDto(booking);

            if (Operation.CREATE.equals(operation)) {
                bookingProducerService.sendSavedBooking(bookingResponse);

                return;
            }

            if (Operation.UPDATE.equals(operation)) {
                bookingProducerService.sendUpdatedBooking(bookingResponse);

                return;
            }

            if (Operation.DELETE.equals(operation)) {
                bookingProducerService.sendDeletedBooking(bookingResponse.id());
            }
        }
    }

    private String getCamelCaseFieldName(String fieldName) {
        if (fieldName.contains(UNDERSCORE)) {
            return CaseUtils.toCamelCase(fieldName, false, UNDERSCORE_CHAR);
        }

        return fieldName;
    }

}
