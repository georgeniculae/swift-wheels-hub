package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarSuggestionResponse;
import com.swiftwheelshub.dto.TripInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarSuggestionService implements RetryListener {

    private final ChatService chatService;
    private final CarService carService;

    public CarSuggestionResponse getChatOutput(HttpServletRequest request, TripInfo tripInfo) {
        List<String> cars = getAvailableCars(request);
        String text = getText();
        Map<String, Object> params = getParams(tripInfo, cars);

        return chatService.getChatReply(text, params);
    }

    private List<String> getAvailableCars(HttpServletRequest request) {
        return carService.getAllAvailableCars(request)
                .stream()
                .map(this::getCarDetails)
                .toList();
    }

    private String getCarDetails(CarResponse carResponse) {
        return carResponse.make() + " " + carResponse.model() + " from " + carResponse.yearOfProduction();
    }

    private String getText() {
        return """
                Which car from the following list {cars} is more suitable for rental from a rental car
                agency for a trip for {peopleCount} people to {destination}, Romania in {month}?
                The car will be used for {tripKind}.""";
    }

    private Map<String, Object> getParams(TripInfo tripInfo, List<String> cars) {
        return Map.of(
                "cars", cars,
                "peopleCount", tripInfo.peopleCount(),
                "destination", tripInfo.destination(),
                "month", getMonth(tripInfo.tripDate()),
                "tripKind", tripInfo.tripKind()
        );
    }

    private String getMonth(LocalDate tripDate) {
        return tripDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

}
