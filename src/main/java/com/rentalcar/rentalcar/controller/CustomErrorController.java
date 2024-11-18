package com.rentalcar.rentalcar.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {
    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @GetMapping("/error")
    public String handleError(WebRequest webRequest, Model model) {
        // Retrieve error attributes
        Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        // Set error attributes in the model
        model.addAttribute("status", errors.get("status"));
        model.addAttribute("error", errors.get("error"));
        model.addAttribute("message", errors.get("message"));

        // Return custom 404 template if the error is 404
        if ((Integer) errors.get("status") == 404) {
            return "error/404";
        }

        // Return custom 404 template if the error is 404
        if ((Integer) errors.get("status") == 403) {
            return "error/403";
        }

        // Return custom 404 template if the error is 404
        if ((Integer) errors.get("status") == 500) {
            return "error/500";
        }

        if ((Integer) errors.get("status") == 401) {
            return "error/403";
        }


        // Otherwise, return a generic error template
        return "error/error";
    }
}
