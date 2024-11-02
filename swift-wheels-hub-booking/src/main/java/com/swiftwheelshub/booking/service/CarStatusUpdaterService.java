package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.dto.UpdateCarRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarStatusUpdaterService implements RetryListener {

    private final CarService carService;

    public StatusUpdateResponse changeCarStatus(AuthenticationInfo authenticationInfo, Long carId, CarState carState) {
        try {
            return carService.changeCarStatus(authenticationInfo, carId, carState);
        } catch (Exception e) {
            log.warn("Error while trying to change car status when booking is created: {}", e.getMessage());

            return new StatusUpdateResponse(false);
        }
    }

    public StatusUpdateResponse updateCarsStatuses(AuthenticationInfo authenticationInfo,
                                                   List<UpdateCarRequest> carsForUpdate) {
        try {
            return carService.updateCarsStatuses(authenticationInfo, carsForUpdate);
        } catch (Exception e) {
            log.warn("Error while trying to change cars statuses when booking is updated: {}", e.getMessage());

            return new StatusUpdateResponse(false);
        }
    }

}
