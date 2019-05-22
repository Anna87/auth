package com.auth.java.config;

import com.auth.java.fiter.JwtAuthenticationFilter;
import com.auth.java.fiter.JwtUsernameAndPasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.stream.Collectors;

@EnableWebSecurity
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    private static final Logger logger = LoggerFactory.getLogger(SecurityCredentialsConfig.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                //.addFilterBefore(jwtUsernameAndPasswordAuthenticationFilter(), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private JwtUsernameAndPasswordAuthenticationFilter jwtUsernameAndPasswordAuthenticationFilter() throws Exception {
        JwtUsernameAndPasswordAuthenticationFilter filter = new JwtUsernameAndPasswordAuthenticationFilter(
                authenticationManager(),
                jwtConfig
        );
        filter.setAuthenticationSuccessHandler(
                (request, response, authentication) ->
                    //Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                    //Optional<String> role = roles.stream().findFirst();
                    //String r = role.orElseThrow("");
                    objectMapper.writeValue(
                            response.getOutputStream(),
                            ImmutableMap.of(jwtConfig.getHeader(), createToken(authentication),
                                    "roles", AuthorityUtils.authorityListToSet(authentication.getAuthorities()),
                                    "userName", authentication.getName())
                    )
                );
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    private String createToken(Authentication auth){
        Long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                //.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            //Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(authToken);
            Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
