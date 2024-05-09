package com.ydg.project.be.lottofinder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/server")
public class ServerController {

    @GetMapping("/health-check")
    public Mono<Void> healthCheck() {
        return Mono.empty();
    }
}
