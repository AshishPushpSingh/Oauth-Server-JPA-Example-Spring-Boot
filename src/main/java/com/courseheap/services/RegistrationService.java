package com.courseheap.services;

/**
 * Created by ashish.p on 1/8/17.
 */

import com.courseheap.Utils.EmailService;
import com.courseheap.entities.UserRegistration;
import com.courseheap.repositories.UserRegistrationRepository;
import com.oauth.entities.User;
import com.oauth.repositories.UserRepository;
import com.oauth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class RegistrationService {

    private EmailService emailService;

    private UserRepository userRepository;

    private UserService userService;

    private UserRegistrationRepository userRegistrationRepository;

    @Autowired
    public RegistrationService(EmailService emailService, UserRepository userRepository,
                               UserService userService, PasswordEncoder passwordEncoder,
                               UserRegistrationRepository userRegistrationRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.userRegistrationRepository = userRegistrationRepository;
    }

    public CompletableFuture<ResponseEntity<String>> UserSignUp(UserRegistration user) {

        return CompletableFuture.supplyAsync(() -> {
            String verifyToken = UUID.randomUUID().toString();
            ResponseEntity<String> stringResponseEntity;
            boolean present = userRepository.findByEmail(user.getEmail()).isPresent();
            try {
                if (!present) {
                    user.setVerifyToken(verifyToken);
                    UserRegistration savedUser = registerUser(user);
                    boolean mailStatus = sendVerificationMail(savedUser);
                    if (mailStatus) {
                        stringResponseEntity = new ResponseEntity<>
                                ("A verification link has been sent to your email ID. Please verify.", HttpStatus.CREATED);
                    } else {
                        userRepository.delete((long) savedUser.getId());
                        stringResponseEntity = new ResponseEntity<>("Sign up failed please try again..!", HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                } else {
                    stringResponseEntity = new ResponseEntity<>("User Already Exists..!", HttpStatus.BAD_REQUEST);
                }

            } catch (Exception e) {
                e.printStackTrace();
                stringResponseEntity = new ResponseEntity<>("Sign up failed please try again..!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return stringResponseEntity;
        });

    }

    public CompletableFuture<ResponseEntity<String>> verifyUser(String token) {
        return CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> stringResponseEntity;
            Optional<UserRegistration> userByVerifyToken = userRegistrationRepository.findByVerifyToken(token);
            if (userByVerifyToken.isPresent()) {
                UserRegistration unVerifiedUser = userByVerifyToken.get();
                unVerifiedUser.setVerifyToken("true");
                User user = prepareUserFromRegisteredUser(unVerifiedUser);
                //userRepository.delete((long)unVerifiedUser.getId());
                userRepository.save(user);
                stringResponseEntity = new ResponseEntity<>("Verification Successfully Done. Congrats!!!", HttpStatus.CREATED);
            } else {
                stringResponseEntity = new ResponseEntity<>("Verification failed please Sign-up again..!", HttpStatus.BAD_REQUEST);
            }
            return stringResponseEntity;
        });
    }

    private boolean sendVerificationMail(UserRegistration user) {
        return emailService.sendMailToUser(user.getEmail(), user.getVerifyToken());
    }

    private UserRegistration registerUser(UserRegistration userRegistration){
        userRegistration.setPassword(userService.getPasswordEncoder().encode(userRegistration.getPassword()));
        userRegistrationRepository.save(userRegistration);
        return userRegistration;
    }

    @org.jetbrains.annotations.Contract("_ -> !null")
    private User prepareUserFromRegisteredUser(UserRegistration userRegistration) {
       return new User(userRegistration.getName(), userRegistration.getLastName(), userRegistration.getEmail(),
                1, userRegistration.getPassword());
    }

}
