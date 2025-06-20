package br.com.eurekaserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desativa CSRF (não necessário no Eureka)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher("/actuator/**")).permitAll() // Permite acesso aos Actuators sem autenticação
                        .anyRequest().authenticated() // Exige autenticação para todas as outras requisições
                )
                .httpBasic(Customizer.withDefaults()); // Forma correta para Spring Security 6.1+

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password("{noop}admin") // {noop} significa que a senha não está criptografada
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
