// package yourstyle.com.shope.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class WebConfig {

	 
//     @Bean
//     public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
//         return http.csrf(csrf -> csrf.disable())
//                 .authorizeHttpRequests((authz) -> authz
//                         .requestMatchers("/yourstyle/order/**").authenticated() // phải đăng nhập mới được phép truy cập
//                         // .requestMatchers("/admin/**").hasAnyRole("STAF","DIRE")
//                         // .requestMatchers("/rest/authorities").hasRole("DIRE")
//                         .anyRequest().permitAll() // những đường dẫn còn lại được phép truy cập

//                 ).formLogin(form -> form.loginPage("/security/login/form")
//                         .loginProcessingUrl("/security/login")
//                         .defaultSuccessUrl("/home", true) // Điều hướng đến trang user sau khi đăng nhập
//                                                           // thành công
//                         .failureUrl("/security/login/error")

//                 ).rememberMe(remem -> remem.tokenValiditySeconds(86400) // 86400 = 24h
//                         .rememberMeParameter("remember-me"))
//                 .exceptionHandling(exception -> exception.accessDeniedPage("/security/unauthorized/")

//                 ).logout(out -> out.logoutUrl("/security/logout")
//                         .logoutSuccessUrl("/security/logout/success"))
//                 .build();
//     }
//     @Bean
//     public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//         InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//         manager.createUser(org.springframework.security.core.userdetails.User.withUsername("user")
//                 .password(passwordEncoder.encode("password"))
//                 .roles("USER")
//                 .build());
//         return manager;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
//     }

// }
