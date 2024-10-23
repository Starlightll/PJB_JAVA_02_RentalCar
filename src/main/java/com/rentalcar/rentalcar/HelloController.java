package com.rentalcar.rentalcar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class HelloController {

   @GetMapping ("/hello")
    public String sayHello() {
        return "Hello, world!";
    }

    @GetMapping ("/")
    public String sayIndex() {
        return "HomepageForGuest";
    }

    @GetMapping ("/login1")
    public String sayLogin() {
        return "Login";
    }

    @GetMapping ("/register")
    public String sayRegister() {
        return "Register";
    }

    @GetMapping("properties")
    @ResponseBody
    java.util.Properties properties() {
        return System.getProperties();
    }
}
