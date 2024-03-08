package ch.so.agi.datahub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/web/welcome")
    public String greeting() {
        return "welcome";
    }

    @GetMapping("/web/login")
    public String login(){
        return "login";
    }
}
