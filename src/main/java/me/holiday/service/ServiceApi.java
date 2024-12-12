package me.holiday.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/service/api/v1")
public class ServiceApi {

    @GetMapping("/hello")
    public String hello() {
        return "hello world !!!";
    }
}
