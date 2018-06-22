package org.example.app.web0;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ExampleController {

    @Value("${welcome.message}")
    private String welcomeMessage;

    @GetMapping("")
    public String index() {
        return welcomeMessage;
    }
}
