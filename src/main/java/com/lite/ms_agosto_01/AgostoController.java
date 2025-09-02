package com.lite.ms_agosto_01;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgostoController {

    @Value("${message}")
    private String message;

    @GetMapping("/")
    public String getAgosto() {
        return message;
    }
}
