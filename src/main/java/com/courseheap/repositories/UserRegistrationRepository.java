package com.courseheap.repositories;

import com.courseheap.entities.UserRegistration;
import com.oauth.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by ashish.p on 7/8/17.
 */
public interface UserRegistrationRepository  extends CrudRepository<UserRegistration, Long> {

    Optional<UserRegistration> findByName(String username);
    Optional<UserRegistration> findByEmail(String email);
    Optional<UserRegistration> findByVerifyToken(String verifyToken);
}
