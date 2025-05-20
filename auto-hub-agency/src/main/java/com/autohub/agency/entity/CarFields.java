package com.autohub.agency.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CarFields {

    MAKE("MAKE"),
    MODEL("MODEL"),
    BODY_TYPE("BODY TYPE"),
    YEAR_OF_PRODUCTION("YEAR OF PRODUCTION"),
    COLOR("COLOR"),
    MILEAGE("MILEAGE"),
    CAR_STATUS("CAR STATUS"),
    AMOUNT("AMOUNT"),
    ORIGINAL_BRANCH_ID("ORIGINAL BRANCH ID"),
    ACTUAL_BRANCH_ID("ACTUAL BRANCH ID"),
    IMAGE("IMAGE");

    private final String excelValue;

}
