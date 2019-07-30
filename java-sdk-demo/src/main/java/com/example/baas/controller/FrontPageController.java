package com.example.baas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author John
 * @create 2019/7/12 10:01
 */
@Controller
public class FrontPageController {

    @GetMapping(value = {"/","index"})
    public String index(){
        return "index";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }
}
