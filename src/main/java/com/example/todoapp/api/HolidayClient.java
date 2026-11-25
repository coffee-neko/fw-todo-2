package com.example.todoapp.api;

import java.time.LocalDate;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Component
public class HolidayClient {

    private final RestClient restClient;

    public HolidayClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    public Map<String, String> findHolidays(LocalDate from, LocalDate to) {
        Map<String, String> holidays = restClient.get()
                .uri("https://holidays-jp.github.io/api/v1/date.json")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (holidays == null) {
            throw new RestClientException("Holiday API returned an empty response");
        }

        return holidays.entrySet().stream()
                .filter(holiday -> isInRange(LocalDate.parse(holiday.getKey()), from, to))
                .collect(LinkedHashMap::new,
                        (result, holiday) -> result.put(holiday.getKey(), holiday.getValue()),
                        LinkedHashMap::putAll);
    }

    public HolidayApiResult findHolidaysForApi(LocalDate from, LocalDate to) {
        try {
            return new HolidayApiResult(findHolidays(from, to), false);
        } catch (RestClientException exception) {
            return new HolidayApiResult(Map.of(), true);
        }
    }

    private boolean isInRange(LocalDate date, LocalDate from, LocalDate to) {
        return (from == null || !date.isBefore(from))
                && (to == null || !date.isAfter(to));
    }

    public record HolidayApiResult(Map<String, String> holidays, boolean unavailable) {
    }
}
