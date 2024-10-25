package com.rentalcar.rentalcar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeForCarOwnerController {


    @GetMapping("/homepage-carowner")
    public String carOwnerHomepage() {
        return "HomepageForCarOwner"; // Return the view for car owner
    }


    @GetMapping("/homepage-carowner/my-profile")
    public String myProfileHomepage() {
        return "MyProfile_ChangPassword";
    }
}
