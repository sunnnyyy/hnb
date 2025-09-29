package com.hnb;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hnb.entities.User;
import com.hnb.helpers.AppConstants;
import com.hnb.repsitories.UserRepo;

@SpringBootApplication
public class Application  implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		  // 1. Create Admin User
	    User admin = new User();
	    admin.setUserId(UUID.randomUUID().toString());
	    admin.setName("admin");
	    admin.setEmail("admin@gmail.com");
	    admin.setPassword(passwordEncoder.encode("admin"));
	    admin.setRole("ROLE_ADMIN");
	    admin.setRoleList(List.of(AppConstants.ROLE_ADMIN)); // optional if you're storing multiple roles
	    admin.setEmailVerified(true);
	    admin.setEnabled(true);
	    admin.setAbout("This is the admin user");
	    admin.setPhoneVerified(true);

	    userRepo.findByEmail("admin@gmail.com").ifPresentOrElse(u -> {}, () -> {
	        userRepo.save(admin);
	        System.out.println("Admin user created");
	    });

	    // 2. Create Regular User
	    User user = new User();
	    user.setUserId(UUID.randomUUID().toString());
	    user.setName("user");
	    user.setEmail("user@gmail.com");
	    user.setPassword(passwordEncoder.encode("user"));
	    user.setRole("ROLE_MANAGER");
	    user.setRoleList(List.of(AppConstants.ROLE_MANAGER));
	    user.setEmailVerified(true);
	    user.setEnabled(true);
	    user.setAbout("This is a regular user");
	    user.setPhoneVerified(true);

	    userRepo.findByEmail("user@gmail.com").ifPresentOrElse(u -> {}, () -> {
	        userRepo.save(user);
	        System.out.println("Regular user created");
	    });

	}
}
