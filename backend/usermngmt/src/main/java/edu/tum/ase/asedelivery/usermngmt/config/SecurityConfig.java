package edu.tum.ase.asedelivery.usermngmt.config;

import edu.tum.ase.asedelivery.usermngmt.service.MongoUserDetailsService;
import edu.tum.ase.asedelivery.usermngmt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


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
        http.csrf().disable()
                .authorizeRequests() // 2. Require authentication in all endpoints except login
                    .antMatchers("/auth").permitAll()
                    .antMatchers("/**").permitAll()
                    .and()
                    .httpBasic() // 3. Use Basic Authentication
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
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

