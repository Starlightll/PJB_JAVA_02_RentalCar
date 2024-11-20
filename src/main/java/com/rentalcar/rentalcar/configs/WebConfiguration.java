package com.rentalcar.rentalcar.configs;
import com.rentalcar.rentalcar.security.CustomAuthenticationFailureHandler;
import com.rentalcar.rentalcar.security.CustomAuthenticationSuccessHandler;
import com.rentalcar.rentalcar.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableAsync
public class WebConfiguration {
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        successHandler.setUserDetailsService((UserDetailsServiceImpl) userDetailsService());
        return successHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/login", "/register", "/homepage-carowner", "/homepage-customer", "/homepage-guest", "/agree-term-service")) // Tắt CSRF cho các URL này
                .authorizeHttpRequests(requests -> requests
                        // Các URL công khai, không yêu cầu xác thực
                        .requestMatchers("/", "/homepage-guest", "/error/**", "/faq", "/contacts", "/privacy").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/vendor/**", "/fonts/**", "/images/**", "/assets/**").permitAll()
                        .requestMatchers("/login/**", "/register/**", "/forgot-password", "/reset-password/**", "/send-activation", "/agree-term-service").permitAll()
                        .requestMatchers("/myProfile", "/change-password").hasAnyAuthority("Customer", "Car Owner", "Admin")
                        .requestMatchers("/homepage-customer").hasAnyAuthority("Customer", "Admin")
                        .requestMatchers("/customer/**").hasAnyAuthority("Customer", "Admin")
                        .requestMatchers("/homepage-carowner", "/car-owner/**", "/car-draft/**", "/carAPI/**").hasAnyAuthority("Car Owner", "Admin")
                        .requestMatchers("/admin/**").hasAuthority("Admin")

                        // Mọi yêu cầu khác đều yêu cầu xác thực
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureHandler(new CustomAuthenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                ;

        return http.build();
    }

}
