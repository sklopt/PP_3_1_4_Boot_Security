package ru.kata.spring.boot_security.pp.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    add user in database
//    insert into roles (name) values ('ROLE_USER'), ('ROLE_ADMIN');
//    insert into users (username, password, email) values ('sklopt', '$2a$12$gfmMgOy3UbCSbQcjeyTNTeqC0Z2xd9XsdF/0c.eYSBHqOc6D2/jGe', 'sklopt@rambler.ru');
//    insert into users_roles (user_id, role_id) VALUES (1, 2);
//    insert into users_roles (user_id, role_id) VALUES (1, 1);
//    insert into users (username, password, email) values ('crazyman', '$2a$12$gfmMgOy3UbCSbQcjeyTNTeqC0Z2xd9XsdF/0c.eYSBHqOc6D2/jGe', 'crazyman@mail.ru');
//    insert into users_roles (user_id, role_id) VALUES (2, 1);

    private final SuccessUserHandler successUserHandler;

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(SuccessUserHandler successUserHandler, UserDetailsService userDetailsService) {
        this.successUserHandler = successUserHandler;
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/admin/**", "/add", "/new", "/{id}/**").hasRole("ADMIN")
                        .requestMatchers("/user").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/admin/**", "/add", "/new", "/{id}/**").hasAuthority("ADMIN")
                )
                .formLogin()
                .loginPage("/login")
                .successHandler(successUserHandler)
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}