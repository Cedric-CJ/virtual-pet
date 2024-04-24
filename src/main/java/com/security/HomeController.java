package com.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.Map;

@RestController
public class HomeController {

    @RestController
    public class GreetingController {

        private static final String template = "Hello, %s!";

        @GetMapping("/greeting")
        public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
            return String.format(template, name);
        }
    }

    @GetMapping("/home")
    public Map<String, Object> homeData() {
        return Map.of(
                "username", "PixelUser",
                "daysAlive", 45,
                "stats", Map.of(
                        "Hunger", 100,
                        "Trinken", 100,
                        "Energie", 100,
                        "Komfort", 100
                )
        );
    }
}