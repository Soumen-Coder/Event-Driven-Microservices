package com.microservices.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//The config server has a support for encrypt and decrypt text and provides us with built-in endpoints for both. Compatible with spring boot command line utility.
//Use postman -> POST http://localhost:8888/encrypt and send raw data : text
//Use postman -> POST http://localhost:8888/decrypt and send raw data : encrypted_password
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/actuator/**") //shell script created will try reaching the actuator endpoint, to see if config-server is up
                .antMatchers("/encrypt/**")
                .antMatchers("/decrypt/**");
        super.configure(web);
    }
}
