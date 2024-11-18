package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.MyProfileDto;
import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.service.MyProfileService;
import com.rentalcar.rentalcar.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


@Controller
public class MyProfileController {

    @Autowired
    MyProfileService myProfileService;
    @Autowired
    private UserService userService;


    @GetMapping("/change-password")
    public String changePassword(Model model, HttpSession session) {
        model.addAttribute("activeTab", "Security"); // Set active tab to Security

        if (!model.containsAttribute("myProfileDto")) {
            model.addAttribute("myProfileDto", new MyProfileDto());
        }
        User user = (User) session.getAttribute("user");

        //truyền dữ liệu cho personal in4
        model.addAttribute("userInfo", user);
        return "MyProfile_ChangPassword";
    }

    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleChangePassword(
            @Valid @ModelAttribute("myProfileDto") MyProfileDto myProfileDto,
            BindingResult result, HttpSession session) {

        Map<String, String> response = new HashMap<>();
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response); // Trả về lỗi 400 kèm thông báo lỗi
        }

        try {
            myProfileService.changePassword(myProfileDto, session);
            response.put("success", "Your password has been changed successfully!");
            return ResponseEntity.ok(response); // Trả về thành công
        } catch (UserException ex) {
            switch (ex.getMessage()) {
                case "User not found":
                    response.put("general", "User not found.");
                    break;
                case "Old password is incorrect":
                    result.rejectValue("oldPassword", "error.oldPassword", "Current password is incorrect");
                    break;
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "New password and Confirm password don’t match");
                    break;
            }
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response); // Trả về lỗi kèm thông báo lỗi
        }
    }




    @GetMapping("/my-profile")
    public String myProfile(Model model, HttpSession session) {
        model.addAttribute("activeTab", "PersonalInfo"); // Set default tab
        model.addAttribute("myProfileDto", new MyProfileDto());
        User user = (User) session.getAttribute("user");

        model.addAttribute("userInfo", user);
        return "MyProfile_ChangPassword";
    }


    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("userInfo") UserInfoDto userInfoRequest,
                           BindingResult bindingResult,
                           HttpSession session,
                           RedirectAttributes models , Model model,
                           @RequestParam(value = "drivingLicense", required = false) MultipartFile drivingLicense) {

        User user = (User) session.getAttribute("user");

        if (!userInfoRequest.getPhone().equals(user.getPhone())
                && userService.checkPhone(userInfoRequest.getPhone())) {
            bindingResult.rejectValue("phone", "error.userInfo", "Phone number already exists");
        }
        if (drivingLicense.isEmpty() && user.getDrivingLicense().isEmpty()) {
            bindingResult.rejectValue("drivingLicense", "error.userInfo", "File upload is required : ");

        }else{

            try {
                String fileName = drivingLicense.getOriginalFilename();
                if(fileName.length() > 50){
                    bindingResult.rejectValue("drivingLicense", "error.userInfo", "Filename is too long. Please rename the file.");

                }
                if (!fileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                    bindingResult.rejectValue("drivingLicense", "error.userInfo", "Invalid file extension. Only JPG, PNG, and GIF are allowed.");

                }

                String uploadDir = "uploads/DriveLicense/" + user.getId()+ "_" + user.getUsername() +  "/"; // Specify your upload directory
                Path filePath = Paths.get(uploadDir, fileName);
                Files.write(filePath, drivingLicense.getBytes());

                // Set the file path in the new field
                userInfoRequest.setDrivingLicensePath(filePath.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String dobError = null;
        String emailError = null;
        String nationalIdError = null;
        String phoneError = null;
        String drivingLicenseError = null;
        String streetError = null;
        String fullNameError = null;
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                switch (error.getField()) {
                    case "dob":
                        dobError = error.getDefaultMessage();
                        break;
                    case "email":
                        emailError = error.getDefaultMessage();
                        break;
                    case "nationalId":
                        nationalIdError = error.getDefaultMessage();
                        break;
                    case "phone":
                        phoneError = error.getDefaultMessage();
                        break;
                    case "drivingLicense":
                        drivingLicenseError = error.getDefaultMessage();
                        break;
                    case "street":
                        streetError = error.getDefaultMessage();
                        break;
                    case "fullName":
                        fullNameError = error.getDefaultMessage();
                        break;
                }
            }

            // Thêm các thông điệp lỗi vào model để hiển thị lại
            models.addFlashAttribute("dobError", dobError);
            models.addFlashAttribute("emailError", emailError);
            models.addFlashAttribute("nationalIdError", nationalIdError);
            models.addFlashAttribute("phoneError", phoneError);
            models.addFlashAttribute("streetError", streetError);
            models.addFlashAttribute("fullNameError", fullNameError);
            models.addFlashAttribute("drivingLicenseError", drivingLicenseError);

            //User user = (User) session.getAttribute("user");

            // model.addAttribute("userInfo", user);
            // Return the view with the current user info to display errors
            //  model.addAttribute("userInfo", userInfoRequest);
            // return "MyProfile_ChangPassword"; // Ensure this is the correct view name

            return "redirect:/my-profile";
        }


        userService.saveUser(userInfoRequest, session);
        models.addFlashAttribute("success1", "Update successfully!!!");
        return "redirect:/my-profile";
    }

}
