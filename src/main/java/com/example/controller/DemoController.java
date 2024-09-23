package com.example.controller;

import com.example.domain.QuartzService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final QuartzService quartzService;

    @GetMapping("/test")
    public String test() {
        quartzService.init();

        return "ok";
    }
}
