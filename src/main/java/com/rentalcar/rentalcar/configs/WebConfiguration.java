package com.rentalcar.rentalcar.configs;
import com.rentalcar.rentalcar.security.CustomAuthenticationFailureHandler;
import com.rentalcar.rentalcar.security.CustomAuthenticationSuccessHandler;
import com.rentalcar.rentalcar.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class WebConfiguration extends WebSecurityConfigurerAdapter {
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        successHandler.setUserDetailsService((UserDetailsServiceImpl) userDetailsService());
        return successHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/homepage-guest").permitAll()
                .antMatchers("myProfile").hasAnyAuthority("Customer", "Car Owner")
                .antMatchers("/homepage-customer").hasAuthority("Customer")
                .antMatchers("/homepage-carowner").hasAuthority("Car Owner")
                .antMatchers("/css/**", "/js/**", "/vendor/**", "/fonts/**", "/images/**").permitAll()
                .antMatchers("/login/**", "/register/**", "/forgot-password", "/reset-password/**", "/send-activation").permitAll()
                .anyRequest().authenticated()
                .and()
          .formLogin().loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(new CustomAuthenticationFailureHandler())
                .permitAll()
                .and()
          .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID") // Xóa cookie nếu cần
                .and()
                .exceptionHandling().accessDeniedPage("/403")
        ;
    }
}
