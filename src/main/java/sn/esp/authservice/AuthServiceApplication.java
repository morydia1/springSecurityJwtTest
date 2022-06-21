package sn.esp.authservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sn.esp.authservice.service.IAuthInitService;

@SpringBootApplication
public class AuthServiceApplication implements CommandLineRunner {

    @Autowired
    private IAuthInitService authInitService;

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {

        authInitService.initAppRoles();
        authInitService.initAppUsers();
        authInitService.initAppUserRoles();

    }
}
