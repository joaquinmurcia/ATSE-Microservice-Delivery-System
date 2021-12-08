package edu.tum.ase.authentication_controller.config;

import edu.tum.ase.authentication_controller.service.MongoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    MongoUserDetailsService mongoUserDetailsService;
    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(mongoUserDetailsService);
    }


// Http Config, Authentication Manager Bean Definition, and BcryptPasswordEncoder

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin() // 1. Use Spring Login Form
                .loginPage("/auth").permitAll()
                .and()
                .csrf().disable() // Note: for demonstration purposes only, this should not be done
                .authorizeRequests() // 2. Require authentication in all endpoints except login
                .antMatchers("/**").authenticated()
                //.antMatchers("/auth/**").permitAll()
                .and()
                .httpBasic() // 3. Use Basic Authentication
                .and()
                .sessionManagement().disable();
    }
    @Override

    @Bean
// Define an authentication manager to execute authentication services
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Bean
// Define an instance of Bcrypt for hashing passwords
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

