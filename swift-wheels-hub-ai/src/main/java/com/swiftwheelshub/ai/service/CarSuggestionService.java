package com.swiftwheelshub.ai.service;

import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.TripInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CarSuggestionService implements RetryListener {

    private final ChatService chatService;
    private final CarService carService;

    public String getChatOutput(HttpServletRequest request, TripInfo tripInfo) {
        List<String> cars = getAvailableCars(request);
        String chatPrompt = createChatPrompt(tripInfo, cars);

        return chatService.openChatReply(chatPrompt);
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

    private String createChatPrompt(TripInfo tripInfo, List<String> cars) {
        return String.format(
                """
                        Which car from the following list %s is more suitable for rental from a rental car agency
                        for a trip for %s people to %s, Romania in %s? The car will be used for %s.""",
                cars,
                tripInfo.peopleCount(),
                tripInfo.destination(),
                getMonth(tripInfo.tripDate()),
                tripInfo.tripKind()
        );
    }

    private String getMonth(LocalDate tripDate) {
        return tripDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

}
