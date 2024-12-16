package yourstyle.com.shope.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import yourstyle.com.shope.controller.site.accounts.LoginWithFBGGController;
import yourstyle.com.shope.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    LoginWithFBGGController loginWithFBGGController;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/*").permitAll()
                        .requestMatchers("/static/**", "/images/**", "/uploads/**", "/site/**").permitAll()
                        .requestMatchers("/yourstyle/home", "/yourstyle/discount/**").permitAll()
                        .requestMatchers("/yourstyle/product/detail/**").permitAll()
                        .requestMatchers("/yourstyle/best-sellers**").permitAll()
                        .requestMatchers("/yourstyle/accounts/**").permitAll()
                        .requestMatchers("/yourstyle/product/**").permitAll()
                        .requestMatchers("/yourstyle/productfavorites/**").permitAll()
                        .requestMatchers("/yourstyle/product/detail/**").permitAll()
                        .requestMatchers("/yourstyle/allproduct/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/yourstyle/carts/**", "/yourstyle/VNPays/**", "/yourstyle/order/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER", "ROLE_EMPLOYEE")
                        .requestMatchers("/yourstyle/admin/slide/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/yourstyle/accounts/login")
                        .loginProcessingUrl("/yourstyle/accounts/login")
                        .failureHandler((request, response, exception) -> {
                            if (exception instanceof LockedException) {
                                response.sendRedirect("/yourstyle/accounts/login?locked=true");
                            } else {
                                response.sendRedirect("/yourstyle/accounts/login?error=true");
                            }
                        })
                        .usernameParameter("username")
                        .passwordParameter("password")
                        // .failureHandler((request, response, authenticationException) -> {
                        // System.out.println("Login failed: " + authenticationException.getMessage());
                        // })
                        .successHandler((request, response, authentication) -> {
                            boolean rememberMe = request.getParameter("remember-me") != null;
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
                            if (isAdmin) {
                                response.sendRedirect("/admin/home");
                            } else {
                                response.sendRedirect("/yourstyle/home");
                            }
                        }))
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/yourstyle/accounts/login")
                        .successHandler(loginWithFBGGController)
                        .failureUrl("/yourstyle/accounts/login")
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                        .and())
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                        .rememberMeParameter("remember-me")
                        .useSecureCookie(false)
                        .alwaysRemember(false))
                .logout(logout -> logout
                        .logoutUrl("/yourstyle/accounts/logout")
                        .logoutSuccessUrl("/yourstyle/accounts/login")
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .invalidateHttpSession(true));

        return http.build();
    }
}
