package com.microservices.base.elastic.query.web.client.config;

import com.microservices.base.config.UserConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserConfigData userConfigData; //to be able to obtain the user credentials n this class

    public WebSecurityConfig(UserConfigData userConfigData) {
        this.userConfigData = userConfigData;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/")
                //allow of the index page by using / in antMatchers and permitAll() method
                .permitAll()
                .antMatchers("/**")
                //to authorize all parts with a role "USER"
                .hasRole("USER")
                .anyRequest()
                .fullyAuthenticated();//full authentication for any request which means do not allow for anonymous users but allow for remember me user
        //Difference b/w full authentication and authentication is that full authentication allows to use "remember me" option
        //Note: we didn't configure logout functionality here, although we have a logout button on front-end
        //this is because httpBasic authentication doesn't have an automatic logout functionality
        //Required to do some tricks to logout users and since this basic authentication is temporary, we will implement oauth in the next modules
        //leaving logout functionality to that module.
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(userConfigData.getUsername())
                .password(passwordEncoder().encode(userConfigData.getPassword())) //encode with bcrypt encoder instead of plain text password
                .roles(userConfigData.getRoles());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
