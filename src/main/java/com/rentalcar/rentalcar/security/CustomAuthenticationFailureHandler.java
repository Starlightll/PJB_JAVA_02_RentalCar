package com.rentalcar.rentalcar.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "Invalid email or password.";

        if (exception.getCause() instanceof UsernameNotFoundException) {
            errorMessage = "No account found with that email.";
        } else if (exception instanceof LockedException) {
            errorMessage = "Your account has been locked. Please contact support.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Your account is not active. Please activate your account.";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid email or password.";
        } else if (exception instanceof AccountExpiredException) {
            errorMessage = "Your account has expired. Please contact support.";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "Your credentials have expired. Please reset your password.";
        }

        response.sendRedirect("/login?error=true&message=" + errorMessage);
    }
}
