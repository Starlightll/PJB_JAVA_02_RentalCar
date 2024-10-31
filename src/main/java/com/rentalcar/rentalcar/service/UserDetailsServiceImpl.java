package com.rentalcar.rentalcar.service;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService, com.rentalcar.rentalcar.service.UserDetailsService {

	@Autowired
	UserRepo userRepo;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println("Email received: " + email);
		User user = userRepo.getUserByEmail(email);

		if (user == null) {
			System.out.println("User not found with email: " + email);
			throw new UsernameNotFoundException("Could not find user");
		}

		System.out.println("User found: " + user.getEmail());
		return new MyUserDetails(user);
	}

	// Thêm phương thức này để trả về User
	@Override
	public User loadUserByEmail(String email) {
		User user = userRepo.getUserByEmail(email);

		if(user == null) {
			throw new UserException("Not found");
		}
		return user;
	}


}
