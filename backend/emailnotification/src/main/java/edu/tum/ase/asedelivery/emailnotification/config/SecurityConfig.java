package edu.tum.ase.asedelivery.emailnotification.config;

import edu.tum.ase.asedelivery.emailnotification.filter.AuthRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    MongoUserDetailsService mongoUserDetailsService;
//
//    @Override
//    public void configure(AuthenticationManagerBuilder builder) throws Exception {
//        builder.userDetailsService(mongoUserDetailsService);
//    }

    @Autowired
    AuthRequestFilter authRequestFilter;


// Http Config, Authentication Manager Bean Definition, and BcryptPasswordEncoder

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(authRequestFilter, UsernamePasswordAuthenticationFilter.class);
//
//                    .antMatchers("/**").authenticated()
//                .and()
//                .sessionManagement().disable();
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

