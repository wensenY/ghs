package com.ghs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ghs")
public class GhsController {


    @GetMapping("/test")
    public String test() {
        return "test";
    }




}
