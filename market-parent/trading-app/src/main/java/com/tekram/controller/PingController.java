package com.tekram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tekram.response.Result;

@RestController
public class PingController {

    @GetMapping("/ping")
    public Result ping() {
        return Result.success("market is running") ;
    }
}