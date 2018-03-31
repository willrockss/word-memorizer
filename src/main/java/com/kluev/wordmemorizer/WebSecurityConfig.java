package com.kluev.wordmemorizer;

import com.kluev.wordmemorizer.web.controller.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DBUtils db;

    @Autowired
    DataSource ds;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/add-word/**").hasAnyAuthority("ADMIN", "EDITOR")
            .antMatchers("/add-dict/**").hasAnyAuthority("ADMIN", "EDITOR")
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll().successHandler(new MySuccessHander().setDb(db))
            .and()
            .logout()
            .permitAll();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/*");
        web.ignoring().antMatchers("/js/*");
        web.ignoring().antMatchers("/img/*");
        web.ignoring().antMatchers("/video/*");
    }

    private static class MySuccessHander extends SavedRequestAwareAuthenticationSuccessHandler {

        public MySuccessHander setDb(DBUtils db) {
            this.db = db;
            return this;
        }

        private DBUtils db;

        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response, Authentication authentication)
                                            throws IOException, ServletException
        {
            // Логгируем успешный логин
            Object principal = authentication.getPrincipal();
            UserDetails details =  principal instanceof  UserDetails ? (UserDetails) principal : null;
            String username = details != null ? details.getUsername() : null;
            db.addLoginInfo(username);
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager s = new JdbcUserDetailsManager();
        s.setDataSource(ds);
        return s;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("admin"));
    }
}