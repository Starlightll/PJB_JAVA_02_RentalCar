package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.MyProfileDto;
import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.MyProfileService;
import com.rentalcar.rentalcar.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Objects;


/**
 * The MyProfileController class provides endpoints to manage user profile related actions
 * including password change, avatar update, and profile information update.
 */

@Controller
public class MyProfileController {

    @Autowired
    MyProfileService myProfileService;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;


    @GetMapping("/change-passwordold")
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

    @PostMapping("/change-passwordold")
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

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePasswordV2(
            @RequestParam(value = "currentPassword") String currentPassword,
            @RequestParam(value = "newPassword") String newPassword,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.put("error", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(currentPassword);
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                response.put("wrongPass", "Old password is incorrect");
                return ResponseEntity.badRequest().body(response);
            }
            if(userService.changePassword(user, currentPassword, newPassword)){
                //Log out user after changing password
                userService.logout(session);
                response.put("success", "Password changed");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(response);
        } catch (UserException ex) {
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/my-profileold")
    public String myProfile(Model model, HttpSession session) {
        model.addAttribute("activeTab", "PersonalInfo"); // Set default tab
        model.addAttribute("myProfileDto", new MyProfileDto());
        User user = (User) session.getAttribute("user");

        model.addAttribute("userInfo", user);
        return "MyProfile_ChangPassword";
    }

    @GetMapping("/my-profile")
    public String myProfileV2(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        User userInfo = userRepo.getUserById(user.getId());

        model.addAttribute("userInfo", userInfo);
        return "MyProfile";
    }

    @PostMapping("/update-avatar")
    public ResponseEntity<Map<String, Object>> updateAvatar(
            @RequestParam(value = "avatar") MultipartFile avatar,
            HttpSession httpSession) {
        User user = userRepo.getUserById(((User) httpSession.getAttribute("user")).getId());
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        userService.setUserAvatar(user, avatar);
        User logedUser = (User) httpSession.getAttribute("user");
        // Update avatar in session if the user is the loged user
        if (Objects.equals(user.getId(), logedUser.getId())) {
            logedUser.setAvatar(user.getAvatar());
            httpSession.setAttribute("user", logedUser);
        }
        return ResponseEntity.ok(Map.of("success", "Avatar updated"));
    }


    @PostMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @ModelAttribute("user") User user,
            @RequestParam(value = "drivingLicenseFile", required = false) MultipartFile drivingLicenseFile,
            BindingResult result,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            response.put("errors", result.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }
        User userInSession = (User) session.getAttribute("user");
        //Check if the user is updating their own profile
        if (!Objects.equals(user.getId(), userInSession.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN);
        }
        try {
            userService.updateProfile(user, drivingLicenseFile, session);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("userInfo") UserInfoDto userInfoRequest,
                           BindingResult bindingResult,
                           HttpSession session,
                           RedirectAttributes models, Model model,
                           @RequestParam(value = "drivingLicense", required = false) MultipartFile drivingLicense) {

        User user = (User) session.getAttribute("user");

        if (!normalizePhone(userInfoRequest.getPhone()).equals(user.getPhone())
                && userService.checkPhone(normalizePhone(userInfoRequest.getPhone()))) {
            bindingResult.rejectValue("phone", "error.userInfo", "Phone number already exists");
        }
        if (drivingLicense.isEmpty() && user.getDrivingLicense() == null) {
            bindingResult.rejectValue("drivingLicense", "error.userInfo", "File upload is required : ");

        } else {

            try {
                String fileName = drivingLicense.getOriginalFilename();
                if (fileName.length() > 50) {
                    bindingResult.rejectValue("drivingLicense", "error.userInfo", "Filename is too long. Please rename the file.");

                } else if (!fileName.matches(".*\\.(jpg|jpeg|png|gif)$") && !fileName.isEmpty()) {
                    bindingResult.rejectValue("drivingLicense", "error.userInfo", "Invalid file extension. Only JPG, PNG, and GIF are allowed.");

                } else if (drivingLicense.getSize() > 200 * 1024 * 1024) { // 200MB = 200 * 1024 * 1024 bytes
                    bindingResult.rejectValue("drivingLicense", "error.userInfo", "File size exceeds 200MB. Please upload a smaller file.");
                } else {
                    String uploadDir = "uploads/User/" + user.getId(); // Specify your upload directory
                    Path filePath = Paths.get(uploadDir, fileName);
                    Files.write(filePath, drivingLicense.getBytes());

                    // Set the file path in the new field
                    userInfoRequest.setDrivingLicensePath(filePath.toString());
                }


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

    private String normalizePhone(String phone) {
        // Loại bỏ khoảng trắng và các ký tự không phải số
        phone = phone.trim().replaceAll("\\D", "");

        // Nếu số bắt đầu bằng "0", thay thế "0" bằng "+84" để chuẩn hóa quốc tế
        if (phone.startsWith("0")) {
            phone = "+84" + phone.substring(1);
        } else if (!phone.startsWith("+")) {
            // Trường hợp khác: nếu không bắt đầu bằng "+" và không phải dạng chuẩn
            phone = "+84" + phone;
        }

        return phone;
    }

    // Controller
    @GetMapping("/my-info")
    public ResponseEntity<?> myInfo(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User userInfo = userRepo.getUserById(user.getId());
            if (userInfo == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error getting user info");
        }

    }


}
