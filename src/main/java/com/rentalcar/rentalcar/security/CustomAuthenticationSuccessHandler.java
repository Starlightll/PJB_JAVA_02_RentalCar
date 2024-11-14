package com.rentalcar.rentalcar.security;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.service.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;


public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private UserDetailsServiceImpl userDetailsService;


    public void setUserDetailsService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();

        String email = authentication.getName();
        User user;
        try {
            user = userDetailsService.loadUserByEmail(email);
        } catch (Exception e) {
            // Xử lý lỗi ở đây, ví dụ như ghi log hoặc chuyển hướng về trang lỗi
            response.sendRedirect("/error");
            return;
        }

        if (user != null) {
            session.setAttribute("user", user);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/homepage-guest";  // Giá trị chuyển hướng mặc định

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("Customer")) {
                redirectUrl = "/homepage-customer";
                break;
            } else if (role.equals("Car Owner")) {
                redirectUrl = "/homepage-carowner";
                break;
            } else if (role.equals("Admin")) {
                redirectUrl = "/admin/dashboard";
                break;
            }
        }

        // Thêm chuyển hướng sau login
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl(redirectUrl);
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }
}