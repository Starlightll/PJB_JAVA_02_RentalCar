package com.rentalcar.rentalcar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeForCustomerController {


    @GetMapping("/homepage-customer")
    public String customerHomepage() {
        return "HomepageForCustomer"; // Return the view for customer
    }


     @GetMapping("/homepage-customer/my-profile")
    public String myProfileHomepage() {
        return "MyProfile_ChangPassword";
     }

}
