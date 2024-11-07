package com.rentalcar.rentalcar.security;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.service.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;


public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Setter
    private UserDetailsServiceImpl userDetailsService;

    private final SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();

        String email = authentication.getName();
        User user;
        try {
            user = userDetailsService.loadUserByEmail(email);
        } catch (Exception e) {
            // Log the error for troubleshooting
            e.printStackTrace();
            response.sendRedirect("/error");
            return;
        }

        if (user != null) {
            session.setAttribute("user", user);
        }

        // Set the default redirect URL based on user role
        String redirectUrl = determineRedirectUrl(authentication.getAuthorities());
        successHandler.setDefaultTargetUrl(redirectUrl);

        // Redirect after login
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }

    private String determineRedirectUrl(Collection<? extends GrantedAuthority> authorities) {
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("Customer")) {
                return "/homepage-customer";
            } else if (role.equals("Car Owner")) {
                return "/homepage-carowner";
            } else if (role.equals("Admin")) {
                return "/add-car";
            }
        }
        return "/homepage-guest"; // Default redirect URL
    }
}
