package com.carrental.requestvalidator.service;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules;
import com.carrental.dto.RequestValidationReport;
import com.carrental.exception.CarRentalException;
import com.carrental.requestvalidator.model.SwaggerFolder;
import com.carrental.requestvalidator.repository.SwaggerRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SwaggerRequestValidatorService {

    private static final String SEPARATOR = "/";
    private static final String SWAGGER = "swagger";
    private static final String V3 = "v3";
    private static final String SWAGGER_PATH = "Swagger path";
    private static final String SWAGGER_MESSAGE = "Swagger message";
    private static final String V3_PATH = "v3 path";
    private static final String V3_MESSAGE = "v3 message";
    private static final String QUERY_SPLIT_REGEX = "&";
    private final SwaggerRepository swaggerRepository;

    public RequestValidationReport filter(HttpServletRequest request) {
        String bodyAsString = getRequestBodyAsString(request);
        SimpleRequest simpleRequest = getSimpleRequest(request, bodyAsString);
        ValidationReport validationReport = getValidationReport(request, simpleRequest);

        return new RequestValidationReport(getValidationErrorMessage(validationReport));
    }

    private String getRequestBodyAsString(HttpServletRequest request) {
        try {
            return request.getReader()
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CarRentalException(e);
        }
    }

    private SimpleRequest getSimpleRequest(HttpServletRequest request, String bodyAsString) {
        SimpleRequest.Builder simpleRequestBuilder =
                new SimpleRequest.Builder(request.getMethod(), request.getContextPath());

        request.getHeaderNames()
                .asIterator()
                .forEachRemaining(simpleRequestBuilder::withHeader);

        Arrays.stream(request.getQueryString().split(QUERY_SPLIT_REGEX))
                .forEach(simpleRequestBuilder::withQueryParam);

        simpleRequestBuilder.withBody(bodyAsString);

        return simpleRequestBuilder.build();
    }

    private ValidationReport getValidationReport(HttpServletRequest request, SimpleRequest simpleRequest) {
        SwaggerFolder swaggerFolder = swaggerRepository.findById(SWAGGER)
                .orElseThrow(() -> new CarRentalException("Swagger folder does not exist"));

        String swaggerFile = getSwaggerFile(request, swaggerFolder.getSwaggerIdentifierAndContent());
        OpenApiInteractionValidator validator = OpenApiInteractionValidator.createForInlineApiSpecification(swaggerFile)
                .withWhitelist(getWhitelist())
                .build();

        return validator.validateRequest(simpleRequest);
    }

    private String getSwaggerFile(HttpServletRequest request, Map<String, String> swaggerIdentifierAndContent) {
        String path = request.getServletPath().replaceFirst(SEPARATOR, StringUtils.EMPTY);

        return swaggerIdentifierAndContent.entrySet()
                .stream()
                .filter(entry -> path.contains(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new CarRentalException("There is no swagger file that contains path: " + path));
    }

    private String getValidationErrorMessage(ValidationReport validationReport) {
        return validationReport.getMessages()
                .stream()
                .map(ValidationReport.Message::getMessage)
                .collect(Collectors.joining());
    }

    private ValidationErrorsWhitelist getWhitelist() {
        return ValidationErrorsWhitelist.create()
                .withRule(SWAGGER_PATH, WhitelistRules.pathContainsSubstring(SWAGGER))
                .withRule(SWAGGER_MESSAGE, WhitelistRules.messageContainsSubstring(SWAGGER))
                .withRule(V3_PATH, WhitelistRules.pathContainsSubstring(V3))
                .withRule(V3_MESSAGE, WhitelistRules.messageContainsSubstring(V3));
    }

}
