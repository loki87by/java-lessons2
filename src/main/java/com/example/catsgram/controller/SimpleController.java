package com.example.catsgram.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleController {

    private  static final Logger log = LoggerFactory.getLogger(SimpleController.class);
    @GetMapping("/home")
    public String homePage() {
        log.info("Запрос получен");
        return "Котограм";
    }
}
