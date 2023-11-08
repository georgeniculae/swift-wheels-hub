package com.carrental.booking.config;

import com.carrental.booking.mapper.BookingMapper;
import com.carrental.booking.service.BookingProducerService;
import com.carrental.dto.BookingDto;
import com.carrental.entity.Booking;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.debezium.data.Envelope.FieldName.AFTER;
import static io.debezium.data.Envelope.FieldName.BEFORE;
import static io.debezium.data.Envelope.FieldName.OPERATION;
import static io.debezium.data.Envelope.Operation;

@Component
@Slf4j
public class DebeziumListener {

    private static final String UNDERSCORE = "_";
    private static final char UNDERSCORE_CHAR = '_';
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final ObjectMapper objectMapper;
    private final BookingProducerService bookingProducerService;
    private final BookingMapper bookingMapper;

    public DebeziumListener(Configuration userConnectorConfiguration,
                            ObjectMapper objectMapper,
                            BookingProducerService bookingProducerService,
                            BookingMapper bookingMapper) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(userConnectorConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        this.objectMapper = objectMapper;
        this.bookingProducerService = bookingProducerService;
        this.bookingMapper = bookingMapper;
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();

        log.info("Key = '" + sourceRecord.key() + "' value = '" + sourceRecord.value() + "'");

        Struct sourceRecordChangeValue = (Struct) sourceRecord.value();

        if (ObjectUtils.isNotEmpty(sourceRecordChangeValue)) {
            Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

            if (Operation.READ != operation) {
                String record = Operation.DELETE == operation ? BEFORE : AFTER;

                Struct struct = (Struct) sourceRecordChangeValue.get(record);
                Map<String, Object> payload = struct.schema()
                        .fields()
                        .stream()
                        .map(Field::name)
                        .filter(fieldName -> ObjectUtils.isNotEmpty(struct.get(fieldName)))
                        .map(fieldName -> getFieldName(struct, fieldName))
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

                handleBookings(payload, operation);

                log.info("Updated Data: {} with Operation: {}", payload, operation.name());
            }
        }
    }

    @PostConstruct
    private void start() {
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() throws IOException {
        if (Objects.nonNull(this.debeziumEngine)) {
            this.debeziumEngine.close();
        }
    }

    private void handleBookings(Map<String, Object> payload, Operation operation) {
        Booking booking = objectMapper.convertValue(payload, Booking.class);
        BookingDto bookingDto = bookingMapper.mapEntityToDto(booking);

        if (Operation.CREATE.equals(operation)) {
            bookingProducerService.sendSavedBooking(bookingDto);
        }

        if (Operation.UPDATE.equals(operation)) {
            bookingProducerService.sendUpdatedBooking(bookingDto);
        }

        if (Operation.DELETE.equals(operation)) {
            bookingProducerService.sendDeletedBooking(bookingDto.getId());
        }
    }

    private Pair<String, Object> getFieldName(Struct struct, String fieldName) {
        if (fieldName.contains(UNDERSCORE)) {
            List<Integer> indexOfUnderscoreCharacters = getIndexOfUnderscoreCharacters(fieldName);
            String updateFieldName = replaceUnderscoresWithUpperCases(fieldName, indexOfUnderscoreCharacters);

            return Pair.of(updateFieldName, struct.get(fieldName));
        }

        return Pair.of(fieldName, struct.get(fieldName));
    }

    private List<Integer> getIndexOfUnderscoreCharacters(String fieldName) {
        List<Integer> indexes = new ArrayList<>();

        for (int index = 0; index < fieldName.length(); index++) {
            if (UNDERSCORE_CHAR == fieldName.charAt(index)) {
                indexes.add(index);
            }
        }

        return indexes;
    }

    private String replaceUnderscoresWithUpperCases(String fieldName, List<Integer> indexes) {
        StringBuilder updatedFieldName = new StringBuilder(fieldName);

        for (Integer index : indexes) {
            updatedFieldName.setCharAt(index + 1, Character.toUpperCase(fieldName.charAt(index + 1)));
        }

        return updatedFieldName.toString().replace(UNDERSCORE, StringUtils.EMPTY);
    }

}
