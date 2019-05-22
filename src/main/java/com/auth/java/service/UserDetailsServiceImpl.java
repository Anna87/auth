package com.auth.java.service;


import com.auth.java.model.User;
import com.auth.java.repositories.UserRepository;
import com.auth.java.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*final List<AppUser> users = Arrays.asList(
                new AppUser(1, "anna", passwordEncoder.encode("12345"), "USER"),
                new AppUser(2, "admin", passwordEncoder.encode("12345"), "ADMIN")
        );
        for(AppUser appUser: users) {
            if(appUser.getUsername().equals(username)) {
                List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                        .commaSeparatedStringToAuthorityList("ROLE_" + appUser.getRole());
                return new User(appUser.getUsername(), appUser.getPassword(), grantedAuthorities);
            }
        }*/
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username)
                );
        return UserPrincipal.create(user);
    }

    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
        return UserPrincipal.create(user);
    }

    private static class AppUser {
        private Integer id;
        private String username, password;
        private String role;

        public AppUser(Integer id, String username, String password, String role) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

    }

    /*
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
*/
}
