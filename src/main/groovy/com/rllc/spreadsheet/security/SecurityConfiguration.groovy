package com.rllc.spreadsheet.security

import com.rllc.spreadsheet.domain.Congregation
import com.rllc.spreadsheet.props.CongregationPropertyLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

/**
 * Created by Steven McAdams on 6/11/15.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CongregationPropertyLoader congregationPropertyLoader

    /**
     * This section defines the user accounts which can be used for
     * authentication as well as the roles each user has.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        congregationPropertyLoader.congregations.each { String name, Congregation congregation ->
            auth.inMemoryAuthentication().withUser(name).password(name).roles('USER')
        }
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity
                .ignoring()
                .antMatchers("/dist/**")
                .antMatchers(HttpMethod.GET, "/")
                .antMatchers(HttpMethod.GET, "/congregations/**")
                .antMatchers(HttpMethod.GET, "/sermons/**")
                .antMatchers(HttpMethod.GET, "/books/**")
                .antMatchers(HttpMethod.GET, "/bibletext/**")
                .antMatchers(HttpMethod.GET, "/ministers/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().and()
                .authorizeRequests()
                .anyRequest().fullyAuthenticated().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable();
    }
}