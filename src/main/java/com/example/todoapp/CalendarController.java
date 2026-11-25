package com.example.todoapp;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

    private static final ZoneId JAPAN_TIME_ZONE = ZoneId.of("Asia/Tokyo");

    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        YearMonth currentMonth = YearMonth.now(JAPAN_TIME_ZONE);
        YearMonth displayedMonth = YearMonth.of(
                year != null ? year : currentMonth.getYear(),
                month != null ? month : currentMonth.getMonthValue());

        model.addAttribute("year", displayedMonth.getYear());
        model.addAttribute("month", displayedMonth.getMonthValue());
        model.addAttribute("firstDay", displayedMonth.atDay(1));
        model.addAttribute("lastDay", displayedMonth.atEndOfMonth());
        model.addAttribute("previousMonth", displayedMonth.minusMonths(1));
        model.addAttribute("nextMonth", displayedMonth.plusMonths(1));
        model.addAttribute("weeks", createWeeks(displayedMonth));
        return "calendar";
    }

    private List<List<LocalDate>> createWeeks(YearMonth displayedMonth) {
        List<List<LocalDate>> weeks = new ArrayList<>();
        List<LocalDate> week = new ArrayList<>();
        int firstDayOffset = displayedMonth.atDay(1).getDayOfWeek().getValue() % 7;

        for (int index = 0; index < firstDayOffset; index++) {
            week.add(null);
        }

        for (int dayOfMonth = 1; dayOfMonth <= displayedMonth.lengthOfMonth(); dayOfMonth++) {
            week.add(displayedMonth.atDay(dayOfMonth));
            if (week.size() == 7) {
                weeks.add(week);
                week = new ArrayList<>();
            }
        }

        if (!week.isEmpty()) {
            while (week.size() < 7) {
                week.add(null);
            }
            weeks.add(week);
        }
        return weeks;
    }
}
