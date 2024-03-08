package ch.so.agi.datahub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HtmlController {
    @GetMapping("/public/foo")
    public String hello(Model model) {
        model.addAttribute("message", "Hello World!");
        return "foo";
    }

//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }

    
    @GetMapping("/web/foo")
    public String foo(Model model) {
        model.addAttribute("message", "Hello World!");
        return "foo";
    }


}
