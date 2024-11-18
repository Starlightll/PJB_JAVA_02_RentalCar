package com.rentalcar.rentalcar.repository;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepo extends CrudRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

}
