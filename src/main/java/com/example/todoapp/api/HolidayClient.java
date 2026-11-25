package com.example.todoapp.api;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HolidayClient {

    private final RestClient restClient;

    public HolidayClient() {
        this.restClient = RestClient.create();
    }

    public Map<String, String> getHolidays(LocalDate from, LocalDate to) {
        Map<String, String> holidays = restClient.get()
                .uri("https://holidays-jp.github.io/api/v1/date.json")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return holidays.entrySet().stream()
                .filter(holiday -> isInRange(LocalDate.parse(holiday.getKey()), from, to))
                .collect(LinkedHashMap::new,
                        (result, holiday) -> result.put(holiday.getKey(), holiday.getValue()),
                        LinkedHashMap::putAll);
    }

    private boolean isInRange(LocalDate date, LocalDate from, LocalDate to) {
        return (from == null || !date.isBefore(from))
                && (to == null || !date.isAfter(to));
    }
}
