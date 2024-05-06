package com.example.catsgram.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class SimpleController {

    @GetMapping("/home")
    public String homePage(HttpServletRequest req) {
        log.info("Получен запрос к эндпоинту: '{}{}',\nСтрока параметров запроса: '{}'",
                req.getMethod(),
                req.getRequestURI(),
                req.getQueryString());
        return "Котограм";
    }
}
