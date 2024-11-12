package yourstyle.com.shope.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/*").permitAll()
                        .requestMatchers("/static/**", "/images/**", "/uploads/**", "/site/**").permitAll()
                        .requestMatchers("/yourstyle/home").permitAll()
                        .requestMatchers("/yourstyle/accounts/**").permitAll()
                        .requestMatchers("/yourstyle/product/**").permitAll()
                        .requestMatchers("/product/detail/**").permitAll()
                        .requestMatchers("/yourstyle/productfavorites/").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/yourstyle/carts/**", "/yourstyle/VNPays/**", "/yourstyle/order/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER", "ROLE_EMPLOYEE")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/yourstyle/accounts/login")
                        .loginProcessingUrl("/yourstyle/accounts/login")
                        .failureUrl("/yourstyle/accounts/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        // .failureHandler((request, response, authenticationException) -> {
                        // System.out.println("Login failed: " + authenticationException.getMessage());
                        // })
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
                            if (isAdmin) {
                                response.sendRedirect("/admin/home");
                            } else {
                                response.sendRedirect("/yourstyle/home");
                            }
                        }))
                .logout(logout -> logout
                        .logoutUrl("/yourstyle/accounts/logout")
                        .logoutSuccessUrl("/yourstyle/accounts/login")
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "loggedInUser")
                        .invalidateHttpSession(true));

        return http.build();
    }
}

