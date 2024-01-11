package com.microservices.base.elastic.query.service.config;

import com.microservices.base.config.UserConfigData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity //This annotation along with WebSecurityConfigurerAdapter allows to add security logic to this class
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserConfigData userConfigData;

    //Didn't add pathsToIgnore to any configuration class, instead used @Value annotation to fetch the path
    @Value("${security.paths-to-ignore}") // Using @Value you can pick up any configuration defined in the configuration file and use it in any classes
    private String[] pathsToIgnore;

    public WebSecurityConfig(UserConfigData userConfigData) {
        this.userConfigData = userConfigData;
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring()
                .antMatchers(pathsToIgnore);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        /* Allow all paths and bypass authentication
        httpSecurity.authorizeRequests()
                .antMatchers("/**")
                .permitAll(); */
        httpSecurity
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/**").hasRole("USER")
                .and()
                .csrf()
                .disable(); // csrf, also known as session riding, protection is only required if we are interacting with browser in which case csrf protection will request to add a token to be sent from browser to server for authentication to stop unwanted attacks to the website in which the user is already logged in and has an active session.
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(userConfigData.getUsername())
                //if we use the below method, we will see in postman a basic authorization header is added to the request and username and password is sent to server for auth by encoding with Base64 encoding.
                //.password("{noop}****") -> "no operation password" for plain text, it's temporary and doesn't look good, only for local testing
                //we shouldn't work with noop password for plain text, and there will be an error as well in the app, it will ask for password encoder
                //we should use password encoder, in that case we won't hold the plain text password in-memory, but only hold a hashed version and do the comparison with a hashed value with a user provided password
                .password(passwordEncoder().encode(userConfigData.getPassword()))
                //.password(userConfigData.getPassword()) // issue no password encoder specified for id null
                .roles(userConfigData.getRoles());
    }

    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // uses BCrypt strong hashing function which is an adaptive function and uses iteration count to increase difficulty against brute force attacks in addition to using salts to protect against rainbow table attacks.
    }
}
