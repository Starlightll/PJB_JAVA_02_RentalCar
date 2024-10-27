package com.rentalcar.rentalcar.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "Invalid email or password.";
        boolean showConfirmationLink = false;
        String email = request.getParameter("email");

        if (exception.getCause() instanceof UsernameNotFoundException) {
            errorMessage = "No account found with that email.";
        } else if (exception instanceof LockedException) {
            errorMessage = "Your account has been locked. Please contact support.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Your account is not active. Please activate your account.";
            showConfirmationLink = true;
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Either email address or Password is incorrect. Please try again";
        } else if (exception instanceof AccountExpiredException) {
            errorMessage = "Your account has expired. Please contact support.";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "Your credentials have expired. Please reset your password.";
        }
        response.sendRedirect("/login?error=true&message=" + errorMessage + "&confirmation=" + showConfirmationLink
                + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8));
    }
}
